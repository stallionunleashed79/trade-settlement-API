package com.tradesettlement.trade_service.service;

import com.tradesettlement.trade_service.models.Trade;
import com.tradesettlement.trade_service.models.TradeRequest;
import com.tradesettlement.trade_service.models.TradeStatus;
import com.tradesettlement.trade_service.models.TradeStatusUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TradeStatusUpdateKafkaConsumer {
    @Autowired
    private StatusBroadcastService statusBroadcastService;

    @KafkaListener(topics = "${settlement.events.topic}", groupId = "${settlement.events.group.id}")
    public void handleStatusUpdate(TradeRequest tradeRequest) {
        final Trade trade = tradeRequest.getPayload();
        TradeStatusUpdate update = TradeStatusUpdate.builder()
                .tradeId(trade.getTradeId())
                .tradeStatus(TradeStatus.valueOf(trade.getStatus()))
                .timestamp(LocalDateTime.now())
                .correlationId(tradeRequest.getCorrelationId())
                .build();
        // Broadcast to all connected web clients
        statusBroadcastService.broadcastStatusUpdate(update);
    }
}
