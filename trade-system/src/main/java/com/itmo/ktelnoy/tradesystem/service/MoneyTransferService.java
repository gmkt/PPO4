package com.itmo.ktelnoy.tradesystem.service;

import com.itmo.ktelnoy.stockexchange.dto.MoneyReceiptDto;
import com.itmo.ktelnoy.tradesystem.exception.NotEnoughMoneyOnBalanceException;
import com.itmo.ktelnoy.tradesystem.model.UserEntity;
import com.itmo.ktelnoy.tradesystem.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoneyTransferService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public MoneyReceiptDto sendUserMoneyToStockExchange(UserEntity user, Double amount) throws NotEnoughMoneyOnBalanceException {
        if (user.getMoneyInDollarsHeld() < amount) {
            throw new NotEnoughMoneyOnBalanceException(user.getId());
        }
        user.setMoneyInDollarsHeld(user.getMoneyInDollarsHeld() - amount);
        userRepository.save(user);
        return new MoneyReceiptDto(amount, "test");
    }

    @Transactional
    public void receiveUserMoneyFromStockExchange(UserEntity user, MoneyReceiptDto receipt) {
        user.setMoneyInDollarsHeld(user.getMoneyInDollarsHeld() + receipt.getAmount());
        userRepository.save(user);
    }

}
