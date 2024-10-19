package com.example.javaproductparser.parser.repository;

import com.example.javaproductparser.parser.entity.ProductChangeSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductChangeSummaryRepository extends JpaRepository<ProductChangeSummary, Long> {
}
