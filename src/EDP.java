import static java.lang.Math.min;

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
        int j = s.getDepth()-1;
        int t = startTime;
        Schedule k = s.findK();
        // Range over this soon
        int delta = 0;//numJobs - s.getDepth();

        Schedule S = s.removeK();
        // -1, because we start counting at 0 instead of at 1 (as in Lawler 1977).
        int kPrime = S.findK().jobID;
        Schedule subSchedule1 = S.getScheduleBetween(0,kPrime + delta - 1).removeK();
        int value1;
        if(subSchedule1.getDepth() <= 3) {
            if(subSchedule1.getDepth() == 1) {
                value1 = subSchedule1.getTardiness();
            }
            else if(subSchedule1.getDepth() == 2) {
                int T1 = subSchedule1.getTardiness();

                Schedule temp = subSchedule1.previous;
                subSchedule1.previous = null;
                temp.previous = subSchedule1;
                int T2 = subSchedule1.getTardiness();
                value1 = min(T1,T2);
                if(T1 == T2) {
                    System.out.println("Watch out! T1 == T2.");
                }
            }
            // Depth is 3
            else {
                value1 = subSchedule1.getTardiness();
            }

        }
        else {
                value1 = computeOptimalTardiness(subSchedule1, t);
            }
        int value2 = 4;
        int value3 = 4;
        return value1 + value2 + value3;
    }


    public int computeTardiness(Schedule s, int startTime) {
        return s.getTardiness() + startTime;
    }
}
