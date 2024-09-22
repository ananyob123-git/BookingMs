package com.assignment.project.BookingMs.repository;

import com.assignment.project.BookingMs.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepo extends JpaRepository<Booking,String> {
}
