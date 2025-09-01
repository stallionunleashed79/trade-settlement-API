package com.tradesettlement.trade_service.repository;

import com.tradesettlement.trade_service.entities.IdempotencyKeyEntity;
import org.springframework.data.repository.CrudRepository;

public interface IdempotencyKeyRepository extends CrudRepository<IdempotencyKeyEntity, Long> {
    IdempotencyKeyEntity findByIdempotencyKey(String idempotencyKey);
}
