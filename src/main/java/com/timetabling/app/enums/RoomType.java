package com.timetabling.app.enums;

public enum RoomType {
	SMALL,
	MEDIUM,
	LARGE;
	
	public static RoomType fromString(String param) {
		for (RoomType pt : values()) {
			if (pt.name().equals(param))
				return pt;
		}
		return null;
	}
}
