package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.sql.SQLException;

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

    @BeforeEach
    private void setUpPerTest() throws Exception {
        /*when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        */dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterEach 
    private void clearDB() {
    	dataBasePrepareService.clearDataBaseEntries();
    }
    
    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ParkingCar");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        
      //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket ticket = ticketDAO.getTicket("ParkingCar");
        int parkingNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        
        assertNotNull(ticket);
        assertEquals(ticket.getParkingSpot().getId(),parkingNumber-1);
        
    }

    @Test
    public void testParkingLotExit() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("CarExit");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        
        parkingService.processExitingVehicle();
        
        //TODO: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("CarExit");
        double price = ticket.getPrice();
        LocalDateTime outTime = ticket.getOutTime();
        
        assertEquals(price,0);
        assertNotNull(outTime);
    }
    
    @Test
    public void testParkingCarTwice() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("SameCar").thenReturn("SameCar");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        
        assertThrows(SQLException.class, () -> parkingService.processIncomingVehicle(),"There is a ticket without out time stored in database.");
    }
    
    @Test
    public void testParkingLotFull() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("Car1").thenReturn("Car2").thenReturn("Car3").thenReturn("TooMuchCar");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        for (int i=0;i<3;i++) {
        	parkingService.processIncomingVehicle();
        }
        
        assertThrows(SQLException.class, () -> parkingService.processIncomingVehicle(), "Error fetching parking number from DB. Parking slots might be full");
        
        
    }
    
    @Test
    public void testParkingLotExitUnknownCar() throws Exception{
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("Unknown");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        assertThrows(SQLException.class, () -> parkingService.processExitingVehicle(), "Error fetching parking number from DB. Parking slots might be full");
    }
    
    

}
