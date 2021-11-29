package com.timetabling.app.model;

import com.timetabling.app.enums.RoomType;

public class RoomsRequested {
	private Integer number;
	private RoomType type;
	
	public RoomsRequested() {}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public RoomType getType() {
		return type;
	}
	public void setType(RoomType type) {
		this.type = type;
	}
	
	public RoomsRequested(Builder builder) {
		this.number = builder.number;
		this.type = builder.type;
	}
	
	public static class Builder {
		private Integer number;
		private RoomType type;
		
		public Builder number(Integer number) {
			this.number = number;
			return this;
		}
		
		public Builder type(RoomType type) {
			this.type = type;
			return this;
		}
		
		public RoomsRequested build() {
			return new RoomsRequested(this);
		}
	}
	
}
