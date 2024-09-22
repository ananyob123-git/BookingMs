package com.assignment.project.BookingMs.Service;

import com.assignment.project.BookingMs.Constants.BookingConstants;
import com.assignment.project.BookingMs.Model.Booking;
import com.assignment.project.BookingMs.Model.Bus;
import com.assignment.project.BookingMs.Model.Passenger;
import com.assignment.project.BookingMs.exception.BookingException;
import com.assignment.project.BookingMs.exception.ResourceNotFoundException;
import com.assignment.project.BookingMs.repository.BookingRepo;
import com.assignment.project.BookingMs.repository.PassengerRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static com.assignment.project.BookingMs.Constants.BookingConstants.PAYMENT_TOPIC;

@Service
@Slf4j
public class BookingService {

    Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private PassengerRepo passengerRepo;

    @Autowired
    private WebClient webClient;

    @Autowired
    private KafkaTemplate<String, Bus> kafkaTemplate;

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "getAvailableSeatsFallback")
    @Retry(name = "inventoryRetry", fallbackMethod = "getAvailableSeatsFallback")
    @RateLimiter(name = "inventoryRateLimiter")
    private Integer getAvailableSeats(Long busNumber) {
        return webClient
                .get()
                .uri("/inventoryApi/getAvailableSeats/" + busNumber)
                .retrieve()
                .bodyToMono(Integer.class)
                .block();
    }

    public String doBooking(Booking booking) {
        logger.info("Inside BookingService.doBooking with request: {}", booking.toString());
        /*AtomicReference<Integer> availableSeatsSub = new AtomicReference<>();

                .subscribe(
                        availableSeatsSub::set,
                        ex -> {
                            if (ex instanceof WebClientResponseException.NotFound) {
                                logger.error("The Inventory service is not found");
                                throw new ResourceNotFoundException("Inventory service not found for busNumber: " + booking.getBusNumber());
                            } else {
                                throw new BookingException("Error occurred while retrieving data from Inventory MS: " + ex.getMessage());
                            }
                        }
                );*/
        Integer availableSeats = getAvailableSeats(booking.getBusNumber());
        logger.info("Available seats from inventory obtained: {}",availableSeats);
        if (availableSeats.equals(booking.getNumberOfSeats()) || availableSeats > booking.getNumberOfSeats()) {
            booking.setStatus(BookingConstants.PENDING);
            bookingRepo.save(booking);
            Bus bus = new Bus(booking.getBookingNumber(), booking.getNumberOfSeats());
            kafkaTemplate.send(PAYMENT_TOPIC, bus);
            return booking.toString();
        } else {
            log.error("booking cannot be done as no available seats");
            throw new BookingException("Booking cannot be done as no available seats");
        }
    }

    @KafkaListener(topics = "booking-confirmation", groupId = "booking-consumer-group")
    public void listenForBookingConfirmation(String bookingNumber) {
        log.debug("Received booking confirmation message from Kafka: {}", bookingNumber);
        if (bookingRepo.findById(bookingNumber).isPresent()) {
            Booking booking = bookingRepo.findById(bookingNumber).get();
            booking.setStatus(BookingConstants.CONFIRMED);
            Passenger passenger = new Passenger();
            passenger.setBookingNumber(booking.getBookingNumber());
            passengerRepo.save(passenger);
            bookingRepo.save(booking);
        } else {
            log.error("Booking not found for booking number: {}", bookingNumber);
            throw new BookingException("Booking not found for booking number: " + bookingNumber);
        }
    }

    public Long getBusNumber(String bookingNumber) {
        if (bookingRepo.findById(bookingNumber).isPresent()) {
            return bookingRepo.findById(bookingNumber).get().getBusNumber();
        }
        return null;
    }

    public void cancelBooking(String bookingNumber) {
        Booking booking = bookingRepo.findById(bookingNumber)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        booking.setStatus("CANCELLED");
        bookingRepo.save(booking);
    }

    // Fallback method in case of circuit breaker failure
    public void getAvailableSeatsFallback(Long busNumber, Throwable ex) {
        logger.error("Fallback triggered due to: " + ex.getMessage());
        if (ex instanceof WebClientResponseException.NotFound) {
            throw new ResourceNotFoundException("Inventory service not found for busNumber: " + busNumber);
        } else {
            throw new BookingException("Error occurred while retrieving data from Inventory MS: " + ex.getMessage());
        }
    }
}