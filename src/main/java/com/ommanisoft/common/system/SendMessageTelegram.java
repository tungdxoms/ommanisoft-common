package com.ommanisoft.common.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

@Service
public class SendMessageTelegram {
  @Value("${spring.application.name}")
  private String appName;

  @Value("${spring.application.environment}")
  private String environment;

  @Value("${telegram.apiToken}")
  private String apiToken;

  @Value("${telegram.chatId}")
  private String chatId;

  @Async
  public void send(String msg) {
    String msg2 = "**** <b>" + environment + " - " + appName + "</b> **** \n" + msg;
    try {
      URI uri = new URI("https", "api.telegram.org", "/bot" + apiToken + "/sendMessage", "chat_id=" + chatId + "&parse_mode=HTML&text=" + msg2, "");
      URL url = uri.toURL();
      URLConnection conn = url.openConnection();
      InputStream is = new BufferedInputStream(conn.getInputStream());
    } catch (Exception e) {

    }
  }

}
