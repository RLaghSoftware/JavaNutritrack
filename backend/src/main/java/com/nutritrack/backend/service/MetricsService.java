package com.nutritrack.backend.service;

import com.nutritrack.backend.dto.DailyMetricsDto;
import com.nutritrack.backend.dto.MetricsReportDto;
import com.nutritrack.backend.entity.User;
import com.nutritrack.backend.repository.DailyMetricsAggregate;
import com.nutritrack.backend.repository.MealRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MetricsService {

    private final MealRepository mealRepository;

    public MetricsService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    @Transactional(readOnly = true)
    public MetricsReportDto getDailyReport(User user, LocalDate startDate, LocalDate endDate) {
        List<DailyMetricsDto> rows = mealRepository
                .sumMacrosByUserAndDateRange(user, startDate, endDate)
                .stream()
                .map(this::toDailyMetricsDto)
                .toList();

        return MetricsReportDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .rows(rows)
                .totals(sumRows(rows))
                .build();
    }

    public byte[] buildExcelReport(User user, LocalDate startDate, LocalDate endDate) {
        MetricsReportDto report = getDailyReport(user, startDate, endDate);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Map<String, Integer> columns = Map.of(
                    "Date", 0,
                    "Protein", 1,
                    "Carbs", 2,
                    "Fat", 3,
                    "Calories", 4
            );

            Sheet sheet = workbook.createSheet("Metrics");

            Row header = sheet.createRow(0);
            header.createCell(columns.get("Date")).setCellValue("Date");
            header.createCell(columns.get("Protein")).setCellValue("Protein (g)");
            header.createCell(columns.get("Carbs")).setCellValue("Carbs (g)");
            header.createCell(columns.get("Fat")).setCellValue("Fat (g)");
            header.createCell(columns.get("Calories")).setCellValue("Calories");

            int rowIndex = 1;
            for (DailyMetricsDto row : report.getRows()) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(columns.get("Date")).setCellValue(row.getDate().toString());
                dataRow.createCell(columns.get("Protein")).setCellValue(row.getProtein().doubleValue());
                dataRow.createCell(columns.get("Carbs")).setCellValue(row.getCarbs().doubleValue());
                dataRow.createCell(columns.get("Fat")).setCellValue(row.getFat().doubleValue());
                dataRow.createCell(columns.get("Calories")).setCellValue(row.getCalories().doubleValue());
            }

            DailyMetricsDto totals = report.getTotals();
            Row totalRow = sheet.createRow(rowIndex);
            totalRow.createCell(columns.get("Date")).setCellValue("Total");
            totalRow.createCell(columns.get("Protein")).setCellValue(totals.getProtein().doubleValue());
            totalRow.createCell(columns.get("Carbs")).setCellValue(totals.getCarbs().doubleValue());
            totalRow.createCell(columns.get("Fat")).setCellValue(totals.getFat().doubleValue());
            totalRow.createCell(columns.get("Calories")).setCellValue(totals.getCalories().doubleValue());

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build Excel report", e);
        }
    }

    public String buildFilename(LocalDate startDate, LocalDate endDate) {
        return "nutritrack-metrics-" + startDate + "-to-" + endDate + ".xlsx";
    }

    private DailyMetricsDto toDailyMetricsDto(DailyMetricsAggregate aggregate) {
        return DailyMetricsDto.builder()
                .date(aggregate.getMealDate())
                .protein(scale(aggregate.getTotalProtein()))
                .carbs(scale(aggregate.getTotalCarbs()))
                .fat(scale(aggregate.getTotalFat()))
                .calories(scale(aggregate.getTotalCalories()))
                .build();
    }

    private DailyMetricsDto sumRows(List<DailyMetricsDto> rows) {
        BigDecimal protein = BigDecimal.ZERO;
        BigDecimal carbs = BigDecimal.ZERO;
        BigDecimal fat = BigDecimal.ZERO;
        BigDecimal calories = BigDecimal.ZERO;

        for (DailyMetricsDto row : rows) {
            protein = protein.add(row.getProtein());
            carbs = carbs.add(row.getCarbs());
            fat = fat.add(row.getFat());
            calories = calories.add(row.getCalories());
        }

        return DailyMetricsDto.builder()
                .protein(scale(protein))
                .carbs(scale(carbs))
                .fat(scale(fat))
                .calories(scale(calories))
                .build();
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
