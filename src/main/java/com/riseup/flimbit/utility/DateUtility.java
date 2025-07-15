package com.riseup.flimbit.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtility {
	public static Timestamp getTimeStampFromText(String timestampString) {
		// String timestampString = "2023-10-26 14:30:00.123";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		try {
			// Parse the string into a LocalDateTime
			LocalDateTime localDateTime = LocalDateTime.parse(timestampString, formatter);

			// Convert LocalDateTime to java.sql.Timestamp
			Timestamp sqlTimestamp = Timestamp.valueOf(localDateTime);
           System.out.println("tst" + sqlTimestamp);
			return sqlTimestamp;
		} catch (Exception e) {
			System.err.println("Error converting timestamp: " + e.getMessage());
		}
		return null;
	}

	public static boolean isOfferWithinShareRange(Timestamp offerStart, Timestamp offerEnd, Timestamp shareStart,
			Timestamp shareEnd) {
		
	   
		return (!offerStart.before(shareStart) && !offerStart.after(shareEnd) && !offerEnd.before(shareStart)
				&& !offerEnd.after(shareEnd));
	}
}
