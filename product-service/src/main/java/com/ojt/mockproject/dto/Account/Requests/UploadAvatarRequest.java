package com.ojt.mockproject.dto.Account.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadAvatarRequest {
    private MultipartFile file;
}
