package com.timetabling.app.model;

import java.util.List;

public class Solution {
	private List<Exam> exams;

	public List<Exam> getExams() {
		return exams;
	}

	public void setExams(List<Exam> exams) {
		this.exams = exams;
	}
	
	public Solution(Builder builder) {
		this.exams = builder.exams;
	}
	
	public static class Builder {
		private List<Exam> exams;
		
		public Builder exams(List<Exam> exams) {
			this.exams = exams;
			return this;
		}
		
		public Solution build() {
			return new Solution(this);
		}
	}
}
