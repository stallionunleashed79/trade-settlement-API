package com.tradesettlement.trade_service.repository;

import com.tradesettlement.trade_service.entities.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {
}
