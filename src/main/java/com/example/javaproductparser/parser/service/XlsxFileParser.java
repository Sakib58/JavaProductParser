package com.example.javaproductparser.parser.service;

import com.example.javaproductparser.parser.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class XlsxFileParser implements FileParser{
    @Override
    public List<ProductDto> parseFile(InputStream inputStream) throws IOException {
        List<ProductDto> productDtoList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            ProductDto productDto = new ProductDto();
            productDto.setSku(row.getCell(0).getStringCellValue());
            productDto.setTitle(row.getCell(1).getStringCellValue());
            productDto.setPrice(BigDecimal.valueOf(row.getCell(2).getNumericCellValue()));
            productDto.setQuantity((int) row.getCell(3).getNumericCellValue());
            productDtoList.add(productDto);
        }
        return productDtoList;
    }
}
