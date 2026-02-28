package com.parking.repository;

import com.parking.entity.ParkingSlot;
import com.parking.entity.ParkingSlot.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, String> {
    
    List<ParkingSlot> findByStatus(SlotStatus status);
    
    List<ParkingSlot> findByFloor(String floor);
    
    List<ParkingSlot> findByFloorAndStatus(String floor, SlotStatus status);
    
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.slotId = :slotId AND ps.status = :status")
    Optional<ParkingSlot> findBySlotIdAndStatus(@Param("slotId") String slotId, @Param("status") SlotStatus status);
    
    @Query("SELECT COUNT(ps) FROM ParkingSlot ps WHERE ps.status = :status")
    long countByStatus(@Param("status") SlotStatus status);
}
