package javaProject;
import java.util.*;

import static javaProject.Constants.*;
import static javaProject.QueuingSystem.*;

public class User implements Comparable<User>{
	static HashMap<User, String> userMap;
	static HashMap<Integer, User> getUserById;
	static HashMap<Integer, String> userStatus;
	static Integer distinct;
	private int id;
	private Integer inTime;
	private Integer outTime;
	private Integer queueInTime;
	private Integer queueTime;
	
	private double serviceRate;
	
	public double getServiceRate() {
		return serviceRate;
	}

	public void setServiceRate(double serviceRate) {
		this.serviceRate = serviceRate;
	}

	private Integer remainingTime;
	
	private Integer departureTime;
	private Integer departureEventId;
	
	public static void flushStaticUser() {
		userMap = null;
		getUserById = null;
		userStatus = null;
		distinct = null;
	}
	
	public User(double serviceRate) {
		if(distinct == null) distinct = 0;
		if(userMap == null) userMap = new HashMap<>();
		if(getUserById == null) getUserById = new HashMap<>();
		if(userStatus == null) userStatus = new HashMap<>();
		allocateId();
		getUserById.put(id, this);
		this.serviceRate = serviceRate;
		departureTime = null;
		departureEventId = null;
		remainingTime = null;
		
		queueInTime = null;
		queueTime = null;
		inTime = null;
		outTime = null;
	}
	
	public Integer getQueueTime() {
		return queueTime;
	}

	public void addQueueTime(int time) {
		if(queueTime == null) queueTime = 0;
		this.queueTime += time;
	}

	public Integer getInTime() {
		return inTime;
	}

	public void setInTime(int inTime) {
		this.inTime = inTime;
	}
	
	public Integer getQueueInTime() {
		return queueInTime;
	}

	public void setQueueInTime(int inTime) {
		this.queueInTime = inTime;
	}

	public Integer getOutTime() {
		return outTime;
	}

	public void setOutTime(int outTime) {
		this.outTime = outTime;
	}

	public Integer getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(Integer remainingTime) {
		this.remainingTime = remainingTime;
	}

	public boolean isCompletelyNew() {
		return departureTime == null;
	}
	
	

	private void allocateId() {
		this.id = distinct;
		distinct++;
	}

	public Integer getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Integer departureTime) {
		this.departureTime = departureTime;
	}

	public Integer getDepartureEventId() {
		return departureEventId;
	}

	public void setDepartureEventId(Integer departureEventId) {
		this.departureEventId = departureEventId;
	}

	@Override
	public int compareTo(User o) {
		return 1;
	}
	
	@Override
	public String toString() {
		return userMap.get(this) + id;
	}

	public void addDepartureEvent() {
		Event event = new Event(this, DEPARTURE);
		setDepartureEventId(event.id);
		addNextEvent(departureTime,this, event);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}