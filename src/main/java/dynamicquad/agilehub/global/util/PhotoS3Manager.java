package dynamicquad.agilehub.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import java.io.File;
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
        //임시 디렉터리 생성
        log.info("upload start");
        File tempUploadDirectory = uploadDirectory(getLocalDirectoryPath(workingDirectory));
        log.info("upload directory : {}", tempUploadDirectory);
        File tempUploadPath = new File(tempUploadDirectory, fileName);
        log.info("upload path : {}", tempUploadPath);
        File file = uploadFileInLocal(multipartFile, tempUploadPath);

        log.info("s3 upload start");
        amazonS3.putObject(new PutObjectRequest(bucket + workingDirectory, fileName, file));

        file.delete();

        return rootURL + "/" + workingDirectory + "/" + fileName;

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
        return SYSTEM_PATH + "/" + localDirectory + workingDirectory;
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
        if (originalImageUrl.contains(rootURL + "/" + workingDirectory)) {
            String fileName = originalImageUrl.substring(originalImageUrl.length() + 1);
            amazonS3.deleteObject(bucket + workingDirectory, fileName);
            amazonS3.deleteObject(bucket, fileName);
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

}
