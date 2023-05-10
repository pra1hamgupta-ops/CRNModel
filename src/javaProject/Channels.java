package javaProject;
import java.util.*;


import static javaProject.Constants.*;
import static javaProject.Distributions.exponential;
import static javaProject.QueuingSystem.*;
import static javaProject.User.*;

public class Channels {

	TreeSet<Integer> pu;
	int puChannels;
	
	TreeMap<Integer, TreeSet<Integer>>  lsu;
	int lsuChannels;
	int minLSU;
	int maxLSU;
	
	TreeMap<Integer, TreeSet<Integer>>  hsu;
	int hsuChannels;
	int minHSU;
	int maxHSU;
	
	int channels;
	
	Queue<User> lpq;
	int lpqSize;
	
	Queue<User> hpq;
	int hpqSize;
	
	double allocationFactor;
	
	public Channels(int channels, int lpqSize, int hpqSize, int minLSU, int maxLSU, int minHSU, int maxHSU, double allocationFactor) {
		pu = new TreeSet<>();
		puChannels = 0;
		
		lsu = new TreeMap<>();
		lsuChannels = 0;
		this.minLSU = minLSU;
		this.maxLSU = maxLSU;
		
		for(int k = minLSU; k <= maxLSU; k++) lsu.put(k, new TreeSet<>());
		
		hsu = new TreeMap<>();
		hsuChannels = 0;
		this.minHSU = minHSU;
		this.maxHSU = maxHSU;
		
		for(int k = minHSU; k <= maxHSU; k++) hsu.put(k, new TreeSet<>());
		
		this.channels = channels;
		
		lpq = new LinkedList<>();
		this.lpqSize = lpqSize;
		
		hpq = new LinkedList<>();
		this.hpqSize = hpqSize;
		
		this.allocationFactor = allocationFactor;
		

	}
	
	public void arriveLSU(User user, int time) {
		addLSUtoTheQueue(user, time);
	}
	
	public void pollingStage(int time) {
		if(getFreeChannels() == 0) return;
		int channelsForLSU = (int)(getFreeChannels()*allocationFactor);
		
		while(lpq.size() > 0) {
			if(channelsForLSU >= minLSU) {
				channelsForLSU -= minLSU;
				User polledUser = lpq.poll();
				int timeSpentInQueue = time - polledUser.getQueueInTime();
				polledUser.addQueueTime(timeSpentInQueue);
				addLSUInTheChannel(polledUser, time, minLSU);
			}else break;
		}
		allocateFreeChannelsToTransmittingHSU();
		allocateFreeChannelsToTransmittingLSU();
	}

	public void arriveHSU(User user, int time) {
		if(getFreeChannels() >= minHSU) {
			int numChannels = Math.min(maxHSU, getFreeChannels());
			addHSUInTheChannel(user, time, numChannels);
		}else {
			if(!canHSUbeAddedInTheChannel(user, time)) {
				addHSUtoTheQueue(user, time);
			}
		}
	}
	
