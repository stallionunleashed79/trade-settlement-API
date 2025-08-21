package com.tradesettlement.trade_service.mapper;

import com.tradesettlement.trade_service.entities.TradeEntity;
import com.tradesettlement.trade_service.models.Trade;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeMapper {
    Trade toDto(TradeEntity tradeEntity);
    TradeEntity toEntity(Trade trade);
}
