package com.example.javaproductparser.parser.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class FileParserFactory {
    private final Map<String, Supplier<FileParser>> parsers = new HashMap<>();

    public FileParserFactory () {
        parsers.put("xlsx", XlsxFileParser::new);
    }

    public FileParser getParser(String fileName) {
        String extension = getFileExtension(fileName)
                .orElseThrow(() -> new IllegalArgumentException("File extension not found: " + fileName));

        Supplier<FileParser> parserSupplier = parsers.get(extension.toLowerCase());
        if (parserSupplier == null) {
            throw new IllegalArgumentException("Unsupported file extension: " + extension);
        }

        return parserSupplier.get();
    }

    private Optional<String> getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }
}