	private boolean canHSUbeAddedInTheChannel(User user,int time) {
		int max = Math.max(maxHSU, maxLSU);
		int min = Math.min(minHSU, minLSU);
		
		HashMap<Integer, List<Integer>> addHSU = new HashMap<>();
		HashMap<Integer, List<Integer>> removeHSU = new HashMap<>();
		
		for(int i = minHSU; i <= maxHSU; i++) {
			addHSU.put(i, new ArrayList<>());
			removeHSU.put(i, new ArrayList<>());
		}
		
		HashMap<Integer, List<Integer>> addLSU = new HashMap<>();
		HashMap<Integer, List<Integer>> removeLSU = new HashMap<>();
		
		for(int i = minLSU; i <= maxLSU; i++) {
			addLSU.put(i, new ArrayList<>());
			removeLSU.put(i, new ArrayList<>());
		}
		
		for(int c = max; getFreeChannels() < minHSU && c > min + 1; c--) {
			// LSU
			if(c > minLSU) {
				if(lsu.containsKey(c)) {
					List<Integer> users = new ArrayList<>(lsu.get(c));
					for(int id : users) {
						int need = minHSU - getFreeChannels();
						int donate = c - minLSU;
						
						// need <= donate
						int newChannels = c -need;
						if(donate < need) newChannels = minLSU;
						decreaseLSUChannels(Math.abs(newChannels - c));
						if(newChannels < c) {
							removeLSU.get(c).add(id);
							addLSU.get(newChannels).add(id);
						}
					}
				}
			}
			
			// HSU
			if(c > minHSU) {
				if(hsu.containsKey(c)) {
					List<Integer> users = new ArrayList<>(hsu.get(c));
					for(int id : users) {
						int need = minHSU - getFreeChannels();
						int donate = c - minHSU;
						
						// need <= donate
						int newChannels = c -need;
						if(donate < need) newChannels = minHSU;
						decreaseHSUChannels(Math.abs(newChannels - c));
						if(newChannels < c) {
							removeHSU.get(c).add(id);
							addHSU.get(newChannels).add(id);
						}
					}
				}
			}
		}
		
		//RemoveHSU
		for(int c : removeHSU.keySet()) {
			if(removeHSU.get(c).size() == 0) continue;
			for(int id : removeHSU.get(c)) {
				hsu.get(c).remove(id);
			}
		}
		
		//AddHSU
		for(int c : addHSU.keySet()) {
			if(addHSU.get(c).size() == 0) continue;
			for(int id : addHSU.get(c)) {
				hsu.get(c).add(id);
			}
		}
		
		// RemoveLSU
		for(int c : removeLSU.keySet()) {
			if(removeLSU.get(c).size() == 0) continue;
			for(int id : removeLSU.get(c)) {
				lsu.get(c).remove(id);
			}
		}
		
		// AddLSU
		for(int c : addLSU.keySet()) {
			if(addLSU.get(c).size() == 0) continue;
			for(int id : addLSU.get(c)) {
				lsu.get(c).add(id);
			}
		}
		
		
		if(getFreeChannels() == minHSU) {
			addHSUInTheChannel(user, time,minHSU);
			return true;
		}
		allocateFreeChannelsToTransmittingHSU();
		allocateFreeChannelsToTransmittingLSU();
		
		return false;
	}

	public void arrivePU(User user, int time) {
		if(getFreeChannels() > 0) {
			addPU(user, time);
		}else {
			if(hsuChannels > 0 || lsuChannels > 0) {
				transmittingPUafterTakingChannelFromTransmittingSU(user, time, Math.max(hsuChannels, lsuChannels));
			}else {
				userStatus.put(user.getId(), BLOCKED);
			}
		}
	}
	
	private void transmittingPUafterTakingChannelFromTransmittingSU(User user, int time, int number) {
		
		int max = 0;
		String SU = "";
		
		for(int key : hsu.keySet()) {
			if(hsu.get(key).size() > 0) {
				if(key >= max) {
					max = key;
					SU = HSU;
				}
			}
		}
		
		for(int key : lsu.keySet()) {
			if(lsu.get(key).size() > 0) {
				if(key >= max) {
					max = key;
					SU = LSU;
				}
			}
		}
		Integer id = null;
		if(SU.equals(LSU)) {
			id = lsu.get(max).pollFirst();
			decreaseLSUChannels(max);
		}else  {
			id = hsu.get(max).pollFirst();
			decreaseHSUChannels(max);
		}
		User su = getUserById.get(id);
		
		removeDepartureEvent(su);
		
		addPU(user, time);
		
		if(SU.equals(HSU)) {
			if(getFreeChannels() >= minHSU) {
				int numChannels = Math.max(Math.min(maxHSU, getFreeChannels()), minHSU);
				addHSUInTheChannel(su, time, numChannels);
			}else {
				addHSUtoTheQueue(su, time);
			}
		}else {
			if(getFreeChannels() >= minLSU) {
				int numChannels = Math.max(Math.min(maxLSU, getFreeChannels()), minLSU);
				addLSUInTheChannel(su, time, numChannels);
			}else {
				addLSUtoTheQueue(su, time);
			}
		}
	}

	private void addHSUtoTheQueue(User su, int time) {
		if(hpq.size() < hpqSize) {
			if(su.isCompletelyNew()) {
				su.setInTime(time);
			}
			su.setQueueInTime(time);
			changeStatusOfUser(su, time);
			userStatus.put(su.getId(), IN_THE_QUEUE);
			hpq.offer(su);
		}else {
			if(!su.isCompletelyNew()) {
				su.setOutTime(time);
				userStatus.put(su.getId(), TERMINATED);
			}else {
				userStatus.put(su.getId(), BLOCKED);
			}
			
		}
	}
	
	

