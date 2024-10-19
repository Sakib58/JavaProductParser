package com.example.javaproductparser.parser.service;

import com.example.javaproductparser.parser.dto.ProductChangeSummaryDto;
import com.example.javaproductparser.parser.dto.ProductDto;
import com.example.javaproductparser.parser.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final FileParser fileParser;

    public ProductService(ProductRepository productRepository, FileParser fileParser) {
        this.productRepository = productRepository;
        this.fileParser = fileParser;
    }

    public ProductChangeSummaryDto uploadFile(InputStream inputStream) throws IOException {
        List<ProductDto> productList = fileParser.parseFile(inputStream);
        //todo: Logic to determine changes and save to database
        return calculateChanges(productList);
    }

    private ProductChangeSummaryDto calculateChanges(List<ProductDto> productList) {
        ProductChangeSummaryDto summary = new ProductChangeSummaryDto();
        //todo: Logic to identify new, updated, and unchanged rows
        return summary;
    }
}
