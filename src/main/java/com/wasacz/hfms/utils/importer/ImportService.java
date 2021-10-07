package com.wasacz.hfms.utils.importer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImportService {

    void importData(Long userId, List<ImportRequest.ImportData> importDataList);

}
