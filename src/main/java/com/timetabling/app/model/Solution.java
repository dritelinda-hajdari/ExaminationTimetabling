package com.timetabling.app.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class Solution {
	private Random randomGenerator;
	private List<Assignment> assignments;
	private int cost;

	public List<Assignment> getAssignment() {
		return assignments;
	}

	public void setAssignment(List<Assignment> assignments) {
		this.assignments = assignments;
	}
	
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	} 

	public Solution(Builder builder) {
		this.assignments = builder.assignments;
		this.cost = builder.cost;
	}
	
	public static class Builder {
		private List<Assignment> assignments;
		private int cost;
		
		public Builder assignments(List<Assignment> assignments) {
			this.assignments = assignments;
			return this;
		}
		
		public Builder cost(int cost) {
			this.cost = cost;
			return this;
		}
		
		public Solution build() {
			return new Solution(this);
		}
	}
	
	public List<Assignment> swap() {
		randomGenerator = new Random();
		Assignment firstAssignment = this.assignments.get(randomGenerator.nextInt(this.assignments.size()));
		Assignment secondAssignment = this.assignments.get(randomGenerator.nextInt(this.assignments.size()));
		List<Event> firstEvents = firstAssignment.getEvents();
		List<Event> secondEvents = secondAssignment.getEvents();
		Event secondEvent = secondAssignment.getEvents().isEmpty() ? null : secondAssignment.getEvents().get(0);
		Event firstEvent = firstAssignment.getEvents().isEmpty() ? null : firstAssignment.getEvents().get(0);
		if(!firstEvents.isEmpty() && !secondEvents.isEmpty()) {
			String firstEventPeriod = firstEvent.getPeriod();
			String secondEventPeriod = secondEvent.getPeriod();
			secondEvent.setPeriod(firstEventPeriod);
			firstEvent.setPeriod(secondEventPeriod);
			firstEvents.set(0, firstEvent);
			secondEvents.set(0, secondEvent);
			this.setAssignment(assignments);
		}
		return assignments;
	}
	
	public List<Assignment> mutation(Instance inst) {
		randomGenerator = new Random();
		Assignment firstAssignment = this.assignments.get(randomGenerator.nextInt(this.assignments.size()));
		Event firstEvent = firstAssignment.getEvents().get(0);
		if(firstEvent != null) {
			Optional<Room> room = inst.getRooms().stream().filter(rt -> rt.getRoom().equals(firstEvent.getRoom())).findAny();
			if(room.isPresent()) {
				List<Room> rooms = inst.getRooms().stream().filter(r -> r.getType().equals(room.get().getType())).collect(Collectors.toList());
				Collections.shuffle(rooms);
				firstEvent.setRoom(rooms.get(0).getRoom());
			}
			this.setAssignment(assignments);
		}
		return assignments;
	}
}
