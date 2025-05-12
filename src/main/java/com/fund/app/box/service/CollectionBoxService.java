package com.fund.app.box.service;

import com.fund.app.box.dto.CreateCollectionBoxRequest;
import com.fund.app.box.exception.NonExistingEventNameException;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.model.FundraisingEvent;
import com.fund.app.box.repository.CollectionBoxRepository;
import com.fund.app.box.repository.FundraisingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CollectionBoxService {

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;
    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;

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

}
