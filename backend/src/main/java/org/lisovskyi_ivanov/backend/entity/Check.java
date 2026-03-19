package org.lisovskyi_ivanov.backend.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Check {
    private String checkNumber;
    private Employee employee;
    private CustomerCard customerCard;
    private LocalDateTime printDate;
    private BigDecimal sumTotal;
    private BigDecimal vat;
}