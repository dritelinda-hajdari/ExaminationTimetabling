package com.timetabling.app.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class Solution {
	private Random randomGenerator;
	private List<Assignment> assignments;
	private BigDecimal cost;

	public List<Assignment> getAssignment() {
		return assignments;
	}

	public void setAssignment(List<Assignment> assignments) {
		this.assignments = assignments;
	}
	
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	
	public Solution(Builder builder) {
		this.assignments = builder.assignments;
		this.cost = builder.cost;
	}
	
	public static class Builder {
		private List<Assignment> assignments;
		private BigDecimal cost;
		
		public Builder assignments(List<Assignment> assignments) {
			this.assignments = assignments;
			return this;
		}
		
		public Builder cost(BigDecimal cost) {
			this.cost = cost;
			return this;
		}
		
		public Solution build() {
			return new Solution(this);
		}
	}
	
	public void mutation() {
		randomGenerator = new Random();
		Assignment firstAssignment = this.assignments.get(randomGenerator.nextInt(this.assignments.size()));
		Assignment secondAssignment = this.assignments.get(randomGenerator.nextInt(this.assignments.size()));
		List<Event> firstEvents = firstAssignment.getEvents();
		List<Event> secondEvents = secondAssignment.getEvents();
		Event secondEvent = secondAssignment.getEvents().get(0);
		Event firstEvent = firstAssignment.getEvents().get(0);
		secondEvent.setPeriod(firstEvent.getPeriod());
		firstEvent.setPeriod(secondEvent.getPeriod());
		firstEvents.remove(0);
		secondEvents.remove(0);
		firstEvents.add(firstEvent);
		secondEvents.add(secondEvent);
		this.setAssignment(assignments);
	}
}
