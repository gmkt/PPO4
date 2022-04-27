package com.itmo.ktelnoy.tradesystem.mongo;

import com.itmo.ktelnoy.tradesystem.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {
}
