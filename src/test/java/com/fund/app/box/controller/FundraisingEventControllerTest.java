package com.fund.app.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fund.app.box.dto.CreateFundraisingEventRequest;
import com.fund.app.box.dto.FundraisingEventDto;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.service.FundraisingEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FundraisingEventController.class)
public class FundraisingEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FundraisingEventService fundraisingEventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateFundraisingEvent() throws Exception {
        CreateFundraisingEventRequest request = new CreateFundraisingEventRequest("Charity Staff", Currency.GBP);

        // mock event returned by service
        FundraisingEvent mockEvent = new FundraisingEvent("Charity Staff", Currency.GBP);
        mockEvent.setId(1L);
        mockEvent.setAccountBalance(BigDecimal.ZERO);

        // creating mockDto
        FundraisingEventDto mockEventDto = new FundraisingEventDto(
                mockEvent.getId(),
                mockEvent.getEventName(),
                mockEvent.getAccountCurrency(),
                mockEvent.getAccountBalance()
        );

        // define the behavior of the mocked service
        when(fundraisingEventService.createFundraisingEvent(any(CreateFundraisingEventRequest.class)))
                .thenReturn(mockEvent);

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.eventName").value("Charity Staff"))
                .andExpect(jsonPath("$.accountCurrency").value(Currency.GBP.name()))
                .andExpect(jsonPath("$.accountBalance").value(0.0));
    }

    @Test
    public void shouldHandleInvalidRequest() throws Exception {
        String invalidRequestJson = "{}";

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }



}
