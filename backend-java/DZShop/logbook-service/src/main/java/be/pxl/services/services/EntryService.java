package be.pxl.services.services;

import be.pxl.services.domain.Entry;
import be.pxl.services.domain.EntryRequest;
import be.pxl.services.domain.EntryResponse;
import be.pxl.services.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntryService implements IEntryService {
    private static final Logger log = LoggerFactory.getLogger(EntryService.class);
    private final EntryRepository entryRepository;

    private EntryResponse mapToEntryResponse(Entry entry) {
        return EntryResponse.builder()
                .id(entry.getId())
                .message(entry.getMessage())
                .producer(entry.getProducer())
                .created(entry.getCreated())
                .build();
    }

    @Override
    public List<EntryResponse> getAllEntries() {
        log.info("Get all entries");
        return entryRepository.findAll().stream().map(this::mapToEntryResponse).toList();
    }

    @Override
    public EntryResponse addEntry(EntryRequest entryRequest) {
        log.info("Add entry: {}", entryRequest);
        Entry entry = Entry.builder()
                .message(entryRequest.getMessage())
                .producer(entryRequest.getProducer())
                .created(new Date())
                .build();
        return mapToEntryResponse(entryRepository.save(entry));
    }

}
