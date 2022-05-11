package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.dao.TicketDAO;

import java.lang.Math;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean isRecurrent){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTimeInMillis();
        double outHour = ticket.getOutTime().getTimeInMillis();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = ((outHour - inHour)/1000)/3600;
        duration = Math.round(duration*100.0)/100.0;
        double quarters = (duration/0.25)%1==0 ? duration/0.25 : (duration/0.25) + 1;
         
        double price = 0;
        if (quarters>2.0) {
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
        ticket.setPrice(price);
    }
}