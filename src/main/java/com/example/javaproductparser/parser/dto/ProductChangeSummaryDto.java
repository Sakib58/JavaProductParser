package com.example.javaproductparser.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductChangeSummaryDto {
    private List<ProductDto> unchangedRows = new ArrayList<>();
    private List<ProductDto> updatedRows = new ArrayList<>();
    private List<ProductDto> newRows = new ArrayList<>();
}
