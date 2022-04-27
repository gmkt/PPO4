package com.itmo.ktelnoy.tradesystem.service;

import com.itmo.ktelnoy.stockexchange.dto.MoneyReceiptDto;
import com.itmo.ktelnoy.tradesystem.model.UserEntity;
import com.itmo.ktelnoy.tradesystem.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public void addMoney(UserEntity user, MoneyReceiptDto receiptDto) {
        user.setMoneyInDollarsHeld(user.getMoneyInDollarsHeld() + receiptDto.getAmount());
        repository.save(user);
    }
}
