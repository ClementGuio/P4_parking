package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParkingSpotDAO {
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public int getNextAvailableSlot(ParkingType parkingType) throws ClassNotFoundException, SQLException{
    	int result=-1;
    	Connection con = null;
        try {
        	con = dataBaseConfig.getConnection();
        	PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
        	ps.setString(1, parkingType.toString());
        	ResultSet rs = ps.executeQuery();
        	if(rs.next()){
        		result = rs.getInt(1);
        	}
        	dataBaseConfig.closeResultSet(rs);
        	dataBaseConfig.closePreparedStatement(ps);
        }catch(SQLException se) {
        	logger.error("Error fetching next available parking spot");
        	throw se;
        }catch(ClassNotFoundException ce) {
        	logger.error("Error fetching next available parking spot");
        	throw ce;
        }finally {
        	dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    public boolean updateParking(ParkingSpot parkingSpot) throws ClassNotFoundException, SQLException{
        Connection con = null;
    	try {
    		con = dataBaseConfig.getConnection();
    		PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
    		ps.setBoolean(1, parkingSpot.isAvailable());
    		ps.setInt(2, parkingSpot.getId());
    		int updateRowCount = ps.executeUpdate();
    		dataBaseConfig.closePreparedStatement(ps);
    		dataBaseConfig.closeConnection(con);	
    		return (updateRowCount == 1);
    	}catch(ClassNotFoundException ce) {
    		logger.error("Error updating parking spot");
    		throw ce;
    	}catch(SQLException se) {
    		logger.error("Error updating parking spot");
    		throw se;
    	}finally {
    		dataBaseConfig.closeConnection(con);
    	}
    }
    
}
