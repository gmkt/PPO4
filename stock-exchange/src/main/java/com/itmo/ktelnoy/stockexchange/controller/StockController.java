package com.itmo.ktelnoy.stockexchange.controller;

import com.itmo.ktelnoy.stockexchange.dto.*;
import com.itmo.ktelnoy.stockexchange.model.CampaignEntity;
import com.itmo.ktelnoy.stockexchange.model.StockEntity;
import com.itmo.ktelnoy.stockexchange.mongo.CampaignRepository;
import com.itmo.ktelnoy.stockexchange.mongo.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/stock", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
public class StockController {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private CampaignRepository campaignRepository;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Object> createStock(@Valid @RequestBody StockEntity stockDTO) {
        CampaignEntity campaign = campaignRepository.findById(stockDTO.getCampaignId()).orElse(null);
        if (campaign == null) {
            return new ResponseEntity<>(String.format("Campaign with id %s not found", stockDTO.getCampaignId()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(stockRepository.save(stockDTO));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockEntity> getStock(@PathVariable("id") String id) {
        return ResponseEntity.of(stockRepository.findById(id));
    }

    @PostMapping(value = "/{id}/purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<StockResponseDto> purchaseStock(@PathVariable("id") String id, @RequestBody @Valid StockPurchaseRequestDTO requestDTO) {
        StockEntity stockDTO = stockRepository.findById(id).orElse(null);
        if (stockDTO != null) {
            if (requestDTO.getStockNumber() > stockDTO.getNumberAvailable()) {
                return new ResponseEntity<>(new StockResponseDto(String.format("Stock %s not available", id)), HttpStatus.BAD_REQUEST);
            } else {
                if (requestDTO.getReceipt().getAmount() < stockDTO.getPriceInDollars() * requestDTO.getStockNumber()) {
                    return new ResponseEntity<>(new StockResponseDto(String.format("Insufficient balance to purchase %d of stock %s", requestDTO.getStockNumber(), id)), HttpStatus.BAD_REQUEST);
                } else {
                    stockDTO.setNumberAvailable(stockDTO.getNumberAvailable() - requestDTO.getStockNumber());
                    stockRepository.save(stockDTO);
                    return ResponseEntity.ok(new StockResponseDto(new StockReceiptDto(requestDTO.getStockNumber(), "test")));
                }
            }
        } else {
            return new ResponseEntity<>(new StockResponseDto("No stock found"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{id}/sell", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<MoneyResponseDto> sellStock(@PathVariable("id") String id, @RequestBody @Valid StockSellRequestDTO requestDTO) {
        StockEntity stockDTO = stockRepository.findById(id).orElse(null);
        if (stockDTO != null) {
            double totalPrice = stockDTO.getPriceInDollars() * requestDTO.getStockNumber();
            if (requestDTO.getReceipt().getNumberOwned() < requestDTO.getStockNumber()) {
                return new ResponseEntity<>(new MoneyResponseDto("Insufficient stocks provided to sell"), HttpStatus.BAD_REQUEST);
            } else {
                stockDTO.setNumberAvailable(stockDTO.getNumberAvailable() + requestDTO.getStockNumber());
                stockRepository.save(stockDTO);
                return ResponseEntity.ok(new MoneyResponseDto(new MoneyReceiptDto(totalPrice, "test")));
            }
        } else {
            return new ResponseEntity<>(new MoneyResponseDto("No stock found"), HttpStatus.NOT_FOUND);
        }
    }

}
