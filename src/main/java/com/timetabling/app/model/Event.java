package com.timetabling.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Event {
	private String period;
	@JsonIgnore
	private Integer periodDay;
	@JsonIgnore
	private Integer periodTimeslot;
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
	
	public Integer getPeriodDay() {
		return periodDay;
	}

	public void setPeriodDay(Integer periodDay) {
		this.periodDay = periodDay;
	}

	public Integer getPeriodTimeslot() {
		return periodTimeslot;
	}

	public void setPeriodTimeslot(Integer periodTimeslot) {
		this.periodTimeslot = periodTimeslot;
	}

	public Event(Builder builder) {
		this.period = builder.period;
		this.room = builder.room;
		this.periodDay = builder.periodDay;
		this.periodTimeslot = builder.periodTimeslot;
	}
	
	public static class Builder {
		private String period;
		private String room;
		private Integer periodDay;
		private Integer periodTimeslot;
		
		public Builder period(String period) {
			this.period = period;
			return this;
		}
		
		public Builder room(String room) {
			this.room = room;
			return this;
		}
		
		public Builder periodDay(Integer periodDay) {
			this.periodDay = periodDay;
			return this;
		}
		
		public Builder periodTimeslot(Integer periodTimeslot) {
			this.periodTimeslot = periodTimeslot;
			return this;
		}
		
		public Event build() {
			return new Event(this);
		}
	}
}
