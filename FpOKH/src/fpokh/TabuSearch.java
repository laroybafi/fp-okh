package fpokh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class TabuSearch {
	int[][] timeslotTabuSearch, initialTimeslot, conflict_matrix, course_sorted;
	int[] timeslot;
	double[] tabuSearchPenaltyList1;
	String file;
	int jumlahexam, jumlahmurid, randomCourse, randomTimeslot, iterasi;
	double initialPenalty, bestPenalty, deltaPenalty;
	
	Schedule schedule;
	
	TabuSearch(String file, int[][] conflict_matrix, int[][] course_sorted, int jumlahexam, int jumlahmurid, int iterasi) { 
		this.file = file; 
		this.conflict_matrix = conflict_matrix;
		this.course_sorted = course_sorted;
		this.jumlahexam = jumlahexam;
		this.jumlahmurid = jumlahmurid;
		this.iterasi = iterasi;
	}
	
	
	public void getTimeslotByTabuSearch() {
		schedule = new Schedule(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		// initial solution
		timeslotTabuSearch = schedule.getSchedule();
		initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid);
		
		int[][] bestTimeslot = Evaluator.getTimeslot(timeslotTabuSearch); // handle current best timeslot
		int[][] bestcandidate  = Evaluator.getTimeslot(timeslotTabuSearch);
		int[][] timeslotTabuSearchSementara = Evaluator.getTimeslot(timeslotTabuSearch);
		
//		int timeslot_dibutuhkan = Arrays.stream(timeslot).max().getAsInt();
		
		//inisiasi tabulist
        LinkedList<int[][]> tabulist = new LinkedList<int[][]>();
        int maxtabusize = 10;
        tabulist.addLast(Evaluator.getTimeslot(timeslotTabuSearch));
        
      //inisiasi iterasi
        int maxiteration = 10000;
        int iteration=0;
        
      //inisasi itung penalty
        double penalty1 = 0;
        double penalty2 = 0;
        double penalty3 = 0;
        
        boolean terminate = false;
        
        while(!terminate){
            iteration++;
            
//            search candidate solution / search neighbor
//            sneighborhood = getneighbor(bestcandidate)
           ArrayList<int[][]> sneighborhood = new ArrayList<>();
//           ArrayList<Double> listPenalty = new ArrayList<>();     
              
//        		int[][] timeslotLLH;
        	LowLevelHeuristics lowLevelHeuristics = new LowLevelHeuristics(conflict_matrix);
        	timeslotTabuSearchSementara = lowLevelHeuristics.move1(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.swap2(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.move2(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.swap3(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.move3(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
				
        		
        		//membandingkan neighbor, pilih best neighbor, membandingkan juga apa ada di tabu list
           int j = 0;
           while (sneighborhood.size() > j) {
//        	   penalty2 = Evaluator.getPenalty(conflict_matrix, sneighborhood.get(j), jumlahmurid);
//               penalty1 = Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid);
               if( !(tabulist.contains(sneighborhood.get(j))) && 
            		   Evaluator.getPenalty(conflict_matrix, sneighborhood.get(j), jumlahmurid) < Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid))
                 bestcandidate = sneighborhood.get(j);
                	
               j++;
           }
                
           sneighborhood.clear();
                
           //bandingkan best neighbor dengan best best solution
           if(Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid) < Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid))
              timeslotTabuSearch = Evaluator.getTimeslot(bestcandidate);
                
           //masukkan best neighbor tadi ke tabu
           tabulist.addLast(bestcandidate);
           if(tabulist.size() > maxtabusize)
              tabulist.removeFirst();
                
           //return sbest;
           tabuSearchPenaltyList1 = new double[100];
           if ((iteration+1)%10 == 0)
               System.out.println("Iterasi: " + (iteration+1) + " memiliki penalty " + Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid));

//           for (int i = 0 ; i < iteration; i ++) {
//        	   tabuSearchPenaltyList1[i] = listPenalty.get(i);
//           }
           
           if (iteration == maxiteration) 
        	   terminate = true;
        }
        bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid);
        deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
        
        System.out.println("=============================================================");
		System.out.println("		Metode TABU SEARCH						 			 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : " + bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotTabuSearch) + "\n");
		System.out.println("=============================================================");
	}
	
	// return timeslot each algorithm
	public int[][] getTimeslotTabuSearch() { return timeslotTabuSearch; }
	
	// return timeslot each algorithm
	public int getJumlahTimeslotTabuSearch() { return schedule.getJumlahTimeSlot(timeslotTabuSearch); }
	
	public double[] getTabuSearchPenaltyList() { 
		return tabuSearchPenaltyList1;
	}
	
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
	
	private static double acceptanceProbability(double penaltySementara, double penaltyLLH, double temperature) {
		// If the new solution is better, accept it
//		if (penaltySementara < penaltyLLH)
//			return 1.0;
		
		// If the new solution is worse, calculate an acceptance probability
		return Math.exp((penaltySementara - penaltyLLH) / temperature);
	}
}