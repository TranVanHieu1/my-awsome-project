package com.ojt.mockproject.dto.Certificate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CertificateRequestDTO {
    private String name;
    private String description;
    private Integer courseId;
}
