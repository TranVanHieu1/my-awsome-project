package com.ojt.mockproject.listener;

import com.ojt.mockproject.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.io.File;

@Component
public class ShutDownListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    FileUtil fileUtil;

    @Autowired
    private Environment env;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        String logFilePath = env.getProperty("logging.file.path");
        if (logFilePath == null) {
            throw new IllegalStateException("Logging file path not configured.");
        }
        // Thực hiện xóa file log khi ứng dụng đang tắt
        File logFile = new File(logFilePath);
        if (logFile.exists()) {
            System.out.println("Founded file!");
            System.out.println(logFile);
            if (logFile.delete()) {
                System.out.println("Deleted log file: " + logFile.getAbsolutePath());
            } else {
                System.out.println("Failed to delete log file: " + logFile.getAbsolutePath());
            }
        } else {
            System.out.println("Log file does not exist: " + logFile.getAbsolutePath());
        }
    }
}
