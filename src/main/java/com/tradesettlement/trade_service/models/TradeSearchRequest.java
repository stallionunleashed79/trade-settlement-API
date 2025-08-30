package com.tradesettlement.trade_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeSearchRequest {
    private Integer startRow;
    private Integer endRow;
    private TradeFilter filterModel;
}
