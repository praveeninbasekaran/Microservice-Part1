package com.example.export.test;

import com.example.export.dto.ExportRequest;
import com.example.export.enums.ExportFormat;
import com.example.export.util.ExportUtil;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMultipleSheet {

    public static class SampleDTO {
        int id;
        String name;
        String description;
        String lastColumn;

        public SampleDTO(int id, String name, String description, String lastColumn) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.lastColumn = lastColumn;
        }
    }

    public static class AnotherSampleDTO {
        String code;
        String title;
        String details;

        public AnotherSampleDTO(String code, String title, String details) {
            this.code = code;
            this.title = title;
            this.details = details;
        }
    }

    public static void main(String[] args) throws Exception {

        // Sheet 1 data
        List<SampleDTO> dataSheet1 = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            dataSheet1.add(new SampleDTO(i, "Name-" + i, "Description-" + i, "Last Column-" + i));
        }

        // Sheet 2 data
        List<AnotherSampleDTO> dataSheet2 = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            dataSheet2.add(new AnotherSampleDTO("Code-" + i, "Title-" + i, "Details-" + i));
        }

        // Sheet names
        List<String> sheetNames = Arrays.asList("Sheet1", "Sheet2");

        // Custom headers for each sheet
        List<List<String>> customHeadersPerSheet = Arrays.asList(
                Arrays.asList("ID", "Name", "Description", "Last Column"),     // Sheet 1 headers
                Arrays.asList("Code", "Title", "Details")                     // Sheet 2 headers
        );

        // Data per sheet
        List<List<?>> dataPerSheet = Arrays.asList(
                dataSheet1,
                dataSheet2
        );

        // Prepare ExportRequest
        ExportRequest<Object> request = new ExportRequest<>();
        request.setSheetNames(sheetNames);
        request.setDataPerSheet(dataPerSheet);
        request.setCustomHeadersPerSheet(customHeadersPerSheet);
        request.setFormat(ExportFormat.EXCEL);

        try (FileOutputStream fos = new FileOutputStream("multi_sheet_output_with_custom_headers.xlsx")) {
            ExportUtil.export(request, fos);
        }

        System.out.println("✅ Excel export with multiple sheets & custom headers complete: multi_sheet_output_with_custom_headers.xlsx");
    }
}