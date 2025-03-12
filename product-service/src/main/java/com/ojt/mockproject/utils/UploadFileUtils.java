package com.ojt.mockproject.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UploadFileUtils {
    @Value("${firebase.storage.bucket}")
    private String bucketName;

    @PostConstruct
    private void initializeStorage() {
        try {
            storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(
                    new ClassPathResource(credentialsFilePath).getInputStream())).build().getService();
        } catch (IOException e) {
            throw new RuntimeException("storage exception");
        }
    }

    @Value("${fcm.credentials.file.path}")
    private String credentialsFilePath;
    private Storage storage;

    private String generateUniqueFileName(String folderName, String originalFileName) {
        String uniqueId = UUID.randomUUID().toString();
        return folderName + "/" + uniqueId + "_" + originalFileName;
    }

    public String uploadFile( String folderName, MultipartFile file, int maxWidthSizeImage) throws IOException {
        String fileName = generateUniqueFileName(folderName, file.getOriginalFilename());
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        try (WriteChannel writer = storage.writer(blobInfo);
            InputStream resizedImageStream = resizeImage(file.getInputStream(), maxWidthSizeImage)) {
                byte[] buffer = new byte[10 * 1024 * 1024]; // 10 MB buffer
            int limit;
            long totalBytes = resizedImageStream.available();
            long uploadedBytes = 0;

            while ((limit = resizedImageStream.read(buffer)) >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, limit));
                uploadedBytes += limit;
                double progress = (double) uploadedBytes / totalBytes * 100;
                // Send progress update over WebSocket
                log.info("Progress: " + progress);
            }
//            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            return String.format("%s", fileName);
        } catch (StorageException e) {
            throw new IOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getSignedImageUrl(String baseImageUrl) throws IOException {
        URL signedUrl = storage.signUrl(
                BlobInfo.newBuilder(BlobId.of(bucketName, baseImageUrl)).build(),
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature());
        return signedUrl.toString();
    }
    //time 30days
    public String getSignedAvatarUrl(String baseImageUrl) throws IOException {
        URL signedUrl = storage.signUrl(
                BlobInfo.newBuilder(BlobId.of(bucketName, baseImageUrl)).build(),
                7, TimeUnit.DAYS,
                Storage.SignUrlOption.withV4Signature());
        return signedUrl.toString();
    }

    private InputStream resizeImage(InputStream inputStream, int maxSize) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // Calculate the new dimensions while maintaining the aspect ratio
        if (originalWidth > maxSize || originalHeight > maxSize) {
            if (originalWidth > originalHeight) {
                newWidth = maxSize;
                newHeight = (newWidth * originalHeight) / originalWidth;
            } else {
                newHeight = maxSize;
                newWidth = (newHeight * originalWidth) / originalHeight;
            }
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

//    private void sendProgressUpdate(double progress) {
//        // Send progress update to client with sessionId using WebSocket
//        messagingTemplate.convertAndSend( "/topic/upload-status", String.valueOf(progress));
//    }
}
