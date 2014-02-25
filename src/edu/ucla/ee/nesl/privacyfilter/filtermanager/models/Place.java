package edu.ucla.ee.nesl.privacyfilter.filtermanager.models;

import com.google.android.gms.maps.model.LatLng;

public class Place {
	private String name;
	private LatLng position;
	private String effectTime;
	private boolean isSensitive;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LatLng getPosition() {
		return position;
	}
	public void setPosition(LatLng position) {
		this.position = position;
	}
	public String getEffectTime() {
		return effectTime;
	}
	public void setEffectTime(String effectTime) {
		this.effectTime = effectTime;
	}
	public boolean isSensitive() {
		return isSensitive;
	}
	public void setSensitive(boolean isSensitive) {
		this.isSensitive = isSensitive;
	}
	
	public Place(String _name, LatLng _position) {
		this.name = _name;
		this.position = _position;
		this.isSensitive = false;
		this.effectTime = "";
	}
}
