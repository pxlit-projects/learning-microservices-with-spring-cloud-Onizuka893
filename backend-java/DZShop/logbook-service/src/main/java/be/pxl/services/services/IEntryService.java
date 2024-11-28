package be.pxl.services.services;

import be.pxl.services.domain.EntryRequest;
import be.pxl.services.domain.EntryResponse;

import java.util.List;

public interface IEntryService {
    List<EntryResponse> getAllEntries();

    EntryResponse addEntry(EntryRequest entryRequest);
}
