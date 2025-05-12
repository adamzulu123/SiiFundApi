package com.fund.app.box.controller;

import com.fund.app.box.dto.CollectionBoxDto;
import com.fund.app.box.dto.CreateCollectionBoxRequest;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.service.CollectionBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sii/api/collection-boxes")
public class CollectionBoxController {

    @Autowired
    private CollectionBoxService collectionBoxService;

    @PostMapping("/create")
    public ResponseEntity<CollectionBoxDto> createCollectionBox(@RequestBody(required = false)
                                                                CreateCollectionBoxRequest request) {

        CollectionBox newBox;
        if (request == null || request.getEventName() == null) {
            newBox = collectionBoxService.createCollectionBox();
        } else{
            newBox = collectionBoxService.createCollectionBox(request.getEventName());
        }

        CollectionBoxDto collectionBoxDto = new CollectionBoxDto(
                newBox.getUniqueIdentifier(),
                newBox.isAssigned(),
                newBox.isEmpty(),
                newBox.isAssigned() ? newBox.getFundraisingEvent().getEventName() : null
        );
        return ResponseEntity.ok(collectionBoxDto);
    }

}
