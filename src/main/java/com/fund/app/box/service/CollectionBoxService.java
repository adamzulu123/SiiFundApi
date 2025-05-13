package com.fund.app.box.service;

import com.fund.app.box.exception.EmptyCollectionBoxException;
import com.fund.app.box.exception.NonExistingCollectionBoxException;
import com.fund.app.box.exception.NonExistingEventNameException;
import com.fund.app.box.exception.UnassignedBoxException;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.model.Currency;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.model.MoneyEntry;
import com.fund.app.box.repository.CollectionBoxRepository;
import com.fund.app.box.repository.FundraisingEventRepository;
import com.fund.app.box.repository.MoneyEntryRepository;
import com.fund.app.box.util.CurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CollectionBoxService {

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;
    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;
    @Autowired
    private MoneyEntryRepository moneyEntryRepository;
    @Autowired
    private CurrencyConverter currencyConverter;

    @Transactional
    public CollectionBox createCollectionBox(String fundraisingEventName) {
        CollectionBox collectionBox = new CollectionBox();

        if (fundraisingEventName != null && !fundraisingEventName.trim().isEmpty()) {
            FundraisingEvent event = fundraisingEventRepository.findByEventName(fundraisingEventName)
                    .orElseThrow(() -> new NonExistingEventNameException("Invalid fundraising event name: " + fundraisingEventName));

            // double check to be sure its empty
            if (!collectionBox.isEmpty()){
                throw new IllegalArgumentException("Cannot assign a non-empty collection box to an event ");
            }

            collectionBox.setFundraisingEvent(event);
        }
        return collectionBoxRepository.save(collectionBox);
    }

    // OVERLOAD method to create box without an event
    @Transactional
    public CollectionBox createCollectionBox() {
        return createCollectionBox(null);
    }

    @Transactional(readOnly = true)
    public List<CollectionBox> getAllCollectionBoxes() {
        return collectionBoxRepository.findAll();
    }

    @Transactional
    public void removeCollectionBox(String identifier){
        CollectionBox collectionBox = collectionBoxRepository.findByUniqueIdentifier(identifier)
                .orElseThrow(() -> new NonExistingCollectionBoxException("Invalid collection box identifier: " + identifier));

        if (!collectionBox.isEmpty()) collectionBox.getMoneyEntries().clear();
        collectionBoxRepository.delete(collectionBox);
    }

    @Transactional
    public void assignBoxToEvent(String uniqueIdentifier, String eventName){
        CollectionBox box = collectionBoxRepository.findByUniqueIdentifier(uniqueIdentifier)
                .orElseThrow(() -> new NonExistingCollectionBoxException("Invalid collection box identifier: " + uniqueIdentifier));

        FundraisingEvent event = fundraisingEventRepository.findByEventName(eventName)
                .orElseThrow(() -> new NonExistingEventNameException("Invalid event name: " + eventName));

        if (!box.isEmpty()){
            throw new IllegalArgumentException("Cannot assign a non-empty collection box to an event");
        }

        if (box.isAssigned()){
            throw new IllegalArgumentException("Cannot assign an assigned collection box to an event");
        }

        box.setFundraisingEvent(event);
    }

    @Transactional
    public void addMoneyToBox(String uniqueIdentifier, BigDecimal amount, Currency currency){
        CollectionBox collectionBox = collectionBoxRepository.findByUniqueIdentifier(uniqueIdentifier)
                .orElseThrow(() -> new NonExistingCollectionBoxException("Invalid collection box identifier: " + uniqueIdentifier));

        MoneyEntry entry = new MoneyEntry();
        entry.setCollectionBox(collectionBox);
        entry.setAmount(amount);
        entry.setCurrency(currency);
        entry.setCreateTime(LocalDateTime.now());

        //saving this transfer to the repository
        collectionBox.getMoneyEntries().add(entry);
        moneyEntryRepository.save(entry);
    }

    //transfer all money to Fundraising event form the collection box
    @Transactional
    public BigDecimal transferMoneyToFundraisingEvent(String uniqueIdentifier){
        CollectionBox collectionBox = collectionBoxRepository.findByUniqueIdentifier(uniqueIdentifier)
                .orElseThrow(() -> new NonExistingCollectionBoxException("Invalid collection box identifier: " + uniqueIdentifier));

        if (!collectionBox.isAssigned()) throw new UnassignedBoxException("Cannot withdrawal money from unassigned collection box");
        if (collectionBox.isEmpty()) throw new EmptyCollectionBoxException("Cannot withdrawal money from empty collection box");

        FundraisingEvent event = collectionBox.getFundraisingEvent();
        Currency currencyTarget = event.getAccountCurrency();

        BigDecimal totalToTransfer = BigDecimal.ZERO;
        for (MoneyEntry entry: collectionBox.getMoneyEntries()){
            BigDecimal convertedAmount = currencyConverter
                    .convert(entry.getAmount(), entry.getCurrency(), currencyTarget);
            totalToTransfer = totalToTransfer.add(convertedAmount);
        }

        event.setAccountBalance(event.getAccountBalance().add(totalToTransfer));
        collectionBox.getMoneyEntries().clear();

        return totalToTransfer;
    }


}
