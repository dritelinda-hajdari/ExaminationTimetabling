package com.timetabling.app.model;

import java.util.List;

public class Assignment {
	private String course;
	private List<Event> events;
	
	public Assignment() {}
	
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	public Assignment(Builder builder) {
		this.course = builder.course;
		this.events = builder.events;
	}
	
	public static class Builder {
		private String course;
		private List<Event> events;
		
		public Builder course(String course) {
			this.course = course;
			return this;
		}
		
		public Builder events(List<Event> events) {
			this.events = events;
			return this;
		}
		
		public Assignment build() {
			return new Assignment(this);
		}
	}
}
