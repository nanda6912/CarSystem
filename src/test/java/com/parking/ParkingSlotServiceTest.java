package com.parking;

import com.parking.entity.ParkingSlot;
import com.parking.repository.ParkingSlotRepository;
import com.parking.service.ParkingSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ParkingSlotService
 * Tests the core parking slot functionality
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ParkingSlotServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    private ParkingSlotService parkingSlotService;

    @BeforeEach
    void setUp() {
        parkingSlotService = new ParkingSlotService(parkingSlotRepository);
    }

    @Test
    void testGetAllSlots() {
        // Arrange
        ParkingSlot slot1 = new ParkingSlot("GA001", "Ground", ParkingSlot.SlotStatus.EMPTY);
        ParkingSlot slot2 = new ParkingSlot("GA002", "Ground", ParkingSlot.SlotStatus.OCCUPIED);
        List<ParkingSlot> expectedSlots = Arrays.asList(slot1, slot2);
        
        when(parkingSlotRepository.findAll()).thenReturn(expectedSlots);

        // Act
        List<ParkingSlot> actualSlots = parkingSlotService.getAllSlots();

        // Assert
        assertNotNull(actualSlots);
        assertEquals(2, actualSlots.size());
        assertEquals("GA001", actualSlots.get(0).getSlotId());
        assertEquals("GA002", actualSlots.get(1).getSlotId());
        verify(parkingSlotRepository, times(1)).findAll();
    }

    @Test
    void testGetSlotById() {
        // Arrange
        String slotId = "GA001";
        ParkingSlot expectedSlot = new ParkingSlot(slotId, "Ground", ParkingSlot.SlotStatus.EMPTY);
        
        when(parkingSlotRepository.findById(slotId)).thenReturn(Optional.of(expectedSlot));

        // Act
        Optional<ParkingSlot> actualSlot = parkingSlotService.getSlotById(slotId);

        // Assert
        assertTrue(actualSlot.isPresent());
        assertEquals(slotId, actualSlot.get().getSlotId());
        assertEquals("Ground", actualSlot.get().getFloor());
        assertEquals(ParkingSlot.SlotStatus.EMPTY, actualSlot.get().getStatus());
        verify(parkingSlotRepository, times(1)).findById(slotId);
    }

    @Test
    void testGetSlotByIdNotFound() {
        // Arrange
        String slotId = "INVALID";
        
        when(parkingSlotRepository.findById(slotId)).thenReturn(Optional.empty());

        // Act
        Optional<ParkingSlot> actualSlot = parkingSlotService.getSlotById(slotId);

        // Assert
        assertFalse(actualSlot.isPresent());
        verify(parkingSlotRepository, times(1)).findById(slotId);
    }

    @Test
    void testBookSlot() {
        // Arrange
        String slotId = "GA001";
        ParkingSlot existingSlot = new ParkingSlot(slotId, "Ground", ParkingSlot.SlotStatus.EMPTY);
        
        when(parkingSlotRepository.findBySlotIdAndStatus(slotId, ParkingSlot.SlotStatus.EMPTY))
            .thenReturn(Optional.of(existingSlot));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenReturn(existingSlot);

        // Act
        boolean result = parkingSlotService.bookSlot(slotId);

        // Assert
        assertTrue(result);
        verify(parkingSlotRepository, times(1)).findBySlotIdAndStatus(slotId, ParkingSlot.SlotStatus.EMPTY);
        verify(parkingSlotRepository, times(1)).save(existingSlot);
    }

    @Test
    void testBookSlotAlreadyOccupied() {
        // Arrange
        String slotId = "GA001";
        
        when(parkingSlotRepository.findBySlotIdAndStatus(slotId, ParkingSlot.SlotStatus.EMPTY))
            .thenReturn(Optional.empty());

        // Act
        boolean result = parkingSlotService.bookSlot(slotId);

        // Assert
        assertFalse(result);
        verify(parkingSlotRepository, times(1)).findBySlotIdAndStatus(slotId, ParkingSlot.SlotStatus.EMPTY);
        verify(parkingSlotRepository, never()).save(any());
    }

    @Test
    void testReleaseSlot() {
        // Arrange
        String slotId = "GA001";
        ParkingSlot existingSlot = new ParkingSlot(slotId, "Ground", ParkingSlot.SlotStatus.OCCUPIED);
        
        when(parkingSlotRepository.findById(slotId)).thenReturn(Optional.of(existingSlot));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenReturn(existingSlot);

        // Act
        boolean result = parkingSlotService.releaseSlot(slotId);

        // Assert
        assertTrue(result);
        verify(parkingSlotRepository, times(1)).findById(slotId);
        verify(parkingSlotRepository, times(1)).save(existingSlot);
    }

    @Test
    void testReleaseSlotNotFound() {
        // Arrange
        String slotId = "INVALID";
        
        when(parkingSlotRepository.findById(slotId)).thenReturn(Optional.empty());

        // Act
        boolean result = parkingSlotService.releaseSlot(slotId);

        // Assert
        assertFalse(result);
        verify(parkingSlotRepository, times(1)).findById(slotId);
        verify(parkingSlotRepository, never()).save(any());
    }

    @Test
    void testGetAvailableSlots() {
        // Arrange
        List<ParkingSlot> expectedSlots = Arrays.asList(
            new ParkingSlot("GA001", "Ground", ParkingSlot.SlotStatus.EMPTY),
            new ParkingSlot("GA002", "Ground", ParkingSlot.SlotStatus.EMPTY)
        );
        
        when(parkingSlotRepository.findByStatus(ParkingSlot.SlotStatus.EMPTY)).thenReturn(expectedSlots);

        // Act
        List<ParkingSlot> actualSlots = parkingSlotService.getAvailableSlots();

        // Assert
        assertNotNull(actualSlots);
        assertEquals(2, actualSlots.size());
        actualSlots.forEach(slot -> assertEquals(ParkingSlot.SlotStatus.EMPTY, slot.getStatus()));
        verify(parkingSlotRepository, times(1)).findByStatus(ParkingSlot.SlotStatus.EMPTY);
    }

    @Test
    void testGetSlotsByFloor() {
        // Arrange
        String floor = "Ground";
        List<ParkingSlot> expectedSlots = Arrays.asList(
            new ParkingSlot("GA001", floor, ParkingSlot.SlotStatus.EMPTY),
            new ParkingSlot("GA002", floor, ParkingSlot.SlotStatus.OCCUPIED)
        );
        
        when(parkingSlotRepository.findByFloor(floor)).thenReturn(expectedSlots);

        // Act
        List<ParkingSlot> actualSlots = parkingSlotService.getSlotsByFloor(floor);

        // Assert
        assertNotNull(actualSlots);
        assertEquals(2, actualSlots.size());
        actualSlots.forEach(slot -> assertEquals(floor, slot.getFloor()));
        verify(parkingSlotRepository, times(1)).findByFloor(floor);
    }

    @Test
    void testGetAvailableSlotCount() {
        // Arrange
        long expectedCount = 5L;
        
        when(parkingSlotRepository.countByStatus(ParkingSlot.SlotStatus.EMPTY)).thenReturn(expectedCount);

        // Act
        long actualCount = parkingSlotService.getAvailableSlotCount();

        // Assert
        assertEquals(expectedCount, actualCount);
        verify(parkingSlotRepository, times(1)).countByStatus(ParkingSlot.SlotStatus.EMPTY);
    }

    @Test
    void testGetOccupiedSlotCount() {
        // Arrange
        long expectedCount = 3L;
        
        when(parkingSlotRepository.countByStatus(ParkingSlot.SlotStatus.OCCUPIED)).thenReturn(expectedCount);

        // Act
        long actualCount = parkingSlotService.getOccupiedSlotCount();

        // Assert
        assertEquals(expectedCount, actualCount);
        verify(parkingSlotRepository, times(1)).countByStatus(ParkingSlot.SlotStatus.OCCUPIED);
    }
}
