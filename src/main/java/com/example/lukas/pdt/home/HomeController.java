package com.example.lukas.pdt.home;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	private GJFormatter gf = GJFormatter.getInstance();
	
	@RequestMapping(value={"/", "/index"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(new SearchFilter());
		modelAndView.setViewName("index");
		return modelAndView;
	}
	
	@RequestMapping(value={"/home"}, method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		return modelAndView;
	}
	
	@RequestMapping(value = "/search",  method = RequestMethod.GET)
    public @ResponseBody String submit(@RequestParam Integer radiusDistance, @RequestParam Boolean bar, @RequestParam Boolean pub,
    		@RequestParam Double lng, @RequestParam Double lat) {
		System.out.println(lng+ " " + lat);
		List<Place> placeInDistance = new DBAccessor().findPlaceInDistance(radiusDistance, pub, bar, lng, lat);
    	return gf.createGJPoints(placeInDistance);

    }
	
	@RequestMapping(value = "/heatmap",  method = RequestMethod.GET)
    public @ResponseBody List<Heatmap> submit(@RequestParam Integer radiusDistance, @RequestParam Boolean bar, @RequestParam Boolean pub) {
		List<Heatmap> heatmap = new DBAccessor().createHeatMap(pub, bar);
		for(Heatmap temp :heatmap) {
			temp.setGeojson(gf.createHeatmap(temp));
		}
    	return heatmap;
    }
	@RequestMapping(value = "/pointsArea",  method = RequestMethod.GET)
    public @ResponseBody String submit(@RequestParam String name, @RequestParam Boolean bar, @RequestParam Boolean pub) {
		List<Place> pointsHeatmap = new DBAccessor().createPointsHeatmap(name,pub, bar);
    	return gf.createGJPoints(pointsHeatmap);
    }
	
	@RequestMapping(value = "/route",  method = RequestMethod.GET)
    public @ResponseBody String submit( @RequestParam Boolean bar, @RequestParam Boolean pub, @RequestParam String placeCord, @RequestParam Double lng, @RequestParam Double lat, @RequestParam Boolean type) {
		placeCord = placeCord.replace(" ", ",");
		placeCord = placeCord.replace("Coordinates:","");
		List<Route> route = null;
		System.out.println(placeCord);
		if (type == false) {
			route = new DBAccessor().createRoute(pub, bar, placeCord, lng,lat);
			if (route.isEmpty()) {
				return "0";
			}
		}
		else {
			route = new DBAccessor().createCrossRoute(pub, bar, placeCord, lng,lat);
			if (route.isEmpty()) {
				return "0";
			}
			return gf.createGJCrossRoute(route);
		}
    	return gf.createGJRoute(route);
    }
}
