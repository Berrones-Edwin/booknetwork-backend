package com.example.book_network.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String file) {

        if (StringUtils.isBlank(file)) {
            return null;
        }
        try {
            Path filePath = new File(file).toPath();
            return Files.readAllBytes(filePath);

        } catch (Exception e) {
            log.warn("No file found in the path {}", file);
        }
        return null;
    }

}
