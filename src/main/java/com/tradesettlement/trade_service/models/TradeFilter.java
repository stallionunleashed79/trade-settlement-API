package com.tradesettlement.trade_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeFilter {
    private String symbol;
    private LocalDate tradeDateBegin;
    private LocalDate tradeDateEnd;
    private String status;
    private String side;
}
