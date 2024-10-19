package com.example.javaproductparser.parser.service;

import com.example.javaproductparser.parser.dto.ProductChangeSummaryDto;
import com.example.javaproductparser.parser.dto.ProductDto;
import com.example.javaproductparser.parser.entity.Product;
import com.example.javaproductparser.parser.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return calculateAndSaveChanges(productList);
    }

    private ProductChangeSummaryDto calculateAndSaveChanges(List<ProductDto> productList) {
        ProductChangeSummaryDto summary = new ProductChangeSummaryDto();
        List<ProductDto> newRows = new ArrayList<>();
        List<ProductDto> updatedRows = new ArrayList<>();
        List<ProductDto> unchangedRows = new ArrayList<>();

        Map<String, Product> existingProductsMap = productRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Product::getSku, Function.identity()));

        for (ProductDto incomingProduct : productList) {
            Product existingProduct = existingProductsMap.get(incomingProduct.getSku());

            if (existingProduct == null) {
                newRows.add(incomingProduct);
            } else {
                boolean isUpdated = false;
                if (!existingProduct.getTitle().equals(incomingProduct.getTitle()) ||
                        existingProduct.getPrice().compareTo(incomingProduct.getPrice()) != 0 ||
                        existingProduct.getQuantity() != incomingProduct.getQuantity()) {
                    updatedRows.add(incomingProduct);
                    isUpdated = true;
                }

                if (!isUpdated) {
                    unchangedRows.add(incomingProduct);
                }
            }
        }

        saveProducts(newRows, updatedRows);

        summary.setNewRows(newRows);
        summary.setUpdatedRows(updatedRows);
        summary.setUnchangedRows(unchangedRows);

        return summary;
    }

    private void saveProducts(List<ProductDto> newRows, List<ProductDto> updatedRows) {
        // Save new products
        for (ProductDto newProduct : newRows) {
            Product productToSave = new Product();
            productToSave.setSku(newProduct.getSku());
            productToSave.setTitle(newProduct.getTitle());
            productToSave.setPrice(newProduct.getPrice());
            productToSave.setQuantity(newProduct.getQuantity());
            productRepository.save(productToSave);
        }

        // Update existing products
        for (ProductDto updatedProduct : updatedRows) {
            Product existingProduct = productRepository.findBySku(updatedProduct.getSku()).orElseThrow(() ->
                    new RuntimeException("Product not found for update: " + updatedProduct.getSku()));

            existingProduct.setTitle(updatedProduct.getTitle());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            productRepository.save(existingProduct);
        }
    }

}
