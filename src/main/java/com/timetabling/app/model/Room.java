package com.timetabling.app.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.timetabling.app.enums.RoomType;

public class Room {
	 private String room;
	 private RoomType type;
	 
	 public Room() {}
	 
	 @Enumerated(EnumType.STRING)
	 public String getRoom() {
		 return room;
	 }
	 public void setRoom(String room) {
		 this.room = room;
	 }
	 public RoomType getType() {
		 return type;
	 }
	 public void setType(RoomType type) {
		 this.type = type;
	 }
	 
	 public Room(Builder builder) {
		 this.room = builder.room;
		 this.type = builder.type;
	 }
	 
	 public static class Builder {
		 private String room;
		 private RoomType type;
		 
		 public Builder number(String room) {
			 this.room = room;
			 return this;
		 }
		 
		 public Builder type(RoomType type) {
			 this.type = type;
			 return this;
		 }
		 
		 public Room build() {
			 return new Room(this);
		 }
	 }
}	
