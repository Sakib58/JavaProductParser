package com.example.javaproductparser.parser.controller;

import com.example.javaproductparser.parser.dto.ProductChangeSummaryDto;
import com.example.javaproductparser.parser.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping(ApiEndpoints.BASE)
public class ProductRestController {
    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(ApiEndpoints.UPLOAD)
    public ResponseEntity<ProductChangeSummaryDto> uploadFile(@RequestParam("file")MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body(null);
        }

        try (InputStream inputStream = file.getInputStream()) {
            ProductChangeSummaryDto productChangeSummaryDto = productService.uploadFile(inputStream);
            return ResponseEntity.ok(productChangeSummaryDto);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
