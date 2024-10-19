package com.example.javaproductparser;

import com.example.javaproductparser.parser.dto.ProductChangeSummaryDto;
import com.example.javaproductparser.parser.dto.ProductDto;
import com.example.javaproductparser.parser.entity.ProductChangeSummary;
import com.example.javaproductparser.parser.service.ProductService;
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

    @Autowired
    private ProductService productService;

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
    void uploadFileTest() throws IOException {
        String filePath = "C:\\Users\\Reve\\OneDrive\\Documents\\product_list.xlsx";
        ProductChangeSummaryDto productChangeSummaryDto = productService.uploadFile(new FileInputStream(filePath));
        assertEquals(10, productChangeSummaryDto.getNewRows().size());
        assertEquals(0, productChangeSummaryDto.getUnchangedRows().size());
        assertEquals(0, productChangeSummaryDto.getUpdatedRows().size());

        // New file with 3 rows changed
        filePath = "C:\\Users\\Reve\\OneDrive\\Documents\\product_list_changed_3_rows.xlsx";
        productChangeSummaryDto = productService.uploadFile(new FileInputStream(filePath));
        assertEquals(0, productChangeSummaryDto.getNewRows().size());
        assertEquals(7, productChangeSummaryDto.getUnchangedRows().size());
        assertEquals(3, productChangeSummaryDto.getUpdatedRows().size());

        // New file with 2 rows changed and 3 new rows added
        filePath = "C:\\Users\\Reve\\OneDrive\\Documents\\product_list_changed_2_rows_added_3.xlsx";
        productChangeSummaryDto = productService.uploadFile(new FileInputStream(filePath));
        assertEquals(3, productChangeSummaryDto.getNewRows().size());
        assertEquals(8, productChangeSummaryDto.getUnchangedRows().size());
        assertEquals(2, productChangeSummaryDto.getUpdatedRows().size());
    }

    @Test
    void getSummaryHistoryOfTheUploadedFileTest(){
        List<ProductChangeSummary> summaryList = productService.getSummaryHistoryOfTheUploadedFile();
        assertEquals(summaryList.size(), 3);
        assertEquals(10, summaryList.get(2).getNewRowsCount()); // Latest entry would be the first, so initially 10 rows were inserted
        assertEquals(3, summaryList.get(1).getChangedRowsCount()); // Then 3 rows modified
        // Then modified two rows and added 3 new rows
        assertEquals(2, summaryList.get(0).getChangedRowsCount());
        assertEquals(3, summaryList.get(0).getNewRowsCount());
    }

    @Test
    void getProductInfoBySkuTest(){
        ProductDto productDto = productService.getProductInfoBySku("PROD-1010");
        assertEquals("Smartwatch", productDto.getTitle());
        assertEquals(80, productDto.getQuantity());
        assertEquals("129.99", productDto.getPrice().toPlainString());
    }

    @Test
    void contextLoads() {
    }

}
