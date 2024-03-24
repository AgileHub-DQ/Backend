package dynamicquad.agilehub.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


@Component
@RequiredArgsConstructor
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
            throw new IllegalArgumentException("이미지 파일이 존재하지 않습니다. multipartFile" + null);
        }

        return uploadPhoto(multipartFile, workingDirectory);
    }

    private String uploadPhoto(MultipartFile multipartFile, String workingDirectory) {
        String fileName = createFileName(multipartFile.getOriginalFilename());
        //임시 디렉터리 생성
        File tempUploadDirectory = uploadDirectory(getLocalDirectoryPath(workingDirectory));
        File tempUploadPath = new File(tempUploadDirectory, fileName);
        File file = uploadFileInLocal(multipartFile, tempUploadPath);

        amazonS3.putObject(new PutObjectRequest(bucket + workingDirectory, fileName, file));

        file.delete();

        return rootURL + "/" + workingDirectory + "/" + fileName;

    }

    private File uploadFileInLocal(MultipartFile multipartFile, File tempUploadPath) {
        try {
            multipartFile.transferTo(tempUploadPath);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 변환 실패");
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
        String extension = StringUtils.getFilenameExtension(originalFileName); // 파일 확장자
        if (extension == null) {
            throw new IllegalArgumentException("파일 확장자는 반드시 포함되어야 합니다. filename: " + originalFileName);
        }
        String fileBaseName = UUID.randomUUID().toString().substring(0, 8);
        validateFileName(fileBaseName);
        validateExtension(extension);
        return fileBaseName + "_" + System.currentTimeMillis() + "." + extension;
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일 이름은 반드시 포함되어야 합니다. filename: " + fileName);
        }
    }

    private void validateExtension(String extension) {
        Set<String> IMAGE_EXTENSIONS = Set.of("jpeg", "jpg", "png", "webp", "heic", "heif");

        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("이미지 파일 확장자만 업로드 가능합니다. extension: " + extension);
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

}
