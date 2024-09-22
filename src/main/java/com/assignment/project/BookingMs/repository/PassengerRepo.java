package com.assignment.project.BookingMs.repository;

import com.assignment.project.BookingMs.Model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepo extends JpaRepository<Passenger,Long> {
}
