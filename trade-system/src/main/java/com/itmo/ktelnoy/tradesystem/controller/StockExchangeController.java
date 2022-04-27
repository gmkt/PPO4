package com.itmo.ktelnoy.tradesystem.controller;

import com.itmo.ktelnoy.tradesystem.dto.StocksOrderDto;
import com.itmo.ktelnoy.tradesystem.exception.TradeSystemBaseException;
import com.itmo.ktelnoy.tradesystem.dto.OwnedStockDto;
import com.itmo.ktelnoy.tradesystem.model.OwnedStockEntity;
import com.itmo.ktelnoy.tradesystem.model.UserEntity;
import com.itmo.ktelnoy.tradesystem.mongo.OwnedStockRepository;
import com.itmo.ktelnoy.tradesystem.mongo.UserRepository;
import com.itmo.ktelnoy.tradesystem.service.StockExchangeIntegrationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/stocks", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
public class StockExchangeController {

    @Getter
    @Autowired
    private StockExchangeIntegrationService stockExchangeIntegrationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OwnedStockRepository ownedStockRepository;

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getStocksOwnedByUser(@PathVariable String userId) {
        List<OwnedStockEntity> ownedStocks = ownedStockRepository.findByIdUserId(userId);
        if (ownedStocks != null && !ownedStocks.isEmpty()) {
            try {
                return ResponseEntity.ok(ownedStocks.stream().map(ownedStock -> {
                    OwnedStockDto ownedStockDto = new OwnedStockDto();
                    Double stockPrice = stockExchangeIntegrationService.getStockPrice(ownedStock.getId().getStockId());
                    ownedStockDto.setStockId(ownedStock.getId().getStockId());
                    ownedStockDto.setStockPrice(stockPrice);
                    ownedStockDto.setNumberHeld(ownedStock.getNumberHeld());
                    return ownedStockDto;
                }).collect(Collectors.toList()));
            } catch (TradeSystemBaseException e) {
                return processTradeSystemBaseException(e);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        }
        return new ResponseEntity<>(String.format("Owned stocks not found for user %s", userId), HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{userId}/total", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTotalMoneyForUser(@PathVariable String userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(String.format("User %s not found", userId), HttpStatus.NOT_FOUND);
        }
        List<OwnedStockEntity> ownedStocks = ownedStockRepository.findByIdUserId(userId);
        if (ownedStocks != null && !ownedStocks.isEmpty()) {
            try {
                return ResponseEntity.ok(ownedStocks.stream().map(ownedStock -> {
                    Double stockPrice = stockExchangeIntegrationService.getStockPrice(ownedStock.getId().getStockId());
                    return stockPrice * ownedStock.getNumberHeld();
                }).reduce(Double::sum).orElse(0.0) + user.getMoneyInDollarsHeld());
            } catch (TradeSystemBaseException e) {
                return processTradeSystemBaseException(e);
            } catch (HttpClientErrorException e) {
                return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
            }
        }
        return ResponseEntity.ok(user.getMoneyInDollarsHeld());
    }

    @PostMapping(value = "/{userId}/purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> purchaseStocks(@PathVariable String userId, @RequestBody @Valid StocksOrderDto stocksOrderDto) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(String.format("User %s not found", userId), HttpStatus.NOT_FOUND);
        }
        boolean success;
        try {
            success = stockExchangeIntegrationService.purchaseStocks(stocksOrderDto.getStockId(), stocksOrderDto.getNumberToPurchase(), user);
        } catch (TradeSystemBaseException e) {
            return processTradeSystemBaseException(e);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
        if (success) {
            return ResponseEntity.ok("Successfully purchased stocks");
        } else {
            return new ResponseEntity<>("Stock exchange integration invalid response", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{userId}/sell", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> sellStocks(@PathVariable String userId, @RequestBody @Valid StocksOrderDto stocksOrderDto) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(String.format("User %s not found", userId), HttpStatus.NOT_FOUND);
        }
        boolean success;
        try {
            success = stockExchangeIntegrationService.sellStocks(stocksOrderDto.getStockId(), stocksOrderDto.getNumberToPurchase(), user);
        } catch (TradeSystemBaseException e) {
            return processTradeSystemBaseException(e);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
        if (success) {
            return ResponseEntity.ok("Successfully sold stocks");
        } else {
            return new ResponseEntity<>("Stock exchange integration invalid response", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Object> processTradeSystemBaseException(TradeSystemBaseException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

}
