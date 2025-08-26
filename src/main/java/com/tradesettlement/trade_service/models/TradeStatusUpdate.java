package com.tradesettlement.trade_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeStatusUpdate {
    private String tradeId;
    private TradeStatus tradeStatus;
    private LocalDateTime timestamp;
    private String errorMessage;
    private String batchId;
    private String correlationId;
}
