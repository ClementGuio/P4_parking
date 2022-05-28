package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.lang.Math;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }
    
    @Test
    public void calculateFareCar(){
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(60);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals(Math.round(Fare.CAR_RATE_PER_HOUR * 100) / 100.0,ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarDiscount() {
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,true);
        assertEquals(Math.round(0.95 * Fare.CAR_RATE_PER_HOUR * 100) / 100.0,ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(35);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals(Math.round(0.75 * Fare.CAR_RATE_PER_HOUR * 100) / 100.0, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(26);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals( Math.round(26 * Fare.CAR_RATE_PER_HOUR * 100) / 100.0, ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithLessThanHalfHour() {
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(30);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals( 0 , ticket.getPrice());
    }    
    
    @Test
    public void calculateFareBike(){
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals(Math.round(Fare.BIKE_RATE_PER_HOUR * 100) / 100.0, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeDiscount(){
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,true);
        assertEquals(Math.round(0.95 * Fare.BIKE_RATE_PER_HOUR * 100) / 100.0,ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals(Math.round(0.75 * Fare.BIKE_RATE_PER_HOUR * 100) / 100.0, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithLessThanHalfHour() {
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(30);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket,false);
        assertEquals( 0 , ticket.getPrice());
    }    
    
    @Test
    public void calculateFareNullType(){
    	LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket,false));
    }
    
    @Test
    public void calculateFareBikeWithFutureInTime(){
    	LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket,false));
    }
    
}
