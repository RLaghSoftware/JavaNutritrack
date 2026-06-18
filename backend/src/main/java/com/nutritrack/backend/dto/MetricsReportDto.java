package com.nutritrack.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MetricsReportDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyMetricsDto> rows;
    private DailyMetricsDto totals;
}
