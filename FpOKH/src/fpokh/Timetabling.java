package fpokh;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author royla
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

class Timetabling {
    //load dataset
    //Path Dataset Bafi: D:\Kuliah\SEMESTER 6\[Pilihan - RDIB] Optimasi Kombinatorik Heuristik\FP\Toronto\
    //Path Dataset Jauhar: C:\Users\Jauhar\Documents\Kuliah Semester 6\OKH\FP\Toronto\
    static String folderDataset = "C:\\Users\\Jauhar\\Documents\\Kuliah Semester 6\\OKH\\FP\\Toronto\\";
    static String[][] file = {	{"car-f-92", "car-f-92"}, {"car-s-91", "car-s-91"}, {"ear-f-83", "ear-f-83"}, {"hec-s-92", "hec-s-92"}, 
					{"kfu-s-93", "kfu-s-93"}, {"lse-f-91", "lse-f-91"}, {"pur-s-93", "pur-s-93"}, {"rye-s-93", "rye-s-93"}, {"sta-f-83", "sta-f-83"},
					{"tre-s-92", "tre-s-92"}, {"uta-s-92", "uta-s-92"}, {"ute-s-92", "ute-s-92"}, {"yor-f-83", "yor-f-83"}
				};
    // fill with course & its timeslot
    static int timeslot[]; 
    static int[][] conflict_matrix, course_sorted, hasil_timeslot;
	
    private static Scanner scanner;
	
    public static void main(String[] args) throws IOException {
        scanner = new Scanner(System.in);
        for	(int i=0; i< file.length; i++)
        	System.out.println(i+1 + ". Penjadwalan " + file[i][1]);
        
        System.out.print("\nSilahkan pilih file untuk dijadwalkan : ");
        int pilih = scanner.nextInt();
        
        String filePilihanInput = file[pilih-1][0];
        String filePilihanOutput = file[pilih-1][1];
        
        String file = folderDataset + filePilihanInput;
       	
        Course course = new Course(file);
        int jumlahexam = course.getCourseTotal();
        
        conflict_matrix = course.getConflictMatrix();
        int jumlahmurid = course.getStudentsTotal();
        
	// sort exam by degree
	course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		
	// scheduling
	/*
	 * Scheduling by largest degree
	 */	
	long starttimeLargestDegree = System.nanoTime();
	Schedule schedule = new Schedule(file, conflict_matrix, jumlahexam);
	timeslot = schedule.schedulingBySaturationDegree(course_sorted,timeslot);
	int[][] timeslotByLargestDegree = schedule.getSchedule();
	long endtimeLargestDegree = System.nanoTime();
        
        /*
	 * params 1: file to be scheduling
	 * params 2: conflict matrix from file
	 * params 3: sort course by degree
	 * params 4: how many course from file
	 * params 5: how many student from file
	 * params 6: how many iterations
	 */
	HillClimbing HillClimbing = new HillClimbing(file, conflict_matrix, course_sorted, jumlahexam, jumlahmurid, 1000000);
	/*
	 * use hill climbing for timesloting
	 */
//	long starttimeHC = System.nanoTime();
//	HillClimbing.getTimeslotByHillClimbing(); // use hillclimbing methode for iterates 1000000 times
//	long endtimeHC = System.nanoTime();
	
//      SIMULATED ANNEALING
//      params : temperature
        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(file, conflict_matrix, course_sorted, jumlahexam, jumlahmurid, 10000);
        long starttimeSA = System.nanoTime();
        simulatedAnnealing.getTimeslotBySimulatedAnnealing(100.0);
        long endtimeSA = System.nanoTime();

	System.out.println("PENJADWALAN UNTUK " + filePilihanOutput + "\n");
	System.out.println("INITIAL SOLUTION - LARGEST DEGREE FIRST");	
	System.out.println("Timeslot 	: " + schedule.getJumlahTimeSlot(schedule.getSchedule()));
	System.out.println("Penalty         : " + Evaluator.getPenalty(conflict_matrix, schedule.getSchedule(), jumlahmurid));
	System.out.println("Running Time    : " + ((double) (endtimeLargestDegree - starttimeLargestDegree)/1000000000) + " detik.\n");
        
//        System.out.println("HILL CLIMBING");
//        System.out.println("Timeslot	: " + HillClimbing.getJumlahTimeslotHC());
//	System.out.println("Penalty         : " + Evaluator.getPenalty(conflict_matrix, HillClimbing.getTimeslotHillClimbing(), jumlahmurid));
//	System.out.println("Running Time    : " + ((double) (endtimeHC - starttimeHC)/1000000000) + " detik.\n");
        
        System.out.println("SIMULATED ANNEALING");
        System.out.println("Timeslot dibutuhkan (menggunakan Simulated Annealing) 		: " + simulatedAnnealing.getJumlahTimeslotSA());
        System.out.println("Penalti Simulated Annealing 					: " + Evaluator.getPenalty(conflict_matrix, simulatedAnnealing.getTimeslotSA(), jumlahmurid));
        System.out.println("Waktu eksekusi yang dibutuhkan Simmulated Annealing " + ((double) (endtimeSA - starttimeSA)/1000000000) + " detik.\n");
        System.out.println("");
    }
}


