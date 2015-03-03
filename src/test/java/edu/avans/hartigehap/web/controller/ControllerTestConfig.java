package edu.avans.hartigehap.web.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// @Configuration enables configuration using java classes (as an alternative to xml or annotations)
@Configuration
// defines that this configuration should be used when the test profile is "active"
@Profile("test")
public class ControllerTestConfig {

}
