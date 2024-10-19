package com.example.javaproductparser.parser.repository;

import com.example.javaproductparser.parser.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
