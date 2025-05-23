package com.fund.app.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fund.app.box.dto.AddMoneyRequest;
import com.fund.app.box.dto.AssignBoxRequest;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.model.MoneyEntry;
import com.fund.app.box.repository.CollectionBoxRepository;
import com.fund.app.box.repository.FundraisingEventRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Whole application testing - integration testing
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class CollectionBoxControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;

    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;

//    @BeforeEach
//    public void setUp() {
//        fundraisingEventRepository.deleteAll();
//        collectionBoxRepository.deleteAll();
//    }


    @Test
    void shouldReturnAllCollectionsBoxes() throws Exception {
        CollectionBox collectionBox = new CollectionBox();
        collectionBoxRepository.save(collectionBox);

        mockMvc.perform(get("/sii/api/collection-boxes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uniqueIdentifier").isNotEmpty())
                .andExpect(jsonPath("$[0].assigned").value(false))
                .andExpect(jsonPath("$[0].empty").value(true));
    }

    @Test
    void shouldDeleteExistingCollectionBox() throws Exception {
        CollectionBox collectionBox = new CollectionBox();
        collectionBoxRepository.save(collectionBox);

        mockMvc.perform(delete("/sii/api/collection-boxes")
                .param("uniqueIdentifier", collectionBox.getUniqueIdentifier()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingBox() throws Exception {
        mockMvc.perform(delete("/sii/api/collection-boxes")
                        .param("uniqueIdentifier", "Not-existing-UI"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAssignCollectionToExistingBox() throws Exception {
        CollectionBox collectionBox = new CollectionBox();
        collectionBoxRepository.save(collectionBox);

        FundraisingEvent fundraisingEvent = new FundraisingEvent("Event", Currency.USD);
        fundraisingEventRepository.save(fundraisingEvent);

        AssignBoxRequest request = new AssignBoxRequest(collectionBox.getUniqueIdentifier(), fundraisingEvent.getEventName());

        mockMvc.perform(post("/sii/api/collection-boxes/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("assigned to event")));

    }

    @Test
    void shouldAddMoneyToExistingBox() throws Exception {
        CollectionBox collectionBox = new CollectionBox();
        collectionBoxRepository.save(collectionBox);

        AddMoneyRequest request = new AddMoneyRequest(collectionBox.getUniqueIdentifier(), new BigDecimal(10), Currency.USD);

        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

    }

    @Test
    void shouldReturn404WhenAddingMoneyToNonExistingBox() throws Exception {
        AddMoneyRequest request = new AddMoneyRequest("invalidID", new BigDecimal(10), Currency.USD);

        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldTransferMoneyToFundraisingEventAndGenerateReport() throws Exception {
        CollectionBox collectionBox = new CollectionBox();
        collectionBoxRepository.save(collectionBox);

        FundraisingEvent event = new FundraisingEvent("Event", Currency.USD);
        fundraisingEventRepository.save(event);

        AssignBoxRequest request = new AssignBoxRequest(collectionBox.getUniqueIdentifier(), event.getEventName());
        mockMvc.perform(post("/sii/api/collection-boxes/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        AddMoneyRequest request2 = new AddMoneyRequest(collectionBox.getUniqueIdentifier(), new BigDecimal(10), Currency.GBP);
        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request2)))
                .andExpect(status().isOk());

        AddMoneyRequest request3 = new AddMoneyRequest(collectionBox.getUniqueIdentifier(), new BigDecimal(20), Currency.EUR);
        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request3)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/sii/api/collection-boxes/transfer")
                .param("uniqueIdentifier", collectionBox.getUniqueIdentifier()))
                .andExpect(status().isOk())
                //we don't need the value because we have test to check if converter is working well too
                .andExpect(content().string(containsString(("Successfully transferred: "))));

        mockMvc.perform(get("/sii/api/events/financial-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundraisingEventName").value("Event"))
                .andExpect(jsonPath("$[0].currency").value("USD"));


    }

    @Test
    void shouldReturn400WhenAssigningFullCollectionBox() throws Exception {
        CollectionBox collectionBox = new CollectionBox();
        collectionBoxRepository.save(collectionBox);
        MoneyEntry entry = new MoneyEntry();
        entry.setCurrency(Currency.EUR);
        entry.setCreateTime(LocalDateTime.now());
        entry.setAmount(new BigDecimal("10"));
        entry.setCollectionBox(collectionBox);

        collectionBox.getMoneyEntries().add(entry);
        collectionBoxRepository.save(collectionBox);

        FundraisingEvent event = new FundraisingEvent("Event", Currency.USD);
        fundraisingEventRepository.save(event);

        AssignBoxRequest request = new AssignBoxRequest(collectionBox.getUniqueIdentifier(), event.getEventName());

        mockMvc.perform(post("/sii/api/collection-boxes/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cannot assign a non-empty collection box to an event")));
    }

    @Test
    void shouldUpdateEventAccountBalanceAfterTransfer() throws Exception {
        FundraisingEvent event = new FundraisingEvent("Event", Currency.USD);
        fundraisingEventRepository.save(event);

        CollectionBox collectionBox = new CollectionBox();
        collectionBoxRepository.save(collectionBox);

        AssignBoxRequest assignRequest = new AssignBoxRequest(collectionBox.getUniqueIdentifier(), event.getEventName());
        mockMvc.perform(post("/sii/api/collection-boxes/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(assignRequest)))
                .andExpect(status().isOk());

        AddMoneyRequest addMoneyRequest = new AddMoneyRequest(collectionBox.getUniqueIdentifier(), new BigDecimal(50), Currency.USD);
        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addMoneyRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/sii/api/collection-boxes/transfer")
                        .param("uniqueIdentifier", collectionBox.getUniqueIdentifier()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/sii/api/events/financial-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundraisingEventName").value("Event"))
                .andExpect(jsonPath("$[0].amount").value(50))
                .andExpect(jsonPath("$[0].currency").value("USD"));
    }

    @Test
    void shouldReturnEmptyReportWhenNoEventsOrBoxes() throws Exception {
        mockMvc.perform(get("/sii/api/events/financial-report"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldRejectInvalidCurrency() throws Exception {
        AddMoneyRequest request = new AddMoneyRequest("someId", new BigDecimal(10), null);

        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNegativeAmount() throws Exception {
        AddMoneyRequest request = new AddMoneyRequest("someId", new BigDecimal(-10), Currency.PLN);

        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }






}
