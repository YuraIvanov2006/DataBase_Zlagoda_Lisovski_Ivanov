package org.lisovskyi_ivanov.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Check {
    private String checkNumber;
    private Employee employee;
    private CustomerCard customerCard;
    private LocalDateTime printDate;
    private BigDecimal sumTotal;
    private BigDecimal vat;
}