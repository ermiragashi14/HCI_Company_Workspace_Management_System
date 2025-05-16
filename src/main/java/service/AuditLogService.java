package service;

import dto.AuditLogDTO;
import repository.AuditLogRepository;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.List;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;


public class AuditLogService {

    private final AuditLogRepository repository = new AuditLogRepository();

    public void log(String actionType, String details) {
        try {
            int userId = SessionManager.getInstance().getLoggedInUserId();
            int companyId = SessionManager.getInstance().getLoggedInCompanyId();
            AuditLogDTO log = new AuditLogDTO(userId, companyId, actionType, details);
            repository.logAction(log);
        } catch (SQLException e) {
            System.err.println("Audit log failed: " + e.getMessage());
        }
    }

    public List<AuditLogDTO> filterLogs(Integer userId, String actionType, LocalDate startDate, LocalDate endDate) {
        try {
            int companyId = SessionManager.getInstance().getLoggedInCompanyId();
            return repository.getLogsWithFilters(userId, actionType, startDate, endDate, companyId);
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<AuditLogDTO> getAuditLogs(int companyId, String userText, String actionType, LocalDate from, LocalDate to) {
        try {
            Integer userId = null;
            if (userText != null && !userText.isBlank()) {
                try {
                    userId = Integer.parseInt(userText);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid user ID entered: " + userText);
                    return Collections.emptyList(); // or show alert
                }
            }
            return repository.getLogsWithFilters(userId, actionType, from, to, companyId);
        } catch (SQLException e) {
            System.err.println("Error fetching audit logs: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void exportToPDF(List<AuditLogDTO> logs) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("audit_logs.pdf"));
            document.open();

            document.add(new Paragraph("Audit Logs"));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("User");
            table.addCell("Action");
            table.addCell("Details");
            table.addCell("Timestamp");

            for (AuditLogDTO log : logs) {
                table.addCell(log.getUserFullName());
                table.addCell(log.getActionType());
                table.addCell(log.getDetails());
                table.addCell(log.getTimestamp());
            }

            document.add(table);
            document.close();
            System.out.println("Audit logs exported.");
        } catch (Exception e) {
            System.err.println("Failed to export audit logs: " + e.getMessage());
        }
    }

    public String getCompanyName(int companyId) {
        return repository.getCompanyName(companyId);
    }



}
