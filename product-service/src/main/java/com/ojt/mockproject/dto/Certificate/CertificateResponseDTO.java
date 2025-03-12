package com.ojt.mockproject.dto.Certificate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CertificateResponseDTO {
    private Integer id;
    private String name;
    private String nameUser;
    private String description;
    private String createAt;
}
