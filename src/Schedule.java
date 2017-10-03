
public class Schedule implements Comparable<Schedule> {
	// A linked-list is a relatively efficient representation of a schedule
	// Feel free to modify it if you feel there exists a better one
	// The main advantage is that in a search-tree there is a lot of overlap
	// between schedules, this implementation stores this overlap only once
	private Schedule previous;
	private int jobID;
	private int jobLength;
	
	// tardiness can be calculated instead of memorized
	// however, we need to calculate it a lot, so we memorize it
	// if memory is an issue, however, try calculating it
	private int tardiness;
	
	public Schedule(){
		this.previous = null;
		this.jobID = -1;
		this.jobLength = 0;
		this.tardiness = 0;
	}
	
	// add an additional job to the schedule
	public Schedule(Schedule s, int jobID, int jobLength, int jobDueTime){		
		this.previous = s;
		this.jobID = jobID;
		this.jobLength = jobLength;
		this.tardiness = Math.max(0, getTotalTime() - jobDueTime);
		
		if(previous != null) {
			this.tardiness += previous.getTardiness();
		}
	}
	
	// used by the best-first search
	// currently, schedules are traversed in smallest total tardiness order
	public int compareTo(Schedule o){
		return getTardiness() - o.getTardiness();
		
		// replace with the following to get a depth-first search
		// return get_depth() - o.get_depth();
	}

	// Used by EDP
	// Gives the _n_'th job
	public Schedule nthJob(int n) {
		if(n == 0) {
			return this;
		}
		else {
			return this.previous.nthJob(n-1);
		}
	}

	// Used by EDP
	// Gives the job with highest processing time
	public Schedule findK() {
		// base case
		if (this.previous == null) {
			return this;
		}
		// recursion
		Schedule candidate = previous.findK();
		if (this.jobLength >= candidate.jobLength) {
			return this;
		} else {
			return candidate;
		}
	}

	public int getDepth(){
		int depth = 1;
		if(previous != null) depth += previous.getDepth();
		return depth;
	}
	
	public int getTotalTime(){
		int time = jobLength;
		if(previous != null) time += previous.getTotalTime();
		return time;
	}
	
	public int getTardiness(){
		return tardiness;
	}
	
	public boolean containsJob(int job){
		return (jobID == job) || (previous != null && previous.containsJob(job));
	}
}
