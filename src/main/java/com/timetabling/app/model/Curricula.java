package com.timetabling.app.model;

import java.util.List;

public class Curricula {
		private String curriculum;
		private List<String> primaryCourses;
		private List<String> secondaryCourses;
		
		public Curricula() {}
		
		public String getCurriculum() {
			return curriculum;
		}
		public void setCurriculum(String curriculum) {
			this.curriculum = curriculum;
		}
		public List<String> getPrimaryCourses() {
			return primaryCourses;
		}
		public void setPrimaryCourses(List<String> primaryCourses) {
			this.primaryCourses = primaryCourses;
		}
		public List<String> getSecondaryCourses() {
			return secondaryCourses;
		}
		public void setSecondaryCourses(List<String> secondaryCourses) {
			this.secondaryCourses = secondaryCourses;
		}
		
		public Curricula(Builder builder) {
			this.curriculum = builder.curriculum;
			this.primaryCourses = builder.primaryCourses;
			this.secondaryCourses = builder.secondaryCourses;
		}
		
		public static class Builder {
			private String curriculum;
			private List<String> primaryCourses;
			private List<String> secondaryCourses;
			
			public Builder curriculum(String curriculum) {
				this.curriculum = curriculum;
				return this;
			}
			
			public Builder primaryCourses(List<String> primaryCourses) {
				this.primaryCourses = primaryCourses;
				return this;
			}
			
			public Builder secondaryCourses(List<String> secondaryCourses) {
				this.secondaryCourses = secondaryCourses;
				return this;
			}
			
			public Curricula build() {
				return new Curricula(this);
			}
		}
	
}
