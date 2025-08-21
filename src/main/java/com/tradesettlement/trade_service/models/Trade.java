package com.tradesettlement.trade_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    String tradeId;
    String symbol;
    Integer quantity;
    BigDecimal price;
    String side;
    String traderId;
    LocalDateTime tradeDate;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
