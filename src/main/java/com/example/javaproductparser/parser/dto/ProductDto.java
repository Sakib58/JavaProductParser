package com.example.javaproductparser.parser.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
    @Null(groups = Create.class, message = "ID should be null when adding a new Product!")
    @NotNull(groups = Update.class, message = "ID is required for updating a Product!")
    private Long id;
    @NotBlank(groups = {Create.class, Update.class}, message = "An Unique Identifier of a Product (sku) is required!")
    private String sku;
    @NotBlank(groups = {Create.class, Update.class}, message = "A Title of a Product is required!")
    private String title;
    private BigDecimal price;
    private int quantity;

    public interface Create {}
    public interface Update {}
}
