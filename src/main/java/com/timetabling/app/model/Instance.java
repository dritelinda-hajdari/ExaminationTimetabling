package com.timetabling.app.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Instance {
	private List<Course> courses;
	private List<Curricula> curricula;
	private Integer periods;
	private BigDecimal primaryPrimaryDistance;
	private BigDecimal primarySecondaryDistance;
	private Integer slotsPerDay;
	private List<String> teachers;
	private List<Room> rooms;
	
	public Instance() {}
	
	public List<Course> getCourses() {
		return courses;
	}
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	public List<Curricula> getCurricula() {
		return curricula;
	}
	public void setCurricula(List<Curricula> curricula) {
		this.curricula = curricula;
	}
	public Integer getPeriods() {
		return periods;
	}
	public void setPeriods(Integer periods) {
		this.periods = periods;
	}
	public BigDecimal getPrimaryPrimaryDistance() {
		return primaryPrimaryDistance;
	}
	public void setPrimaryPrimaryDistance(BigDecimal primaryPrimaryDistance) {
		this.primaryPrimaryDistance = primaryPrimaryDistance;
	}
	public BigDecimal getPrimarySecondaryDistance() {
		return primarySecondaryDistance;
	}
	public void setPrimarySecondaryDistance(BigDecimal primarySecondaryDistance) {
		this.primarySecondaryDistance = primarySecondaryDistance;
	}
	public Integer getSlotsPerDay() {
		return slotsPerDay;
	}
	public void setSlotsPerDay(Integer slotsPerDay) {
		this.slotsPerDay = slotsPerDay;
	}
	public List<String> getTeachers() {
		return teachers;
	}
	public void setTeachers(List<String> teachers) {
		this.teachers = teachers;
	}
	public List<Room> getRooms() {
		return rooms;
	}
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	public Instance(Builder builder) {
		 this.courses = builder.courses;
		 this.curricula = builder.curricula;
		 this.periods = builder.periods;
		 this.primaryPrimaryDistance = builder.primaryPrimaryDistance;
		 this.primarySecondaryDistance =  builder.primarySecondaryDistance;
		 this.slotsPerDay = builder.slotsPerDay;
		 this.teachers = builder.teachers;
		 this.rooms = builder.rooms;
	}
	
	public static class Builder {
		private List<Course> courses;
		private List<Curricula> curricula;
		private Integer periods;
		private BigDecimal primaryPrimaryDistance;
		private BigDecimal primarySecondaryDistance;
		private Integer slotsPerDay;
		private List<String> teachers;
		private List<Room> rooms;
		
		public Builder courses(List<Course> courses) {
			this.courses = courses;
			return this;
		}
		public Builder curricula(List<Curricula> curricula) {
			this.curricula = curricula;
			return this;
		}
		public Builder periods(Integer periods) {
			this.periods = periods;
			return this;
		}
		public Builder primaryPrimaryDistance(BigDecimal primaryPrimaryDistance) {
			this.primaryPrimaryDistance = primaryPrimaryDistance;
			return this;
		}
		public Builder primarySecondaryDistance(BigDecimal primarySecondaryDistance) {
			this.primarySecondaryDistance = primarySecondaryDistance;
			return this;
		}
		public Builder slotsPerDay(Integer slotsPerDay) {
			this.slotsPerDay = slotsPerDay;
			return this;
		}
		public Builder teachers(List<String> teachers) {
			this.teachers = teachers;
			return this;
		}
		public Builder rooms(List<Room> rooms) {
			this.rooms = rooms;
			return this;
		}
		
		public Instance build() {
			return new Instance(this);
		}
	}
} 
