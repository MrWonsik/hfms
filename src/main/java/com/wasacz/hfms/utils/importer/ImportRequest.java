package com.wasacz.hfms.utils.importer;

import com.wasacz.hfms.finance.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class ImportRequest {
    private Long userId;
    private List<ImportData> importDataList;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    static class ImportData {
        private Long categoryId;
        private ServiceType serviceType;
        private String startDate;
        private List<Double> values;
        private String name;
    }
}
