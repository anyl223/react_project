package com.gis.gis.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class OtpLimiter {
  private static final int OTP_COUNT_LIMIT = 3;
  private static final int OTP_RESET_DURATION_MINS = 24 * 60;
  @Autowired
  private ApplicationContext appContext;

  public boolean shouldSendOtp(String username, String otp) {
    DataSource ds = (DataSource) appContext.getBean("dataSource");

    try (Connection conn = ds.getConnection()) {
      conn.setAutoCommit(false);
      PreparedStatement stmt =
          conn.prepareStatement("SELECT * FROM users WHERE username = ? FOR UPDATE",
              ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
      stmt.setString(1, username);
      ResultSet results = stmt.executeQuery();
      if (!results.next()) {
        return false;
      }

      String otpFromDb = results.getString("otp");
      int otpCount = results.getInt("otp_count");
      Long otpTimestamp = results.getLong("otp_timestamp");

      if (otpTimestamp == null || minutesElapsedSince(otpTimestamp) >= OTP_RESET_DURATION_MINS) {
        results.updateString("otp", otp);
        results.updateInt("otp_count", 1);
        results.updateLong("otp_timestamp", System.currentTimeMillis());
      } else if (otpCount < OTP_COUNT_LIMIT) {
        results.updateString("otp", otp);
        results.updateInt("otp_count", otpCount + 1);
      } else {
        System.out.println("most recent OTP: " + otpFromDb);
        conn.rollback();
        return false;
      }

      results.updateRow();
      conn.commit();
      return true;
    } catch (Exception ex) {
      System.err.println("--- Caught error in OTP limiter ---");
      ex.printStackTrace();
      System.err.println("--- x ---");
      return false;
    }

  }

  private static long minutesElapsedSince(Long timestamp) throws ParseException {
    return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - timestamp);
  }
}

