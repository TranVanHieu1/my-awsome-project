package com.ojt.mockproject.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public void deleteLogFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            // Kiểm tra nếu file tồn tại
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("Deleted file: " + filePath);
            } else {
                logger.warn("File not found: " + filePath);
            }
        } catch (IOException e) {
            logger.error("Failed to delete file: " + filePath, e);
        }
    }
}
