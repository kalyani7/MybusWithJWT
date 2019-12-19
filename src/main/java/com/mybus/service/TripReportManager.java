package com.mybus.service;

import com.mybus.dao.TripReportDAO;
import com.mybus.model.TripReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TripReportManager {

	private static final Logger logger = LoggerFactory.getLogger(TripReportManager.class);

	@Autowired
	private TripReportDAO tripReportDAO;

	/**
	 * Start the TripReport
	 * @param tripReport
	 * @return
	 */
	public TripReport startTripReport(TripReport tripReport){
		if(tripReport.getStartDate() == null) {
			throw new RuntimeException("TripReport missing a startdate");
		}
		if(tripReport.getStatus().equalsIgnoreCase(TripReportStatus.CLOSED.toString())){
			throw new RuntimeException("TripReport is closed");
		}
		tripReport.setStatus(TripReportStatus.STARTED.toString());
		return tripReportDAO.save(tripReport);
	}


	enum TripReportStatus {
		CLOSED,
		CANCELED,
		STARTED;
		@Override
		public String toString() {
			return name();
		}
	}
}