class Course {
    int studentsTotal;
    
    String fileInput;
	
    public Course(String fileInput) { this.fileInput = fileInput; }
    public int getStudentsTotal() { return this.studentsTotal; }
    
    // Melihat total course
    public int getCourseTotal() throws IOException {
        int courseTotal = 0;
	BufferedReader readCourse = new BufferedReader(new FileReader(fileInput + ".crs"));
	while (readCourse.readLine() != null) 
	courseTotal++; 
	readCourse.close();
	return courseTotal;
    }
    
    // Membuat Conflix Matrix
    public int[][] getConflictMatrix() throws IOException {
        int[][] conflict_matrix = new int[getCourseTotal()][getCourseTotal()];
     	for (int i=0; i<conflict_matrix.length; i++)
            for(int j=0; j<conflict_matrix.length; j++)
     		conflict_matrix[i][j] = 0;
     	
	BufferedReader readStudent = new BufferedReader(new FileReader(fileInput + ".stu"));
	String spasi = " ";
	while ((spasi = readStudent.readLine()) != null) {
            studentsTotal++;
            String tmp [] = spasi.split(" ");
            if	(tmp.length > 1) {
		for(int i=0; i<tmp.length; i++)
                    for(int j=i+1; j<tmp.length; j++) {
			conflict_matrix[Integer.parseInt(tmp[i])-1][Integer.parseInt(tmp[j])-1]++;
			conflict_matrix[Integer.parseInt(tmp[j])-1][Integer.parseInt(tmp[i])-1]++;
                    }
            }
	}
	readStudent.close();
	return conflict_matrix;
    }

    public int [][] sortingByDegree(int[][] conflictmatrix, int jumlahcourse) {
	int[][] course_degree = new int [jumlahcourse][2];
	int degree = 0;
	for (int i=0; i<course_degree.length; i++)
            for (int j=0; j<course_degree[0].length; j++)
		course_degree[i][0] = i+1; // fill course_sorted column 1 with course index
		
    	for (int i=0; i<jumlahcourse; i++) {
            for (int j=0; j<jumlahcourse; j++)
		if(conflictmatrix[i][j] > 0)
		degree++;
		else
		degree = degree;					
		course_degree[i][1] = degree; // fill amount of degree for each course
		degree=0;
	}
        
    	// sorting by degree
    	int[][] max = new int[1][2]; // make max array with 1 row 2 column. untuk ngehandle degree
    	max[0][0] = -1;
		max[0][1] = -1;
		int x = 0;
		int[][] course_sorted = new int[jumlahcourse][2];
		
		for(int a=0; a<course_degree.length; a++) {
			for(int i=0; i<course_degree.length; i++) {
				if(max[0][1]<course_degree[i][1]) {
					max[0][0] = course_degree[i][0];
					max[0][1] = course_degree[i][1];
					x = i;
				}				
			}
			course_degree[x][0] = -2;
			course_degree[x][1] = -2;
			course_sorted[a][0] = max[0][0];
			course_sorted[a][1] = max[0][1];
			max[0][0] = -1;
			max[0][1] = -1;
		
		}
		return course_sorted;
	}
	
}

