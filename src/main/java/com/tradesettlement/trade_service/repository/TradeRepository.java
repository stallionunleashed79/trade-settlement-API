package com.tradesettlement.trade_service.repository;

import com.tradesettlement.trade_service.entities.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TradeRepository extends JpaRepository<TradeEntity, Long>, JpaSpecificationExecutor<TradeEntity> {

    @Modifying
    @Transactional // Required for modifying queries
    @Query("UPDATE TradeEntity t SET t.status = :status WHERE t.tradeId = :tradeId")
    int updateTradeStatusByTradeId(@Param("status") String status, @Param("tradeId") String tradeId);
}
