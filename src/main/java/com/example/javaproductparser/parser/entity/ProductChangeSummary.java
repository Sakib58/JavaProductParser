package com.example.javaproductparser.parser.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "product_change_summary")
public class ProductChangeSummary {
    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "product_change_summary_seq_gen" )
    @SequenceGenerator( name = "product_change_summary_seq_gen", sequenceName = "product_change_summary_seq", allocationSize = 1)
    private Long id;

    private Integer newRowsCount;
    private Integer changedRowsCount;
    private String summary;
    private LocalDateTime createdAt;
}
