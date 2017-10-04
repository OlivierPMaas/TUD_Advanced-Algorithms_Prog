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
        int delta = numJobs - k.jobID - 1;

        Schedule S = s.removeK();

        Schedule kPrime = S.findK();

        // ---------------- CALCULATION OF VALUE 1

        // -1 at id2, because we start counting at 0 instead of at 1 (as in Lawler 1977).

        // WATCH OOUUUUUUT: I think we might not actually need to subtract 1 after all, because our kPrime jobID is
        // itself starting count from 0. I've removed the -1 for now, but let's discuss soon.
        int value1;
        Schedule subSchedule1WithK = S.getScheduleBetween(startTime,kPrime.jobID + delta);
        if(subSchedule1WithK == null) {
            value1 = 0;
        }
        else {
            Schedule subSchedule1 = subSchedule1WithK.removeK();
            if(subSchedule1 == null) {
                value1 = 0;
            }
            else {
                if (subSchedule1.getDepth() <= 3) {
                    if (subSchedule1.getDepth() == 1) {
                        value1 = subSchedule1.getTardiness();
                    } else if (subSchedule1.getDepth() == 2) {
                        subSchedule1 = subSchedule1.fixTardiness(startTime);
                        int T1 = computeTardiness(subSchedule1, startTime);

                        Schedule temp = subSchedule1.previous;
                        subSchedule1.previous = null;
                        temp.previous = subSchedule1;
                        int T2 = computeTardiness(temp, startTime);
                        value1 = min(T1, T2);
                    }
                    // Depth is 3
                    else {
                        //CHANGE TO CORRECT CALCULATION
                        value1 = subSchedule1.getTardiness();
                    }

                } else {
                    value1 = computeOptimalTardiness(subSchedule1, t);
                }
            }
        }

        // ---------------- CALCULATION OF VALUE 2
        // Note: s, not S. (Note: S = s - {job_kPrime}).
        Schedule subSchedule2 = s.getScheduleBetween(0,kPrime.jobID + delta);
        int value2;
        if(subSchedule2 != null) {
            value2 = Math.max(0, subSchedule2.getCompletionTime(startTime) - kPrime.jobDueTime);
        }
        else {
            throw new java.lang.Error("Something went wrong. "
                    + "This schedule can't be empty; at the very least, it should include k.");
        }

        // ---------------- CALCULATION OF VALUE 3
        int value3;
        Schedule subSchedule3WithK = S.getScheduleBetween(kPrime.jobID + delta + 1,j);
        if(subSchedule3WithK == null) {
            value3 = 0;
        }
        else {
            // CALCULATE VALUE3
            value3=4;
        }

        // ---------------- Result
        return value1 + value2 + value3;
    }

    public int computeTardiness(Schedule s, int startTime) {
        return s.fixTardiness(startTime).getTardiness();
    }
}
