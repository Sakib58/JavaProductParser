package com.example.javaproductparser;

import com.example.javaproductparser.parser.dto.ProductDto;
import com.example.javaproductparser.parser.service.XlsxFileParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JavaProductParserApplicationTests {

    @Autowired
    private XlsxFileParser xlsxFileParser;

    @Test
    void parseFileTest() throws IOException {
        String filePath = "C:\\Users\\Reve\\OneDrive\\Documents\\product_list.xlsx";
        InputStream inputStream = new FileInputStream(filePath);

        List<ProductDto> productList = xlsxFileParser.parseFile(inputStream);

        assertNotNull(productList);
        assertFalse(productList.isEmpty());
        assertEquals(10, productList.size());

        // To make sure that the row with column names is ignored
        assertNotEquals("sku", productList.get(0).getSku().toLowerCase());

        // To make sure dto contains data
        assertEquals("PROD-1001", productList.get(0).getSku());
    }

    @Test
    void contextLoads() {
    }

}
