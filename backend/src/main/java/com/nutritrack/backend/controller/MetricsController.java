package com.nutritrack.backend.controller;

import com.nutritrack.backend.dto.MealResponse;
import com.nutritrack.backend.dto.MetricsReportDto;
import com.nutritrack.backend.security.UserPrincipal;
import com.nutritrack.backend.service.MetricsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/report")
    public ResponseEntity<?> getReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(MealResponse.failure("End date must be on or after start date."));
        }
        MetricsReportDto report = metricsService.getDailyReport(
                principal.getUser(), startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }

        byte[] file = metricsService.buildExcelReport(principal.getUser(), startDate, endDate);
        String filename = metricsService.buildFilename(startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
}
