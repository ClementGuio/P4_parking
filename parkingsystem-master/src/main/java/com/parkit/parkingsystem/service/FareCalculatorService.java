package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.lang.Math;
import java.time.temporal.ChronoUnit;

public class FareCalculatorService {

	public static final double BIKE_RATE_PER_QUARTER = Fare.BIKE_RATE_PER_HOUR / 4;
    public static final double CAR_RATE_PER_QUARTER = Fare.CAR_RATE_PER_HOUR / 4;
	
    public void calculateFare(Ticket ticket, boolean isRecurrent) throws IllegalArgumentException{
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        } 
        //Compute duration in quarters
        long duration = ticket.getInTime().until(ticket.getOutTime(), ChronoUnit.MINUTES);
        long quarters = (duration%15)==0 ? duration/15 : (duration/15) + 1; 
        double price = 0;
        //Check if more than half hour, else let price to 0
        if (quarters>2) {
        	switch (ticket.getParkingSpot().getParkingType()){
            	case CAR: {
            		price = quarters * CAR_RATE_PER_QUARTER;
            		break;
            	}
            	case BIKE: {
            		price = quarters * BIKE_RATE_PER_QUARTER;
            		break;
            	}
            	default: throw new IllegalArgumentException("Unkown Parking Type");
            	}
        	//Discount for recurrent users
        	if (isRecurrent) {
        		price *= 0.95;
        	}
        }
        //Round price and set ticket
        ticket.setPrice(Math.round(price*100)/100.0);
    }
}