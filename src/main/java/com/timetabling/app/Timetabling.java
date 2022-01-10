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
import java.util.Random;
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
		Instance inst =  JSONUtils.convert(JSONUtils.getFileData("D1-1-16.json"), new TypeReference<Instance>(){});
		List<Course> courses = inst.getCourses();
		List<Room> rooms = inst.getRooms();
		List<Curricula> curriculas = inst.getCurricula();
		//generate componnets, evaporation constant (0 < x < 1), pheromones 
		Random rand = new Random(); 
		int evaporation = rand.nextInt(1);
		int initializePheromoneValue = 0;
		int numberOfTrails = 10;
		int[] pheromones = initializePheromones(numberOfTrails, initializePheromoneValue);
		int[] components = generateDummyComponents();
		int repeat = 0;
		while(repeat < 5) {
			List<Solution> solutions = new ArrayList<Solution>();
			for (int i = 0; i < 10; i++) {
				int[] componentsOfSolution = getRandomSolutionComponents(components);
				List<Assignment> assignments = new ArrayList<>();
				List<Exam> exams = new ArrayList<Exam>();
				periodRoomRelation = getPeriods(inst.getPeriods(), inst.getSlotsPerDay(), rooms);
				curriculas.stream().forEach(curricula -> {
					List<String> primaryCourses = curricula.getPrimaryCourses();
					List<String> secondaryCourses = curricula.getSecondaryCourses();
					checkingConstraints(inst, courses, exams, curricula, primaryCourses, assignments, inst.getPrimaryPrimaryDistance());
					checkingConstraints(inst, courses, exams, curricula, secondaryCourses, assignments, inst.getPrimarySecondaryDistance());
				});
				Solution solution = new Solution.Builder().assignments(assignments).cost(cost).components(componentsOfSolution).build();
				//check components and best solution 
				
				System.out.println(JSONUtils.convert(solution));
				if(!assignments.isEmpty()) {
					solution.mutation(inst);
					solution.swap();
				}
				if(best == null) {
					best = solution;
				} else if (best.getCost() <= solution.getCost()) {
					best = solution;
				}
				solutions.add(solution);
				pheromones = updatePheromones(evaporation, pheromones, i);
				for (int j = 0; j < solutions.size(); j++) {
					int[] componentsOfCurrentSolution = solutions.get(j).getComponents();
					for (int j2 = 0; j2 < components.length; j2++) {
						if(contains(componentsOfCurrentSolution, components[j2])) {
							pheromones[j2] = pheromones[j2] + solutions.get(j).getCost();
						}
					}
				}
				cost = 0;
			}
			repeat++;
		}
		JSONUtils.saveFile(JSONUtils.convert(best), "Assignments.json");
	}

	private static int[] updatePheromones(int evaporation, int[] pheromones, int i) {
		for (int j = 0; j < pheromones.length; j++) {
			pheromones[i] = (1 - evaporation) * pheromones[i];
		}
		return pheromones;
	}
	
	 public static boolean contains(final int[] arr, final int key) {
	        return Arrays.stream(arr).anyMatch(i -> i == key);
	    }

	private static int[] generateDummyComponents() {
		Random rand = new Random(); 
		return new int[] {rand.nextInt(25),rand.nextInt(25),rand.nextInt(25),rand.nextInt(25),rand.nextInt(25)};
	}
	
	private static int[] initializePheromones(int numberOfTails, int initializePheromoneValue) {
		int[] pheromones = new int[numberOfTails];
		for (int i = 0; i < pheromones.length; i++) {
			pheromones[i] = initializePheromoneValue;
		}
		return pheromones;
	}
	
	private static int[] getRandomSolutionComponents(int [] components) {
		Random rand = new Random(); 
		List<Integer> intList = new ArrayList<Integer>(components.length);
		for (int i : components)
		{
		    intList.add(i);
		}
		Collections.shuffle(intList);
		List<Integer> comp = intList.subList(0,rand.nextInt(components.length));
		return comp.stream().mapToInt(Integer::intValue).toArray();
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
					selectedPeriod.stream().forEach(period -> {
						Event event = new Event.Builder().period(periodAndCourseRoom.getKey().getId()).room(period.getRoom()).build();
						events.add(event);
					});
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
			 return periodOfRooms.entrySet().iterator().next();
		 } else {
			Entry<Period, List<Room>> softConstraintRoomPeriod = checkPeriodSoftConstraint(periodOfRooms, exams, curricula, course, courses, distance);
			if(softConstraintRoomPeriod != null) {
				return softConstraintRoomPeriod;
			}
			Optional<Entry<Period, List<Room>>> periodOfRoomSelected = periodOfRooms.entrySet().stream().filter(
					e -> exams.stream().anyMatch(ex -> !ex.getCourse().getTeacher().equals(course.getTeacher()) && !ex.getPeriod().equals(e.getKey())))
					.findFirst();
			return periodOfRoomSelected.isPresent() ? periodOfRoomSelected.get() : null;
		}
	}
	
	public static Entry<Period, List<Room>> checkPeriodSoftConstraint(Map<Period, List<Room>> periodOfRooms, List<Exam> exams, Curricula curricula, Course course, List<String> courses, BigDecimal distance) {
		List<String> allCurriculumCourses = new ArrayList<String>(curricula.getPrimaryCourses());
		allCurriculumCourses.addAll(curricula.getSecondaryCourses());
		List<Exam> addedCurriculumCourses = exams.stream().filter(exam -> allCurriculumCourses.contains(exam.getCourse().getCourse())).collect(Collectors.toList());
		Map<Period, List<Room>> availablePeriodOfRooms = periodOfRooms.entrySet().stream().filter(
				e -> addedCurriculumCourses.stream().anyMatch(ex -> !ex.getCourse().getTeacher().equals(course.getTeacher()) && !ex.getPeriod().equals(e.getKey())))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Entry<Period, List<Room>> periodOfRoom = checkCourseDistanceSoftConstraint(availablePeriodOfRooms, exams, courses, course, distance);
		if(periodOfRoom != null) {
			cost = cost + 2;
			return periodOfRoom;
		}
		Optional<Entry<Period, List<Room>>> periodOfRoomSelected = periodOfRooms.entrySet().stream().filter(
				e -> addedCurriculumCourses.stream().anyMatch(ex -> !ex.getCourse().getTeacher().equals(course.getTeacher()) && !ex.getPeriod().equals(e.getKey())))
				.findFirst();
		if(periodOfRoomSelected.isPresent()) {
			cost = cost + 1;
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
}
