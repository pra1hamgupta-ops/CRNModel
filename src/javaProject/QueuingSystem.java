package javaProject;
import java.util.*;



import static javaProject.Constants.*;
import static javaProject.User.*;
import static javaProject.Event.*;
import static javaProject.Distributions.*;

public class QueuingSystem {
	
	double lambdaP;
	double muP;
	double lambdaS;
	double muHS;
	double muLS;
	
	static int tmax;
	
	Channels channels;
	int numberOfChannles;

	private static TreeMap<Integer, HashMap<String,HashSet<Integer>>> nextEvents;
	
	
	public QueuingSystem(int numberOfChannels, double lambdaP, double muP, double lambdaS, double muHS, double muLS, int minLSU, int maxLSU, int minHSU, int maxHSU,
			int hpqSize, int lpqSize, double allocationFactor, int time) {
		
		this.lambdaP = lambdaP;
		this.muP = muP;
		this.muHS = muHS;
		this.muLS = muLS;
		this.lambdaS= lambdaS;
		
		tmax = time;
		this.numberOfChannles = numberOfChannels;
		channels = new Channels(numberOfChannels,lpqSize, hpqSize, minLSU, maxLSU, minHSU, maxHSU, allocationFactor);
		
		nextEvents = new TreeMap<>();
		fillNextEvents();
	}


	private void fillNextEvents() {
		fillPUEvents();
		fillSUEvents();
	}


	


	


	private void fillSUEvents() {
		int time = 0;
		
		while(time < tmax) {
			int arrivals = poisson(lambdaS);
			
			for(int i = 0; i < arrivals; i++){
				String priority = typeOfSU();
				if(priority.equals(HSU)) {
					User user = new User(muHS);
					userMap.put(user, HSU);
					addNextEvent(time,user, new Event(user, ARRIVAL));
					
				}else {
					User user = new User(muLS);
					userMap.put(user, LSU);
					addNextEvent(time,user, new Event(user, ARRIVAL));
				}
			}
			time += 1;
		}
	}
	
	private void fillPUEvents() {
		int time = 0;
		while(time < tmax) {
			
			int arrivals = poisson(lambdaP);
			
			for(int i = 0; i < arrivals; i++){
				User user = new User(muP);
				userMap.put(user, PU);
				addNextEvent(time,user, new Event(user, ARRIVAL));
			}
			time += 1;
		}
	}


	public static void addNextEvent(int time,User user, Event event) {
		
		HashMap<String, HashSet<Integer>> map = nextEvents.getOrDefault(time, new HashMap<>());
		
		String userType = userMap.get(user);
		String eventType = event.getEventType();
		
		HashSet<Integer> events = map.getOrDefault(userType+eventType, new HashSet<>());
		events.add(event.id);
		
		map.put(userType+eventType, events);
		
		nextEvents.put(time, map);
	}
	
	public static void removeDepartureEvent(User user) {
		int departureTime = user.getDepartureTime();
		int eventId = user.getDepartureEventId();
		nextEvents.get(departureTime).get(userMap.get(user)+DEPARTURE).remove(eventId);
		if(nextEvents.get(departureTime).size() == 0) nextEvents.remove(departureTime);
		if(nextEvents.get(departureTime).get(userMap.get(user) + DEPARTURE).size() == 0) nextEvents.get(departureTime).remove(userMap.get(user) + DEPARTURE);
	}


	private String typeOfSU() {
		Random random = new Random();
		
		double data = random.nextDouble();
		double performance = random.nextDouble();
		double recenty = random.nextDouble();
		
		FuzzySU fuzzy = new FuzzySU(data, performance, recenty);
		String suPriority = fuzzy.getPriority();
		
		if(suPriority.equals(VERY_HIGH_PRIORITY) || suPriority.equals(HIGH_PRIORITY)) return HSU;
		return LSU;
	}
	
	
	public int simulate() {
		int CIDT = 0;
		int time = 0;
		Integer next = nextEvents.ceilingKey(time);
		
		while(time < tmax) {
//			System.out.println("time -> " + time);
			if(next != null && time == next) {
				HashMap<String, HashSet<Integer>> currentEvents = nextEvents.get(time);
				
//				for(String key : currentEvents.keySet()) {
//					System.out.println(key + " -> " + currentEvents.get(key).size());
//				}
//				System.out.println();
				departureEvents(currentEvents,time);
				arrivalEvents(currentEvents, time);
				
				nextEvents.remove(time);
				next = nextEvents.ceilingKey(time);
			}else {
				if(isPollingStage(time)) {
//					System.out.println("No other events happening");
//					System.out.println();
//					System.out.println("Polling stage started");
//					System.out.println("PU Channels -> " + channels.puChannels);
//					System.out.println("LSU Channels -> " + channels.lsuChannels);
//					System.out.println("HSU Channels -> " + channels.hsuChannels);
//					System.out.println("PU -> " + channels.pu);
//					System.out.println("LSU -> " + channels.lsu);
//					System.out.println("HSU -> " + channels.hsu);
//					System.out.println("Low Priority Queue -> " + channels.lpq );
//					System.out.println("High Priority Queue -> " + channels.hpq);
//					System.out.println();
					channels.pollingStage(time);
//					System.out.println("Polling stage ended");
//					System.out.println("PU Channels -> " + channels.puChannels);
//					System.out.println("LSU Channels -> " + channels.lsuChannels);
//					System.out.println("HSU Channels -> " + channels.hsuChannels);
//					System.out.println("PU -> " + channels.pu);
//					System.out.println("LSU -> " + channels.lsu);
//					System.out.println("HSU -> " + channels.hsu);
//					System.out.println("Low Priority Queue -> " + channels.lpq );
//					System.out.println("High Priority Queue -> " + channels.hpq);
//					System.out.println();
				}
			}
			
//			for(int i = 0; i <= 30; i++) {
//				System.out.print("_");
//			}
//			System.out.println();
			
			CIDT += channels.getFreeChannels();
			time++;
		}
		
		channels.adjustQueueTimeAfterCompletion(tmax);
		return CIDT;
	}
	
