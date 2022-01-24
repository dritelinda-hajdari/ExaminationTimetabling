package com.timetabling.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.parser.ParseException;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.timetabling.app.model.Assignment;
import com.timetabling.app.model.Course;
import com.timetabling.app.model.Curricula;
import com.timetabling.app.model.Event;
import com.timetabling.app.model.Exam;
import com.timetabling.app.model.Instance;
import com.timetabling.app.model.Period;
import com.timetabling.app.model.Room;
import com.timetabling.app.model.Solution;
import com.timetabling.app.util.JSONUtils;

public class Timetabling {
	
	private static Map<Period, List<Room>> periodRoomRelation = new HashMap<Period, List<Room>>();
	private static int cost = 0;
	private static Solution best = null;
	
	public static void main(String[] args) throws ParseException {
		Instance inst =  JSONUtils.convert(JSONUtils.getFileData("D1-2-16.json"), new TypeReference<Instance>(){});
		List<Course> courses = inst.getCourses();
		List<Room> rooms = inst.getRooms();
		List<Curricula> curriculas = inst.getCurricula();
		List<Solution> tabuList = new ArrayList<Solution>();
		int numberOfTweak = 5;
		
		Solution solution = generateSolution(inst, courses, rooms, curriculas);
		calculateCost(solution, inst);
		best = solution;
		int tabuListLength = 10;
		tabuList.add(solution);
		int repeat = 0;
		while(repeat < 50) {
			Solution R;
			if(tabuList.size() > tabuListLength) {
				tabuList.remove(0);
			}
			cost = 0;
			R = new Solution.Builder().assignments(applyTweaks(solution, inst)).cost(cost).build();
			calculateCost(R, inst);
			System.out.println(R.getCost());
			for (int i = 0; i < numberOfTweak; i++) {
				cost = 0;
				//Apply operators mutation or swap or crossover:
				Solution W = new Solution.Builder().assignments(applyTweaks(solution, inst)).cost(cost).build();
				calculateCost(W, inst);
				System.out.println(W.getCost());
				if(!tabuList.contains(W) && (W.getCost() > R.getCost() || tabuList.contains(R))) {
					R = W;
				}
				cost = 0;
			}
			if(!tabuList.contains(R)) {
				solution = R;
				tabuList.add(R);
			}
			if(solution.getCost() >= best.getCost()) {
				best = solution;
			}
			repeat++;
		}
		JSONUtils.saveFile(JSONUtils.convert(best), "Assignments.json");
	}

	private static Solution generateSolution(Instance inst, List<Course> courses, List<Room> rooms,
			List<Curricula> curriculas) {
		List<Assignment> assignments = new ArrayList<>();
		List<Exam> exams = new ArrayList<Exam>();
		periodRoomRelation = getPeriods(inst.getPeriods(), inst.getSlotsPerDay(), rooms);
		curriculas.stream().forEach(curricula -> {
			checkingConstraints(inst, courses, exams, curricula, curricula.getPrimaryCourses(), assignments, inst.getPrimaryPrimaryDistance());
			checkingConstraints(inst, courses, exams, curricula, curricula.getSecondaryCourses(), assignments, inst.getPrimarySecondaryDistance());
		});
		return new Solution.Builder().assignments(assignments).cost(cost).build();
	}
	
	private static List<Assignment> applyTweaks(Solution solution, Instance inst) {
		solution.swap();
		return solution.mutation(inst);
	}

