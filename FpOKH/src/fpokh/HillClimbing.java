/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fpokh;

/**
 *
 * @author royla
 */

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class HillClimbing {
	int[][] timeslotHillClimbing, initialTimeslot, conflict_matrix, course_sorted;
	int[] timeslot;
	String file;
	int jumlahexam, jumlahmurid, randomCourse, randomTimeslot, iterasi;
	double initialPenalty, bestPenalty, deltaPenalty;
	
	Schedule schedule;
	
	HillClimbing(String file, int[][] conflict_matrix, int[][] course_sorted, int jumlahexam, int jumlahmurid, int iterasi) { 
		this.file = file; 
		this.conflict_matrix = conflict_matrix;
		this.course_sorted = course_sorted;
		this.jumlahexam = jumlahexam;
		this.jumlahmurid = jumlahmurid;
		this.iterasi = iterasi;
	}
	

	public void getTimeslotByHillClimbing() throws IOException {
		schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		int[][] initialTimeslot = schedule.getSchedule(); // get initial solution
		timeslotHillClimbing = Evaluator.getTimeslot(initialTimeslot);
		initialPenalty = Evaluator.getPenalty(conflict_matrix, initialTimeslot, jumlahmurid);
		
		int[][] timeslotHillClimbingSementara = Evaluator.getTimeslot(timeslotHillClimbing); // handle temporary solution. if better than feasible, replace initial
		
		bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
		
		for(int i = 0; i < iterasi; i++) {
			try {
				randomCourse = random(jumlahexam); // random course
				randomTimeslot = random(schedule.getJumlahTimeSlot(initialTimeslot)); // random timeslot
				
				if (Schedule.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotHillClimbingSementara)) {	
					timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
					double penaltiAfterHillClimbing = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
					
					// compare between penalti. replace bestPenalty with penaltiAfterHillClimbing if initial penalti is greater
					if(bestPenalty > penaltiAfterHillClimbing) {
						bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
						timeslotHillClimbing[randomCourse][1] = timeslotHillClimbingSementara[randomCourse][1];
					} 
						else 
							timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
				}
				System.out.println("Iterasi ke " + (i+1) + " memiliki penalti : "+ bestPenalty);
			}
				catch (ArrayIndexOutOfBoundsException e) {
					//System.out.println("randomCourseIndex index ke- " + randomCourseIndex);
					//System.out.println("randomTimeslot index ke- " + randomTimeslot);
				}
			
		}
		
		deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
		       
                System.out.println("=============================================================");
		System.out.println("		Metode HILL CLIMBING								 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi" + "\n");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotHillClimbing) + "\n");
		System.out.println("=============================================================");
		
	}
	
	
	public int[][] getTimeslotHillClimbing() { return timeslotHillClimbing; }
	public int getJumlahTimeslotHC() { return schedule.getJumlahTimeSlot(timeslotHillClimbing); }
	private static int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}
	
	/**
	 * this method returns a random number n such that
	 * 0.0 <= n <= 1.0
	 * @return random such that 0.0 <= random <= 1.0
	 */
	private static double randomDouble() {
		Random r = new Random();
		return r.nextInt(1000) / 1000.0;
	}
	private static int random(int number) {
		Random random = new Random();
		return random.nextInt(number);
	}

}

class LowLevelHeuristics {

	static int[][] conflict_matrix;
	LowLevelHeuristics(int[][] conflictmatrix) {
		conflict_matrix = conflictmatrix;
	}
	
	public static int[][] move(int[][] timeslot, int jumlahmove) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		for (int i = 0; i < jumlahmove; i++) {
//			int randomCourse = randomNumber(1, timeslot.length);
//			int randomTimeSlot = randomNumber(1, Arrays.stream(slot).max().getAsInt());
			int randomCourse = random(timeslot.length);
			int randomTimeSlot = random(Arrays.stream(slot).max().getAsInt());
			System.out.println("number to random: " + Arrays.stream(slot).max().getAsInt());
			
			timeslotSementara[randomCourse][1] = randomTimeSlot;
		}
		
