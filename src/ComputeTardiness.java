import com.sun.deploy.security.MozillaJSSDSASignature;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ComputeTardiness {	
	public static ProblemInstance readInstance(String filename){
		ProblemInstance instance = null;
		
		try {
			int numJobs = 0;
			double[][] jobs = null;
			
			Scanner sc = new Scanner(new BufferedReader(new FileReader(filename)));
			if(sc.hasNextInt()){
				numJobs = sc.nextInt();
				jobs = new double[numJobs][2];
				int nextJobID = 0;
			
				while (sc.hasNextInt() && nextJobID < numJobs) {
					jobs[nextJobID][0] = sc.nextInt();
					jobs[nextJobID][1] = sc.nextInt();
					nextJobID++;
				}
			}
			sc.close();
			
			instance = new ProblemInstance(numJobs, jobs);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return instance;
	}

	// reads a problem, and outputs the result of both greedy, best-first and EDP
    public static void main (String args[]) {
		//long startTime = System.nanoTime();

		if (args.length == 0){
			throw new java.lang.Error("Please provide filename in command");
		}

		String fileName = args[0];
		ProblemInstance instance = readInstance(fileName);

		EDP edp = new EDP(instance);
		double edpTardiness = edp.findOptimalTardiness();
		System.out.println("EDP tardiness: " + edpTardiness);
		//System.out.println(edpTardiness);

		//Greedy greedy = new Greedy(instance);
		//Schedule greedySchedule = greedy.getSchedule();
		//System.out.println("Greedy tardiness: " + greedySchedule.getTardiness());

		//if(edpTardiness > greedySchedule.getTardiness()) {
		//	System.out.println("-----------------------------------------EDP SUBOPTIMAL\n\n\n");
		//}

//		BestFirst bestFirst = new BestFirst(instance);
//		Schedule bestFirstSchedule = bestFirst.getSchedule();
//		System.out.println("BestFirst tardiness: " + bestFirst.getSchedule().getTardiness());

		//long endTime   = System.nanoTime();
		//long totalTime = (endTime - startTime)/1000;
		//System.out.println(totalTime);

		//if(edpTardiness > bestFirstSchedule.getTardiness()) {
		//	System.out.println("-----------------------------------------EDP SUBOPTIMAL\n\n\n");
		//}

		//if(edpTardiness != bestFirst.getSchedule().getTardiness()) {
		//	System.out.println("-----------------------------------------EDP INCORRECT\n\n\n");
		//}

		//if(edpTardiness > 0) {
		//	System.out.println("-----------------------------------------EDP > 0\n\n\n");
		//}

		double epsilon = 0.01;
		Approx approx = new Approx(instance);
		double approxTardiness = approx.ApproximateOptimalTardiness(epsilon);
		System.out.println("Approx tardiness: " + approxTardiness);

	}
}
