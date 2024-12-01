package be.pxl.services.services;

import be.pxl.services.domain.LogbookEntryRequest;
import be.pxl.services.domain.EntryResponse;

import java.util.List;

public interface IEntryService {
    List<EntryResponse> getAllEntries();

    EntryResponse addEntry(LogbookEntryRequest logbookEntryRequest);
}
