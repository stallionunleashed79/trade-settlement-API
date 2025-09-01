package com.tradesettlement.trade_service.models;

public enum TradeStatus {
    UPLOADED,
    VALIDATING,
    VALIDATED,
    SETTLING,
    SETTLED,
    FAILED,
    VALIDATION_FAILED
}
