package com.timetabling.app.model;

public class Teacher {

	private String id;
	private String name;
	
	public Teacher() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Teacher(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
	}
	
	public static class Builder {
		private String id;
		private String name;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Teacher build() {
			return new Teacher(this);
		}
		
	}
}
