package com.fund.app.box.controller;

import com.fund.app.box.dto.AssignBoxRequest;
import com.fund.app.box.dto.CollectionBoxDto;
import com.fund.app.box.dto.CreateCollectionBoxRequest;
import com.fund.app.box.model.CollectionBox;
import com.fund.app.box.service.CollectionBoxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
                newBox.isEmpty()
        );
        return ResponseEntity.ok(collectionBoxDto);
    }

    @GetMapping
    public ResponseEntity<List<CollectionBoxDto>> getCollectionBoxes() {
        List<CollectionBox> allCollectionBoxes = collectionBoxService.getAllCollectionBoxes();
        List<CollectionBoxDto> collectionBoxDtos = allCollectionBoxes.stream()
                .map(box -> new CollectionBoxDto(
                        box.getUniqueIdentifier(),
                        box.isAssigned(),
                        box.isEmpty()
                ))
                .toList();

        return ResponseEntity.ok(collectionBoxDtos);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeCollectionBox(@RequestParam String uniqueIdentifier) {
        collectionBoxService.removeCollectionBox(uniqueIdentifier);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignCollectionBox(@Valid @RequestBody AssignBoxRequest request) {
        collectionBoxService.assignBoxToEvent(request.getUniqueIdentifier(), request.getEventName());
        return ResponseEntity.ok("CollectionBox assigned to event " + request.getEventName());

    }




}