	private void addLSUtoTheQueue(User su, int time) {
		if(lpq.size() < lpqSize) {
			if(su.isCompletelyNew()) {
				su.setInTime(time);
			}
			su.setQueueInTime(time);
			changeStatusOfUser(su, time);
			userStatus.put(su.getId(), IN_THE_QUEUE);
			lpq.offer(su);
		}else {
			if(!su.isCompletelyNew()) {
				su.setOutTime(time);
				userStatus.put(su.getId(), TERMINATED);
			}else {
				userStatus.put(su.getId(), BLOCKED);
			}
		}
	}
	
	private void changeStatusOfUser(User su, int currentTime) {
		if(!su.isCompletelyNew()) {
			int remainingTime = su.getDepartureTime() - currentTime;
			su.setRemainingTime(remainingTime);
		}
	}

	private void addPU(User user, int time) {
		if(user.isCompletelyNew()) {
			user.setInTime(time);
		}
		userStatus.put(user.getId(), TRANSMITTING);
		createNewDepartureEventForArrivalEvent(user, time);
		pu.add(user.getId());
		puChannels++;
	}

	public void departSU(User user, boolean isPollingStage, int time) {
		if(userMap.get(user).equals(HSU)) removeHSU(user, time);
		else removeLSU(user, time);
		
		if(isPollingStage) {
			allocateFreeChannelsToTransmittingHSU();
			allocateFreeChannelsToTransmittingLSU();
		}
		allocateHSUFromHPQ(time);
		
		if(isPollingStage) {
			allocateLSUFromLPQ(time);
		}
		
		allocateFreeChannelsToTransmittingHSU();
		
		allocateFreeChannelsToTransmittingLSU();
		
	}


	private void removeHSU(User user, int time) {
		user.setOutTime(time);
		int released = 0;
		for(int key : hsu.keySet()) {
			if(hsu.get(key).contains(user.getId())) {
				hsu.get(key).remove(user.getId());
				released = key;
				break;
			}
		}
		userStatus.put(user.getId(), TRANSMISSION_COMPLETED);
		decreaseHSUChannels(released);
	}
	
	private void removeLSU(User user, int time) {
		user.setOutTime(time);
		int released = -1;
		for(int key : lsu.keySet()) {
			if(lsu.get(key).contains(user.getId())) {
				lsu.get(key).remove(user.getId());
				released = key;
				break;
			}
		}
		userStatus.put(user.getId(), TRANSMISSION_COMPLETED);
		decreaseLSUChannels(released);
	}

	public void departPU(User user, boolean isPollingStage, int time) {
		removePU(user, time);
		
		if(isPollingStage) {
			allocateLSUFromLPQ(time);
		}
		allocateHSUFromHPQ(time);
		
		allocateFreeChannelsToTransmittingHSU();
		
		allocateFreeChannelsToTransmittingLSU();
	}
	
	private void removePU(User user, int time) {
		user.setOutTime(time);
		pu.remove(user.getId());
		userStatus.put(user.getId(), TRANSMISSION_COMPLETED);
		puChannels--;
	}

	private void allocateFreeChannelsToTransmittingHSU() {
		HashMap<Integer, List<Integer>> add = new HashMap<>();
		HashMap<Integer, List<Integer>> remove = new HashMap<>();
		
		for(int key : hsu.keySet()) {
			add.put(key, new ArrayList<>());
			remove.put(key, new ArrayList<>());
		}
		for(int key : hsu.keySet()) {
			for(int id : hsu.get(key)) {
				int need = maxHSU - key;
				int donate = getFreeChannels();
				int newChannels = key + donate;
				if(donate >= need) newChannels = maxHSU;
				if(newChannels > key) {
					remove.get(key).add(id);
					add.get(newChannels).add(id);
					increaseHSUChannels(newChannels - key);
				}
			}
		}
		
		for(int key : remove.keySet()) {
			for(int a : remove.get(key)) {
				hsu.get(key).remove(a);
			}
		}
		
		for(int key : add.keySet()) {
			for(int a : add.get(key)) {
				hsu.get(key).add(a);
			}
		}
	}


