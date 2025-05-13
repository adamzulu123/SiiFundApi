package com.fund.app.box.service;

import com.fund.app.box.dto.CreateFundraisingEventRequest;
import com.fund.app.box.dto.FundraisingEventDto;
import com.fund.app.box.dto.FundraisingEventFinancialReportDto;
import com.fund.app.box.exception.EventAlreadyExistsException;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.repository.FundraisingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FundraisingEventService {

    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;

    //create new Event
    @Transactional
    public FundraisingEvent createFundraisingEvent(CreateFundraisingEventRequest request) {
        boolean exists = fundraisingEventRepository.existsByEventName(request.getEventName());
        if (exists) {
            throw new EventAlreadyExistsException("Event already exists: " + request.getEventName());
        }

        FundraisingEvent fundraisingEvent = new FundraisingEvent(
                request.getEventName(),
                request.getAccountCurrency()
        );
        return fundraisingEventRepository.save(fundraisingEvent);
    }

    @Transactional
    public List<FundraisingEventFinancialReportDto> generateFinancialReport() {
        return fundraisingEventRepository.findAll().stream()
                .map(event -> new FundraisingEventFinancialReportDto(
                            event.getEventName(),
                            event.getAccountBalance(),
                            event.getAccountCurrency()
                ))
                .collect(Collectors.toList());
    }




}
