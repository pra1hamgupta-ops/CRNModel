package javaProject;

import static javaProject.Constants.*;


public class FuzzySU {
	
	double[][][] ruleBase;
	double data;
	double performance;
	double recenty;
	
	public FuzzySU(double data, double performance, double recenty) {
		ruleBase = new double[3][3][3];
		computeRuleBase();
		this.data = data;
		this.performance = performance;
		this.recenty = recenty;
	}
	
	public String getPriority() {
		double[] dataTypeMembership = new double[3];
		dataTypeMembership[0] = nonRealTime(data);
		dataTypeMembership[1] = realTime(data);
		dataTypeMembership[2] = urgent(data);
		
		double[] performanceMembership = new double[3];
		performanceMembership[0] = worsePerformance(performance);
		performanceMembership[1] = moderatePerformance(performance);
		performanceMembership[2] = goodPerformance(performance);
		
		
		
		double[] recentyMembership = new double[3];
		recentyMembership[0] = old(recenty);
		recentyMembership[1] = moderatelyRecent(recenty);
		recentyMembership[2] = recent(recenty);
		
		
		double[][][] ruleStrength = new double[3][3][3];
		
		for(int d = 0; d < 3; d++) {
			for(int p = 0; p < 3; p++) {
				for(int r = 0; r< 3; r++) {
					ruleStrength[d][p][r] = Math.min(dataTypeMembership[d], Math.min(performanceMembership[p],recentyMembership[r]));
				}
			}
		}
		
		int D = 0;
		int P = 0;
		int R = 0;
		double maxStrength = -1;
		
		for(int d = 0; d < 3; d++) {
			for(int p = 0; p < 3; p++) {
				for(int r = 0; r< 3; r++) {
					double strength = ruleStrength[d][p][r];
					if(strength > maxStrength) {
						D= d;
						P= p;
						R= r;
						maxStrength = strength;
					}
				}
			}
		}
		
		double priorityDouble = ruleBase[D][P][R];
		
		
		
		double[] priorityMemberShip = new double[5];
		priorityMemberShip[0] = veryLowPriority(priorityDouble);
		priorityMemberShip[1] = lowPriority(priorityDouble);
		priorityMemberShip[2] = moderatePriority(priorityDouble);
		priorityMemberShip[3] = highPriority(priorityDouble);
		priorityMemberShip[4] = veryHighPriority(priorityDouble);
		
		
		double maxPriorityMembership = -1;
		for(int i = 0; i < 5; i++) {
			maxPriorityMembership = Math.max(maxPriorityMembership, priorityMemberShip[i] );
		}
		
		if(maxPriorityMembership == priorityMemberShip[0]) {
			return VERY_LOW_PRIORITY;
		}else if(maxPriorityMembership == priorityMemberShip[1]) {
			return LOW_PRIORITY;
		}else if(maxPriorityMembership == priorityMemberShip[2]) {
			return MODERATE_PRIORITY;
		}else if(maxPriorityMembership == priorityMemberShip[3]) {
			return HIGH_PRIORITY;
		}else return VERY_HIGH_PRIORITY;
		
	}
	
	private void computeRuleBase() {
		for(int d = 0; d < 3; d++) {
			for(int p = 0; p < 3; p++) {
				for(int r = 0; r < 3; r++) {
					double dataType = d/2d;
					double performance = p/2d;
					double recent = r/2d;
					double priority = (dataType + performance + recent)/3d;
					ruleBase[d][p][r] = priority;
				}
			}
		}
	}
	
	private double veryLowPriority(double x) {
		if(x < 0 || x > 0.4) return 0;
		if(x >= 0 && x <= 0.2) {
			return 5d*x;
		}else {
			return 2d - 5d*x;
		}
	}

	
	private double lowPriority(double x) {
		if(x < 0.2 || x > 0.6) return 0;
		if(x >= 0.2 && x <= 0.4) {
			return (x - 0.2d)/0.2d;
		}else {
			return (0.6d - x)/(0.2d);
		}
	}
	
	private double moderatePriority(double x) {
		if(x < 0.4 || x > 0.8) return 0;
		if(x >= 0.4 && x <= 0.6) {
			return (x - 0.4d)/0.2d;
		}else {
			return (0.8d - x)/0.2d;
		}
	}
	
	private double highPriority(double x) {
		if(x < 0.6 || x > 1) return 0;
		if(x >= 0.6 && x <= 0.8) {
			return (x - 0.6d)/0.2d;
		}else {
			return (1d - x)/0.2d;
		}
	}
	
	private double veryHighPriority(double x) {
		if(x < 0.8 || x > 1) return 0;
		return (x - 0.8d)/0.2d;
	}
	
	
	private double urgent(double x) {
		if(x < 0.5 || x > 1) return 0;
		return 2*x -1;
	}
	
	private double realTime(double x) {
		if(x < 0 || x > 1) return 0;
		if(x >= 0 && x <= 0.5) {
			return 2*x;
		}else {
			return 2*(1-x);
		}
	}
	
	private double nonRealTime(double x) {
		if(x < 0 || x > 0.5) return 0;
		return 1 - 2*x;
	}
	
	
	
	private double goodPerformance(double x) {
		if(x < 0.5 || x > 1) return 0;
		return 2*x -1;
	}
	
	private double moderatePerformance(double x) {
		if(x < 0 || x > 1) return 0;
		if(x >= 0 && x <= 0.5) {
			return 2*x;
		}else {
			return 2*(1-x);
		}
	}
	
	private double worsePerformance(double x) {
		if(x < 0 || x > 0.5) return 0;
		return 1 - 2*x;
	}
	
	
	private double recent(double x) {
		if(x < 0.5 || x > 1) return 0;
		return 2*x -1;
	}

	private double moderatelyRecent(double x) {
		if(x < 0 || x > 1) return 0;
		if(x >= 0 && x <= 0.5) {
			return 2*x;
		}else {
			return 2*(1-x);
		}
	}
	
	private double old(double x) {
		if(x < 0 || x > 0.5) return 0;
		return 1 - 2*x;
	}
}
