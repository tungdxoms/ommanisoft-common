package com.ommanisoft.common.utils;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ommanisoft.common.utils.values.HttpResponse;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class RequestUtils {
  private static RestTemplate restTemplate;
  /**
   * 5 second
   */
  private static int timeout = 5;

  public static RestTemplate getTemplate() {
    if (restTemplate != null) {
      return restTemplate;
    }
    restTemplate = new RestTemplate(getClientHttpRequestFactory(timeout));
    return restTemplate;
  }

  public static <T> HttpResponse sendRequest(
    HttpMethod method, String requestUrl, T mData, Map<String, String> headerParam) {
    try {
      if (mData != null && mData instanceof Map && method == HttpMethod.GET) {
        String query = JsonParser.mapToQueryStringUTF8((Map<String, Object>) mData);
        if (query != null) {
          requestUrl = requestUrl + (requestUrl.contains("?") ? "&" + query : "?" + query);
        }
      }

      RestTemplate restTemplate = getTemplate();
      HttpHeaders headers = new HttpHeaders();

      headerParam = headerParam == null ? new HashMap<>() : headerParam;
      for (Map.Entry<String, ?> entry : headerParam.entrySet()) {
        headers.add(entry.getKey(), entry.getValue().toString());
      }
      HttpEntity<?> data = new HttpEntity<T>(mData, headers);

      if (headers.getContentType() == null) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        data = new HttpEntity<T>(mData, headers);
      }
      if (headers.getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED)) {
        Map<String, Object> formData;
        formData = JsonParser.objectToMap(mData);
        data = new HttpEntity<>(formData, headers);
      }
      System.out.println(requestUrl);
      ResponseEntity<String> response =
        restTemplate.exchange(requestUrl, method, data, String.class);

      return new HttpResponse(response.getStatusCode(), response.getBody(), response.getHeaders());
    } catch (HttpClientErrorException hex) {
      return new HttpResponse(
        hex.getStatusCode(), hex.getResponseBodyAsString(), hex.getResponseHeaders());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Call api: " + requestUrl + " error:" + e);
    }
  }

  //set timeout and disable ssl check
  private static HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory(int timeOut) {
    try {
      TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

      SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
        .loadTrustMaterial(null, acceptingTrustStrategy)
        .build();

      SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

      CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLSocketFactory(csf)
        .build();

      HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();

      requestFactory.setHttpClient(httpClient);
      requestFactory.setConnectTimeout(timeOut * 1000);
      requestFactory.setReadTimeout(timeOut * 1000);

      return requestFactory;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
