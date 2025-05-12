package com.fund.app.box.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fund.app.box.exception.NonExistingEventNameException;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.repository.CollectionBoxRepository;
import com.fund.app.box.repository.FundraisingEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CollectionBoxServiceTest {

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;
    @Mock
    private CollectionBoxRepository collectionBoxRepository;
    @InjectMocks
    private CollectionBoxService collectionBoxService;

    @Test
    void createCollectionBoxWithoutEventName() {
        CollectionBox mockBox = new CollectionBox();
        mockBox.setUniqueIdentifier("test-unique-identifier");

        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(mockBox);

        CollectionBox createdBox = collectionBoxService.createCollectionBox();

        assertNotNull(createdBox);
        assertNull(createdBox.getFundraisingEvent());
        verify(collectionBoxRepository).save(any(CollectionBox.class));
    }

    @Test
    void createCollectionBoxWithEventName() {
        String eventName = "test-event-name";
        FundraisingEvent fundraisingEvent = new FundraisingEvent(eventName, Currency.USD);

        when(fundraisingEventRepository.findByEventName(eventName))
                .thenReturn(Optional.of(fundraisingEvent));
        when(collectionBoxRepository.save(any(CollectionBox.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CollectionBox createdBox = collectionBoxService.createCollectionBox(eventName);

        assertNotNull(createdBox);
        assertNotNull(createdBox.getFundraisingEvent());
        assertThat(createdBox.getFundraisingEvent().getEventName()).isEqualTo(eventName);

        // capture the actual object passed to repository
        ArgumentCaptor<CollectionBox> captor = ArgumentCaptor.forClass(CollectionBox.class);
        verify(collectionBoxRepository).save(captor.capture());
        CollectionBox savedBox = captor.getValue();

        assertNotNull(savedBox.getFundraisingEvent());
        assertThat(savedBox.getFundraisingEvent().getEventName()).isEqualTo(eventName);

        verify(fundraisingEventRepository).findByEventName(eventName);
    }

    @Test
    void createCollectionBoxWithNonExistentEventName(){
        String eventName = "test-event-name";

        when(fundraisingEventRepository.findByEventName(eventName)).thenReturn(Optional.empty());

        assertThrows(NonExistingEventNameException.class, () -> collectionBoxService.createCollectionBox(eventName));
    }

}
