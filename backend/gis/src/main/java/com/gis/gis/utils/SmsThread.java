package com.gis.gis.utils;

public class SmsThread {
  public static void send(String otp, String mobile) {
    new Thread() {
      @Override
      public void run() {
        System.out.println("sending SMS with OTP: " + otp);
        try {
          String smsresponse = new SmsService()
              .sendOtpSMS("Your OTP is " + otp + " - Digital India Corporation", mobile);
          System.out.println("SMS API response: " + smsresponse);
        } catch (Exception e) {
          System.err.println("SMS API error: " + e);
        }
      }
    }.start();
  }
}
