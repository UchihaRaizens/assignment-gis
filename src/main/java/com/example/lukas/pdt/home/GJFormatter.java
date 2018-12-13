package com.example.lukas.pdt.home;

import java.util.List;

public class GJFormatter {

	private static GJFormatter singleton = null;

 	private static Integer idNumber = 0;
	private String start = "{\"id\": \"%d\",\r\n" + 
			"        \"type\": \"circle\",\r\n" + 
			"        \"source\": {\r\n" + 
			"            \"type\": \"geojson\",\r\n" + 
			"            \"data\": {\r\n" + 
			"                \"type\": \"FeatureCollection\",\r\n" + 
			"                \"features\": [";
	
	private String startLine = "{\"id\": \"%d\",\r\n" + 
			"        \"type\": \"line\",\r\n" + 
		    "   \"layout\": { " +
	        "\"line-join\": \"round\", " +
	        "\"line-cap\": \"round\" " +
	    "}, " +
	    "\"paint\": { " +
	     "   \"line-color\": \"#888\", " +
	      "  \"line-width\": 8 " +
	    "}, " +
			"        \"source\": {\r\n" + 
			"            \"type\": \"geojson\",\r\n" + 
			"            \"data\": {\r\n" + 
			"                \"type\": \"FeatureCollection\",\r\n" + 
			"                \"features\": [";
	private static String end = "]\r\n" + 
			"            }\r\n" + 
			"     } "
			+ "  }";
	
	private String startHeatmap = "{\"id\": \"%d\",\r\n" + 
			"        \"type\": \"fill\",\r\n" +
			"\"paint\": { " +
        "\"fill-color\": \"%s\", " +
        "\"fill-opacity\": 0.4 " +
    	"}, " +
			"        \"source\": {\r\n" + 
			"            \"type\": \"geojson\",\r\n" + 
			"            \"data\": {\r\n" + 
			"                \"type\": \"FeatureCollection\",\r\n" + 
			"                \"features\": [";
	
	private static String endHeatmap = "]\r\n" + 
			"            }\r\n" + 
			"     } "
			+ "  }";
	
	private GJFormatter() {
		
	}
	
	public static GJFormatter getInstance() {
		if (singleton == null) {
			singleton = new GJFormatter();
		}
		return singleton;
	}


	public String createGJPoints(List<Place> points) {
		String temp = String.format(start, idNumber);
		for(Place point: points) {
			temp += "                    {\"type\": \"Feature\",\r\n" + 
					"                    \"geometry\":"+ point.getGeojson() +",\"properties\": {\r\n" + 
							"      \"title\": \"Mapbox SF\",\r\n" + 
							"      \"description\": \""+ point.getPlaceName() +"\",\r\n" + 
							"      \"marker-color\": \"#3bb2d0\",\r\n" + 
							"      \"marker-size\": \"large\",\r\n" + 
							"      \"marker-symbol\": \"rocket\"\r\n" + 
							"    }},";
		}
		temp = temp.substring(0, temp.length() -1);
		temp += end;
		System.out.println(temp);
		idNumber++;
		return temp;
	}

	public String createHeatmap(Heatmap heatmap) {
		String json = String.format(startHeatmap, idNumber,fill(heatmap.getPercentage()));
			json +=  "                    {\"type\": \"Feature\",\r\n" + 
					"                    \"geometry\":"+ heatmap.getGeojson() +",\"properties\": {\r\n" +
										"\"polygon-color\": \""+ fill(heatmap.getPercentage())+"\"\r\n"
					
							+ "}},";
		json = json.substring(0, json.length() -1);
		json += end;
		System.out.println(json);
		idNumber++;
		return json;
	}

	private String fill(Double percentage) {
		if(percentage <= 0.5) {
			return "#ffe6e6";
		}
		if(percentage <= 1) {
			return "#ffcccc";
		}
		if(percentage <= 2) {
			return "#ffb3b3";
		}
		if(percentage <= 3) {
			return "#ff9999";
		}
		if(percentage <= 4) {
			return "#ff8080";
		}
		if(percentage <= 5) {
			return "#ff6666";
		}
		if(percentage <= 6) {
			return "#ff4d4d";
		}
		if(percentage <= 7) {
			return "#ff3333";
		}
		if(percentage <= 8) {
			return "#ff1a1a";
		}
		if(percentage <= 9) {
			return "#ff0000";
		}
		if(percentage <= 10) {
			return "#e60000";
		}
		if(percentage > 10) {
			return "#cc0000";
		}
		return "#ffe6e6";
	}

	public String createGJRoute(List<Route> route) {
		String temp = String.format(startLine, idNumber);
		for(Route point: route) {
			temp += "                    {\"type\": \"Feature\",\r\n" + 
					"                    \"geometry\":"+ point.getGeojson() +",\"properties\": {\r\n" + 
							"      \"title\": \"Mapbox SF\"\r\n" + 
							"    }},";
		}
		temp = temp.substring(0, temp.length() -1);
		temp += end;
		System.out.println(temp);
		idNumber++;
		return temp;
	}

	public String createGJCrossRoute(List<Route> routes) {
		String temp = "";
		for(Route route: routes ) {
			temp += route.getAut1() + " " + route.getZast1() + " " +route.getAut2() + " " + route.getZast2() + "\n";
		}
		return temp;
	}
}
