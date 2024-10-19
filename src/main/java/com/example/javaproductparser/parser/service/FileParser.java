package com.example.javaproductparser.parser.service;

import com.example.javaproductparser.parser.dto.ProductDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileParser {
    List<ProductDto> parseFile(InputStream inputStream) throws IOException;
}