class Schedule {
	
	String file;
	int[][] conflictmatrix;
	int[] timeslot;
	int jumlahexam, timeslotindex;
	
	public Schedule(String file, int[][] conflictmatrix, int jumlahexam) {
		this.file = file;
		this.conflictmatrix = conflictmatrix;
		this.jumlahexam = jumlahexam;
	}
	
	public int[][] getSchedule() {
		// fill hasiltimeslot array
		int [][] timeslotSchedule = new int[jumlahexam][2];
    	for (int course = 0; course < jumlahexam; course++) {
    		timeslotSchedule[course][0] = (course+1);
    		timeslotSchedule[course][1] = timeslot[course];
    	}
		return timeslotSchedule; 
	}
	
	public int[] scheduling(int[] timeslot) {
		this.timeslot = new int[jumlahexam];
		timeslotindex = 1;
    	for(int i= 0; i < conflictmatrix.length; i++)
    		this.timeslot[i] = 0;
    	
		for(int i = 0; i < conflictmatrix.length; i++) {
			for (int j = 1; j <= timeslotindex; j++) {
				if(isTimeslotAvailable(i, j, conflictmatrix, timeslot)) {
					this.timeslot[i] = j;
					break;
				}
					else
						timeslotindex = timeslotindex+1;
			}
		}
		return this.timeslot;
	}
	public int[] schedulingByDegree(int [][] sortedCourse) {
    	this.timeslot = new int[jumlahexam];
    	timeslotindex = 1; // starting timeslot from 1
    	for(int i= 0; i < sortedCourse.length; i++)
    		this.timeslot[i] = 0;
    	
		for(int course = 0; course < sortedCourse.length; course++) {
			for (int time_slotindex = 1; time_slotindex <= timeslotindex; time_slotindex++) {
				if(isTimeslotAvailableWithSorted(course, time_slotindex, conflictmatrix, sortedCourse, this.timeslot)) {
					this.timeslot[sortedCourse[course][0]-1] = time_slotindex;
					break;
				}
					else
						timeslotindex = timeslotindex+1; // move to ts+1 if ts is crash
			}
		}
		return this.timeslot;
    }
	
	public int[] schedulingBySaturationDegree(int [][] sortedCourse, int[] timeslot) {
    	this.timeslot = new int[jumlahexam];
    	timeslotindex = 1; // starting timeslot from 1
    	for(int i= 0; i < sortedCourse.length; i++)
    		this.timeslot[i] = 0;
    	
		for(int course = 0; course < sortedCourse.length; course++) {
			for (int time_slotindex = 1; time_slotindex <= timeslotindex; time_slotindex++) {
				if(isTimeslotAvailableWithSaturation(course, time_slotindex, conflictmatrix, sortedCourse, this.timeslot)) {
					this.timeslot[sortedCourse[course][0]-1] = time_slotindex;
					break;
				}
					else
						timeslotindex = timeslotindex+1; // move to ts+1 if ts is crash
			}
		}
		return this.timeslot;
    }
	
	public int getHowManyTimeSlot(int[] timeslot) { 
		int jumlah_timeslot = 0;
		
		for(int i = 0; i < timeslot.length; i++) {
			if(timeslot[i] > jumlah_timeslot)
				jumlah_timeslot = timeslot[i];
		}
		return jumlah_timeslot; 
	}
	
