package uk.ac.ed.acp.cw2.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.net.URL;


@Configuration
@EnableScheduling
public class IlpRestServiceConfig {

  @Value("${ilp.service.url}")
  public URL serviceUrl;
  @Bean
  public String ilpEnpoint(){
    String endpoint = System.getenv().getOrDefault("ILP_ENDPOINT", "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net/");
    if (!endpoint.endsWith("/")) {
        endpoint += "/";
    }
    System.out.println("Using ILP endpoint: " + endpoint);
    return endpoint;
  }
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }


}
