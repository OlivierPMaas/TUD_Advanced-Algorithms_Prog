import java.util.*;

import static java.lang.Math.min;

public class EDP {
    private int numJobs;
    private double[][] jobs;
    private Schedule greedyScheduleFixed;
    private HashMap<List<Object>,Double> memo;

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
        this.memo = new HashMap<List<Object>,Double>();
    }

    public Schedule resetIDs(Schedule s, double[][] jobs) {
        int depth = s.getDepth();
        double jobDueTime = jobs[s.jobID][1];
        // depth - 1 because we start counting at 0.
        if(s.previous == null) {
            return new Schedule(null, depth - 1, s.jobLength, jobDueTime);
        }
        return new Schedule(resetIDs(s.previous, jobs), depth - 1, s.jobLength, jobDueTime);
    }

    public double findOptimalTardiness()
    {
        return computeOptimalTardinessMaster(this.greedyScheduleFixed,0);
    }


    public double computeOptimalTardinessMaster(Schedule s, double startTime) {
        int j = s.jobID;
        Schedule k = s.findK();
        List<Integer> jobArray = s.getJobs();

        List<Object> input = new ArrayList<Object>();
        input.add(jobArray);
        input.add(startTime);
        if(memo.containsKey(input)) {
            return memo.get(input);
        }

        if(s == null) {
            return 0;
        }
        else if (s.getDepth()==1) {
            return Math.max(0, startTime + k.jobLength - k.jobDueTime);
        }
        else {
            List<Double> tardinesses = new ArrayList<Double>();
            // [jobs start at 0 instead of 1] cancel each other out here in j and k
            for (int delta = 0; delta <= j - k.jobID; delta++) {
                tardinesses.add(computeOptimalTardiness(s, startTime, delta));
            }

            double output = tardinesses.stream().min(Double::compare).get();
            memo.put(input, output);
            return output;
        }
    }

    public double computeOptimalTardiness(Schedule s, double startTime, int delta) {
        int i = s.getMinJobID();
        int j = s.jobID;

        Schedule kPrime = s.findK();
        Schedule S = s.removeK();

        // ---------------- CALCULATION OF VALUE 1
        double value1;
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
        double value2;
        double completionTimeKPrime;
        if (subSchedule2 != null) {
            completionTimeKPrime = subSchedule2.getCompletionTime(startTime);
            value2 = Math.max(0, completionTimeKPrime - kPrime.jobDueTime);
        } else {
            throw new java.lang.Error("Something went wrong. "
                    + "This schedule can't be empty; at the very least, it should include k.");
        }

        // ---------------- CALCULATION OF VALUE 3
        double value3;
        Schedule subSchedule3 = S.getScheduleBetween(kPrime.jobID + delta + 1, j);
        if (subSchedule3 == null) {
            value3 = 0;
        } else {
            if (subSchedule3 == null) {
                value3 = 0;
            } else {
                value3 = computeOptimalTardinessMaster(subSchedule3, completionTimeKPrime);
            }
        }

        // ---------------- Result
        return value1 + value2 + value3;
    }

    public double computeTardiness(Schedule s, int startTime) {
        return s.fixTardiness(startTime).getTardiness();
    }
}
