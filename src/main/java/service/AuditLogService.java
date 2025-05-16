package service;

import dto.AuditLogDTO;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.User;
import repository.AuditLogRepository;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
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

    public String getCompanyName(int companyId) {
        return repository.getCompanyName(companyId);
    }

    public List<User> getUsersByCompany(int companyId) {
        return repository.getUsersByCompany(companyId);
    }

    public void exportToPDF(List<AuditLogDTO> logs, Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Audit Logs PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("audit_logs.pdf");

        File file = fileChooser.showSaveDialog(parentWindow);
        if (file == null) return;

        try {
            int companyId = SessionManager.getInstance().getLoggedInCompanyId();
            String companyName = getCompanyName(companyId);
            String today = LocalDate.now().toString(); // You can replace this with a selected filter date if needed

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Header
            document.add(new Paragraph("Audit Logs for " + companyName + " on " + today));
            document.add(Chunk.NEWLINE);

            // Table
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

            System.out.println("Audit logs exported to: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Failed to export audit logs: " + e.getMessage());
        }
    }

}