	private void allocateFreeChannelsToTransmittingLSU() {
		HashMap<Integer, List<Integer>> add = new HashMap<>();
		HashMap<Integer, List<Integer>> remove = new HashMap<>();
		
		for(int key : lsu.keySet()) {
			add.put(key, new ArrayList<>());
			remove.put(key, new ArrayList<>());
		}
		for(int key : lsu.keySet()) {
			for(int id : lsu.get(key)) {
				int need = maxLSU - key;
				int donate = getFreeChannels();
				int newChannels = key + donate;
				if(donate >= need) newChannels = maxLSU;
				if(newChannels > key) {
					remove.get(key).add(id);
					add.get(newChannels).add(id);
					increaseLSUChannels(newChannels - key);
				}
			}
		}
		
		for(int key : remove.keySet()) {
			for(int a : remove.get(key)) {
				lsu.get(key).remove(a);
			}
		}
		
		for(int key : add.keySet()) {
			for(int a : add.get(key)) {
				lsu.get(key).add(a);
			}
		}
	}
	

	public void allocateLSUFromLPQ(int time) {
		while(lpq.size() != 0 && getFreeChannels() >= minLSU ) {
			User polledUser = lpq.poll();
			int timeSpentInQueue = time - polledUser.getQueueInTime();
			polledUser.addQueueTime(timeSpentInQueue);
			addLSUInTheChannel(polledUser, time, minLSU);
		}
	}
	
	public void allocateHSUFromHPQ(int time) {
		while(hpq.size() != 0 && getFreeChannels() >= minHSU ) {
			User polledUser = hpq.poll();
			int timeSpentInQueue = time - polledUser.getQueueInTime();
			polledUser.addQueueTime(timeSpentInQueue);
			addHSUInTheChannel(polledUser, time, minHSU);
		}
	}

	private void addHSUInTheChannel(User user, int time, int numChannels) {
		if(user.isCompletelyNew()) {
			user.setInTime(time);
		}
		createNewDepartureEventForArrivalEvent(user, time);
		addHSU(numChannels, user, time);
	}
	
	private void addLSUInTheChannel(User user, int time, int numChannels) {
		createNewDepartureEventForArrivalEvent(user, time);
		addLSU(numChannels, user, time);
	}
	
	private void createNewDepartureEventForArrivalEvent(User user, int time) {
		if(user.isCompletelyNew()) {
			int service = 0;
			while(service == 0) {
				service = exponential(user.getServiceRate());
			}
			user.setDepartureTime(time + service);
			user.setRemainingTime(service);
		}else {
			int remaining = user.getRemainingTime();
			int departureTime = time + remaining;
			user.setDepartureTime(departureTime);
		}
		
		user.addDepartureEvent();
	}
	
	private void addLSU(int num, User user, int time) {
		
		userStatus.put(user.getId(), TRANSMITTING);
		lsu.get(num).add(user.getId());
		increaseLSUChannels(num);
		
	}
	
	private void addHSU(int num, User user, int time) {
		userStatus.put(user.getId(), TRANSMITTING);
		hsu.get(num).add(user.getId());
		increaseHSUChannels(num);
		
	}
	
	private void increaseLSUChannels(int addedChannels) {
		lsuChannels += addedChannels;
	}
	
	private void increaseHSUChannels(int addedChannels) {
		hsuChannels += addedChannels;
	}
	
	private void decreaseHSUChannels(int removedChannels) {
		hsuChannels -= removedChannels;
	}
	
	private void decreaseLSUChannels(int removedChannels) {
		lsuChannels -= removedChannels;
	}
	
	public int getFreeChannels() {      
		return channels - (puChannels + lsuChannels + hsuChannels);
	}

	public void adjustQueueTimeAfterCompletion(int tmax) {
		Queue<User> LPQ = new LinkedList<>(lpq);
		while(LPQ.size() > 0) {
			User polledUser = LPQ.poll();
			int timeSpentInQueue = tmax - polledUser.getQueueInTime();
			polledUser.addQueueTime(timeSpentInQueue);
		}
		Queue<User> HPQ = new LinkedList<>(hpq);
		while(HPQ.size() > 0) {
			User polledUser = HPQ.poll();
			int timeSpentInQueue = tmax - polledUser.getQueueInTime();
			polledUser.addQueueTime(timeSpentInQueue);
		}
	}

	
	
}
