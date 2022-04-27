package com.itmo.ktelnoy.stockexchange.service;

import com.itmo.ktelnoy.stockexchange.model.StockEntity;
import com.itmo.ktelnoy.stockexchange.mongo.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class StockPriceRandomizer {

    private final StockRepository stockRepository;
    private final ThreadLocalRandom random;

    @Autowired
    public StockPriceRandomizer(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.random = ThreadLocalRandom.current();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::randomizeStockPrices, 5, 1000, TimeUnit.SECONDS);
    }

    @Transactional
    private void randomizeStockPrices() {
        List<StockEntity> stocks = stockRepository.findAll().stream().peek(stockDTO -> {
            Double newPrice = stockDTO.getPriceInDollars() * (0.5 + random.nextDouble());
            stockDTO.setPriceInDollars(newPrice);
        }).collect(Collectors.toList());
        stockRepository.saveAll(stocks);
    }
}
