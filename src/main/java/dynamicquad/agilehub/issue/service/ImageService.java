package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.global.util.PhotoS3Manager;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.Image;
import dynamicquad.agilehub.issue.repository.ImageRepository;
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
        List<String> imagePath = photoS3Manager.uploadPhotos(files, workingDirectory);
        if (imagePath.isEmpty()) {
            return;
        }

        List<Image> images = imagePath.stream()
            .map(path -> Image.builder().path(path).build().setIssue(issue))
            .toList();

        imageRepository.saveAll(images);
    }

    public void cleanupMismatchedImages(Issue issue, List<String> imageUrls, String workingDirectory) {
        List<Image> images = imageRepository.findByIssue(issue);

        if (images.isEmpty()) {
            return;
        }
        if (imageUrls == null || imageUrls.isEmpty()) {
            photoS3Manager.deletePhotos(images.stream().map(Image::getPath).toList(), workingDirectory);
            imageRepository.deleteImages(images);
            return;
        }

        List<String> deleteImagePath = images.stream()
            .map(Image::getPath)
            .filter(path -> !imageUrls.contains(path))
            .toList();

        photoS3Manager.deletePhotos(deleteImagePath, workingDirectory);
        imageRepository.deletePaths(deleteImagePath);
    }
}
