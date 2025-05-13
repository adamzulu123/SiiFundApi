package com.fund.app.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fund.app.box.dto.AddMoneyRequest;
import com.fund.app.box.dto.AssignBoxRequest;
import com.fund.app.box.dto.CreateCollectionBoxRequest;
import com.fund.app.box.exception.NonExistingCollectionBoxException;
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

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
                .andExpect(jsonPath("$.assigned").value(true));
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

    @Test
    void shouldRemoveCollectionBoxSuccessfully() throws Exception {
        String uniqueIdentifier = "id-id";

        doNothing().when(collectionBoxService).removeCollectionBox(uniqueIdentifier);

        mockMvc.perform(delete("/sii/api/collection-boxes")
                        .param("uniqueIdentifier", uniqueIdentifier))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenRemovingNonExistingBox() throws Exception {
        String uniqueIdentifier = "id-id";

        doThrow(new NonExistingCollectionBoxException("Invalid collection box identifier: " + uniqueIdentifier))
                .when(collectionBoxService).removeCollectionBox(uniqueIdentifier);

        mockMvc.perform(delete("/sii/api/collection-boxes")
                        .param("uniqueIdentifier", uniqueIdentifier))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldAssignCollectionBoxSuccessfully() throws Exception {
        String uniqueIdentifier = "id-id";
        String eventName = "test-event-name";

        AssignBoxRequest request = new AssignBoxRequest(uniqueIdentifier, eventName);

        mockMvc.perform(post("/sii/api/collection-boxes/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        AssignBoxRequest request = new AssignBoxRequest("", "");

        mockMvc.perform(post("/sii/api/collection-boxes/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddMoneyAndReturnOk() throws Exception {
        String uniqueIdentifier = "id-id";

        AddMoneyRequest request = new AddMoneyRequest(uniqueIdentifier, new BigDecimal(10), Currency.EUR);

        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddMoneyAndReturnBadRequest_NonExistingBox() throws Exception {
        String invalidIdentifier = "id-id";

        AddMoneyRequest request = new AddMoneyRequest(invalidIdentifier, new BigDecimal(10), Currency.EUR);

        doThrow(new NonExistingCollectionBoxException("Invalid collection box identifier: " + invalidIdentifier))
                .when(collectionBoxService).addMoneyToBox(request.getUniqueIdentifier(), request.getAmount(), request.getCurrency());

        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMoneyToBox_NegativeAmount_ReturnsBadRequest() throws Exception {
        // Given
        AddMoneyRequest request =
                new AddMoneyRequest("test-box-id", new BigDecimal("-10.00"), Currency.EUR);

        // When & Then
        mockMvc.perform(post("/sii/api/collection-boxes/fund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(collectionBoxService, never()).addMoneyToBox(any(), any(), any());
    }

    //todo:: the rest of controller tests like no Id or no amount, but it's working because I tested it manually









}
