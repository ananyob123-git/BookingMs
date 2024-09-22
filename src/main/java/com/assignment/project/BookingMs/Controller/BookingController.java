package com.assignment.project.BookingMs.Controller;

import com.assignment.project.BookingMs.Model.Booking;
import com.assignment.project.BookingMs.Model.BookingRequest;
import com.assignment.project.BookingMs.Model.Bus;
import com.assignment.project.BookingMs.Service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookingApi")
@Slf4j
public class BookingController {

    Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService service;

    @PostMapping("/bookingRequest")
    public ResponseEntity<String> doBooking(@RequestBody Booking booking){
        logger.debug("Inside BookingController.doBooking for the booking request: {}", booking.toString());
        if(service.doBooking(booking) != null) {
            return ResponseEntity.ok(service.doBooking(booking));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/getBusNumber/{bookingNumber}")
    public ResponseEntity<Long> getBusNumber(@PathVariable String bookingNumber){
        if(service.getBusNumber(bookingNumber) != null) {
            return ResponseEntity.ok(service.getBusNumber(bookingNumber));
        }
        return ResponseEntity.notFound().build();
    }

}
