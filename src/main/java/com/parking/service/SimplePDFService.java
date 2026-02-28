package com.parking.service;

import com.parking.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class SimplePDFService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public byte[] generateTicket(Booking booking) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            // Create a simple text-based ticket for now
            StringBuilder ticketContent = new StringBuilder();
            ticketContent.append("========================================\n");
            ticketContent.append("        SMART PARKING SYSTEM\n");
            ticketContent.append("========================================\n\n");
            ticketContent.append("           PARKING TICKET\n");
            ticketContent.append("========================================\n");
            ticketContent.append(String.format("Ticket ID: %s\n", booking.getBookingId()));
            ticketContent.append(String.format("Slot Number: %s\n", booking.getParkingSlot().getSlotId()));
            ticketContent.append(String.format("Floor: %s\n", booking.getParkingSlot().getFloor()));
            ticketContent.append(String.format("Vehicle Number: %s\n", booking.getVehicleNumber()));
            ticketContent.append(String.format("Phone Number: %s\n", booking.getPhoneNumber()));
            ticketContent.append(String.format("Entry Time: %s\n", booking.getEntryTime().format(DATE_FORMATTER)));
            ticketContent.append("========================================\n");
            ticketContent.append("Charges: ₹20 per hour or part thereof\n");
            ticketContent.append("========================================\n");
            ticketContent.append("Please present this ticket at the Exit Counter\n");
            ticketContent.append("========================================\n");
            
            outputStream.write(ticketContent.toString().getBytes());
            
            log.info("Generated text ticket for booking {}", booking.getBookingId());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating ticket for booking {}", booking.getBookingId(), e);
            throw e;
        }
    }
    
    public byte[] generateReceipt(Booking booking) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            StringBuilder receiptContent = new StringBuilder();
            receiptContent.append("========================================\n");
            receiptContent.append("        SMART PARKING SYSTEM\n");
            receiptContent.append("========================================\n\n");
            receiptContent.append("           EXIT RECEIPT\n");
            receiptContent.append("========================================\n");
            receiptContent.append(String.format("Ticket ID: %s\n", booking.getBookingId()));
            receiptContent.append(String.format("Slot Number: %s\n", booking.getParkingSlot().getSlotId()));
            receiptContent.append(String.format("Vehicle Number: %s\n", booking.getVehicleNumber()));
            receiptContent.append(String.format("Entry Time: %s\n", booking.getEntryTime().format(DATE_FORMATTER)));
            receiptContent.append(String.format("Exit Time: %s\n", booking.getExitTime().format(DATE_FORMATTER)));
            receiptContent.append("========================================\n");
            receiptContent.append(String.format("Amount Due: ₹%.2f\n", booking.getAmountCharged()));
            receiptContent.append("========================================\n");
            receiptContent.append("Thank you for using Smart Parking System\n");
            receiptContent.append("========================================\n");
            
            outputStream.write(receiptContent.toString().getBytes());
            
            log.info("Generated text receipt for booking {}", booking.getBookingId());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating receipt for booking {}", booking.getBookingId(), e);
            throw e;
        }
    }
}
