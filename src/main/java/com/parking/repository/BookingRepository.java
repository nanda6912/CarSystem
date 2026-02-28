package com.parking.repository;

import com.parking.entity.Booking;
import com.parking.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    
    List<Booking> findByStatus(BookingStatus status);
    
    List<Booking> findByParkingSlotSlotId(String slotId);
    
    Optional<Booking> findByBookingIdAndStatus(String bookingId, BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingId = :bookingId")
    Optional<Booking> findByBookingId(@Param("bookingId") String bookingId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    long countByStatus(@Param("status") BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.entryTime BETWEEN :startTime AND :endTime")
    List<Booking> findByEntryTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT b FROM Booking b WHERE b.vehicleNumber = :vehicleNumber AND b.status = :status")
    Optional<Booking> findByVehicleNumberAndStatus(@Param("vehicleNumber") String vehicleNumber, 
                                                   @Param("status") BookingStatus status);
}
