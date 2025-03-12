package com.ojt.mockproject.controller;


import com.ojt.mockproject.dto.Certificate.CertificateResponseDTO;
import com.ojt.mockproject.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    CertificateService  certificateService;

    @GetMapping("/get/{id}")
    public ResponseEntity getCertificateByCourse(@PathVariable Integer id){
        CertificateResponseDTO certificate = certificateService.getCertificateByCourse(id);
        return ResponseEntity.ok(certificate);
    }
}
