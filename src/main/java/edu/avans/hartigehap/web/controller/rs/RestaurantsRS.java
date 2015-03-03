package edu.avans.hartigehap.web.controller.rs;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import edu.avans.hartigehap.domain.Restaurant;
import edu.avans.hartigehap.service.RestaurantService;

// http://briansjavablog.blogspot.nl/2012/08/rest-services-with-spring.html
@Controller
public class RestaurantsRS {
	private final Logger logger = LoggerFactory.getLogger(RestaurantsRS.class);

	@Autowired
	private RestaurantService restaurantService;

// TODO: reason to comment out that it gives a problem in unit test of DiningTableController	
//	orig:_@Autowired
//	orig:_private_View_jsonView;
// new: 
	private View jsonView = null;

	private static final String DATA_FIELD = "data";
	private static final String ERROR_FIELD = "error";

	/**
	 * list all restaurants.
	 * 
	 * @return
	 */
	@RequestMapping(value = RSConstants.URL_PREFIX + "/restaurants", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Restaurant> restaurants() {
		logger.debug("");
		return restaurantService.findAll();
	}

	/**
	 * create a new restaurant.
	 */
	@RequestMapping(value = RSConstants.URL_PREFIX + "/restaurants", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createRestaurantJson(@RequestBody Restaurant restaurant, HttpServletResponse httpResponse,
	        WebRequest httpRequest) {
		logger.debug("body: {}", restaurant);

		try {
			Restaurant savedRestaurant = restaurantService.save(restaurant);
			httpResponse.setStatus(HttpStatus.CREATED.value());
			httpResponse
			        .setHeader("Location", httpRequest.getContextPath() + "/restaurants/" + savedRestaurant.getId());
			return new ModelAndView(jsonView, DATA_FIELD, savedRestaurant);
		} catch (Exception e) {
			logger.error("Error creating new restaurant", e);
			String message = "Error creating new restaurant. [%1$s]";
			return createErrorResponse(String.format(message, e.toString()));
		}
	}

	@RequestMapping(value = RSConstants.URL_PREFIX + "/restaurants/{restaurantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Restaurant findById(
	        @PathVariable String restaurantId,
	        HttpServletResponse httpResponse,
	        WebRequest httpRequest) {
		logger.debug("restaurantId: {}", restaurantId);
		return restaurantService.findById(restaurantId);
	}

	private ModelAndView createErrorResponse(String sMessage) {
		return new ModelAndView(jsonView, ERROR_FIELD, sMessage);
	}

	public void setRestaurantService(RestaurantService restaurantService) {
		this.restaurantService = restaurantService;
	}

	public void setJsonView(View jsonView) {
		this.jsonView = jsonView;
	}

}
