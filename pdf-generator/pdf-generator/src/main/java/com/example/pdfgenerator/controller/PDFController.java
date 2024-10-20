package com.example.pdfgenerator.controller;

import com.example.pdfgenerator.model.InvoiceRequest;
import com.example.pdfgenerator.service.PDFService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PDFController {

    @Autowired
    private PDFService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<FileSystemResource> generatePDF(@RequestBody InvoiceRequest request) {
        try {
            File pdfFile = pdfService.generateOrRetrievePDF(request);
            FileSystemResource resource = new FileSystemResource(pdfFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
