package com.timetabling.app.model;

public class Event {
	private String period;
	private String room;
	
	public Event() {}
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	
	public Event(Builder builder) {
		this.period = builder.period;
		this.room = builder.room;
	}
	
	public static class Builder {
		private String period;
		private String room;
		
		public Builder period(String period) {
			this.period = period;
			return this;
		}
		
		public Builder room(String room) {
			this.room = room;
			return this;
		}
		
		public Event build() {
			return new Event(this);
		}
	}
}
