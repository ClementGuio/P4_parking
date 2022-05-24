package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.NoSuchElementException; 

public class InputReaderUtil {

    private static Scanner scan = new Scanner(System.in);
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");

    public int readSelection(){
        int input = -1;
    	try {
            input = Integer.parseInt(scan.nextLine());
        }catch(NumberFormatException ne){
            logger.error("Error while reading user input from Shell", ne);
            System.out.println("Error reading input. Please enter valid number for proceeding further");
        }
        return input;
    }
 
    public String readVehicleRegistrationNumber(){
        String vehicleRegNumber = "";
    	try {
            vehicleRegNumber= scan.nextLine();
            if(vehicleRegNumber == null || vehicleRegNumber.trim().length()==0) {//verifier caractères spéciaux
                throw new IllegalArgumentException("Invalid input provided");
            }
        }catch(IllegalStateException ie) {
        	logger.error("Error while reading user input from Shell", ie);
        	System.out.println("Error reading input. Please try again");
    	}catch(NoSuchElementException ne){
            logger.error("Error while reading user input from Shell", ne);
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
    	}catch (IllegalArgumentException iae) {
    		logger.error("Error while reading user input from shell", iae);
    		System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
    	}
    	return vehicleRegNumber;
    }


}
