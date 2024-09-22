package com.assignment.project.BookingMs.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingSagaListener {

    @Autowired
    private BookingService bookingService;

    @KafkaListener(topics = "payment-failure-topic", groupId = "booking-consumer-group")
    public void handlePaymentFailure(String bookingNumber) {
        System.out.println("Received payment failure for booking: " + bookingNumber);
        bookingService.cancelBooking(bookingNumber);
    }
}

