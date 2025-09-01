package com.tradesettlement.trade_service.repository;

import com.tradesettlement.trade_service.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    
    @Query("SELECT oe FROM OutboxEvent oe WHERE oe.status IN :statuses ORDER BY oe.createdAt ASC")
    List<OutboxEvent> findPendingEvents(@Param("statuses") List<OutboxEvent.EventStatus> statuses);
}