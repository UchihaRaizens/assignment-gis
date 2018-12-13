package com.example.lukas.pdt.home;

public class Route {
	private String geojson;
	private String aut1;
	private String aut2;
	private String zast1;
	private String zast2;
	public Route(String geojson ) {
		this.geojson = geojson;
	}

	public Route(String aut1, String aut2, String zast1, String zast2) {
		this.aut1 = aut1;
		this.aut2 = aut2;
		this.zast1 = zast1;
		this.zast2 = zast2;
	}

	public String getGeojson() {
		return geojson;
	}

	public void setGeojson(String geojson) {
		this.geojson = geojson;
	}

	public String getAut1() {
		return aut1;
	}

	public void setAut1(String aut1) {
		this.aut1 = aut1;
	}

	public String getAut2() {
		return aut2;
	}

	public void setAut2(String aut2) {
		this.aut2 = aut2;
	}

	public String getZast1() {
		return zast1;
	}

	public void setZast1(String zast1) {
		this.zast1 = zast1;
	}

	public String getZast2() {
		return zast2;
	}

	public void setZast2(String zast2) {
		this.zast2 = zast2;
	}
	
	
}
