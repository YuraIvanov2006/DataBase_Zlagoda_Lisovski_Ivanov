package org.lisovskyi_ivanov.backend.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Check {
    @Setter
    private String checkNumber;
    private Employee employee;
    private CustomerCard customerCard;
    private LocalDateTime printDate;
    private BigDecimal sumTotal;
    private BigDecimal vat;
}