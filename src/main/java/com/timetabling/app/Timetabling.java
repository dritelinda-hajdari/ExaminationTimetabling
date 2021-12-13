package com.timetabling.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
	private static BigDecimal cost = BigDecimal.ZERO;
	
	public static void main(String[] args) throws ParseException {
		List<Assignment> assignments = new ArrayList<>();
		Instance inst =  JSONUtils.convert(JSONUtils.getFileData("D1-1-16.json"), new TypeReference<Instance>(){});
		//Map<Course, List<Room>> courseRoomRelation = new HashMap<Course, List<Room>>();
		List<Course> courses = inst.getCourses();
		List<Room> rooms = inst.getRooms();
		List<Curricula> curriculas = inst.getCurricula();
		List<Exam> exams = new ArrayList<Exam>();
		periodRoomRelation = getPeriods(inst.getPeriods(), inst.getSlotsPerDay(), rooms);
		for (int i = 0; i < 10; i++) {
			curriculas.stream().forEach(curricula -> {
				List<String> primaryCourses = curricula.getPrimaryCourses();
				List<String> secondaryCourses = curricula.getSecondaryCourses();
				checkingConstraints(inst, courses, exams, curricula, primaryCourses, assignments);
				checkingConstraints(inst, courses, exams, curricula, secondaryCourses, assignments);
			});
			Solution solution = new Solution.Builder().assignments(assignments).cost(cost).build();
			cost = BigDecimal.ZERO;
			JSONUtils.saveFile(JSONUtils.convert(solution), "Assignments.json");
			System.out.println(JSONUtils.convert(solution));
			solution.mutation();
		}
		
	}

	private static List<Assignment> checkingConstraints(Instance inst, List<Course> courses, List<Exam> exams, Curricula curricula,
			List<String> curricumCourses, List<Assignment> assignments) {
		Collections.shuffle(curricumCourses);
			for (String prCourse : curricumCourses) {
				Optional<Course> course = courses.stream().filter(cr -> cr.getCourse().equalsIgnoreCase(prCourse)).findAny();
				Entry<Period, List<Room>> periodAndCourseRoom = getRequestedCourseRooms(course.get(), exams, inst);
				if(periodAndCourseRoom != null) {
					cost = cost.add(new BigDecimal(10));
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
	
	public static Entry<Period, List<Room>> getRequestedCourseRooms(Course course, List<Exam> exams, Instance inst) {
		 List<Room> rooms = inst.getRooms().stream().filter(room -> room.getType().equals(course.getRoomsRequested().getType()))
				.limit(course.getRoomsRequested().getNumber()).collect(Collectors.toList());
		 Map<Period, List<Room>> periodOfRooms = periodRoomRelation.entrySet().stream().filter(e -> CollectionUtils.containsAny(e.getValue(), rooms))
				 .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		 if(exams.isEmpty()) {
			 return periodOfRooms.entrySet().iterator().next();
		 } else {
			Optional<Entry<Period, List<Room>>> periodOfRoomSelected = periodOfRooms.entrySet().stream().filter(
					e -> exams.stream().anyMatch(ex -> !ex.getCourse().getTeacher().equals(course.getTeacher()) && !ex.getPeriod().equals(e.getKey())))
					.findFirst();
			return periodOfRoomSelected.isPresent() ? periodOfRoomSelected.get() : null;
		}
	}
}
