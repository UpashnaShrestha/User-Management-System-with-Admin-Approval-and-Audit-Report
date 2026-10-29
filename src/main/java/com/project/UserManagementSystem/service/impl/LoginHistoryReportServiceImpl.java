package com.project.UserManagementSystem.service.impl;

import com.project.UserManagementSystem.entity.LoginHistory;
import com.project.UserManagementSystem.repo.LoginHistoryRepository;
import com.project.UserManagementSystem.service.LoginHistoryReportService;

import lombok.RequiredArgsConstructor;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginHistoryReportServiceImpl
        implements LoginHistoryReportService {

    private final LoginHistoryRepository loginHistoryRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[] HEADERS = {
            "Login Date",
            "IP Address",
            "Browser",
            "Operating System",
            "Device",
            "Status"
    };

    // --------------------------------------------------
    // Common helper methods
    // --------------------------------------------------

    private List<LoginHistory> getFilteredHistories(
            String username,
            String browser,
            String os,
            String ipAddress,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate) {

        LocalDateTime effectiveFromDate = fromDate != null
                ? fromDate
                : LocalDateTime.of(1970, 1, 1, 0, 0);

        LocalDateTime effectiveToDate = toDate != null
                ? toDate
                : LocalDateTime.of(9999, 12, 31, 23, 59, 59);

        if (effectiveFromDate.isAfter(effectiveToDate)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date."
            );
        }

        return loginHistoryRepository.findAllForReport(
                normalize(username),
                normalize(browser),
                normalize(os),
                normalize(ipAddress),
                normalize(status).toUpperCase(),
                effectiveFromDate,
                effectiveToDate
        );
    }

    private String normalize(String value) {
        return value == null || value.isBlank()
                ? ""
                : value.trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null
                ? ""
                : dateTime.format(DATE_FORMATTER);
    }

    private String getStatus(LoginHistory history) {
        return history.getLoginStatus() == null
                ? ""
                : history.getLoginStatus().name();
    }

    // --------------------------------------------------
    // PDF report generation using OpenPDF
    // --------------------------------------------------

    @Override
    public byte[] generatePdfReport(
            String username,
            String browser,
            String os,
            String ipAddress,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate) throws IOException {

        List<LoginHistory> histories = getFilteredHistories(
                username, browser, os, ipAddress,
                status, fromDate, toDate
        );

        try (ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate());

            try {
                PdfWriter.getInstance(document, output);

                document.open();

                Font titleFont = new Font(
                        Font.HELVETICA,
                        18,
                        Font.BOLD
                );

                Font normalFont = new Font(
                        Font.HELVETICA,
                        10,
                        Font.NORMAL
                );

                Font headerFont = new Font(
                        Font.HELVETICA,
                        9,
                        Font.BOLD
                );

                document.add(new Paragraph(
                        "Login History Report",
                        titleFont
                ));

                document.add(Chunk.NEWLINE);

                document.add(new Paragraph(
                        "Generated: " + formatDate(LocalDateTime.now()),
                        normalFont
                ));

                document.add(new Paragraph(
                        "Username filter: " +
                                (username == null || username.isBlank()
                                        ? "All"
                                        : username),
                        normalFont
                ));

                document.add(new Paragraph(
                        "Total records: " + histories.size(),
                        normalFont
                ));

                document.add(new Paragraph(
                        "Filters: Browser=" + filterValue(browser)
                                + ", OS=" + filterValue(os)
                                + ", IP=" + filterValue(ipAddress)
                                + ", Status=" + filterValue(status)
                                + ", From=" + filterValue(fromDate)
                                + ", To=" + filterValue(toDate),
                        normalFont
                ));

                document.add(Chunk.NEWLINE);

                // Create the report table.
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);

                table.setWidths(new float[]{
                        2.3f, 2f, 1.8f, 2.2f, 1.8f, 1.5f
                });

                // Table headers.
                for (String header : HEADERS) {
                    PdfPCell cell = new PdfPCell(
                            new Phrase(header, headerFont)
                    );

                    cell.setPadding(6);
                    table.addCell(cell);
                }

                // Table data.
                Font dataFont = new Font(
                        Font.HELVETICA,
                        8,
                        Font.NORMAL
                );

                for (LoginHistory history : histories) {
                    table.addCell(new PdfPCell(
                            new Phrase(
                                    formatDate(history.getLoginTime()),
                                    dataFont
                            )
                    ));

                    table.addCell(new PdfPCell(
                            new Phrase(
                                    safe(history.getIpAddress()),
                                    dataFont
                            )
                    ));

                    table.addCell(new PdfPCell(
                            new Phrase(
                                    safe(history.getBrowser()),
                                    dataFont
                            )
                    ));

                    table.addCell(new PdfPCell(
                            new Phrase(
                                    safe(history.getOperatingSystem()),
                                    dataFont
                            )
                    ));

                    table.addCell(new PdfPCell(
                            new Phrase(
                                    safe(history.getDevice()),
                                    dataFont
                            )
                    ));

                    table.addCell(new PdfPCell(
                            new Phrase(getStatus(history), dataFont)
                    ));
                }

                document.add(table);

            } catch (DocumentException e) {
                throw new IOException(
                        "Failed to generate PDF report.", e
                );
            } finally {
                if (document.isOpen()) {
                    document.close();
                }
            }

            return output.toByteArray();
        }
    }

    private String filterValue(String value) {
        return value == null || value.isBlank()
                ? "All"
                : value.trim();
    }

    private String filterValue(LocalDateTime value) {
        return value == null
                ? "All"
                : formatDate(value);
    }

    // --------------------------------------------------
    // Word report generation using Apache POI
    // --------------------------------------------------

    @Override
    public byte[] generateWordReport(
            String username,
            String browser,
            String os,
            String ipAddress,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate) throws IOException {

        List<LoginHistory> histories = getFilteredHistories(
                username, browser, os, ipAddress,
                status, fromDate, toDate
        );

        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            // Title.
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun titleRun = title.createRun();
            titleRun.setText("Login History Report");
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            // Report information.
            addWordParagraph(
                    document,
                    "Generated: " + formatDate(LocalDateTime.now())
            );

            addWordParagraph(
                    document,
                    "Username filter: " + filterValue(username)
            );

            addWordParagraph(
                    document,
                    "Total records: " + histories.size()
            );

            addWordParagraph(
                    document,
                    "Filters: Browser=" + filterValue(browser)
                            + ", OS=" + filterValue(os)
                            + ", IP=" + filterValue(ipAddress)
                            + ", Status=" + filterValue(status)
                            + ", From=" + filterValue(fromDate)
                            + ", To=" + filterValue(toDate)
            );

            // Create table with one header row.
            XWPFTable table = document.createTable(
                    histories.size() + 1,
                    HEADERS.length
            );

            // Header row.
            XWPFTableRow headerRow = table.getRow(0);

            for (int i = 0; i < HEADERS.length; i++) {
                XWPFTableCell cell = headerRow.getCell(i);
                cell.setText(HEADERS[i]);

                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        run.setBold(true);
                    }
                }
            }

            // Data rows.
            for (int i = 0; i < histories.size(); i++) {
                LoginHistory history = histories.get(i);
                XWPFTableRow row = table.getRow(i + 1);

                row.getCell(0).setText(
                        formatDate(history.getLoginTime())
                );

                row.getCell(1).setText(
                        safe(history.getIpAddress())
                );

                row.getCell(2).setText(
                        safe(history.getBrowser())
                );

                row.getCell(3).setText(
                        safe(history.getOperatingSystem())
                );

                row.getCell(4).setText(
                        safe(history.getDevice())
                );

                row.getCell(5).setText(
                        getStatus(history)
                );
            }

            document.write(output);

            return output.toByteArray();
        }
    }

    private void addWordParagraph(
            XWPFDocument document,
            String text) {

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().setText(text);
    }

    // --------------------------------------------------
    // Excel report generation using Apache POI
    // --------------------------------------------------

    @Override
    public byte[] generateExcelReport(
            String username,
            String browser,
            String os,
            String ipAddress,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate) throws IOException {

        List<LoginHistory> histories = getFilteredHistories(
                username, browser, os, ipAddress,
                status, fromDate, toDate
        );

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Login History");

            // Report title.
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0)
                    .setCellValue("Login History Report");

            // Report metadata.
            Row generatedRow = sheet.createRow(1);
            generatedRow.createCell(0).setCellValue(
                    "Generated: " + formatDate(LocalDateTime.now())
            );

            Row usernameRow = sheet.createRow(2);
            usernameRow.createCell(0).setCellValue(
                    "Username filter: " + filterValue(username)
            );

            Row totalRow = sheet.createRow(3);
            totalRow.createCell(0).setCellValue(
                    "Total records: " + histories.size()
            );

            // Header row.
            Row headerRow = sheet.createRow(5);

            CellStyle headerStyle = workbook.createCellStyle();

            org.apache.poi.ss.usermodel.Font headerFont =
                    workbook.createFont();

            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows.
            for (int i = 0; i < histories.size(); i++) {
                LoginHistory history = histories.get(i);

                Row row = sheet.createRow(i + 6);

                row.createCell(0).setCellValue(
                        formatDate(history.getLoginTime())
                );

                row.createCell(1).setCellValue(
                        safe(history.getIpAddress())
                );

                row.createCell(2).setCellValue(
                        safe(history.getBrowser())
                );

                row.createCell(3).setCellValue(
                        safe(history.getOperatingSystem())
                );

                row.createCell(4).setCellValue(
                        safe(history.getDevice())
                );

                row.createCell(5).setCellValue(
                        getStatus(history)
                );
            }

            // Enable filtering on the header and data.
            sheet.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            5,
                            histories.size() + 5,
                            0,
                            HEADERS.length - 1
                    )
            );

            // Keep column headers visible when scrolling.
            sheet.createFreezePane(0, 6);

            // Automatically resize columns.
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(output);

            return output.toByteArray();
        }
    }
}