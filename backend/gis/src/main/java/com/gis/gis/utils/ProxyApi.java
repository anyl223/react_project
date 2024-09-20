package com.gis.gis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class ProxyApi {

  public static String call(List<ImmutablePair<String, String>> nameValuePairs, String appendUrl) {
    String output = "";
    try {
      URL url = new URL(appendUrl);
      System.setProperty("https.protocols", "TLSv1.2");
      SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(new KeyManager[0], new TrustManager[] {new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {}

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {}
      }}, new SecureRandom());
      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.194.81.10", 8080));

      HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(proxy);
      urlConnection.setRequestMethod("POST");
      urlConnection.setUseCaches(false);
      urlConnection.setDoOutput(true);

      urlConnection.setSSLSocketFactory(sslSocketFactory);//
      urlConnection.setHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
          return false;
        }
      });
      urlConnection.setInstanceFollowRedirects(false);
      urlConnection.connect();
      OutputStream os = urlConnection.getOutputStream();
      os.write(getQuery(nameValuePairs).getBytes());
      os.flush();
      os.close();
      if (urlConnection != null) {
        int responseCode = urlConnection.getResponseCode();
        BufferedReader rd = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
          if (urlConnection != null) {
            rd = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(), Charset.forName("UTF-8")));
            String inputLine = "";
            StringBuilder response = new StringBuilder();
            while ((inputLine = rd.readLine()) != null) {
              response.append(inputLine);
            }
            rd.close();
            output = response.toString();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return output;
  }

  private static String getQuery(List<ImmutablePair<String, String>> params)
      throws UnsupportedEncodingException {
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (ImmutablePair<String, String> pair : params) {
      if (first)
        first = false;
      else
        result.append("&");
      result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
      result.append("=");
      result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
    }

    return result.toString();
  }
}
