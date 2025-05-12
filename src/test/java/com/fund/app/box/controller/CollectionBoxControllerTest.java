package com.fund.app.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fund.app.box.dto.CreateCollectionBoxRequest;
import com.fund.app.box.dto.CreateFundraisingEventRequest;
import com.fund.app.box.exception.NonExistingEventNameException;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.service.CollectionBoxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CollectionBoxController.class)
public class CollectionBoxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CollectionBoxService collectionBoxService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCollectionBoxWithoutEventName() throws Exception {
        CollectionBox mockBox = new CollectionBox();
        mockBox.setUniqueIdentifier("test-id");

        when(collectionBoxService.createCollectionBox()).thenReturn(mockBox);

        mockMvc.perform(post("/sii/api/collection-boxes/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueIdentifier").value("test-id"))
                .andExpect(jsonPath("$.empty").value(true))
                .andExpect(jsonPath("$.assigned").value(false));

    }

    @Test
    void shouldCreateCollectionBoxWithEventName() throws Exception {
        String eventName = "test-event-name";
        CollectionBox mockBox = new CollectionBox();
        mockBox.setUniqueIdentifier("test-id");

        FundraisingEvent mockEvent = new FundraisingEvent(eventName, Currency.EUR);
        mockBox.setFundraisingEvent(mockEvent);

        CreateCollectionBoxRequest request = new CreateCollectionBoxRequest(eventName);

        when(collectionBoxService.createCollectionBox(eventName)).thenReturn(mockBox);

        mockMvc.perform(post("/sii/api/collection-boxes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueIdentifier").value("test-id"))
                .andExpect(jsonPath("$.empty").value(true))
                .andExpect(jsonPath("$.assigned").value(true))
                .andExpect(jsonPath("$.fundraisingEventName").value(eventName));
    }

    @Test
    void shouldNotCreateCollectionBoxWithInvalidEventName() throws Exception {
        String eventName = "Non-existing event name";
        CreateCollectionBoxRequest request = new CreateCollectionBoxRequest();
        request.setEventName(eventName);

        when(collectionBoxService.createCollectionBox(eventName))
        .thenThrow(new NonExistingEventNameException("Invalid fundraising event name: " + eventName));

        mockMvc.perform(post("/sii/api/collection-boxes/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }



}
