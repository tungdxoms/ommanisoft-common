package com.ommanisoft.common.annotations;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ommanisoft.common.utils.OmsHeader;
import com.ommanisoft.common.utils.RequestUtils;
import com.ommanisoft.common.utils.values.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionOmAspect {
  @Value("${ommani.sso-service:null}")
  private String ssoApi;

  @Before("@annotation(PermissionOm)")
  public void process(JoinPoint joinPoint) {
    if(ssoApi == null){
      throw new ExceptionOm(HttpStatus.INTERNAL_SERVER_ERROR, "PermissionOm anonation required ommani.sso-service not null");
    }
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    PermissionOm om = method.getAnnotation(PermissionOm.class);
    HttpServletRequest request =
      ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    if (request.getHeader(OmsHeader.headerUserId) == null) {
      throw new ExceptionOm(HttpStatus.UNAUTHORIZED, "unauthorized");
    }
    if (om.accepts().length > 0) {
      HttpResponse res = RequestUtils.sendRequest(HttpMethod.POST,
        ssoApi + "/sso/v1/auth/authorization", om.accepts(),
        new HashMap<String, String>() {{
          put(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
        }});

      if (res.getStatus() != HttpStatus.OK) {
        throw new ExceptionOm(HttpStatus.INTERNAL_SERVER_ERROR, "Api authorization error");
      }
      long status = Long.parseLong(res.getBody());
      if (status == 401) {
        throw new ExceptionOm(HttpStatus.UNAUTHORIZED, "unauthorized");
      }
      if (status == 403) {
        throw new ExceptionOm(HttpStatus.FORBIDDEN, "forbidden");
      }
    }
  }
}
