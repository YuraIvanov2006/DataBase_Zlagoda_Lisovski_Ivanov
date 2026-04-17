package org.lisovskyi_ivanov.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CheckDto (
     String checkNumber,
     String employeeName,
     Long employeeId,
     String customerCardNumber,
     Integer customerDiscountPercent,
     LocalDateTime printDate,
     BigDecimal sumTotal,
     BigDecimal vat
) {}