	private static List<Assignment> checkingConstraints(Instance inst, List<Course> courses, List<Exam> exams, Curricula curricula,
			List<String> curricumCourses, List<Assignment> assignments, BigDecimal distance) {
		Collections.shuffle(curricumCourses);
			for (String prCourse : curricumCourses) {
				Optional<Course> course = courses.stream().filter(cr -> cr.getCourse().equalsIgnoreCase(prCourse)).findAny();
				Entry<Period, List<Room>> periodAndCourseRoom = getRequestedCourseRooms(course.get(), exams, inst, curricula, distance ,curricumCourses);
				if(periodAndCourseRoom != null) {
					List<Room> selectedPeriod = periodAndCourseRoom.getValue().stream().filter(r -> r.getType().equals(course.get().getRoomsRequested().getType())).limit(course.get().getRoomsRequested().getNumber()).collect(Collectors.toList());
					Exam exam = new Exam.Builder().course(course.get()).rooms(selectedPeriod)
							.period(periodAndCourseRoom.getKey()).curriculum(curricula.getCurriculum()).build();
					exams.add(exam);
					List<Event> events = new ArrayList<>();
					if(selectedPeriod.isEmpty()) {
						Event event = new Event.Builder().period(periodAndCourseRoom.getKey().getId()).periodDay(periodAndCourseRoom.getKey().getDay()).
								periodTimeslot(periodAndCourseRoom.getKey().getTimeslot()).room("").build();
						events.add(event);
					} else {
						selectedPeriod.stream().forEach(period -> {
							Event event = new Event.Builder().period(periodAndCourseRoom.getKey().getId()).periodDay(periodAndCourseRoom.getKey().getDay()).
									periodTimeslot(periodAndCourseRoom.getKey().getTimeslot()).room(period.getRoom()).build();
							events.add(event);
						});
					}
					Assignment assignment = new Assignment.Builder().course(exam.getCourse().getCourse())
							.events(events).build();
					assignments.add(assignment);
					periodRoomRelation.remove(periodAndCourseRoom.getKey());
				}
			}
		return assignments;
	}
	
	public static Map<Period, List<Room>> getPeriods(Integer day, Integer timeslots, List<Room> rooms) {
		Map<Period, List<Room>> periodRooms = new HashMap<Period, List<Room>>();
		int id = 0;
		for (int i = 1; i <= day/timeslots; i++) {
			for (int j = 1; j <= timeslots; j++) {
				Period period = new Period.Builder().id(String.valueOf(id)).day(i).timeslot(j).build();
				periodRooms.put(period, rooms);
				id++;
			}
		}
		return periodRooms;
	}
	
	public static Entry<Period, List<Room>> getRequestedCourseRooms(Course course, List<Exam> exams, Instance inst, Curricula curricula, BigDecimal distance, List<String> courses) {
		 List<Room> rooms = inst.getRooms().stream().filter(room -> room.getType().equals(course.getRoomsRequested().getType()))
				.limit(course.getRoomsRequested().getNumber()).collect(Collectors.toList());
		 Map<Period, List<Room>> periodOfRooms = periodRoomRelation.entrySet().stream().filter(e -> CollectionUtils.containsAny(e.getValue(), rooms))
				 .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		 if(exams.isEmpty()) {
			 return periodOfRooms.isEmpty() ? periodRoomRelation.entrySet().iterator().next() : periodOfRooms.entrySet().iterator().next();
		 } else {
			Map<Period, List<Room>> availablePeriodOfRooms = periodOfRooms.isEmpty() ? periodRoomRelation : periodOfRooms;
			Entry<Period, List<Room>> softConstraintRoomPeriod = checkPeriodSoftConstraint(availablePeriodOfRooms, exams, curricula, course, courses, distance);
			if(softConstraintRoomPeriod != null) {
				return softConstraintRoomPeriod;
			}
			Optional<Entry<Period, List<Room>>> periodOfRoomSelected = periodOfRooms.entrySet().stream().filter(
					e -> exams.stream().anyMatch(ex -> !ex.getCourse().getTeacher().equals(course.getTeacher()) && !ex.getPeriod().equals(e.getKey())))
					.findFirst();
			return periodOfRoomSelected.isPresent() ? periodOfRoomSelected.get() : null;
		}
	}
	
