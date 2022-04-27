package com.itmo.ktelnoy.stockexchange.mongo;

import com.itmo.ktelnoy.stockexchange.model.CampaignEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CampaignRepository extends MongoRepository<CampaignEntity, String> {
}
