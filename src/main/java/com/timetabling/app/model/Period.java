package com.timetabling.app.model;

public class Period {
	
	private String id;
	private Integer day;
	private Integer timeslot;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer day) {
		this.day = day;
	}
	public Integer getTimeslot() {
		return timeslot;
	}
	public void setTimeslot(Integer timeslot) {
		this.timeslot = timeslot;
	}
	
	public Period(Builder builder) {
		this.id = builder.id;
		this.day = builder.day;
		this.timeslot = builder.timeslot;
	}
	
	public static class Builder {
		private String id;
		private Integer day;
		private Integer timeslot;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}
		
		public Builder day(Integer day) {
			this.day = day;
			return this;
		}
		
		public Builder timeslot(Integer timeslot) {
			this.timeslot = timeslot;
			return this;
		}
		
		public Period build() {
			return new Period(this);
		}
	}
}
