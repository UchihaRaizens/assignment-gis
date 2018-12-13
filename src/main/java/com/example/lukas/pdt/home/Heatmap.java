package com.example.lukas.pdt.home;

public class Heatmap {
	private String areaName;
	private String geojson;
	private Double percentage;
	
	public Heatmap(String areaName, String geojson, Double percentage) {
		this.areaName = areaName;
		this.geojson = geojson;
		this.percentage = percentage;
	}

	
	public String getGeojson() {
		return geojson;
	}
	
	public void setGeojson(String geojson) {
		this.geojson = geojson;
	}


	public String getAreaName() {
		return areaName;
	}


	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}


	public Double getPercentage() {
		return percentage;
	}


	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	
	
}
