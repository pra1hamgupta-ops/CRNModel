package javaProject;
import static javaProject.User.*;
import static javaProject.Event.*;

import static javaProject.Constants.*;

public class Project {

	public static void main(String[] args) {
		
		int channels = 6;
		double lambdaP = 1;
		double muP = 0.5;
		double lambdaS = 2;
		double muHS = 1;
		double muLS = 1;
		int minHSU = 1;
		int minLSU = 2;
		int maxHSU = 4;
		int maxLSU = 4;
		int hpqSize = 1;
		int lpqSize = 2;
		double allocationFactor = 0.5;
		int tmax = 5000;
		
		int count = 50;
		
		int cumulativeCIDT = 0;
		
		int cumulativeTotalUsers = 0;
		
		int cumulativeTotalPU = 0;
		int cumulativePuBlocked = 0;
		int cumulativePuTransmitted = 0;
		int cumulativePuInTheChannel = 0;
		
		int cumulativeTotalLSU = 0;
		int cumulativeLsuTerminated = 0;
		int cumulativeLsuBlocked = 0;
		int cumulativeLsuTransmitted = 0;
		int cumulativeLsuInTheChannel = 0;
		int cumulativeLsuInTheQueue = 0;
		
		int cumulativeTotalHSU = 0;
		int cumulativeHsuTerminated = 0;
		int cumulativeHsuBlocked = 0;
		int cumulativeHsuTransmitted = 0;
		int cumulativeHsuInTheChannel = 0;
		int cumulativeHsuInTheQueue = 0;
		
		long cumulativePuCWTS = 0;
		long cumulativeLsuCWTS = 0;
		long cumulativeLsuCWTQ = 0;
		long cumulativeHsuCWTS = 0;
		long cumulativeHsuCWTQ = 0;
		
		
		double cumulativeMeanNumberOfPUInTheSystem = 0;
		double cumulativeMeanNumberOfLSUInTheSystem = 0;
		double cumulativeMeanNumberOfHSUInTheSystem = 0;
		
		
		double cumulativeMeanNumberOfLSUInTheQueue = 0;
		double cumulativeMeanNumberOfHSUInTheQueue = 0;
		
		double cumulativeMeanWaitingTimeOfPUInTheSystem = 0;
		double cumulativeMeanWaitingTimeOfLSUInTheSystem = 0;
		double cumulativeMeanWaitingTimeOfHSUInTheSystem = 0;
		
		double cumulativeMeanWaitingTimeOfLSUInTheQueue = 0;
		double cumulativeMeanWaitingTimeOfHSUInTheQueue = 0;
		
		double cumulativeEstimateOfTheProportionOfIdleTimeOfChannels =  0;
		
		double cumulativeLsuBlockingProbability = 0;
		double cumulativeHsuBlockingProbability = 0;
		
		double cumulativeLsuTerminationProbability = 0;
		double cumulativeHsuTerminationProbability = 0;
		
		double cumulativeLsuArrivalRate = 0;
		double cumulativeHsuArrivalRate = 0;
		
		double cumulativePuThroughput = 0;
		double cumulativeLsuThroughput = 0;
		double cumulativeHsuThroughput = 0;
		
		
		for(int t = 0; t < count; t++) {
			QueuingSystem queuingSystem = new QueuingSystem(channels, lambdaP, muP, lambdaS, muHS,muLS, minLSU, maxLSU, minHSU, maxHSU, hpqSize, lpqSize, allocationFactor, tmax);
			int CIDT = queuingSystem.simulate();
			
			cumulativeCIDT += CIDT;
			
			cumulativeTotalUsers += userStatus.size();
			
			int totalPU = 0;
			int puBlocked = 0;
			int puTransmitted = 0;
			int puInTheChannel = 0;
			
			int totalLSU = 0;
			int lsuTerminated = 0;
			int lsuBlocked = 0;
			int lsuTransmitted = 0;
			int lsuInTheChannel = 0;
			int lsuInTheQueue = 0;
			
			int totalHSU = 0;
			int hsuTerminated = 0;
			int hsuBlocked = 0;
			int hsuTransmitted = 0;
			int hsuInTheChannel = 0;
			int hsuInTheQueue = 0;
			
			for(int id : userStatus.keySet()) {
				User user= getUserById.get(id);
				String userType = userMap.get(user);
				String status = userStatus.get(id);
				
				if(userType.equals(PU)) {
					totalPU++;
					if(status.equals(BLOCKED)) {
						puBlocked++;
					}else if(status.equals(TRANSMISSION_COMPLETED)) {
						puTransmitted++;
					}else if(status.equals(TRANSMITTING)) {
						puInTheChannel++;
					}
				}
				else if(userType.equals(LSU)) {
					totalLSU++;
					if(status.equals(TERMINATED)) {
						lsuTerminated++;
					}else if(status.equals(BLOCKED)) {
						lsuBlocked++;
					}else if(status.equals(TRANSMISSION_COMPLETED)) {
						lsuTransmitted++;
					}else if(status.equals(TRANSMITTING)) {
						lsuInTheChannel++;
					}else if(status.equals(IN_THE_QUEUE)) {
						lsuInTheQueue++;
					}
				}
				else if(userType.equals(HSU)) {
					totalHSU++;
					if(status.equals(TERMINATED)) {
						hsuTerminated++;
					}else if(status.equals(BLOCKED)) {
						hsuBlocked++;
					}else if(status.equals(TRANSMISSION_COMPLETED)) {
						hsuTransmitted++;
					}else if(status.equals(TRANSMITTING)) {
						hsuInTheChannel++;
					}else if(status.equals(IN_THE_QUEUE)) {
						hsuInTheQueue++;
					}
				}
			}
			
			
			cumulativeTotalPU += totalPU;
			cumulativePuBlocked += puBlocked;
			cumulativePuTransmitted += puTransmitted;
			cumulativePuInTheChannel += puInTheChannel;
			
			cumulativeTotalLSU +=  totalLSU;
			cumulativeLsuTerminated += lsuTerminated;
			cumulativeLsuBlocked += lsuBlocked;
			cumulativeLsuTransmitted += lsuTransmitted;
			cumulativeLsuInTheChannel += lsuInTheChannel;
			cumulativeLsuInTheQueue += lsuInTheQueue;
			
			cumulativeTotalHSU += totalHSU;
			cumulativeHsuTerminated += hsuTerminated;
			cumulativeHsuBlocked += hsuBlocked;
			cumulativeHsuTransmitted += hsuTransmitted;
			cumulativeHsuInTheChannel += hsuInTheChannel;
			cumulativeHsuInTheQueue += hsuInTheQueue;
			
			long puCWTS = 0;
			long lsuCWTS = 0;
			long lsuCWTQ = 0;
			long hsuCWTS = 0;
			long hsuCWTQ = 0;
			
			for(int id : getUserById.keySet()) {
				User user = getUserById.get(id);
				String userType = userMap.get(user);
				if(userType.equals(PU)) {
					if(user.getInTime() == null) {
					}else {
						if(user.getOutTime() == null) {
							puCWTS += tmax - user.getInTime();
						}else {
							puCWTS += user.getOutTime() - user.getInTime();
						}
					}
				}else if(userType.equals(LSU)) {
					if(user.getInTime() == null) {
					}else {
						if(user.getOutTime() == null) {
							lsuCWTS += tmax - user.getInTime();
						}else {
							lsuCWTS += user.getOutTime() - user.getInTime();
						}
					}
					
					if(user.getQueueTime() == null) {
					}else {
						lsuCWTQ += user.getQueueTime();
					}
				}else if(userType.equals(HSU)) {
					if(user.getInTime() == null) {
					}else {
						if(user.getOutTime() == null) {
							hsuCWTS += tmax - user.getInTime();
						}else {
							hsuCWTS += user.getOutTime() - user.getInTime();
						}
					}
					
					if(user.getQueueTime() == null) {
					}else {
						hsuCWTQ += user.getQueueTime();
					}
				}
			}
			
			cumulativePuCWTS += puCWTS;
			cumulativeLsuCWTS += lsuCWTS;
			cumulativeLsuCWTQ += lsuCWTQ;
			cumulativeHsuCWTS += hsuCWTS;
			cumulativeHsuCWTQ += hsuCWTQ;
			
			cumulativeMeanNumberOfPUInTheSystem += puCWTS/(1d*tmax);
			cumulativeMeanNumberOfLSUInTheSystem += lsuCWTS/(1d*tmax);
			cumulativeMeanNumberOfHSUInTheSystem += hsuCWTS/(1d*tmax);
			
			cumulativeMeanNumberOfLSUInTheQueue += lsuCWTQ/(1d*tmax);
			cumulativeMeanNumberOfHSUInTheQueue += hsuCWTQ/(1d*tmax);
			
			cumulativeMeanWaitingTimeOfPUInTheSystem += puCWTS/(1d*totalPU);
			cumulativeMeanWaitingTimeOfLSUInTheSystem += lsuCWTS/(1d*totalLSU);
			cumulativeMeanWaitingTimeOfHSUInTheSystem += hsuCWTS/(1d*totalHSU);

			cumulativeMeanWaitingTimeOfLSUInTheQueue += lsuCWTQ/(1d*totalLSU);
			cumulativeMeanWaitingTimeOfHSUInTheQueue += hsuCWTQ/(1d*totalHSU);
			
			cumulativeEstimateOfTheProportionOfIdleTimeOfChannels +=  CIDT/(1d*channels*tmax);
			
			cumulativeLsuBlockingProbability += lsuBlocked/(1d*totalLSU);
			cumulativeHsuBlockingProbability += hsuBlocked/(1d*totalHSU);
			
			cumulativeLsuTerminationProbability += lsuTerminated/(1d*totalLSU);
			cumulativeHsuTerminationProbability += hsuTerminated/(1d*totalHSU);

			cumulativeLsuArrivalRate += totalLSU/(1d*tmax);
			cumulativeHsuArrivalRate += totalHSU/(1d*tmax);
			
			cumulativePuThroughput += puTransmitted/(1d*tmax);
			cumulativeLsuThroughput += lsuTransmitted/(1d*tmax);
			cumulativeHsuThroughput += hsuTransmitted/(1d*tmax);
			
			flushStaticUser();
			flushStaticEvent();
			
		}
		
		
		
		System.out.println();
		System.out.println("CIDT -> " + cumulativeCIDT/count);
		System.out.println("Estimate Of The Proportion Of Idle Time Of Channels -> " + cumulativeEstimateOfTheProportionOfIdleTimeOfChannels/count);
		System.out.println();
		System.out.println("Total Users -> " + cumulativeTotalUsers/count);
		
		System.out.println();
		System.out.println("PU");
		System.out.println("Total Number -> " + cumulativeTotalPU/count);
		System.out.println("Transmitting -> " + cumulativePuInTheChannel/count);
		System.out.println("Transmission Completed -> " + cumulativePuTransmitted/count);
		System.out.println("Blocked -> " + cumulativePuBlocked/count);
		System.out.println("CWTS -> " + cumulativePuCWTS/count);
		System.out.println("Mean Number Of PU In The System -> " + cumulativeMeanNumberOfPUInTheSystem/count);
		System.out.println("Mean Waiting Time Of PU In The System -> " + cumulativeMeanWaitingTimeOfPUInTheSystem/count);
		System.out.println("Throughput -> " + cumulativePuThroughput/count);
		
		System.out.println();
		System.out.println("LSU");
		System.out.println("Total Number -> " + cumulativeTotalLSU/count);
		System.out.println("Transmitting -> " + cumulativeLsuInTheChannel/count);
		System.out.println("Transmission Completed -> " + cumulativeLsuTransmitted/count);
		System.out.println("Blocked -> " + cumulativeLsuBlocked/count);
		System.out.println("Terminated -> " + cumulativeLsuTerminated/count);
		System.out.println("In the Queue -> " + cumulativeLsuInTheQueue/count);
		System.out.println("Arrivate Rate -> " + cumulativeLsuArrivalRate/count);
		System.out.println("CWTS -> " + cumulativeLsuCWTS/count);
		System.out.println("CWTQ -> " + cumulativeLsuCWTQ/count);
		System.out.println("Mean Number Of LSU In The System -> " + cumulativeMeanNumberOfLSUInTheSystem/count);
		System.out.println("Mean Waiting Time Of LSU In The System -> " + cumulativeMeanWaitingTimeOfLSUInTheSystem/count);
		System.out.println("Mean Number Of LSU In The Queue -> " + cumulativeMeanNumberOfLSUInTheQueue/count);
		System.out.println("Mean Waiting Time Of LSU In The Queue -> " + cumulativeMeanWaitingTimeOfLSUInTheQueue/count);
		System.out.println("Blocking Probability -> " + cumulativeLsuBlockingProbability/count);
		System.out.println("Forced Termination Probability -> " + cumulativeLsuTerminationProbability/count);
		System.out.println("Throughput -> " + cumulativeLsuThroughput/count);
		
		System.out.println();
		System.out.println("HSU");
		System.out.println("Total Number -> " + cumulativeTotalHSU/count);
		System.out.println("Transmitting -> " + cumulativeHsuInTheChannel/count);
		System.out.println("Transmission Completed -> " + cumulativeHsuTransmitted/count);
		System.out.println("Blocked -> " + cumulativeHsuBlocked/count);
		System.out.println("Terminated -> " + cumulativeHsuTerminated/count);
		System.out.println("In the Queue -> " + cumulativeHsuInTheQueue/count);
		System.out.println("Arrivate Rate -> " + cumulativeHsuArrivalRate/count);
		System.out.println("CWTS -> " + cumulativeHsuCWTS/count);
		System.out.println("CWTQ -> " + cumulativeHsuCWTQ/count);
		System.out.println("Mean Number Of HSU In The System -> " + cumulativeMeanNumberOfHSUInTheSystem/count);
		System.out.println("Mean Waiting Time Of HSU In The System -> " + cumulativeMeanWaitingTimeOfHSUInTheSystem/count);
		System.out.println("Mean Number Of HSU In The Queue -> " + cumulativeMeanNumberOfHSUInTheQueue/count);
		System.out.println("Mean Waiting Time Of HSU In The Queue -> " + cumulativeMeanWaitingTimeOfHSUInTheQueue/count);
		System.out.println("Blocking Probability -> " + cumulativeHsuBlockingProbability/count);
		System.out.println("Forced Termination Probability -> " + cumulativeHsuTerminationProbability/count);
		System.out.println("Throughput -> " + cumulativeHsuThroughput/count);
		
		for(int i = 0; i <= 30; i++) {
			System.out.print("_");
		}
		System.out.println();
		System.out.println();
		
		
	}

}
