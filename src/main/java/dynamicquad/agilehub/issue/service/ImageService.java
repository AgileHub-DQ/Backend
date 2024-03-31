package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.global.util.PhotoS3Manager;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.issue.domain.image.ImageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final PhotoS3Manager photoS3Manager;

    public void saveImages(Issue issue, List<MultipartFile> files, String workingDirectory) {
        log.info("save images");
        List<String> imagePath = photoS3Manager.uploadPhotos(files, workingDirectory);
        log.info("save images = {} ", imagePath);
        if (imagePath.isEmpty()) {
            return;
        }

        List<Image> images = imagePath.stream()
            .map(path -> Image.builder().path(path).build().setIssue(issue))
            .toList();

        imageRepository.saveAll(images);
    }

    public void cleanupMismatchedImages(Epic epic, List<String> imageUrls, String workingDirectory) {
        List<Image> images = imageRepository.findByIssue(epic);
        List<String> deleteImagePath = images.stream()
            .map(Image::getPath)
            .filter(path -> !imageUrls.contains(path))
            .toList();

        photoS3Manager.deletePhotos(deleteImagePath, workingDirectory);
        imageRepository.deletePaths(deleteImagePath);
    }
}
