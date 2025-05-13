package com.fund.app.box.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fund.app.box.dto.AddMoneyRequest;
import com.fund.app.box.exception.NonExistingCollectionBoxException;
import com.fund.app.box.exception.NonExistingEventNameException;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.model.MoneyEntry;
import com.fund.app.box.repository.CollectionBoxRepository;
import com.fund.app.box.repository.FundraisingEventRepository;
import com.fund.app.box.repository.MoneyEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CollectionBoxServiceTest {

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;
    @Mock
    private CollectionBoxRepository collectionBoxRepository;
    @Mock
    private MoneyEntryRepository moneyEntryRepository;
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
    void createCollectionBoxWithNonExistentEventName() {
        String eventName = "test-event-name";

        when(fundraisingEventRepository.findByEventName(eventName)).thenReturn(Optional.empty());

        assertThrows(NonExistingEventNameException.class, () -> collectionBoxService.createCollectionBox(eventName));
    }

    @Test
    void removeExistingEmptyCollectionBox() {
        String identifier = "test-identifier";
        CollectionBox box = new CollectionBox();
        box.setUniqueIdentifier(identifier);

        when(collectionBoxRepository.findByUniqueIdentifier(identifier))
                .thenReturn(Optional.of(box));

        collectionBoxService.removeCollectionBox(identifier);
        verify(collectionBoxRepository).delete(box);
    }

    @Test
    void removeExistingNonEmptyCollectionBox() {
        String identifier = "test-identifier";
        CollectionBox box = new CollectionBox();
        box.setUniqueIdentifier(identifier);

        MoneyEntry moneyEntry = new MoneyEntry();
        box.getMoneyEntries().add(moneyEntry);

        when(collectionBoxRepository.findByUniqueIdentifier(identifier))
                .thenReturn(Optional.of(box));

        collectionBoxService.removeCollectionBox(identifier);

        assertTrue(box.getMoneyEntries().isEmpty());
        verify(collectionBoxRepository).delete(box);
    }

    @Test
    void removeNonExistingCollectionBox_throwsException() {
        String identifier = "non-existing";

        when(collectionBoxRepository.findByUniqueIdentifier(identifier))
                .thenReturn(Optional.empty());

        assertThrows(NonExistingCollectionBoxException.class, () ->
                collectionBoxService.removeCollectionBox(identifier)
        );
    }

    @Test
    void assignEmptyAndNotAssignedCollectionBox() {
        String identifier = "test-identifier";
        String eventName = "test-event-name";

        CollectionBox box = new CollectionBox();
        box.setUniqueIdentifier(identifier);

        FundraisingEvent event = new FundraisingEvent(eventName, Currency.USD);

        when(collectionBoxRepository.findByUniqueIdentifier(identifier)).thenReturn(Optional.of(box));
        when(fundraisingEventRepository.findByEventName(eventName)).thenReturn(Optional.of(event));

        collectionBoxService.assignBoxToEvent(identifier, eventName);
        assertEquals(event, box.getFundraisingEvent());
    }

    @Test
    void shouldThrowExceptionWhenBoxNotFound() {
        String identifier = "test-identifier";

        when(collectionBoxRepository.findByUniqueIdentifier(identifier)).thenReturn(Optional.empty());

        assertThrows(NonExistingCollectionBoxException.class, () ->
                collectionBoxService.assignBoxToEvent(identifier, "event"));
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        String eventName = "test-event-name";
        String identifier = "test-identifier";
        CollectionBox box = new CollectionBox();
        box.setUniqueIdentifier(identifier);

        when(collectionBoxRepository.findByUniqueIdentifier(identifier)).thenReturn(Optional.of(box));
        when(fundraisingEventRepository.findByEventName(eventName)).thenReturn(Optional.empty());

        assertThrows(NonExistingEventNameException.class, () ->
                collectionBoxService.assignBoxToEvent(identifier, eventName));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenBoxIsNotEmpty(){
        String identifier = "test-identifier";
        String eventName = "test-event-name";

        CollectionBox box = new CollectionBox();
        box.setUniqueIdentifier(identifier);

        FundraisingEvent event = new FundraisingEvent(eventName, Currency.USD);
        box.setFundraisingEvent(event);

        when(collectionBoxRepository.findByUniqueIdentifier(identifier)).thenReturn(Optional.of(box));
        when(fundraisingEventRepository.findByEventName(eventName)).thenReturn(Optional.of(event));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                collectionBoxService.assignBoxToEvent(identifier, eventName));

        assertEquals("Cannot assign an assigned collection box to an event", ex.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenBoxIsAssigned() {
        String identifier = "test-identifier";
        String eventName = "test-event-name";

        CollectionBox box = new CollectionBox();
        box.setUniqueIdentifier(identifier);

        FundraisingEvent event = new FundraisingEvent(eventName, Currency.USD);

        MoneyEntry moneyEntry = new MoneyEntry();
        box.getMoneyEntries().add(moneyEntry);

        when(collectionBoxRepository.findByUniqueIdentifier(identifier)).thenReturn(Optional.of(box));
        when(fundraisingEventRepository.findByEventName(eventName)).thenReturn(Optional.of(event));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                collectionBoxService.assignBoxToEvent(identifier, eventName));

        assertEquals("Cannot assign a non-empty collection box to an event", ex.getMessage());

    }

    @Test
    void addMoneyToCollectionBoxSuccessfully() {
        String uniqueIdentifier = "test-identifier";
        CollectionBox box = new CollectionBox();
        box.setUniqueIdentifier(uniqueIdentifier);

        AddMoneyRequest request = new AddMoneyRequest();
        request.setCurrency(Currency.USD);
        request.setUniqueIdentifier(uniqueIdentifier);
        request.setAmount(BigDecimal.valueOf(10));

        when(collectionBoxRepository.findByUniqueIdentifier(uniqueIdentifier)).thenReturn(Optional.of(box));

        collectionBoxService.addMoneyToBox(request.getUniqueIdentifier(), request.getAmount(), request.getCurrency());

        ArgumentCaptor<MoneyEntry> moneyEntryCaptor = ArgumentCaptor.forClass(MoneyEntry.class);
        verify(moneyEntryRepository).save(moneyEntryCaptor.capture());

        MoneyEntry savedEntry = moneyEntryCaptor.getValue();
        assertEquals(request.getAmount(), savedEntry.getAmount());
        assertEquals(request.getCurrency(), savedEntry.getCurrency());
        assertEquals(box, savedEntry.getCollectionBox());
        assertNotNull(savedEntry.getCreateTime());
    }

    @Test
    void addMoneyToInvalidBoxId(){
        String invalidIdentifier = "test-identifier";

        AddMoneyRequest request = new AddMoneyRequest();
        request.setCurrency(Currency.USD);
        request.setUniqueIdentifier(invalidIdentifier);
        request.setAmount(BigDecimal.valueOf(10));

        when(collectionBoxRepository.findByUniqueIdentifier(invalidIdentifier)).thenReturn(Optional.empty());

        assertThrows(NonExistingCollectionBoxException.class, () ->
                collectionBoxService.addMoneyToBox(request.getUniqueIdentifier(), request.getAmount(), request.getCurrency()));

        verify(moneyEntryRepository, never()).save(any(MoneyEntry.class));
    }




}


