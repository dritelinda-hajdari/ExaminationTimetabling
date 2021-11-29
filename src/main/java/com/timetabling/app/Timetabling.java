package com.timetabling.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.parser.ParseException;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.timetabling.app.model.Course;
import com.timetabling.app.model.Curricula;
import com.timetabling.app.model.Exam;
import com.timetabling.app.model.Instance;
import com.timetabling.app.model.Period;
import com.timetabling.app.model.Room;
import com.timetabling.app.model.Solution;
import com.timetabling.app.util.JSONUtils;

public class Timetabling {
	
	private static Map<Period, List<Room>> periodRoomRelation = new HashMap<Period, List<Room>>();
	
	public static void main(String[] args) throws ParseException {
		
		Instance inst =  JSONUtils.convert(JSONUtils.getFileData("toy.json"), new TypeReference<Instance>(){});
		//Map<Course, List<Room>> courseRoomRelation = new HashMap<Course, List<Room>>();
		List<Course> courses = inst.getCourses();
		List<Room> rooms = inst.getRooms();
		List<Curricula> curriculas = inst.getCurricula();
		List<Exam> exams = new ArrayList<Exam>();
		periodRoomRelation = getPeriods(inst.getPeriods(), inst.getSlotsPerDay(), rooms);
		curriculas.stream().forEach(curricula -> {
			List<String> primaryCourses = curricula.getPrimaryCourses();
			List<String> secondaryCourses = curricula.getSecondaryCourses();
			checkingConstraints(inst, courses, exams, curricula, primaryCourses);
			checkingConstraints(inst, courses, exams, curricula, secondaryCourses);
		});
		Solution solution = new Solution.Builder().exams(exams).build();
		System.out.println( JSONUtils.convert(solution));
		
	}

	private static void checkingConstraints(Instance inst, List<Course> courses, List<Exam> exams, Curricula curricula,
			List<String> curricumCourses) {
		curricumCourses.stream().forEach(prCourse -> {
			Optional<Course> course = courses.stream().filter(cr -> cr.getCourse().equalsIgnoreCase(prCourse)).findAny();
			Entry<Period, List<Room>> periodAndCourseRoom = getRequestedCourseRooms(course.get(), exams, inst);
			if(periodAndCourseRoom != null) {
				List<Room> selectedPeriod = periodAndCourseRoom.getValue().stream().filter(r -> r.getType().equals(course.get().getRoomsRequested().getType())).limit(course.get().getRoomsRequested().getNumber()).collect(Collectors.toList());
				Exam exam = new Exam.Builder().course(course.get()).rooms(selectedPeriod)
						.period(periodAndCourseRoom.getKey()).curriculum(curricula.getCurriculum()).build();
				exams.add(exam);
				periodRoomRelation.remove(periodAndCourseRoom.getKey());
			}
		});
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
