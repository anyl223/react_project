package com.gis.gis.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * @author Mobile Seva < msdp@cdac.in >
 *         <p>
 *         Kindly add require Jar files to run Java client
 *         </p>
 *         <p>
 *         Apache commons-codec-1.9 <br>
 *         Apache commons-httpclient-3.1 <br>
 *         Apache commons-logging-1.2
 * @see <a href="https://mgov.gov.in/doc/RequiredJars.zip">Download required Jar files</a>
 */
public class SmsService {
  /**
   * Send Single text SMS
   *
   * @param username : Department Login User Name
   * @param password : Department Login Password
   * @param message : Message e.g. 'Welcome to mobile Seva'
   * @param senderId : Department allocated SenderID
   * @param mobileNumber : Single Mobile Number e.g. '99XXXXXXX'
   * @param secureKey : Department key generated by login to services portal
   * @return {@link String} response from Mobile Seva Gateway e.g. '402,MsgID =
   *         150620161466003974245msdgsms'
   * @see <a href="https://mgov.gov.in/msdp_sms_push.jsp">Return types code details</a>
   */
  // secureKey : 0008031b-89f6-41d7-a771-c64a077e3e70
  // Username : NCoGSMS
  // Password : NcOg!10#20$
  // Senderid : NCOGIT
  // templetid : 1307160387219360144
  public String sendSingleSMS(String message, String mobileNumber) {
    String username = "NCoGSMS";
    String password = "NcOg!10#20$";
    String senderId = "NCOGIT";
    String secureKey = "0008031b-89f6-41d7-a771-c64a077e3e70";
    String templateid = "1307160387219360144";
    String encryptedPassword;
    String responseString = "";
    try {
      encryptedPassword = MD5(password);
      String genratedhashKey = hashGenerator(username, senderId, message, secureKey);
      List<ImmutablePair<String, String>> nameValuePairs =
          new ArrayList<ImmutablePair<String, String>>(1);
      nameValuePairs.add(ImmutablePair.of("mobileno", mobileNumber));
      nameValuePairs.add(ImmutablePair.of("senderid", senderId));
      nameValuePairs.add(ImmutablePair.of("content", message));
      nameValuePairs.add(ImmutablePair.of("smsservicetype", "singlemsg"));
      nameValuePairs.add(ImmutablePair.of("username", username));
      nameValuePairs.add(ImmutablePair.of("password", encryptedPassword));
      nameValuePairs.add(ImmutablePair.of("key", genratedhashKey));
      nameValuePairs.add(ImmutablePair.of("templateid", templateid));
      ProxyApi.call(nameValuePairs, "https://msdgweb.mgov.gov.in/esms/sendsmsrequestDLT");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return responseString;
  }

  /**
   * 109 Send Bulk text SMS 110
   *
   * @param username : Department Login User Name 111
   * @param password : Department Login Password 112
   * @param message : Message e.g. 'Welcome to mobile Seva' 113
   * @param senderId : Department allocated SenderID 114
   * @param mobileNumber : Bulk Mobile Number with comma separated e.g. '99XXXXXXX,99XXXXXXXX' 115
   * @param secureKey : Department key generated by login to services portal 116
   * @return {@link String} response from Mobile Seva Gateway e.g. '402,MsgID =
   *         150620161466003974245msdgsms' 117
   * @see <a href="https://mgov.gov.in/msdp_sms_push.jsp">Return types code details</a> 118 119
   */
  public String sendBulkSMS(String message, String mobileNumber) {
    String username = "NCoGSMS";
    String password = "NcOg!10#20$";
    String senderId = "NCOGIT";
    String secureKey = "0008031b-89f6-41d7-a771-c64a077e3e70";
    String templateid = "1307160387219360144";
    String responseString = "";
    String encryptedPassword;
    try {
      encryptedPassword = MD5(password);
      String genratedhashKey = hashGenerator(username, senderId, message, secureKey);
      List<ImmutablePair<String, String>> nameValuePairs =
          new ArrayList<ImmutablePair<String, String>>(1);
      nameValuePairs.add(ImmutablePair.of("bulkmobno", mobileNumber));
      nameValuePairs.add(ImmutablePair.of("senderid", senderId));
      nameValuePairs.add(ImmutablePair.of("content", message));
      nameValuePairs.add(ImmutablePair.of("smsservicetype", "bulkmsg"));
      nameValuePairs.add(ImmutablePair.of("username", username));
      nameValuePairs.add(ImmutablePair.of("password", encryptedPassword));
      nameValuePairs.add(ImmutablePair.of("key", genratedhashKey));
      nameValuePairs.add(ImmutablePair.of("templateid", templateid));
      ProxyApi.call(nameValuePairs, "https://msdgweb.mgov.gov.in/esms/sendsmsrequestDLT");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return responseString;
  }

  /**
   * Send Unicode text SMS 175
   *
   * @param username : Department Login User Name 176
   * @param password : Department Login Password 177
   * @param message : Unicode Message e.g. 'Ã Â¤ÂµÃ Â¤Â¿Ã Â¤Â•Ã Â¤Â¾Ã Â¤Â¸ Ã Â¤Â†Ã Â¤Â£Ã Â¤Â¿ Ã
   *        Â¤ÂªÃ Â¥ÂÃ Â¤Â°Ã Â¤Â—Ã Â¤Â¤ Ã Â¤Â¸Ã Â¤Â‚Ã Â¤Â—Ã Â¤Â£Ã Â¤Â¨ Ã Â¤Â•Ã Â¥Â‡Ã Â¤Â‚Ã Â¤Â¦Ã
   *        Â¥ÂÃ Â¤Â° Ã Â¤Â®Ã Â¤Â§Ã Â¥ÂÃ Â¤Â¯Ã Â¥Â‡ Ã Â¤Â¸Ã Â¥ÂÃ Â¤ÂµÃ Â¤Â¾Ã Â¤Â—Ã Â¤Â¤ Ã Â¤Â†Ã
   *        Â¤Â¹Ã Â¥Â‡' 178
   * @param senderId : Department allocated SenderID 179
   * @param mobileNumber : Bulk Mobile Number with comma separated e.g. '99XXXXXXX,99XXXXXXXX' 180
   * @param secureKey : Department key generated by login to services portal 181
   * @return {@link String} response from Mobile Seva Gateway e.g. '402,MsgID =
   *         150620161466003974245msdgsms' 182
   * @see <a href="https://mgov.gov.in/msdp_sms_push.jsp">Return types code details</a> 183 184
   */
  public String sendUnicodeSMS(String message, String mobileNumber) {
    String username = "NCoGSMS";
    String password = "NcOg!10#20$";
    String senderId = "NCOGIT";
    String secureKey = "0008031b-89f6-41d7-a771-c64a077e3e70";
    String templateid = "1307160387219360144";
    String responseString = "";
    String finalmessage = "";
    for (int i = 0; i < message.length(); i++) {
      char ch = message.charAt(i);
      int j = (int) ch;
      String sss = "&#" + j + ";";
      finalmessage = finalmessage + sss;
    }
    String encryptedPassword;
    try {
      encryptedPassword = MD5(password);
      String genratedhashKey = hashGenerator(username, senderId, finalmessage, secureKey);
      List<ImmutablePair<String, String>> nameValuePairs =
          new ArrayList<ImmutablePair<String, String>>(1);
      nameValuePairs.add(ImmutablePair.of("bulkmobno", mobileNumber));
      nameValuePairs.add(ImmutablePair.of("senderid", senderId));
      nameValuePairs.add(ImmutablePair.of("content", finalmessage));
      nameValuePairs.add(ImmutablePair.of("smsservicetype", "unicodemsg"));
      nameValuePairs.add(ImmutablePair.of("username", username));
      nameValuePairs.add(ImmutablePair.of("password", encryptedPassword));
      nameValuePairs.add(ImmutablePair.of("key", genratedhashKey));
      nameValuePairs.add(ImmutablePair.of("templateid", templateid));
      ProxyApi.call(nameValuePairs, "https://msdgweb.mgov.gov.in/esms/sendsmsrequestDLT");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return responseString;
  }

  /**
   * 252 Send Single OTP text SMS 253
   * </NameValuePair></NameValuePair></NameValuePair></NameValuePair></NameValuePair></NameValuePair>
   * </p>
   * <p>
   * Use only in case of OTP related message
   * </p>
   * <p>
   * 254 Messages other than OTP will not be delivered to the kkg_users 255
   *
   * @param username : Department Login User Name 256
   * @param password : Department Login Password 257
   * @param message : Message e.g. 'Welcome to mobile Seva' 258
   * @param senderId : Department allocated SenderID 259
   * @param mobileNumber : Single Mobile Number e.g. '99XXXXXXX' 260
   * @param secureKey : Department key generated by login to services portal 261
   * @return {@link String} response from Mobile Seva Gateway e.g. '402,MsgID =
   *         150620161466003974245msdgsms' 262
   * @see <a href="https://mgov.gov.in/msdp_sms_push.jsp">Return types code details</a> 263 264
   */
  public String sendOtpSMS(String message, String mobileNumber) {
    String username = "NCoGSMS";
    String password = "NcOg!10#20$";
    String senderId = "NCOGIT";
    String secureKey = "0008031b-89f6-41d7-a771-c64a077e3e70";
    String templateid = "1307165847021622765";
    String responseString = "";
    String encryptedPassword;
    try {
      encryptedPassword = MD5(password);
      String genratedhashKey = hashGenerator(username, senderId, message, secureKey);
      List<ImmutablePair<String, String>> nameValuePairs =
          new ArrayList<ImmutablePair<String, String>>(1);
      nameValuePairs.add(ImmutablePair.of("mobileno", mobileNumber));
      nameValuePairs.add(ImmutablePair.of("senderid", senderId));
      nameValuePairs.add(ImmutablePair.of("content", message));
      nameValuePairs.add(ImmutablePair.of("smsservicetype", "otpmsg"));
      nameValuePairs.add(ImmutablePair.of("username", username));
      nameValuePairs.add(ImmutablePair.of("password", encryptedPassword));
      nameValuePairs.add(ImmutablePair.of("key", genratedhashKey));
      nameValuePairs.add(ImmutablePair.of("templateid", templateid));
      responseString =
          ProxyApi.call(nameValuePairs, "https://msdgweb.mgov.gov.in/esms/sendsmsrequestDLT");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return responseString;
  }

  /**
   * 322 Send Single Unicode OTP text SMS 323
   *
   * @param username : Department Login User Name 324
   * @param password : Department Login Password 325
   * @param message : Unicode Message e.g. 'Ã Â¤ÂµÃ Â¤Â¿Ã Â¤Â•Ã Â¤Â¾Ã Â¤Â¸ Ã Â¤Â†Ã Â¤Â£Ã Â¤Â¿ Ã
   *        Â¤ÂªÃ Â¥ÂÃ Â¤Â°Ã Â¤Â—Ã Â¤Â¤ Ã Â¤Â¸Ã Â¤Â‚Ã Â¤Â—Ã Â¤Â£Ã Â¤Â¨ Ã Â¤Â•Ã Â¥Â‡Ã Â¤Â‚Ã Â¤Â¦Ã
   *        Â¥ÂÃ Â¤Â° Ã Â¤Â®Ã Â¤Â§Ã Â¥ÂÃ Â¤Â¯Ã Â¥Â‡ Ã Â¤Â¸Ã Â¥ÂÃ Â¤ÂµÃ Â¤Â¾Ã Â¤Â—Ã Â¤Â¤ Ã Â¤Â†Ã
   *        Â¤Â¹Ã Â¥Â‡' 326
   * @param senderId : Department allocated SenderID 327
   * @param mobileNumber : Bulk Mobile Number with comma separated e.g. '99XXXXXXX,99XXXXXXXX' 328
   * @param secureKey : Department key generated by login to services portal 329
   * @return {@link String} response from Mobile Seva Gateway e.g. '402,MsgID =
   *         150620161466003974245msdgsms' 330
   * @see <a href="https://mgov.gov.in/msdp_sms_push.jsp">Return types code details</a> 331 332
   */
  public String sendUnicodeOtpSMS(String message, String mobileNumber) {
    String username = "NCoGSMS";
    String password = "NcOg!10#20$";
    String senderId = "NCOGIT";
    String secureKey = "0008031b-89f6-41d7-a771-c64a077e3e70";
    String templateid = "1307160387219360144";
    String finalmessage = "";
    for (int i = 0; i < message.length(); i++) {
      char ch = message.charAt(i);
      int j = (int) ch;
      String sss = "&#" + j + ";";
      finalmessage = finalmessage + sss;
    }
    String responseString = "";
    String encryptedPassword;
    try {
      encryptedPassword = MD5(password);
      String genratedhashKey = hashGenerator(username, senderId, finalmessage, secureKey);
      List<ImmutablePair<String, String>> nameValuePairs =
          new ArrayList<ImmutablePair<String, String>>(1);
      nameValuePairs.add(ImmutablePair.of("mobileno", mobileNumber));
      nameValuePairs.add(ImmutablePair.of("senderid", senderId));
      nameValuePairs.add(ImmutablePair.of("content", finalmessage));
      nameValuePairs.add(ImmutablePair.of("smsservicetype", "unicodeotpmsg"));
      nameValuePairs.add(ImmutablePair.of("username", username));
      nameValuePairs.add(ImmutablePair.of("password", encryptedPassword));
      nameValuePairs.add(ImmutablePair.of("key", genratedhashKey));
      nameValuePairs.add(ImmutablePair.of("templateid", templateid));
      ProxyApi.call(nameValuePairs, "https://msdgweb.mgov.gov.in/esms/sendsmsrequestDLT");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return responseString;
  }

  protected String hashGenerator(String userName, String senderId, String content,
      String secureKey) {
    StringBuffer finalString = new StringBuffer();
    finalString.append(userName.trim()).append(senderId.trim()).append(content.trim())
        .append(secureKey.trim());
    String hashGen = finalString.toString();
    StringBuffer sb = new StringBuffer();
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-512");
      md.update(hashGen.getBytes());
      byte byteData[] = md.digest();
      // convert the byte to hex format method 1
      for (int i = 0; i < byteData.length; i++) {
        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  /**
   * 427 Get units of the unicode message 428
   *
   * @param message e.g. 'Ã Â¤ÂµÃ Â¤Â¿Ã Â¤Â•Ã Â¤Â¾Ã Â¤Â¸ Ã Â¤Â†Ã Â¤Â£Ã Â¤Â¿ Ã Â¤ÂªÃ Â¥ÂÃ Â¤Â°Ã
   *        Â¤Â—Ã Â¤Â¤ Ã Â¤Â¸Ã Â¤Â‚Ã Â¤Â—Ã Â¤Â£Ã Â¤Â¨ Ã Â¤Â•Ã Â¥Â‡Ã Â¤Â‚Ã Â¤Â¦Ã Â¥ÂÃ Â¤Â° Ã Â¤Â®Ã
   *        Â¤Â§Ã Â¥ÂÃ Â¤Â¯Ã Â¥Â‡ Ã Â¤Â¸Ã Â¥ÂÃ Â¤ÂµÃ Â¤Â¾Ã Â¤Â—Ã Â¤Â¤ Ã Â¤Â†Ã Â¤Â¹Ã Â¥Â‡' 429
   * @return int message unit 430
   **/
  public int getUnicodeTextMessageUnit(String message) {
    String charInUnicode = "";
    int msgUnit = 1;
    int msgLen = 0;
    String unicodeMessgae = "";
    String finalmessage = null;
    for (int i = 0; i < message.length(); i++) {
      char ch = message.charAt(i);
      int j = (int) ch;
      String sss = "&#" + j + ";";
      finalmessage = finalmessage + sss;
    }
    StringTokenizer st = new StringTokenizer(finalmessage, " ");
    while (st.hasMoreElements()) {
      String str1 = (String) st.nextElement();
      StringTokenizer dd = new StringTokenizer(str1, ";");
      while (dd.hasMoreElements()) {
        charInUnicode = (String) dd.nextElement();
        if (charInUnicode.startsWith("&#")) {
          StringTokenizer df = new StringTokenizer(charInUnicode, "&#");
          while (df.hasMoreElements()) {
            String kk = (String) df.nextElement();
            unicodeMessgae = unicodeMessgae + "," + kk;
            msgLen = msgLen + 1;
          }
        } else {
          if (charInUnicode.contains("&#")) {
            StringTokenizer st1 = new StringTokenizer(charInUnicode, "&#");
            while (st1.hasMoreElements()) {
              String kk = (String) st1.nextElement();
              for (int i1 = 0; i1 < kk.length(); ++i1) {
                char c = kk.charAt(i1);
                int j = (int) c;
                unicodeMessgae = unicodeMessgae + "," + j;
                msgLen = msgLen + 1;
              }
              String uni = st1.nextToken();
              unicodeMessgae = unicodeMessgae + "," + uni;
              msgLen = msgLen + 1;
            }
          } else {
            for (int i1 = 0; i1 < charInUnicode.length(); ++i1) {
              char c = charInUnicode.charAt(i1);
              int j = (int) c;
              unicodeMessgae = unicodeMessgae + "," + j;
              msgLen = msgLen + 1;
            }
          }
        }
      }
      unicodeMessgae = unicodeMessgae + " ";
    }
    if (msgLen > 70) {
      msgUnit = 2;
      if (msgLen > 134) {
        msgUnit = 3;
        if (msgLen > 201) {
          msgUnit = 4;
          if (msgLen > 268) {
            msgUnit = 5;
            if (msgLen > 335) {
              msgUnit = 6;
              if (msgLen > 402) {
                msgUnit = 7;
                if (msgLen > 469) {
                  msgUnit = 8;
                  if (msgLen > 536) {
                    msgUnit = 9;
                    if (msgLen > 603) {
                      msgUnit = 10;
                    }
                  }
                }
              }
            }
          }
        }
      }
    } else {
      msgUnit = 1;
    }
    return msgUnit;
  }

  /**
   * 530 Get units of the text message 531
   *
   * @param message e.g. 'Welcome to Mobile Seva' 532
   * @return int message unit 533
   **/
  public int getNormalTextMessageUnit(String message) {
    int msgUnit = 1;
    if (message.length() > 160) {
      msgUnit = 2;
      if (message.length() > 306) {
        msgUnit = 3;
      }
      if (message.length() > 459) {
        msgUnit = 4;
      }
      if (message.length() > 612) {
        msgUnit = 5;
      }
      if (message.length() > 765) {
        msgUnit = 6;
      }
      if (message.length() > 918) {
        msgUnit = 7;
      }
      if (message.length() > 1071) {
        msgUnit = 8;
      }
      if (message.length() > 1224) {
        msgUnit = 9;
      }
      if (message.length() > 1377) {
        msgUnit = 10;
      }
    } else {
      msgUnit = 1;
    }
    return msgUnit;
  }

  /****
   * 572 Method to convert Normal Plain Text Password to MD5 encrypted password 573
   ***/
  private static String MD5(String text)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest md;
    md = MessageDigest.getInstance("SHA-1");
    byte[] md5 = new byte[64];
    md.update(text.getBytes("iso-8859-1"), 0, text.length());
    md5 = md.digest();
    return convertedToHex(md5);
  }

  private static String convertedToHex(byte[] data) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < data.length; i++) {
      int halfOfByte = (data[i] >>> 4) & 0x0F;
      int twoHalfBytes = 0;
      do {
        if ((0 <= halfOfByte) && (halfOfByte <= 9)) {
          buf.append((char) ('0' + halfOfByte));
        } else {
          buf.append((char) ('a' + (halfOfByte - 10)));
        }
        halfOfByte = data[i] & 0x0F;
      } while (twoHalfBytes++ < 1);
    }
    return buf.toString();
  }
}
