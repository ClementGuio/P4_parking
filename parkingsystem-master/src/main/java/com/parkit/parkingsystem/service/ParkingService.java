package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.time.format.FormatStyle;
import java.time.format.DateTimeFormatter;

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }
    

    public void processIncomingVehicle() {
        try{
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0){
                String vehicleRegNumber = getVehichleRegNumber();
                
                if (ticketDAO.hasUnpaidTicket(vehicleRegNumber)) {
                	System.out.println("You have an unpaid ticket. Please pay your due.");
                	throw new SQLException("There is an unpaid ticket stored in database.");
                }
                
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                LocalDateTime inTime = LocalDateTime.now();
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"+parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber
                		+" is:"+inTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                		+" "+inTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            }
        }catch(ClassNotFoundException ce){
            logger.error("Unable to process incoming vehicle",ce);
        }catch(SQLException se) {
        	logger.error("Unable to process incoming vehicle",se);
        }
    }

    private String getVehichleRegNumber() {
    	String vehicleRegNumber = "";
        System.out.println("Please type the vehicle registration number and press enter key");
        try {
        	vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
        }catch(IllegalArgumentException iae) {
        	logger.error("Invalid input", iae);
        	System.out.println("Please enter a valid string for vehicle registration number");
        }catch(IllegalStateException ise) {
        	logger.error("Error while reading user input from Shell", ise);
        	System.out.println("Error reading input. Please try try again");
        }catch(NoSuchElementException ne){
            logger.error("Error while reading user input from Shell", ne);
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
        }
        return vehicleRegNumber;
        
    }

    public ParkingSpot getNextParkingNumberIfAvailable() throws IllegalArgumentException, NumberFormatException, SQLException, ClassNotFoundException{
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;
        
        ParkingType parkingType = getVehichleType();
        parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
        if(parkingNumber > 0){
            parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
        }else{
         	System.out.println("Parking lot is full.");
            throw new SQLException("Error fetching parking number from DB. Parking slots might be full");
        }
        
        return parkingSpot;
    }

    private ParkingType getVehichleType() throws IllegalArgumentException, NumberFormatException{
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }
    
    public void processExitingVehicle() {
        try{
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            LocalDateTime outTime = LocalDateTime.now();
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket,ticketDAO.isRecurrentUser(ticket));
            if(ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() 
                	+" is:"+outTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        			+" "+outTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            }else{
                System.out.println("Unable to update ticket information. Error occurred");
            }
        }catch(ClassNotFoundException ce){
            logger.error("Unable to process exiting vehicle",ce);
        }catch(SQLException se){
            logger.error("Unable to process exiting vehicle",se);
        }catch(IllegalArgumentException iae){
            logger.error("Unable to process exiting vehicle",iae);
        }catch(IllegalStateException ise){
            logger.error("Unable to process exiting vehicle",ise);
        }catch(NoSuchElementException ne){
            logger.error("Unable to process exiting vehicle",ne);
        }catch(NullPointerException ne) {
        	logger.error("Unable to process exiting vehicle", ne);
        }
    }
}
