package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.lang.Thread;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @AfterEach 
    private void clearDB() {
    	dataBasePrepareService.clearDataBaseEntries();
    }
    
    @Test
    public void testParkingACar() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ParkingCar");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket("ParkingCar");
        int parkingNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        //Assert
        assertNotNull(ticket);
        assertEquals(ticket.getParkingSpot().getId(),parkingNumber-1);
        
    }

    @Test
    public void testParkingLotCarExit() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("CarExit");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act
        parkingService.processIncomingVehicle();
        Thread.sleep(1500);
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("CarExit");
        double price = ticket.getPrice();
        LocalDateTime outTime = ticket.getOutTime();
        //Assert
        assertEquals(price,0);
        assertNotNull(outTime);
    }
    
    @Test
    public void testParkingCarTwice() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("SameCar").thenReturn("SameCar");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act
        parkingService.processIncomingVehicle();
        //Assert
        assertThrows(SQLException.class, () -> parkingService.processIncomingVehicle(),"There is a ticket without out time stored in database.");
    }
    
    @Test
    public void testParkingLotFull() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("Car1").thenReturn("Car2").thenReturn("Car3").thenReturn("TooMuchCar");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act
        for (int i=0;i<3;i++) {
        	parkingService.processIncomingVehicle();
        }
        //Assert
        assertThrows(SQLException.class, () -> parkingService.processIncomingVehicle(), "Error fetching parking number from DB. Parking slots might be full");    
    }
    
    @Test
    public void testParkingLotExitUnknownCar() throws Exception{
    	//Arrange
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("Unknown");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act-Assert
        assertThrows(SQLException.class, () -> parkingService.processExitingVehicle(), "Error fetching parking number from DB. Parking slots might be full");
    }
    
    @Test
    public void testParkingBike() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(2);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ParkingBik");
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act
    	parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket("ParkingBik");
        int parkingNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
        //Assert
        assertNotNull(ticket);
        assertEquals(ticket.getParkingSpot().getId(),parkingNumber-1);
    }
    
    @Test
    public void testParkingLotBikeExit() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("BikeExit");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act
        parkingService.processIncomingVehicle();
        Thread.sleep(1500);
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("BikeExit");
        double price = ticket.getPrice();
        LocalDateTime outTime = ticket.getOutTime();
        //Assert
        assertEquals(price,0);
        assertNotNull(outTime);
    }
    
    @Test
    public void testParkingUnkownVehicleType() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(4);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Act-Assert
        assertThrows(IllegalArgumentException.class, () -> parkingService.processIncomingVehicle(),"Entered input is invalid");
    }
    
    @Test
    public void testParkingCarAndBikeWitheSameRegNumber() throws Exception{
    	//Arrange
    	when(inputReaderUtil.readSelection()).thenReturn(1).thenReturn(2);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("SameReg");
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    	//Act
    	parkingService.processIncomingVehicle();
    	//Assert
    	assertThrows(SQLException.class, () -> parkingService.processIncomingVehicle(),"There is a ticket without out time stored in database.");
    }

}
