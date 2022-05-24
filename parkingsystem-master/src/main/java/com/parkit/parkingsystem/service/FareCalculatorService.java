package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.lang.Math;
import java.time.temporal.ChronoUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean isRecurrent) throws IllegalArgumentException{
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long duration = ticket.getInTime().until(ticket.getOutTime(), ChronoUnit.MINUTES);
        long quarters = (duration%15)==0 ? duration/15 : (duration/15) + 1; 
        double price = 0;
        
        if (quarters>2) {
        	switch (ticket.getParkingSpot().getParkingType()){
            	case CAR: {
            		price = quarters * Fare.CAR_RATE_PER_QUARTER;
            		break;
            	}
            	case BIKE: {
            		price = quarters * Fare.BIKE_RATE_PER_QUARTER;
            		break;
            	}
            	default: throw new IllegalArgumentException("Unkown Parking Type");
            	}
        	if (isRecurrent) {
        		price *= 0.95;
        	}
        }
        ticket.setPrice(Math.round(price*100)/100.0);
    }
}