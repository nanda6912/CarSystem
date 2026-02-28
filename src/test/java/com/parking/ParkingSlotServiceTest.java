package com.parking;

import com.parking.entity.ParkingSlot;
import com.parking.entity.ParkingSlot.SlotStatus;
import com.parking.repository.ParkingSlotRepository;
import com.parking.service.ParkingSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSlotServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @InjectMocks
    private ParkingSlotService parkingSlotService;

    private ParkingSlot testSlot;

    @BeforeEach
    void setUp() {
        testSlot = new ParkingSlot("G1", "Ground", SlotStatus.EMPTY);
    }

    @Test
    void testGetAllSlots() {
        List<ParkingSlot> slots = Arrays.asList(testSlot);
        when(parkingSlotRepository.findAll()).thenReturn(slots);

        List<ParkingSlot> result = parkingSlotService.getAllSlots();

        assertEquals(1, result.size());
        assertEquals("G1", result.get(0).getSlotId());
        verify(parkingSlotRepository).findAll();
    }

    @Test
    void testGetAvailableSlots() {
        List<ParkingSlot> availableSlots = Arrays.asList(testSlot);
        when(parkingSlotRepository.findByStatus(SlotStatus.EMPTY)).thenReturn(availableSlots);

        List<ParkingSlot> result = parkingSlotService.getAvailableSlots();

        assertEquals(1, result.size());
        assertEquals(SlotStatus.EMPTY, result.get(0).getStatus());
        verify(parkingSlotRepository).findByStatus(SlotStatus.EMPTY);
    }

    @Test
    void testBookSlot_Success() {
        when(parkingSlotRepository.findBySlotIdAndStatus("G1", SlotStatus.EMPTY))
                .thenReturn(Optional.of(testSlot));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenReturn(testSlot);

        boolean result = parkingSlotService.bookSlot("G1");

        assertTrue(result);
        verify(parkingSlotRepository).findBySlotIdAndStatus("G1", SlotStatus.EMPTY);
        verify(parkingSlotRepository).save(testSlot);
    }

    @Test
    void testBookSlot_Failure() {
        when(parkingSlotRepository.findBySlotIdAndStatus("G1", SlotStatus.EMPTY))
                .thenReturn(Optional.empty());

        boolean result = parkingSlotService.bookSlot("G1");

        assertFalse(result);
        verify(parkingSlotRepository).findBySlotIdAndStatus("G1", SlotStatus.EMPTY);
        verify(parkingSlotRepository, never()).save(any());
    }
}
