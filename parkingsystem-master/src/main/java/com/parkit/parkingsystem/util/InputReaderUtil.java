package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.NoSuchElementException; 

public class InputReaderUtil {

    private static Scanner scan = new Scanner(System.in);
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");

    public int readSelection() throws NumberFormatException{
        int input = -1;
    	try {
            input = Integer.parseInt(scan.nextLine());
        }catch(NumberFormatException ne){
            logger.error("Error while reading user input from Shell");
            System.out.println("Error reading input. Please enter valid number for proceeding further");
            throw ne;
        }
        return input;
    }
 
    public String readVehicleRegistrationNumber() throws IllegalStateException, IllegalArgumentException, NoSuchElementException{
        String vehicleRegNumber = "";
    	try {
            vehicleRegNumber= scan.nextLine();
            if(vehicleRegNumber == null || vehicleRegNumber.trim().length()==0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
        }catch(IllegalStateException ie) {
        	logger.error("Error while reading user input from Shell");
        	System.out.println("Error reading input. Please try again");
        	throw ie;
    	}catch(NoSuchElementException ne){
            logger.error("Error while reading user input from Shell");
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
            throw ne;
    	}catch (IllegalArgumentException iae) {
    		logger.error("Error while reading user input from shell");
    		System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
    		throw iae;
    	}
    	return vehicleRegNumber;
    }


}
