package it.gov.pagopa.apiconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class Application {

  @Value("${timezone}")
  private String timezone;
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  public void init(){
    // Setting Spring Boot SetTimeZone
    TimeZone.setDefault(TimeZone.getTimeZone(timezone));
  }
}
