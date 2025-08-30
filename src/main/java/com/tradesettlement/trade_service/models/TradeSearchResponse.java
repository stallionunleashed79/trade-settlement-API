package com.tradesettlement.trade_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeSearchResponse {
    private List<Trade> data;
    private long totalRows;
    private long lastRow;
}
