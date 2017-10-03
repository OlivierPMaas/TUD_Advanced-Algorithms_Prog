/**
 * Created by Olivier on 10/3/2017.
 */
public class EDP {
    private int numJobs;
    private int[][] jobs;
    private Schedule greedyScheduleFixed;

    public EDP(ProblemInstance instance) {
        numJobs = instance.getNumJobs();
        jobs = instance.getJobs();
        Greedy greedy = new Greedy(instance);
        //build up schedule, sorted in nondecreasing deadline order
        Schedule greedySchedule = greedy.getSchedule();
        // Rewrite jobID's to be consistent with our algorithm's notation:
        // We number the jobs in nondecreasing order.
        // Note that we now lose the ability to return a schedule for the original jobs.
        // That's fine for this assignment; we only need to return the optimal tardiness.
        this.greedyScheduleFixed = resetIDs(greedySchedule, jobs);
    }

    public Schedule resetIDs(Schedule s, int[][] jobs) {
        int depth = s.getDepth();
        int jobDueTime = jobs[s.jobID][1];
        // depth - 1 because we start counting at 0.
        if(s.previous == null) {
            return new Schedule(null, depth - 1, s.jobLength, jobDueTime);
        }
        return new Schedule(resetIDs(s.previous, jobs), depth - 1, s.jobLength, jobDueTime);
    }

    public int findOptimalTardiness() {
        return computeOptimalTardiness(this.greedyScheduleFixed,0);
    }

    //CLEAN UP LATER
    public void printer(Schedule s) {
        if(s == null) {
            return;
        }
        else {
            System.out.println(s.jobID);
            printer(s.previous);
        }
    }

    public int computeOptimalTardiness(Schedule s, int startTime) {
        // base cases!
        



        int j = s.getDepth()-1;
        int t = startTime;
        Schedule k = s.findK();
        // Range over this soon
        int delta = 0;//numJobs - s.getDepth();

        Schedule S = s.removeK();
        // -1, because we start counting at 0 instead of at 1 (as in Lawler 1977).
        int kPrime = S.findK().jobID;
        int value1 = computeOptimalTardiness(S.getScheduleBetween(0,kPrime + delta - 1).removeK(),t);
        int value2 = 4;
        int value3 = 4;
        return 4;
    }


    public int computeTardiness(Schedule s, int startTime) {
        return s.getTardiness() + startTime;
    }
}
