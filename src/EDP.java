import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public int findOptimalTardiness()
    {
        return computeOptimalTardinessMaster(this.greedyScheduleFixed,0);
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


    public int computeOptimalTardinessMaster(Schedule s, int startTime) {
        int j = s.jobID;
        Schedule k = s.findK();

        if(s == null) {
            //System.out.println("Got here");
            return 0; // This, or 0? And: startTime, or some other value related to k?
        }
        else if (s.getDepth()==1) {
            return Math.max(0, startTime + k.jobLength - k.jobDueTime);
        }
        else {
            List<Integer> tardinesses = new ArrayList<Integer>();
            // [jobs start at 0 instead of 1] cancel each other out here in j and k
            for (int delta = 0; delta <= j - k.jobID; delta++) {
                tardinesses.add(computeOptimalTardiness(s, startTime, delta));
            }

            return tardinesses.stream().min(Integer::compare).get();
        }
    }

    public int computeOptimalTardiness(Schedule s, int startTime, int delta) {
        int i = s.getMinJobID();
        int j = s.jobID;

        Schedule kPrime = s.findK();
        Schedule S = s.removeK();

        // ---------------- CALCULATION OF VALUE 1

        int value1;

        // -1 at id2, because we start counting at 0 instead of at 1 (as in Lawler 1977).
        //
        // WATCH OOUUUUUUT: I think we might not actually need to subtract 1 after all, because our kPrime jobID is
        // itself starting count from 0. I've removed the -1 for now, but let's discuss soon.
        //
        //Also: id = 0 , or id = startTime?!
        Schedule subSchedule1WithK = S.getScheduleBetween(i, kPrime.jobID + delta);
        if (subSchedule1WithK == null) {
            value1 = 0;
        } else {
            Schedule subSchedule1 = subSchedule1WithK.removeID(kPrime.jobID);
            if (subSchedule1 == null) {
                value1 = 0;
            } else {
                value1 = computeOptimalTardinessMaster(subSchedule1, startTime);
            }
        }

        // ---------------- CALCULATION OF VALUE 2
        // small s, because we want kPrime to be included and counted as well.
        Schedule subSchedule2 = s.getScheduleBetween(i, kPrime.jobID + delta);
        int value2;
        int completionTimeKPrime;
        if (subSchedule2 != null) {
            completionTimeKPrime = subSchedule2.getCompletionTime(startTime);
            value2 = Math.max(0, completionTimeKPrime - kPrime.jobDueTime);
        } else {
            throw new java.lang.Error("Something went wrong. "
                    + "This schedule can't be empty; at the very least, it should include k.");
        }

        // ---------------- CALCULATION OF VALUE 3
        int value3;
        Schedule subSchedule3WithK = S.getScheduleBetween(kPrime.jobID + delta + 1, j);
        if (subSchedule3WithK == null) {
            value3 = 0;
        } else {
            Schedule subSchedule3 = subSchedule3WithK.removeID(kPrime.jobID);
            if (subSchedule3 == null) {
                value3 = 0;
            } else {
                value3 = computeOptimalTardinessMaster(subSchedule3, completionTimeKPrime);
            }
        }

        // ---------------- Result
        return value1 + value2 + value3;
    }

    public int computeTardiness(Schedule s, int startTime) {
        return s.fixTardiness(startTime).getTardiness();
    }
}
