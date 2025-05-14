package com.fund.app.box.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fund.app.box.dto.CreateFundraisingEventRequest;
import com.fund.app.box.model.Currency;
import com.fund.app.box.repository.CollectionBoxRepository;
import com.fund.app.box.repository.FundraisingEventRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class FundraisingEventControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;

    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;

    @Test
    public void shouldCreateFundraisingEvent() throws Exception {
        CreateFundraisingEventRequest request = new CreateFundraisingEventRequest("Charity Staff", Currency.GBP);

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Charity Staff"))
                .andExpect(jsonPath("$.accountCurrency").value(Currency.GBP.name()))
                .andExpect(jsonPath("$.accountBalance").value(0.0));
    }

    @Test
    public void shouldGenerateFinancialReport() throws Exception {
        CreateFundraisingEventRequest eventRequest1 = new CreateFundraisingEventRequest("Charity Staff", Currency.GBP);
        CreateFundraisingEventRequest eventRequest2 = new CreateFundraisingEventRequest("Hope Foundation", Currency.USD);

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(eventRequest1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(eventRequest2)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/sii/api/events/financial-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundraisingEventName").value("Charity Staff"))
                .andExpect(jsonPath("$[0].currency").value("GBP"))
                .andExpect(jsonPath("$[1].fundraisingEventName").value("Hope Foundation"))
                .andExpect(jsonPath("$[1].currency").value("USD"));
    }

    @Test
    public void shouldReturn400WhenRequestIsInvalid() throws Exception {
        String invalidRequestJson = "{}";

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnEmptyFinancialReportWhenNoEventsExist() throws Exception {
        mockMvc.perform(get("/sii/api/events/financial-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldRejectEmptyEventName() throws Exception {
        String invalidRequestJson = "{\"eventName\":\"\",\"accountCurrency\":\"USD\"}";

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRejectNullCurrency() throws Exception {
        String invalidRequestJson = "{\"eventName\":\"Test Event\",\"accountCurrency\":null}";

        mockMvc.perform(post("/sii/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }



}
