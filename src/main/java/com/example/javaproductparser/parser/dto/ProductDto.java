package com.example.javaproductparser.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String sku;
    private String title;
    private BigDecimal price;
    private int quantity;

    public interface Create {}
    public interface Update {}
}
