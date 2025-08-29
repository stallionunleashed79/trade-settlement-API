package com.tradesettlement.trade_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TradeStatusUpdate {
    private String tradeId;
    private String status;
    private LocalDateTime timestamp;
    private String errorMessage;
    private String batchId;
    private String correlationId;
}
