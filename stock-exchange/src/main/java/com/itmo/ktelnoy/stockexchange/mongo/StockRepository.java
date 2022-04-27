package com.itmo.ktelnoy.stockexchange.mongo;

import com.itmo.ktelnoy.stockexchange.model.StockEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockRepository extends MongoRepository<StockEntity, String> {
    List<StockEntity> findByCampaignId(String campaignId);
}
