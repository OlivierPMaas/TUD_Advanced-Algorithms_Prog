
public class Schedule implements Comparable<Schedule> {
	// A linked-list is a relatively efficient representation of a schedule
	// Feel free to modify it if you feel there exists a better one
	// The main advantage is that in a search-tree there is a lot of overlap
	// between schedules, this implementation stores this overlap only once
	public Schedule previous;
	public int jobID;
	public int jobLength;
	
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

	// Used by EDP
	public Schedule removeK() {
		int k = this.findK().jobID;
		return removeID(k);
	}

	// Used by EDP
	public Schedule removeID(int id) {
		if(this.jobID == id) {
			return this.previous;
		}
		else {
			Schedule output = this;
			output.previous = this.previous.removeID(id);
			return output;
		}
	}

	public int getDepth(){
		int depth = 1;
		if(previous != null) depth += previous.getDepth();
		return depth;
	}


	// Used by EDP
	public Schedule getScheduleBetween(int id, int id2) {
		if(id < 0) {
			throw new java.lang.Error("Tried to get too large a schedule 1");
		}
		// Put back id2 > this.jobID-case?
		if(id2 <= this.jobID) {
			return this.cutOffBefore(id);
		}
		else {
			return this.previous.getScheduleBetween(id, id2);
		}
	}

	public Schedule cutOffBefore(int id) {
		if(this.jobID < id) {
			return null;
		}
		else if(this.jobID == id) {
			Schedule cutOff = this;
			cutOff.previous = null;
			return cutOff;
		}
		// if(this.jobID > id)
		else {
			System.out.println(this.jobID);
			Schedule result = this;
			if(this.previous != null) {
				result.previous = this.previous.cutOffBefore(id);
			}
			else {
				result.previous = null;
			}
			return result;
		}
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
