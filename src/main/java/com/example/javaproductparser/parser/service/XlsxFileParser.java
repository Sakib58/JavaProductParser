package com.example.javaproductparser.parser.service;

import com.example.javaproductparser.parser.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class XlsxFileParser implements FileParser{
    @Override
    public List<ProductDto> parseFile(InputStream inputStream) throws IOException {
        return null;
    }
}
