package com.example.javaproductparser.parser.service;

import com.example.javaproductparser.parser.dto.ProductChangeSummaryDto;
import com.example.javaproductparser.parser.dto.ProductDto;
import com.example.javaproductparser.parser.entity.Product;
import com.example.javaproductparser.parser.entity.ProductChangeSummary;
import com.example.javaproductparser.parser.repository.ProductChangeSummaryRepository;
import com.example.javaproductparser.parser.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final FileParserFactory fileParserFactory;
    private final ProductChangeSummaryRepository productChangeSummaryRepository;

    public ProductService(ProductRepository productRepository,
                          FileParserFactory fileParserFactory,
                          ProductChangeSummaryRepository productChangeSummaryRepository) {
        this.productRepository = productRepository;
        this.fileParserFactory = fileParserFactory;
        this.productChangeSummaryRepository = productChangeSummaryRepository;
    }

    public ProductChangeSummaryDto uploadFile(InputStream inputStream, String fileName) throws IOException {
        log.info("Uploading file: {}", fileName);

        FileParser fileParser = fileParserFactory.getParser(fileName);
        log.info("Using parser: {}", fileParser.getClass().getSimpleName());

        List<ProductDto> productList = fileParser.parseFile(inputStream);
        log.info("Parsed {} products from file", productList.size());

        return calculateAndSaveChanges(productList);
    }

    private ProductChangeSummaryDto calculateAndSaveChanges(List<ProductDto> productList) {
        log.info("Calculating changes for {} products", productList.size());

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
                log.info("New product found: SKU {}", incomingProduct.getSku());
            } else {
                boolean isUpdated = false;
                if (!existingProduct.getTitle().equals(incomingProduct.getTitle()) ||
                        existingProduct.getPrice().compareTo(incomingProduct.getPrice()) != 0 ||
                        existingProduct.getQuantity() != incomingProduct.getQuantity()) {
                    updatedRows.add(incomingProduct);
                    isUpdated = true;
                    log.info("Product updated: SKU {}", incomingProduct.getSku());
                }

                if (!isUpdated) {
                    unchangedRows.add(incomingProduct);
                }
            }
        }

        log.info("New products: {}, Updated products: {}, Unchanged products: {}",
                newRows.size(), updatedRows.size(), unchangedRows.size());

        summary.setNewRows(newRows);
        summary.setUpdatedRows(updatedRows);
        summary.setUnchangedRows(unchangedRows);

        saveChangeSummary(summary);
        saveProducts(newRows, updatedRows);

        return summary;
    }

    private void saveChangeSummary(ProductChangeSummaryDto summaryDto) {
        log.info("Saving change summary");

        ProductChangeSummary summary = new ProductChangeSummary();
        summary.setNewRowsCount(summaryDto.getNewRows().size());
        summary.setChangedRowsCount(summaryDto.getUpdatedRows().size());
        summary.setCreatedAt(LocalDateTime.now());
        summary.setSummary(createChangeSummary(summaryDto.getNewRows(), summaryDto.getUpdatedRows()));

        productChangeSummaryRepository.save(summary);

        log.info("Change summary saved with {} new products and {} updated products",
                summaryDto.getNewRows().size(), summaryDto.getUpdatedRows().size());
    }

    private String createChangeSummary(List<ProductDto> newRows, List<ProductDto> updatedRows) {
        StringBuilder summaryBuilder = new StringBuilder();

        // Add new products details
        if (!newRows.isEmpty()) {
            log.info("Adding new product details to summary");
            summaryBuilder.append("New Products:\n");
            for (ProductDto newProduct : newRows) {
                summaryBuilder.append("SKU: ").append(newProduct.getSku())
                        .append(", Title: ").append(newProduct.getTitle())
                        .append(", Price: ").append(newProduct.getPrice())
                        .append(", Quantity: ").append(newProduct.getQuantity())
                        .append("\n");
            }
        }

        // Add updated products details
        if (!updatedRows.isEmpty()) {
            log.info("Adding updated product details to summary");
            summaryBuilder.append("Updated Products:\n");
            for (ProductDto updatedProduct : updatedRows) {
                Optional<Product> optionalExistingProduct = productRepository.findBySku(updatedProduct.getSku());
                if (optionalExistingProduct.isPresent()) {
                    Product existingProduct = optionalExistingProduct.get();
                    summaryBuilder.append("SKU: ").append(updatedProduct.getSku()).append("\n");

                    if (!Objects.equals(existingProduct.getTitle(), updatedProduct.getTitle())) {
                        summaryBuilder.append(" - Title: '").append(existingProduct.getTitle())
                                .append("' changed to '").append(updatedProduct.getTitle()).append("'\n");
                    }
                    if (existingProduct.getPrice() != null && updatedProduct.getPrice() != null &&
                            existingProduct.getPrice().compareTo(updatedProduct.getPrice()) != 0) {
                        summaryBuilder.append(" - Price: '").append(existingProduct.getPrice())
                                .append("' changed to '").append(updatedProduct.getPrice()).append("'\n");
                    }
                    if (existingProduct.getQuantity() != updatedProduct.getQuantity()) {
                        summaryBuilder.append(" - Quantity: '").append(existingProduct.getQuantity())
                                .append("' changed to '").append(updatedProduct.getQuantity()).append("'\n");
                    }
                }
            }
        }

        return summaryBuilder.toString();
    }

    private void saveProducts(List<ProductDto> newRows, List<ProductDto> updatedRows) {
        // Save new products
        for (ProductDto newProduct : newRows) {
            log.info("Saving new product: SKU {}", newProduct.getSku());
            Product productToSave = new Product();
            productToSave.setSku(newProduct.getSku());
            productToSave.setTitle(newProduct.getTitle());
            productToSave.setPrice(newProduct.getPrice());
            productToSave.setQuantity(newProduct.getQuantity());
            productRepository.save(productToSave);
        }

        // Update existing products
        for (ProductDto updatedProduct : updatedRows) {
            log.info("Updating product: SKU {}", updatedProduct.getSku());
            Product existingProduct = productRepository.findBySku(updatedProduct.getSku()).orElseThrow(() -> {
                log.error("Product not found for update: SKU {}", updatedProduct.getSku());
                return new RuntimeException("Product not found for update: " + updatedProduct.getSku());
            });

            existingProduct.setTitle(updatedProduct.getTitle());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            productRepository.save(existingProduct);
        }
    }

    public List<ProductChangeSummary> getSummaryHistoryOfTheUploadedFile(){
        log.info("Fetching change summary history");
        return productChangeSummaryRepository
                .findAll().stream().sorted(Comparator.comparing(ProductChangeSummary::getCreatedAt).reversed()).toList();
    }

    public ProductDto getProductInfoBySku(String sku) {
        log.info("Fetching product info for SKU: {}", sku);
        Product product = productRepository.findBySku(sku).orElseThrow();
        return ProductDto.builder()
                .id(product.getId())
                .sku(product.getSku())
                .title(product.getTitle())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

    public List<ProductDto> getProductList() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    ProductDto productDto = new ProductDto();
                    BeanUtils.copyProperties(product, productDto);
                    return productDto;
                })
                .toList();
    }
}
