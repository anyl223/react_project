package com.gis.gis.controllers;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import com.gis.gis.models.User;
import com.gis.gis.models.Users;
import com.gis.gis.payloads.request.EncryptedRequest;
import com.gis.gis.payloads.request.Translate;
import com.gis.gis.payloads.response.EncryptedResponse;
import com.gis.gis.utils.Json;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ApiController {

  private static final String PROXY_HOST = "10.194.81.43";
  private static final int PROXY_PORT = 8080;
  private static OkHttpClient httpClient = null;

  @Value("${spring.profiles.active}")
  void setActiveProfile(String activeProfile) {
    var builder = new OkHttpClient.Builder().connectionPool(new ConnectionPool(180, 5, TimeUnit.MINUTES));
    if ("prod".equals(activeProfile)) {
      builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT)));
    }
    httpClient = builder.build();
  }

  @PostMapping("/verify")
  public <json> EncryptedResponse verifyUser(HttpServletRequest req) throws Exception {
    var user = (Users) req.getAttribute("user");
    var result = Map.of("verified", true, "user", user);
    System.out.println(result);
    return new EncryptedResponse(result);
  }

  @PostMapping("/translate")
  public <json> EncryptedResponse translate(@RequestBody EncryptedRequest req) throws Exception {
    var body = Json.deserialize(Translate.class, req.data());
    String source = body.source();
    String sourceLanguage = body.sourceLanguage();
    String destinationLanguage = body.destinationLanguage();

    String url = "https://dhruva-api.bhashini.gov.in/services/inference/pipeline";
    String requestBodyJson = """
        {
        "pipelineTasks": [
        {
        "taskType": "translation",
        "config": {
        "language": {
        "sourceLanguage": \"""" + sourceLanguage + """
        \"
        ,
        "targetLanguage": \"""" + destinationLanguage + """
        \"
        },"serviceId":"ai4bharat/indictrans-v2-all-gpu--t4"}}],"inputData":{"input":[{"source":\""""
        + source + """
            \"}]}}""";

    okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(requestBodyJson, MediaType.parse("application/json"));

    Request request = new Request.Builder().url(url).post(requestBody).addHeader("Authorization",
        "U0Ij4_EsEj9mZdln51Sm9u3Pxw6BD7-ZQixH1yNRhatfm4N87wF6yxoou3ORSf-q").build();

    Response response = httpClient.newCall(request).execute();
    ResponseBody responseBody = response.body();
    if (responseBody != null) {
      return new EncryptedResponse(responseBody.string());
    }
    return new EncryptedResponse("Translation Problem !" + responseBody);
  }

}
