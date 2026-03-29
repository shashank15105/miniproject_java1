package com.coworking.service;

import com.coworking.model.BookingDetails;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public byte[] generateReceipt(BookingDetails bookingDetails) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float y = 760;
                writeLine(contentStream, "Co-Working Space Booking Receipt", 20, y, true);
                y -= 34;
                writeLine(contentStream, "Booking ID: " + bookingDetails.getBookingId(), 12, y, false);
                y -= 22;
                writeLine(contentStream, "User: " + bookingDetails.getUserName(), 12, y, false);
                y -= 18;
                writeLine(contentStream, "Email: " + displayValue(bookingDetails.getUserEmail()), 12, y, false);
                y -= 18;
                writeLine(contentStream, "Phone: " + displayValue(bookingDetails.getUserPhone()), 12, y, false);
                y -= 28;
                writeLine(contentStream, "Workspace: " + bookingDetails.getWorkspaceName(), 12, y, false);
                y -= 18;
                writeLine(contentStream, "Location: " + bookingDetails.getLocation(), 12, y, false);
                y -= 18;
                writeLine(contentStream, "Start Time: " + DATE_TIME_FORMATTER.format(bookingDetails.getStartTime()), 12, y, false);
                y -= 18;
                writeLine(contentStream, "End Time: " + DATE_TIME_FORMATTER.format(bookingDetails.getEndTime()), 12, y, false);
                y -= 18;
                writeLine(contentStream, "Duration: " + formatDuration(bookingDetails), 12, y, false);
                y -= 28;
                writeLine(contentStream, "Total Paid: Rs " + bookingDetails.getTotalPrice(), 14, y, true);
                y -= 34;
                writeLine(contentStream, "Thank you for booking with our coworking network.", 12, y, false);
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to generate PDF receipt.", ex);
        }
    }

    private void writeLine(PDPageContentStream contentStream, String text, int fontSize, float y, boolean bold)
        throws IOException {
        contentStream.beginText();
        contentStream.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(60, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private String formatDuration(BookingDetails bookingDetails) {
        long minutes = Duration.between(bookingDetails.getStartTime(), bookingDetails.getEndTime()).toMinutes();
        long hours = minutes / 60;
        long remainderMinutes = minutes % 60;
        if (remainderMinutes == 0) {
            return hours + " hour" + (hours == 1 ? "" : "s");
        }
        return hours + "h " + remainderMinutes + "m";
    }

    private String displayValue(String value) {
        return value == null || value.isBlank() ? "NA" : value;
    }
}
