package edu.se.extweb.config;


/*
  @author   george
  @project   proj-test
  @class  AuditionConfiguration
  @version  1.0.0 
  @since 10.04.25 - 13.18
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@Configuration
public class AuditionConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }


}
