package com.itmo.ktelnoy.tradesystem.service;

import com.itmo.ktelnoy.stockexchange.dto.*;
import com.itmo.ktelnoy.stockexchange.model.StockEntity;
import com.itmo.ktelnoy.stockexchange.util.ProofUpdater;
import com.itmo.ktelnoy.tradesystem.exception.IntegrationStockExchangeException;
import com.itmo.ktelnoy.tradesystem.exception.NotEnoughStockException;
import com.itmo.ktelnoy.tradesystem.exception.StockNotFoundException;
import com.itmo.ktelnoy.tradesystem.model.OwnedStockEntity;
import com.itmo.ktelnoy.tradesystem.model.UserEntity;
import com.itmo.ktelnoy.tradesystem.mongo.OwnedStockRepository;
import com.itmo.ktelnoy.tradesystem.mongo.UserRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class StockExchangeIntegrationService {

    @Autowired
    private OwnedStockRepository ownedStockRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MoneyTransferService moneyTransferService;

    @Autowired
    @Setter
    @Qualifier("stockExchangeRestTemplate")
    RestTemplate restTemplate;

    @Value("${integration.stock-exchange.rest.get-stock}")
    private String getStockUrl;
    @Value("${integration.stock-exchange.rest.purchase-stock}")
    private String purchaseStockUrl;
    @Value("${integration.stock-exchange.rest.sell-stock}")
    private String sellStockUrl;

    public Double getStockPrice(String stockId) throws StockNotFoundException {
        StockEntity stock = restTemplate.getForObject(getStockUrl, StockEntity.class, stockId);
        if (stock != null) {
            return stock.getPriceInDollars();
        } else {
            throw new StockNotFoundException(stockId);
        }
    }

    @Transactional
    public boolean purchaseStocks(String stockId, Integer numberToPurchase, UserEntity user) {
        Double stockTotalPrice = getStockPrice(stockId) * numberToPurchase;
        StockPurchaseRequestDTO request = new StockPurchaseRequestDTO(numberToPurchase, moneyTransferService.sendUserMoneyToStockExchange(user, stockTotalPrice));
        StockResponseDto stockResponse = restTemplate.postForObject(purchaseStockUrl, request, StockResponseDto.class, stockId);
        if (stockResponse != null) {
            if (stockResponse.getReceipt() != null) {
                OwnedStockEntity ownedStock = getOwnedStock(user.getId(), stockId);
                ownedStockRepository.delete(ownedStock);
                ownedStock.setNumberHeld(ownedStock.getNumberHeld() + numberToPurchase);
                ownedStock.setProof(ProofUpdater.addProofUpdate(ownedStock.getProof(), stockResponse.getReceipt().getProof()));
                ownedStockRepository.save(ownedStock);
                return true;
            } else if (stockResponse.getMessage() != null) {
                throw new IntegrationStockExchangeException(stockResponse.getMessage());
            }
        }
        return false;
    }

    @Transactional
    public boolean sellStocks(String stockId, Integer numberToSell, UserEntity user) {
        OwnedStockEntity ownedStock = getOwnedStock(user.getId(), stockId);
        if (ownedStock.getNumberHeld() < numberToSell) {
            throw new NotEnoughStockException(stockId);
        } else {
            ownedStockRepository.delete(ownedStock);
            ownedStock.setNumberHeld(ownedStock.getNumberHeld() - numberToSell);
            Map.Entry<String, String> proofs = ProofUpdater.subtractProofUpdate(ownedStock.getProof(), numberToSell);
            ownedStock.setProof(proofs.getValue());
            ownedStockRepository.save(ownedStock);

            StockSellRequestDTO request = new StockSellRequestDTO(numberToSell, new StockReceiptDto(numberToSell, proofs.getKey()));
            MoneyResponseDto moneyResponse = restTemplate.postForObject(sellStockUrl, request, MoneyResponseDto.class, stockId);
            if (moneyResponse != null) {
                if (moneyResponse.getReceipt() != null) {
                    userService.addMoney(user, moneyResponse.getReceipt());
                    return true;
                } else if (moneyResponse.getMessage() != null) {
                    throw new IntegrationStockExchangeException(moneyResponse.getMessage());
                }
            }
            return false;
        }
    }

    private OwnedStockEntity getOwnedStock(String userId, String stockId) {
        OwnedStockEntity ownedStock = ownedStockRepository.findById(new OwnedStockEntity.CompositeOwnedStockId(stockId, userId)).orElse(null);
        if (ownedStock != null) {
            return ownedStock;
        } else {
            ownedStock = new OwnedStockEntity();
            ownedStock.setId(new OwnedStockEntity.CompositeOwnedStockId(stockId, userId));
            ownedStock.setNumberHeld(0);
            ownedStock.setProof(ProofUpdater.EMPTY_PROOF);
            return ownedStock;
        }
    }

}
