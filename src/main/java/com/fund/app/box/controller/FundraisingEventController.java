package com.fund.app.box.controller;

import com.fund.app.box.dto.CreateFundraisingEventRequest;
import com.fund.app.box.dto.FundraisingEventDto;
import com.fund.app.box.dto.FundraisingEventFinancialReportDto;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.service.FundraisingEventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sii/api/events")
public class FundraisingEventController {
    @Autowired
    private FundraisingEventService fundraisingEventService;

    @PostMapping("/create")
    public ResponseEntity<FundraisingEventDto> createEvent(@Valid @RequestBody CreateFundraisingEventRequest eventDto) {
        FundraisingEvent event = fundraisingEventService.createFundraisingEvent(eventDto);
        FundraisingEventDto response = new FundraisingEventDto(
                event.getId(),
                event.getEventName(),
                event.getAccountCurrency(),
                event.getAccountBalance()
        );

        return ResponseEntity.ok(response);

    }

    @PostMapping("/financial-report")
    public ResponseEntity<List<FundraisingEventFinancialReportDto>> financialReport() {
        List<FundraisingEventFinancialReportDto> report = fundraisingEventService.generateFinancialReport();
        return ResponseEntity.ok(report);
    }



}
