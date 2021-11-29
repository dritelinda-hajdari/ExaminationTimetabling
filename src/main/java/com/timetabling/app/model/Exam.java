package com.timetabling.app.model;

import java.util.List;

public class Exam {
	private Course course;
	private List<Room> rooms;
	private Period period;
	private String curriculum;
	
	public Exam() {}
	
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}
	public List<Room> getRooms() {
		return rooms;
	}
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public String getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(String curriculum) {
		this.curriculum = curriculum;
	}

	public Exam(Builder builder) {
		this.course = builder.course;
		this.rooms = builder.rooms;
		this.period = builder.period;
		this.curriculum = builder.curriculum;
	}
	
	public static class Builder {
		private Course course;
		private List<Room> rooms;
		private Period period;
		private String curriculum;
		
		public Builder course(Course course) {
			this.course = course;
			return this;
		}
		
		public Builder rooms(List<Room> rooms) {
			this.rooms = rooms;
			return this;
		}
		
		public Builder period(Period period) {
			this.period = period;
			return this;
		}
		
		public Builder curriculum(String curriculum) {
			this.curriculum = curriculum;
			return this;
		}
		public Exam build() {
			return new Exam(this);
		}
	}
	
}