	private void arrivalEvents(HashMap<String, HashSet<Integer>> currentEvents, int time) {
		
		if(currentEvents.containsKey(PU+ARRIVAL))
			arrivalPU(currentEvents.get(PU+ARRIVAL), time);
		
		if(currentEvents.containsKey(HSU+ARRIVAL))
			arrivalHSU(currentEvents.get(HSU+ARRIVAL), time);
		
		if(isPollingStage(time)) {
//			System.out.println("Polling stage started");
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
			channels.pollingStage(time);
//			System.out.println("Polling stage ended");
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
		}
		
		if(currentEvents.containsKey(LSU+ARRIVAL))
			arrivalLSU(currentEvents.get(LSU+ARRIVAL), time);
	}


	private void arrivalLSU(HashSet<Integer> list, int time) {
		HashSet<Integer> arr = new HashSet<>(list);
		for(int id : arr) {
			Event event = getEventById.get(id);
			User user = event.getUser();
//			System.out.println("LSU Arrival started -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
			channels.arriveLSU(user, time);
//			System.out.println("LSU Arrival ended -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
		}
	}


	private void arrivalHSU(HashSet<Integer> list, int time) {
		HashSet<Integer> arr = new HashSet<>(list);
		for(int id : arr) {
			Event event = getEventById.get(id);
			User user = event.getUser();
//			System.out.println("HSU Arrival started -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
			channels.arriveHSU(user, time);
//			System.out.println("HSU Arrival ended -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
		}
	}


	private void arrivalPU(HashSet<Integer> list, int time) {
		HashSet<Integer> arr = new HashSet<>(list);
		for(int id : arr) {
			Event event = getEventById.get(id);
			User user = event.getUser();
//			System.out.println("PU Arrival started -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
			channels.arrivePU(user, time);
//			System.out.println("PU Arrival ended -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
		}
	}


	private void departureEvents(HashMap<String, HashSet<Integer>> currentEvents, int time) {
		if(currentEvents.containsKey(PU+DEPARTURE))
			departurePU(currentEvents.get(PU+DEPARTURE), time);
		
		if(currentEvents.containsKey(HSU+DEPARTURE))
			departureSU(currentEvents.get(HSU+DEPARTURE), time);
		
		if(currentEvents.containsKey(LSU+DEPARTURE))
			departureSU(currentEvents.get(LSU+DEPARTURE), time);
	}
	
	

	private void departureSU(HashSet<Integer> list, int time) {
		HashSet<Integer> arr = new HashSet<>(list);
		for(int id : arr) {
			Event event = getEventById.get(id);
			User user = event.getUser();
//			System.out.println("SU departing started -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
			channels.departSU(user, isPollingStage(time), time); 
//			System.out.println("SU departing completed -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
		}
	}


	private void departurePU(HashSet<Integer> list, int time) {
		HashSet<Integer> arr = new HashSet<>(list);
		for(int id : arr) {
			Event event = getEventById.get(id);
			User user = event.getUser();
//			System.out.println("PU departing started -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
			channels.departPU(user, isPollingStage(time), time); 
//			System.out.println("PU departing completed -> " + user);
//			System.out.println("PU Channels -> " + channels.puChannels);
//			System.out.println("LSU Channels -> " + channels.lsuChannels);
//			System.out.println("HSU Channels -> " + channels.hsuChannels);
//			System.out.println("PU -> " + channels.pu);
//			System.out.println("LSU -> " + channels.lsu);
//			System.out.println("HSU -> " + channels.hsu);
//			System.out.println("Low Priority Queue -> " + channels.lpq );
//			System.out.println("High Priority Queue -> " + channels.hpq);
//			System.out.println();
			
		}
	}


	public boolean isPollingStage(int time) {
		return time%2 ==0;
	}
	
	
}
