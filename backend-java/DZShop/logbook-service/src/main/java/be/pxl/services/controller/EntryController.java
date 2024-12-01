package be.pxl.services.controller;

import be.pxl.services.domain.LogbookEntryRequest;
import be.pxl.services.services.EntryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EntryController {
    private static final Logger log = LoggerFactory.getLogger(EntryController.class);
    private final EntryService entryService;

    @GetMapping
    public ResponseEntity getEntries() {
        log.info("Get entries endpoint called");
        return new ResponseEntity(entryService.getAllEntries(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createEntry(@RequestBody LogbookEntryRequest logbookEntryRequest) {
        log.info("Create entry endpoint called with body {}", logbookEntryRequest);
        return new ResponseEntity(entryService.addEntry(logbookEntryRequest), HttpStatus.CREATED);
    }
}