	public static Entry<Period, List<Room>> checkPeriodSoftConstraint(Map<Period, List<Room>> periodOfRooms, List<Exam> exams, Curricula curricula, 
			Course course, List<String> courses, BigDecimal distance) {
		List<String> allCurriculumCourses = new ArrayList<String>(curricula.getPrimaryCourses());
		allCurriculumCourses.addAll(curricula.getSecondaryCourses());
		List<Exam> addedCurriculumCourses = exams.stream().filter(exam -> allCurriculumCourses.contains(exam.getCourse().getCourse())).collect(Collectors.toList());
		Map<Period, List<Room>> availablePeriodOfRooms = periodOfRooms.entrySet().stream().filter(
				e -> addedCurriculumCourses.stream().anyMatch(ex -> !ex.getCourse().getTeacher().equals(course.getTeacher()) && !ex.getPeriod().equals(e.getKey())))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Entry<Period, List<Room>> periodOfRoom = checkCourseDistanceSoftConstraint(availablePeriodOfRooms, exams, courses, course, distance);
		if(periodOfRoom != null) {
			return periodOfRoom;
		}
		Optional<Entry<Period, List<Room>>> periodOfRoomSelected = periodOfRooms.entrySet().stream().filter(
				e -> addedCurriculumCourses.stream().anyMatch(ex -> !ex.getCourse().getTeacher().equals(course.getTeacher()) && !ex.getPeriod().equals(e.getKey())))
				.findFirst();
		if(periodOfRoomSelected.isPresent()) {
			return periodOfRoomSelected.get();
		}
		return null;
	}
	
	public static Entry<Period, List<Room>> checkCourseDistanceSoftConstraint(Map<Period, List<Room>> periodOfRooms, List<Exam> exams, List<String> courses, Course course, BigDecimal distance) {
		List<Exam> addedCourses = exams.stream().filter(exam -> courses.contains(exam.getCourse().getCourse())).collect(Collectors.toList());
		Collections.sort(addedCourses, Comparator.comparing(o -> ((Exam) o).getPeriod().getDay()).reversed());
		if(addedCourses.isEmpty()) {
			return null;
		}
		BigDecimal lastAddedExamDayPeriod = new BigDecimal (addedCourses.get(0).getPeriod().getDay());
		Optional<Entry<Period, List<Room>>> periodOfRoomSelected = periodOfRooms.entrySet().stream().filter(e -> new BigDecimal(e.getKey().getDay()).compareTo(lastAddedExamDayPeriod.add(distance)) > 1 ).findFirst();
		return periodOfRoomSelected.isPresent() ? periodOfRoomSelected.get() : null;
	}
	
	public static void calculateCost(Solution solution, Instance inst) {
		Map<String, List<String>> periodOfCourses = new HashMap<String, List<String>>();
		solution.getAssignment().forEach(assignment -> {
			List<Event> assignmentEvents = assignment.getEvents();
			assignmentEvents.stream().forEach(event -> {
				if(periodOfCourses.containsKey(event.getPeriod())) {
					List<String> courses = periodOfCourses.get(event.getPeriod());
					if(!courses.contains(assignment.getCourse())) {
						courses.add(assignment.getCourse());
					}
				} else {
					periodOfCourses.put(event.getPeriod(), new ArrayList<>(Arrays.asList(assignment.getCourse())));
				}
			});
		});
		
		inst.getCurricula().stream().forEach(curricula -> {
			List<String> curriculaCourses = curricula.getPrimaryCourses();
			//Check second soft constraint 
			checkSecondConstraintCost(solution, inst, curriculaCourses);
			checkSecondConstraintCost(solution, inst, curricula.getSecondaryCourses());
			curriculaCourses.addAll(curricula.getSecondaryCourses());
			periodOfCourses.entrySet().stream().forEach(courses-> {
				if(!curriculaCourses.containsAll(courses.getValue())) {
					cost = cost + courses.getValue().size();
				}
			});
		});
		solution.setCost(cost);
		
	}

	private static void checkSecondConstraintCost(Solution solution, Instance inst, List<String> curriculaCourses) {
		List<Assignment> filteredAssignments = solution.getAssignment().stream().filter(a -> curriculaCourses.contains(a.getCourse())).collect(Collectors.toList());
		Collections.sort(filteredAssignments, Comparator.comparing(a -> ((Assignment) a).getEvents().get(0).getPeriodDay()).reversed());
		for (int i = 0; i < filteredAssignments.size()-1; i++) {
			if(filteredAssignments.get(i).getEvents().get(0).getPeriodDay() + inst.getPrimaryPrimaryDistance().intValue() 
			>=  filteredAssignments.get(i+1).getEvents().get(0).getPeriodDay()) {
				cost = cost +1;
			}
		}
	}
}
