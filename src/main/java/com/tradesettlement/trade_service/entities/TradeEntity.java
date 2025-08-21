package com.tradesettlement.trade_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "trade_id")
    private String tradeId;

    @Basic
    @Column(name = "symbol")
    private String symbol;

    @Basic
    @Column(name = "quantity")
    private int quantity;

    @Basic
    @Column(name = "price")
    private BigDecimal price;

    @Basic
    @Column(name = "side")
    private String side;

    @Basic
    @Column(name = "trader_id")
    private String traderId;

    @Basic
    @Column(name = "trade_date")
    private LocalDateTime tradeDate;

    @Basic
    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;

    @Basic
    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    @Basic
    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Or Date, Timestamp

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Or Date, Timestamp

}