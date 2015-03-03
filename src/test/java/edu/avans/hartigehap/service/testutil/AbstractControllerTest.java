package edu.avans.hartigehap.service.testutil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.avans.hartigehap.web.controller.ControllerTestConfig;


// to indicate that running the test should be done using JUnit version 4
@RunWith(SpringJUnit4ClassRunner.class)
// configures the ApplicationContext that is used for the test 
@ContextConfiguration(classes = {ControllerTestConfig.class})
// sets the profile to "test"
@ActiveProfiles("test")
public abstract class AbstractControllerTest {

	@Test
	// class must contain a test to prevent "initialization error"
	public void dummy() {
		// deliberately empty
	}

}
