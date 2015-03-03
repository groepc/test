package edu.avans.hartigehap.web.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.RestaurantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RestaurantControllerTest.class })
@WebAppConfiguration
@ImportResource({ "classpath:/test-root-context.xml","classpath:*servlet-context.xml" })
@Slf4j
public class RestaurantControllerTest {

  private static final String RESTAURANT_ID = "De Plak";

  @Autowired
  private RestaurantController restaurantController;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Autowired
  private RestaurantService restaurantServiceMock;

  @Before
  public void setUp() {
    // Thanks to: Petri Kinulainen:
    // http://www.petrikainulainen.net/programming/spring-framework/unit-testing-of-spring-mvc-controllers-normal-controllers/
    // https://github.com/pkainulainen/spring-mvc-test-examples/tree/master/controllers-unittest
    
    //We have to reset our mock between tests because the mock objects
    //are managed by the Spring container. If we would not reset them,
    //stubbing and verified behavior would "leak" from one test to another.
    Mockito.reset(restaurantServiceMock);

    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Bean
  public RestaurantService restaurantService() {
    return Mockito.mock(RestaurantService.class);
  }

  /**
   * verifies the 'wiring' of the test case:
   * <ul>
   * <li>autowiring of RestaurantController.</li>
   * <li>autowiring of RestaurantService mock.</li>
   * </ul>
   * @throws Exception
   */
  @Test
  public void dummy() throws Exception {
    log.debug("test the configuration of the test case, 'the wiring'");
    assertNotNull(restaurantController);
    Object restaurantServiceMock = ReflectionTestUtils.getField(restaurantController, "restaurantService");
    assertTrue(restaurantServiceMock instanceof RestaurantService);
    String className = restaurantServiceMock.getClass().getName();
    log.debug("className: {}", className);
    assertTrue("classname contains 'Mock' since it is a mockito mock", className.indexOf("Mock") >= 0);
  }

  /**
   * verifies the following qualities of the RestaurantController:
   * <ul>
   * <li>@RequestMapping annotation.</li>
   * <li>view resolving: the correct view name is returned.</li>
   * </ul>
   * 
   * @throws Exception
   */
  @Test
  public void listRestaurants() throws Exception {
    List<Restaurant> restaurants = getRestaurants();
    Mockito.when(restaurantServiceMock.findAll()).thenReturn(restaurants);
    mockMvc.perform(get( "/restaurants"))
        .andExpect(status().isOk())
        .andExpect(view().name("hartigehap/listrestaurants"))
        .andExpect(model().attribute("restaurants", hasItems(restaurants.toArray(new Restaurant[]{}))));
  }

  private List<Restaurant> getRestaurants() {
    LinkedList<Restaurant> retval = new LinkedList<Restaurant>();
    Restaurant r1 = new Restaurant();
    r1.setId(RESTAURANT_ID);
    retval.add(r1);
    return retval;
  }

}
