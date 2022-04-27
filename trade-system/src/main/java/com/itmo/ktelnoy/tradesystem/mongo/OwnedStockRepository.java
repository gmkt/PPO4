package com.itmo.ktelnoy.tradesystem.mongo;

import com.itmo.ktelnoy.tradesystem.model.OwnedStockEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OwnedStockRepository extends MongoRepository<OwnedStockEntity, OwnedStockEntity.CompositeOwnedStockId> {
    List<OwnedStockEntity> findByIdUserId(String userId);
}
