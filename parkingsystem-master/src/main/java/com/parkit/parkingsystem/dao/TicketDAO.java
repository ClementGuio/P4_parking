package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;


public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean isRecurrentUser(Ticket ticket) throws ClassNotFoundException, SQLException{
    	Connection con = null;
    	try {
    		con = dataBaseConfig.getConnection();
    		PreparedStatement ps = con.prepareStatement(DBConstants.GET_RECURRENT_TICKET);
    		ps.setString(1,ticket.getVehicleRegNumber());
    		ResultSet rs = ps.executeQuery();
    		return rs.next();
    	}catch(SQLException se) {
    		logger.error("Error fetching ticket from recurrent user");
    		throw se;
    	}catch(ClassNotFoundException ce) {
    		logger.error("Error fetching ticket from recurrent user");
    		throw ce;
    	}
    }
    
    public boolean hasTicketWithoutOutTime(String vehicleRegNumber) throws SQLException, ClassNotFoundException  {
    	Connection con = null; 
    	try {
    		con = dataBaseConfig.getConnection();
    		PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET_WITHOUT_OUT_TIME);
    		ps.setString(1,vehicleRegNumber);
    		ResultSet rs = ps.executeQuery();
    		return rs.next();
    	}catch(SQLException se) {
    		logger.error("Error fetching ticket without out time");
    		throw se;
    	}catch(ClassNotFoundException ce) {
    		logger.error("Error fetching ticket without out time");
    		throw ce;
    	}finally {
    		dataBaseConfig.closeConnection(con);
    	}
    }
    
    public boolean saveTicket(Ticket ticket) throws SQLException, ClassNotFoundException{
        Connection con = null;
        try {
        	con = dataBaseConfig.getConnection();
        	PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
        	//Fields : ID (automatic increment), PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME
        	ps.setInt(1,ticket.getParkingSpot().getId());
        	ps.setString(2, ticket.getVehicleRegNumber());
        	ps.setDouble(3, ticket.getPrice());
        	ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
        	ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : Timestamp.valueOf(ticket.getOutTime()));
        	return ps.execute();
        }catch(SQLException se) {
        	logger.error("Error saving ticket");
        	throw se;
        }catch(ClassNotFoundException ce) {
        	logger.error("Error saving ticket");
        	throw ce;
        }finally {
        	dataBaseConfig.closeConnection(con);
        }
    }

    public Ticket getTicket(String vehicleRegNumber) throws ClassNotFoundException, SQLException{
        Ticket ticket = null;
        Connection con = null;
        try {
        	con = dataBaseConfig.getConnection();
        	PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
        	//Fields : ID (automatic increment), PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME
        	ps.setString(1,vehicleRegNumber);
        	ResultSet rs = ps.executeQuery();
        	if(rs.next()){
        		ticket = new Ticket();
        		ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
        		ticket.setParkingSpot(parkingSpot);
        		ticket.setId(rs.getInt(2));
        		ticket.setVehicleRegNumber(vehicleRegNumber);
        		ticket.setPrice(rs.getDouble(3));
        		ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
        		ticket.setOutTime((rs.getTimestamp(5) == null) ? null : rs.getTimestamp(5).toLocalDateTime());
        	}
        	dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch(SQLException se) {
        	logger.error("Error fetching ticket");
        	throw se;
        }catch(ClassNotFoundException ce) {
        	logger.error("Error fetching ticket");
        	throw ce;
        }finally {
        	dataBaseConfig.closeConnection(con);
        }
        
        return ticket;
    }

    public boolean updateTicket(Ticket ticket) throws ClassNotFoundException, SQLException {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (ClassNotFoundException ce){
            logger.error("Error updating ticket",ce);
            throw ce;
        }catch (SQLException se) {
        	logger.error("Error updating ticket",se);
        	throw se;
    	}finally {
            dataBaseConfig.closeConnection(con);
        }
    }
}