	public int getJumlahTimeSlot(int[][] timeslot) { 
		int jumlah_timeslot = 0;
		
		for(int i = 0; i < timeslot.length; i++) {
			if(timeslot[i][1] > jumlah_timeslot)
				jumlah_timeslot = timeslot[i][1];
		}
		return jumlah_timeslot; 
	}
	public static boolean isTimeslotAvailable(int course, int timeslot, int[][] conflictmatrix, int[] timeslotarray) {
		for(int i = 0; i < conflictmatrix.length; i++)
			if(conflictmatrix[course][i] != 0 && timeslotarray[i] == timeslot)
				return false;
		
		return true;
	}
    public static boolean isTimeslotAvailableWithSorted(int course, int timeslot, int[][] conflictmatrix, int[][] sortedmatrix, int[] timeslotarray) {
		for(int i = 0; i < sortedmatrix.length; i++) 
			if(conflictmatrix[sortedmatrix[course][0]-1][i] != 0 && timeslotarray[i] == timeslot) {
				return false;
			}
		
		return true;
	}
    public static boolean isTimeslotAvailableWithSaturation(int course, int timeslot, int[][] conflictmatrix, int[][] sortedmatrix, int[] timeslotarray) {
		for(int i = 0; i < sortedmatrix.length; i++) 
			if(conflictmatrix[sortedmatrix[course][0]-1][i] != 0 && timeslotarray[i] == timeslot) {
				return false;
			}
		
		return true;
	}
    
    public static boolean checkRandomTimeslot(int randomCourse, int randomTimeslot, int[][] conflict_matrix, int[][] jadwal){
        for(int i=0; i<conflict_matrix.length; i++)
            if(conflict_matrix[randomCourse][i] !=0 && jadwal[i][1]==randomTimeslot)
                return false;
        return true;              
    }
    
    public static boolean checkRandomTimeslotForLLH(int randomCourse, int randomTimeslot, int[][] conflict_matrix, int[][] jadwal){
        for(int i=0; i<conflict_matrix.length; i++)
            if(conflict_matrix[randomCourse][i] !=0 && jadwal[i][1]==randomTimeslot)
                return false;
        return true;              
    }
    
	public void printSchedule() {
		System.out.println("\n================================================\n");
    	for (int i = 0; i < jumlahexam; i++)
    		System.out.println("Timeslot untuk course "+ (i+1) +" adalah timeslot: " + timeslot[i]);       
	    
    	System.out.println("\n================================================"); 
	    System.out.println("Jumlah timeslot yang dibutuhkan untuk menjadwalkan " + jumlahexam + " course di file " + file + " adalah "+ Arrays.stream(timeslot).max().getAsInt() + " timeslot.");
	}
}
class Evaluator {	
    public static int[][] getTimeslot(int[][] timeslot) {
	int[][] copySolution = new int[timeslot.length][2];
            for(int i = 0; i < timeslot.length; i++) {
			copySolution[i][0] = timeslot[i][0];
			copySolution[i][1] = timeslot[i][1];
            }	
            return copySolution;
	}
	
	public static double getPenalty(int[][] matrix, int[][] jadwal, int jumlahMurid) {
		double penalty = 0;
		
		for(int i = 0; i < matrix.length - 1; i++) {
			for(int j = i+1; j < matrix.length; j++) {
				if(matrix[i][j] != 0) {
					if(Math.abs(jadwal[j][1] - jadwal[i][1]) >= 1 && Math.abs(jadwal[j][1] - jadwal[i][1]) <= 5) {
						penalty = penalty + (matrix[i][j] * (Math.pow(2, 5-(Math.abs(jadwal[j][1] - jadwal[i][1])))));
					}
				}
			}
		}
		
		return penalty/jumlahMurid;
	}
	
	public static int getRandomNumber(int min, int max) {
	    Random random = new Random();
	    return random.nextInt(max - min) + min;
	}
}

