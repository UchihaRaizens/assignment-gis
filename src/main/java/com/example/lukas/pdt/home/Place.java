package com.example.lukas.pdt.home;

public class Place {

	private String placeName;
	private String typeName;
	private Integer distance;
	private String geojson;
	
	public Place(String placeName, String typeName, String geojson, int distance) {
		this.placeName = placeName;
		this.typeName = typeName;
		this.geojson = geojson;
		this.distance = distance;
	}

	public String getPlaceName() {
		return placeName;
	}
	
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public Integer getDistance() {
		return distance;
	}
	
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	
	public String getGeojson() {
		return geojson;
	}
	
	public void setGeojson(String geojson) {
		this.geojson = geojson;
	}
	
	
}
