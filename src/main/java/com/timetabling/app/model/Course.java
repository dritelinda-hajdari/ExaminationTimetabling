package com.timetabling.app.model;

public class Course {
	private String course;
	private RoomsRequested roomsRequested;
	private String teacher;
	
	public Course() {}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public RoomsRequested getRoomsRequested() {
		return roomsRequested;
	}
	public void setRoomsRequested(RoomsRequested roomsRequested) {
		this.roomsRequested = roomsRequested;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	
	public Course(Builder builder) {
		this.course = builder.course;
		this.roomsRequested = builder.roomsRequested;
		this.teacher = builder.teacher;
	}
	
	public static class Builder {
		private String course;
		private RoomsRequested roomsRequested;
		private String teacher;
		
		public Builder course(String course) {
			this.course = course;
			return this;
		}
		
		public Builder roomsRequested(RoomsRequested roomsRequested) {
			this.roomsRequested = roomsRequested;
			return this;
		}
		
		public Builder teacher(String teacher) {
			this.teacher = teacher;
			return this;
		}
		
		public Course build() {
			return new Course(this);
		}
	}
}
