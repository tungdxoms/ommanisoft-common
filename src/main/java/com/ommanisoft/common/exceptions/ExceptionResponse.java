package com.ommanisoft.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
public class ExceptionResponse {
  private Integer status;
  private Date timestamp;
  private String message;
  private String messageCode;
  private String description;
  private String path;

  public ExceptionResponse(HttpStatus status, Date timestamp, String message, String description, String path) {
    super();
    this.status = status.value();
    this.timestamp = timestamp;
    this.message = message;
    this.description = description;
    this.path = path;
  }

  public ExceptionResponse(HttpStatus status, Date timestamp, String message, String messageCode, String description, String path) {
    super();
    this.status = status.value();
    this.timestamp = timestamp;
    this.message = message;
    this.description = description;
    this.messageCode = messageCode;
    this.path = path;
  }
}
