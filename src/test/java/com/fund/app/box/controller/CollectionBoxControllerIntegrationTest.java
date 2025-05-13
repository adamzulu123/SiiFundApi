package com.fund.app.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fund.app.box.dto.AddMoneyRequest;
import com.fund.app.box.dto.AssignBoxRequest;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.repository.CollectionBoxRepository;
import com.fund.app.box.repository.FundraisingEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CollectionBoxControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;

    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;

    @BeforeEach
    public void setUp() {
        collectionBoxRepository.deleteAll();
    }

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



}
