package com.itmo.ktelnoy.tradesystem;

import com.itmo.ktelnoy.stockexchange.model.CampaignEntity;
import com.itmo.ktelnoy.stockexchange.model.StockEntity;
import com.itmo.ktelnoy.tradesystem.controller.StockExchangeController;
import com.itmo.ktelnoy.tradesystem.controller.UserController;
import com.itmo.ktelnoy.tradesystem.dto.StocksOrderDto;
import com.itmo.ktelnoy.tradesystem.dto.OwnedStockDto;
import com.itmo.ktelnoy.tradesystem.model.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

@Testcontainers
@SpringBootTest(args = {"--spring.data.mongodb.port=28017"})
public class BasicIntegrationTest {

    private final static String CREATE_STOCK_URL = "/stock/";
    private final static String CREATE_CAMPAIGN = "/campaign/";

    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserController userController;
    @Autowired
    private StockExchangeController stockExchangeController;

    @Test
    public void test() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        try (Network network = Network.SHARED;
             GenericContainer<?> mongoDBContainer = new FixedHostPortGenericContainer("mongo:4.0.10")
                     .withFixedExposedPort(28017, 27017)
                     .withExposedPorts(27017)
                     .withNetwork(network)
                     .withNetworkAliases("test");
        ) {
            mongoDBContainer.start();

            Integer mongoPort = mongoDBContainer.getFirstMappedPort();

            try (GenericContainer<?> stockExchangeContainer = new GenericContainer<>("stock-exchange:1.0-SNAPSHOT")
                    .withNetwork(network)
                    .withCommand("java", "-jar", "maven/stock-exchange-1.0-SNAPSHOT.jar", "--spring.data.mongodb.host=test")
                    .withExposedPorts(1234)
            ) {
                stockExchangeContainer.start();

                Assertions.assertTrue(stockExchangeContainer.isRunning());
                Assertions.assertTrue(mongoDBContainer.isRunning());

                String stockExchangeAddress = getContainerAddress(stockExchangeContainer);
                RestTemplate realStockExchangeRestTemplate = new RestTemplateBuilder().rootUri(stockExchangeAddress).build();
                stockExchangeController.getStockExchangeIntegrationService().setRestTemplate(realStockExchangeRestTemplate);

                CampaignEntity campaignEntity = new CampaignEntity("2", "test");
                StockEntity stockEntity = new StockEntity("2", 5, 10.0, "2");
                UserEntity userEntity = new UserEntity("2", "user", "stringstringstringstringstringstringstringstringstringstringstri", 100.0);

                CampaignEntity resultCampaign = restTemplate.postForEntity(stockExchangeAddress + CREATE_CAMPAIGN, campaignEntity, CampaignEntity.class).getBody();
                StockEntity resultStock = restTemplate.postForEntity(stockExchangeAddress + CREATE_STOCK_URL, stockEntity, StockEntity.class).getBody();
                UserEntity resultUser = userController.createUser(userEntity).getBody();

                Assertions.assertEquals(campaignEntity, resultCampaign);
                Assertions.assertEquals(stockEntity, resultStock);
                Assertions.assertEquals(userEntity, resultUser);

                Double money = mapper.readValue(stockExchangeController.getTotalMoneyForUser(userEntity.getId()).getBody().toString(), Double.class);

                Assertions.assertEquals(100.0, money);

                Assertions.assertEquals("Successfully purchased stocks", stockExchangeController.purchaseStocks(userEntity.getId(), new StocksOrderDto(stockEntity.getId(), 2)).getBody());
                Assertions.assertEquals(ImmutableList.of(new OwnedStockDto(stockEntity.getId(), 2, stockEntity.getPriceInDollars())), stockExchangeController.getStocksOwnedByUser(userEntity.getId()).getBody());

                money = mapper.readValue(stockExchangeController.getTotalMoneyForUser(userEntity.getId()).getBody().toString(), Double.class);
                Assertions.assertEquals(100.0, money);

                Assertions.assertEquals("Successfully sold stocks", stockExchangeController.sellStocks(userEntity.getId(), new StocksOrderDto(stockEntity.getId(), 2)).getBody());
                Assertions.assertEquals(ImmutableList.of(new OwnedStockDto(stockEntity.getId(), 0, stockEntity.getPriceInDollars())), stockExchangeController.getStocksOwnedByUser(userEntity.getId()).getBody());

                money = mapper.readValue(stockExchangeController.getTotalMoneyForUser(userEntity.getId()).getBody().toString(), Double.class);
                Assertions.assertEquals(100.0, money);
            }
        }
    }

    private String getContainerAddress(GenericContainer<?> container) {
        return "http://" + container.getHost() + ":" + container.getFirstMappedPort();
    }
}
