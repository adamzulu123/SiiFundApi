package com.fund.app.box.service;

import com.fund.app.box.dto.CreateFundraisingEventRequest;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.repository.FundraisingEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundraisingEventServiceTest {

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;
    @InjectMocks
    private FundraisingEventService fundraisingEventService;

    @Test
    void shouldCreateFundraisingEvent() {
        CreateFundraisingEventRequest request = new CreateFundraisingEventRequest("Save Earth", Currency.EUR);

        fundraisingEventService.createFundraisingEvent(request);

        ArgumentCaptor<FundraisingEvent> captor = ArgumentCaptor.forClass(FundraisingEvent.class);
        verify(fundraisingEventRepository).save(captor.capture());

        FundraisingEvent saved = captor.getValue();
        assertThat(saved.getEventName()).isEqualTo("Save Earth");
        assertThat(saved.getAccountCurrency()).isEqualTo(Currency.EUR);
        assertThat(saved.getAccountBalance()).isEqualTo(BigDecimal.ZERO);

    }

    @Test
    void shouldThrowExceptionIfEventAlreadyExists(){
        CreateFundraisingEventRequest request = new CreateFundraisingEventRequest("Save Earth", Currency.EUR);

        when(fundraisingEventRepository.existsByEventName("Save Earth")).thenReturn(true);

        assertThatThrownBy(()->fundraisingEventService.createFundraisingEvent(request))
            .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Event already exists: Save Earth");

    }


}
