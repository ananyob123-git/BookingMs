package com.assignment.project.BookingMs.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Booking {

    @Id
    private String bookingNumber;
    private Long busNumber;
    private String bookingDate;
    private String source;
    private String destination;
    private Integer numberOfSeats;
    private String status;

    @Override
    public String toString() {
        return "Booking{" +
                "bookingNumber='" + bookingNumber + '\'' +
                ", busNumber=" + busNumber +
                ", bookingDate='" + bookingDate + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", status='" + status + '\'' +
                '}';
    }
}
