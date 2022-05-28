package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    
	private static final Logger logger = LogManager.getLogger("App");
    
	public static void main(String args[]){
        logger.info("Initializing Parking System");
        try {
        	InteractiveShell.loadInterface();
        }catch(SQLException se) {
        	logger.error("Error concerning database", se);
        }catch(ClassNotFoundException ce) {
        	logger.error("Error getting connection", ce);
        }catch(NumberFormatException ne) {
        	logger.error("Error typing numbers", ne);
        }catch(IllegalStateException ise) {
        	logger.error("Error concerning scanner", ise);
        }catch(IllegalArgumentException iae) {
        	logger.error("Error concerning bad arguments", iae);
        }catch(NoSuchElementException ne) {
        	logger.error("Error concerning entries", ne);
        }
        
    }
}
