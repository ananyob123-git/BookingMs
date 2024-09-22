package com.assignment.project.BookingMs.Model;

import lombok.Data;

import java.util.Date;

@Data
public class BookingRequest {

    private Long bookingNumber;
    private Long busNumber;
    private String source;
    private String destination;
    private Integer numberOfSeats;
    private Long passengerId;
    private String date;
}
