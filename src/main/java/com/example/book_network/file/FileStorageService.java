package com.example.book_network.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${file.upload.photos-outup-path}")
    private String fileUploadPath;

    public String saveFile(@Nonnull MultipartFile file, @Nonnull Integer id) {
        final String fileUploadSubPath = "users" + File.separator + id;

        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile file, @Nonnull String fileUploadSubPath) {

        final String finalUploadPath = fileUploadSubPath + File.separator + fileUploadPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();

            if (!folderCreated) {

                log.warn("Failed to create the target folder");
                return null;
            }
        }

        final String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + fileExtension;
        Path targePath = Paths.get(targetFilePath);

        try {
            Files.write(targePath, file.getBytes());
            log.info("File saved to " + targetFilePath);
            return targetFilePath;
        } catch (IOException e) {

            log.error("File was not saved ", e);
        }
        return null;

    }

    private String getFileExtension(String originalFilename) {

        if (originalFilename == null || originalFilename.isEmpty())
            return "";

        int lastDotIndex = originalFilename.lastIndexOf(("."));
        if (lastDotIndex == -1) {
            return "";
        }

        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }

}
