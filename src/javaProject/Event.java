package javaProject;

import java.util.HashMap;

public class Event {
	
	private User user;
	private String eventType;
	int id;
	static Integer distinct;
	
	static HashMap<Integer, Event> getEventById;
	
	public static void flushStaticEvent() {
		distinct = null;
		getEventById = null;
	}
	
	public Event(User user, String eventType) {
		if(distinct == null) distinct = 0;
		this.user = user;
		this.eventType = eventType;
		allocateId();
		if(getEventById == null) getEventById = new HashMap<>();
		getEventById.put(id, this);
	}
	
	public void allocateId() {
		this.id = distinct;
		distinct++;
	}
	
	public User getUser() {
		return user;
	}

	public String getEventType() {
		return eventType;
	}
	
	public String toString() {
		return this.eventType;
	}
	
}
