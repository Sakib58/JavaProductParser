package com.example.javaproductparser.parser.controller;

import com.example.javaproductparser.parser.dto.ProductChangeSummaryDto;
import com.example.javaproductparser.parser.dto.ProductDto;
import com.example.javaproductparser.parser.entity.ProductChangeSummary;
import com.example.javaproductparser.parser.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;

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

    @GetMapping(ApiEndpoints.CHANGE_HISTORIES)
    public ResponseEntity<List<ProductChangeSummary>> getProductChangeHistories(){
        try{
            List<ProductChangeSummary> productChangeSummaries = productService.getSummaryHistoryOfTheUploadedFile();
            return ResponseEntity.ok(productChangeSummaries);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(ApiEndpoints.PRODUCT_BY_SKU)
    public ResponseEntity<ProductDto> getProductInfoBySku(@RequestParam("sku") String sku) {
        try {
            ProductDto productDto = productService.getProductInfoBySku(sku);
            return ResponseEntity.ok(productDto);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