		return timeslotSementara;
	}
	public int[][] move1(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		
		int randomCourse = random(timeslot.length);
		int randomTimeSlot = random(Arrays.stream(slot).max().getAsInt());
//		System.out.println("number timeslot to random: " + randomTimeSlot);
		if (Schedule.checkRandomTimeslot(randomCourse, randomTimeSlot, conflict_matrix, timeslot))
			timeslotSementara[randomCourse][1] = randomTimeSlot;
		
		return timeslotSementara;
	}
	public int[][] move2(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		int randomCourse1 = random(timeslot.length);
		int randomCourse2 = random(timeslot.length);
		int randomTimeSlot1 = random(Arrays.stream(slot).max().getAsInt());
		int randomTimeSlot2 = random(Arrays.stream(slot).max().getAsInt());
//		System.out.println("number timeslot to random: " + randomTimeSlot1 + " , " + randomTimeSlot2);
		if (Schedule.checkRandomTimeslot(randomCourse1, randomTimeSlot1, conflict_matrix, timeslot))
			timeslotSementara[randomCourse1][1] = randomTimeSlot1;
	
		if (Schedule.checkRandomTimeslot(randomCourse2, randomTimeSlot2, conflict_matrix, timeslot))
			timeslotSementara[randomCourse2][1] = randomTimeSlot2;
		
		return timeslotSementara;
	}
	public int[][] move3(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		int[] slot = new int[timeslotSementara.length];
		
		for (int i=0 ; i < timeslotSementara.length; i++) {
			slot[i] = timeslotSementara[i][1];
		}
		
		int randomCourse1 = random(timeslot.length);
		int randomCourse2 = random(timeslot.length);
		int randomCourse3 = random(timeslot.length);
		int randomTimeSlot1 = random(Arrays.stream(slot).max().getAsInt());
		int randomTimeSlot2 = random(Arrays.stream(slot).max().getAsInt());
		int randomTimeSlot3 = random(Arrays.stream(slot).max().getAsInt());
//		System.out.println("number timeslot to random: " + randomTimeSlot1 + " , " + randomTimeSlot2 + " , " + randomTimeSlot3);
		if (Schedule.checkRandomTimeslot(randomCourse1, randomTimeSlot1, conflict_matrix, timeslot))
			timeslotSementara[randomCourse1][1] = randomTimeSlot1;
		
		if (Schedule.checkRandomTimeslot(randomCourse2, randomTimeSlot2, conflict_matrix, timeslot))
			timeslotSementara[randomCourse2][1] = randomTimeSlot2;
		
		if (Schedule.checkRandomTimeslot(randomCourse3, randomTimeSlot3, conflict_matrix, timeslot))
			timeslotSementara[randomCourse3][1] = randomTimeSlot3;
		
		return timeslotSementara;
	}
	
	public static int[][] swap(int[][] timeslot, int jumlahswap) {
		int[][] timeslotSementara = timeslot;
		
		for(int i=0; i < jumlahswap; i++) {
			int exam1 = randomNumber(0, timeslot.length);
			int exam2 = randomNumber(0, timeslot.length);
			
			int slot1 = timeslot[exam1][1];
			int slot2 = timeslot[exam2][1];
			
			timeslotSementara[exam1][1] = slot2;
			timeslotSementara[exam2][1] = slot1;
		}
		
		return timeslotSementara;
	}
	public int[][] swap2(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		
		
		int randomcourse1 = random(timeslot.length);
		int randomcourse2 = random(timeslot.length);
		while (randomcourse2 == randomcourse1) {
			randomcourse2 = random(timeslot.length);
		}
		
		int slot1 = timeslot[randomcourse1][1];
		int slot2 = timeslot[randomcourse2][1];
		
		if (Schedule.checkRandomTimeslot(randomcourse1, slot2, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse1][1] = slot2;
		}
		if (Schedule.checkRandomTimeslot(randomcourse2, slot1, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse2][1] = slot1;
		}
		return timeslotSementara;
	}
	public int[][] swap3(int[][] timeslot) {
		int[][] timeslotSementara = timeslot;
		
		int randomcourse1, randomcourse2, randomcourse3;
		do {
			randomcourse1 = random(timeslot.length);
			randomcourse2 = random(timeslot.length);
			randomcourse3 = random(timeslot.length);
		} while (randomcourse2 == randomcourse1 || randomcourse2 == randomcourse3 || randomcourse1 == randomcourse3);
		
		int slot1 = timeslot[randomcourse1][1];
		int slot2 = timeslot[randomcourse2][1];
		int slot3 = timeslot[randomcourse3][1];
			
//		timeslotSementara[randomcourse1][1] = slot2;
//		timeslotSementara[randomcourse2][1] = slot1;
		
		if (Schedule.checkRandomTimeslot(randomcourse1, slot2, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse1][1] = slot2;
		}
		if (Schedule.checkRandomTimeslot(randomcourse2, slot3, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse2][1] = slot3;
		}
		if (Schedule.checkRandomTimeslot(randomcourse3, slot1, conflict_matrix, timeslot)) {
			timeslotSementara[randomcourse3][1] = slot1;
		}
		return timeslotSementara;
	}
	private static int randomNumber(int min, int max) {
		Random random = new Random();
		try {
			return random.nextInt(max - min) + min;	
		}
			catch(Exception e) {
//				System.out.println("ERROR di nextInt: " + (max-min));
				//return random.nextInt(Math.abs(max - min)) + min;
				if (Math.abs(max - min) == 0) {
					return random.nextInt(Math.abs(max - min)+1) + min;
				}
					else
						return random.nextInt(Math.abs(max - min)) + min;
			}
	}
	
	private static int random(int number) {
		Random random = new Random();
		return random.nextInt(number);
	}
}