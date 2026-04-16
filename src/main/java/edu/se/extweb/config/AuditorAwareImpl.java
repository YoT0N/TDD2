package edu.se.extweb.config;


/*
  @author   george
  @project   proj-test
  @class  AuditorAware
  @version  1.0.0 
  @since 10.04.25 - 13.14
*/

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        //return Optional.of("admin");
        return Optional.of(System.getProperty("user.name"));
    }
}
