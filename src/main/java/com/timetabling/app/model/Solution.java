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
	private int[] components; 

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
	
	public int[] getComponents() {
		return components;
	}

	public void setComponents(int[] components) {
		this.components = components;
	}

	public Solution(Builder builder) {
		this.assignments = builder.assignments;
		this.cost = builder.cost;
		this.components = builder.components;
	}
	
	public static class Builder {
		private List<Assignment> assignments;
		private int cost;
		private int[] components;
		
		public Builder assignments(List<Assignment> assignments) {
			this.assignments = assignments;
			return this;
		}
		
		public Builder cost(int cost) {
			this.cost = cost;
			return this;
		}
		
		public Builder components(int [] components) {
			this.components = components;
			return this;
		}
		
		public Solution build() {
			return new Solution(this);
		}
	}
	
	public void swap() {
		randomGenerator = new Random();
		Assignment firstAssignment = this.assignments.get(randomGenerator.nextInt(this.assignments.size()));
		Assignment secondAssignment = this.assignments.get(randomGenerator.nextInt(this.assignments.size()));
		List<Event> firstEvents = firstAssignment.getEvents();
		List<Event> secondEvents = secondAssignment.getEvents();
		Event secondEvent = secondAssignment.getEvents().get(0);
		Event firstEvent = firstAssignment.getEvents().get(0);
		if(!firstEvents.isEmpty() && !secondEvents.isEmpty()) {
			secondEvent.setPeriod(firstEvent.getPeriod());
			firstEvent.setPeriod(secondEvent.getPeriod());
			firstEvents.remove(0);
			secondEvents.remove(0);
			firstEvents.add(firstEvent);
			secondEvents.add(secondEvent);
			this.setAssignment(assignments);
		}
	}
	
	public void mutation(Instance inst) {
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
	}
}
