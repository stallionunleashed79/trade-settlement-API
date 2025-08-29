package com.tradesettlement.trade_service.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Trade extends TradeStatusUpdate {
    private String symbol;
    private Integer quantity;
    private BigDecimal price;
    private String side;
    private String traderId;
    private LocalDateTime tradeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
