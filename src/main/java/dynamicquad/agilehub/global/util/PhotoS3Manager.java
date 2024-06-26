package dynamicquad.agilehub.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoS3Manager implements PhotoManager {

    private static final String SYSTEM_PATH = System.getProperty("user.dir"); // 현재 디렉터리
    private static final String SLASH = "/";

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.rootURL}")
    private String rootURL;

    @Value("${aws.s3.directory}")
    private String localDirectory;

    private final AmazonS3 amazonS3;


    @Override
    public String upload(MultipartFile multipartFile, String workingDirectory) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_NOT_EXIST);
        }

        return uploadPhoto(multipartFile, workingDirectory);
    }

    private String uploadPhoto(MultipartFile multipartFile, String workingDirectory) {

        String fileName = createFileName(multipartFile.getOriginalFilename());
        File tempUploadDirectory = uploadDirectory(getLocalDirectoryPath(workingDirectory));
        File tempUploadPath = new File(tempUploadDirectory, fileName);
        File file = uploadFileInLocal(multipartFile, tempUploadPath);

        ObjectMetadata metadata = getObjectMetadata(multipartFile);
        try {
            amazonS3.putObject(
                new PutObjectRequest(bucket, workingDirectory + SLASH + fileName, new FileInputStream(file), metadata));
        } catch (FileNotFoundException e) {
            log.error("파일 업로드 실패", e);
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAIL);
        }

        file.delete();
        return rootURL + SLASH + workingDirectory + SLASH + fileName;
    }

    private ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        String mimeType = multipartFile.getContentType();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(mimeType);
        metadata.setContentLength(multipartFile.getSize());
        return metadata;
    }

    private File uploadFileInLocal(MultipartFile multipartFile, File tempUploadPath) {
        try {
            multipartFile.transferTo(tempUploadPath);
        } catch (IOException e) {
            log.error("파일 변환 실패", e);
            throw new GeneralException(ErrorStatus.FILE_CONVERT_FAIL);
        }
        return tempUploadPath;
    }

    private File uploadDirectory(String localDirectoryPath) {
        File directory = new File(localDirectoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private String getLocalDirectoryPath(String workingDirectory) {
        return SYSTEM_PATH + SLASH + localDirectory + workingDirectory;
    }


    private String createFileName(String originalFileName) {
        String extension = StringUtils.getFilenameExtension(originalFileName); // 확장자
        if (extension == null) {
            throw new GeneralException(ErrorStatus.FILE_EXTENSION_NOT_EXIST);
        }
        String fileBaseName = UUID.randomUUID().toString().substring(0, 8);
        log.info("fileBaseName : {}", fileBaseName);
        validateFileName(fileBaseName);
        validateExtension(extension);

        StringBuilder sb = new StringBuilder();
        sb.append(fileBaseName).append("_").append(System.currentTimeMillis()).append(".").append(extension);
        return sb.toString();
    }

    private void validateFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new GeneralException(ErrorStatus.FILE_NAME_NOT_EXIST);
        }
    }

    private void validateExtension(String extension) {
        Set<String> IMAGE_EXTENSIONS = Set.of("jpeg", "jpg", "png", "webp", "heic", "heif");

        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new GeneralException(ErrorStatus.FILE_EXTENSION_NOT_IMAGE);
        }
    }


    @Override
    public void delete(String originalImageUrl, String workingDirectory) {
        if (originalImageUrl.contains(rootURL + SLASH + workingDirectory)) {
            String combinedURL = rootURL + SLASH + workingDirectory + SLASH;
            String fileName = originalImageUrl.substring(combinedURL.length());
            amazonS3.deleteObject(bucket, workingDirectory + SLASH + fileName);
        }
    }


    public List<String> uploadPhotos(List<MultipartFile> files, String workingDirectory) {

        if (files == null || files.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_NOT_EXIST);
        }

        return files.stream()
            .map(file -> upload(file, workingDirectory))
            .toList();
    }


    public boolean deletePhotos(List<String> deleteImagePath, String workingDirectory) {
        if (deleteImagePath.isEmpty()) {
            return true;
        }

        deleteImagePath.forEach(imagePath -> delete(imagePath, workingDirectory));
        return true;
    }
}
