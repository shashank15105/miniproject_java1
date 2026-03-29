package com.coworking.service;

import com.coworking.model.BookingDetails;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class BookingNotificationService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public BookingNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public boolean sendConfirmationEmail(BookingDetails bookingDetails) {
        if (bookingDetails == null) {
            return false;
        }

        String email = bookingDetails.getUserEmail();
        if (email == null || email.isBlank() || "NA".equalsIgnoreCase(email)) {
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Booking Confirmation - " + bookingDetails.getWorkspaceName());
            message.setText(buildPlainText(bookingDetails));
            mailSender.send(message);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public String buildEmailPreview(BookingDetails bookingDetails) {
        String start = DATE_TIME_FORMATTER.format(bookingDetails.getStartTime());
        String end = DATE_TIME_FORMATTER.format(bookingDetails.getEndTime());

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Booking Confirmation</title>
                <style>
                    body { font-family: Inter, Arial, sans-serif; background: #f6f8fb; color: #182033; padding: 24px; }
                    .card { max-width: 720px; margin: 0 auto; background: #ffffff; border-radius: 24px; padding: 32px; box-shadow: 0 20px 48px rgba(24, 32, 51, 0.10); }
                    .eyebrow { display: inline-block; padding: 6px 12px; background: #fff2ea; color: #8f4627; border-radius: 999px; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; }
                    h1 { margin: 16px 0 10px; font-size: 30px; }
                    p { color: #66738f; line-height: 1.6; }
                    .grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; margin-top: 24px; }
                    .tile { border: 1px solid #d9dee8; border-radius: 18px; padding: 16px; background: #fbfcfe; }
                    .label { display: block; font-size: 12px; letter-spacing: 0.08em; text-transform: uppercase; color: #66738f; margin-bottom: 6px; font-weight: 700; }
                    .value { font-size: 18px; font-weight: 700; color: #182033; }
                    .footer { margin-top: 28px; padding-top: 18px; border-top: 1px solid #e7ebf2; color: #66738f; }
                </style>
            </head>
            <body>
                <div class="card">
                    <span class="eyebrow">Booking Confirmed</span>
                    <h1>Your workspace is reserved, %s</h1>
                    <p>Thanks for choosing our coworking network. Here are your confirmed reservation details for %s.</p>
                    <div class="grid">
                        <div class="tile">
                            <span class="label">Booking ID</span>
                            <div class="value">%s</div>
                        </div>
                        <div class="tile">
                            <span class="label">Workspace</span>
                            <div class="value">%s</div>
                        </div>
                        <div class="tile">
                            <span class="label">Location</span>
                            <div class="value">%s</div>
                        </div>
                        <div class="tile">
                            <span class="label">Total Price</span>
                            <div class="value">Rs %d</div>
                        </div>
                        <div class="tile">
                            <span class="label">Start Time</span>
                            <div class="value">%s</div>
                        </div>
                        <div class="tile">
                            <span class="label">End Time</span>
                            <div class="value">%s</div>
                        </div>
                    </div>
                    <div class="footer">
                        You can review your booking history, download a PDF receipt, and manage reservations from the My Bookings page.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
            escapeHtml(bookingDetails.getUserName()),
            escapeHtml(bookingDetails.getWorkspaceName()),
            escapeHtml(bookingDetails.getBookingId()),
            escapeHtml(bookingDetails.getWorkspaceName()),
            escapeHtml(bookingDetails.getLocation()),
            bookingDetails.getTotalPrice(),
            escapeHtml(start),
            escapeHtml(end)
        );
    }

    private String buildPlainText(BookingDetails bookingDetails) {
        return """
            Hello %s,

            Your booking has been confirmed.

            Booking ID: %s
            Workspace: %s
            Location: %s
            Start: %s
            End: %s
            Total Price: Rs %d

            You can log in to the website to view your bookings and download your receipt.
            """.formatted(
            bookingDetails.getUserName(),
            bookingDetails.getBookingId(),
            bookingDetails.getWorkspaceName(),
            bookingDetails.getLocation(),
            DATE_TIME_FORMATTER.format(bookingDetails.getStartTime()),
            DATE_TIME_FORMATTER.format(bookingDetails.getEndTime()),
            bookingDetails.getTotalPrice()
        );
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
