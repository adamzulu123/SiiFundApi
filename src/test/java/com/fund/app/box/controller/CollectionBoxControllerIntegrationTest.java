package com.fund.app.box.controller;

import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.repository.CollectionBoxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CollectionBoxControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;

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



}
