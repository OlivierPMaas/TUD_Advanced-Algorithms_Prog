
public class ProblemInstance {
	private int numJobs;
	private double[][] jobs; //size = [num_jobs][2], for every job [0] is the length, [1] is the due time
	
	public ProblemInstance(int numJobs, double[][] jobs) {
		this.numJobs = numJobs;
		this.jobs = jobs;
	}
	
	public int getNumJobs() {
		return numJobs;
	}
	
	public double[][] getJobs() {
		return jobs;
	}
}
