import java.util.ArrayList;
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

    public int findOptimalTardiness() {
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
        int j = s.getDepth()-1;
        Schedule k = s.findK();

        Schedule S = s.removeK();

        Schedule kPrime = S.findK();
        List<Integer> tardinesses = new ArrayList<Integer>();
        // <, so no -1 correction needed for the fact that our jobs start at ID 0 rather than 1.
        for(int delta = 0; delta < numJobs - k.jobID; delta++) {
            tardinesses.add(computeOptimalTardiness(s, startTime, delta));
        }

        return tardinesses.stream().min(Integer::compare).get();


    }

    public int computeOptimalTardiness(Schedule s, int startTime, int delta) {
        int j = s.jobID;
        
        //... When is k scheduled???
        Schedule k = s.findK();

        Schedule S = s.removeK();

        if(S == null) {
            //System.out.println("Got here");
            return 4; // Should we really?
        }
        else {
            Schedule kPrime = S.findK();

            // ---------------- CALCULATION OF VALUE 1

            int value1;

            // -1 at id2, because we start counting at 0 instead of at 1 (as in Lawler 1977).
            //
            // WATCH OOUUUUUUT: I think we might not actually need to subtract 1 after all, because our kPrime jobID is
            // itself starting count from 0. I've removed the -1 for now, but let's discuss soon.
            //
            //Also: id = 0 , or id = startTime?!
            Schedule subSchedule1WithK = S.getScheduleBetween(0, kPrime.jobID + delta);
            if (subSchedule1WithK == null) {
                value1 = 0;
            } else {
                Schedule subSchedule1 = subSchedule1WithK.removeK();
                if (subSchedule1 == null) {
                    value1 = 0;
                } else {
                    if (subSchedule1.getDepth() <= 2) {
                        if (subSchedule1.getDepth() == 1) {
                            value1 = computeTardiness(subSchedule1, startTime);
                        }
                        //subSchedule1.getDepth() == 2
                        else {
                            subSchedule1 = subSchedule1.fixTardiness(startTime);
                            int T1 = computeTardiness(subSchedule1, startTime);

                            Schedule temp = subSchedule1.previous;
                            subSchedule1.previous = null;
                            temp.previous = subSchedule1;
                            int T2 = computeTardiness(temp, startTime);
                            value1 = min(T1, T2);
                        }
                    } else {
                        value1 = computeOptimalTardinessMaster(subSchedule1, startTime);
                    }
                }
            }

            // ---------------- CALCULATION OF VALUE 2
            // Note: s, not S. (Note: S = s - {job_kPrime}).
            Schedule subSchedule2 = s.getScheduleBetween(0, kPrime.jobID + delta);
            int completionTimeKPrime = subSchedule2.getCompletionTime(startTime);
            int value2;
            if (subSchedule2 != null) {
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
                Schedule subSchedule3 = subSchedule3WithK.removeK();
                if (subSchedule3 == null) {
                    value3 = 0;
                } else {
                    if (subSchedule3.getDepth() <= 2) {
                        if (subSchedule3.getDepth() == 1) {
                            value3 = computeTardiness(subSchedule3, completionTimeKPrime);
                        }
                        //subSchedule1.getDepth() == 2
                        else {
                            subSchedule3 = subSchedule3.fixTardiness(completionTimeKPrime);
                            int T1 = computeTardiness(subSchedule3, completionTimeKPrime);

                            Schedule temp = subSchedule3.previous;
                            subSchedule3.previous = null;
                            temp.previous = subSchedule3;
                            int T2 = computeTardiness(temp, completionTimeKPrime);
                            value3 = min(T1, T2);
                        }
                    } else {
                        value3 = computeOptimalTardinessMaster(subSchedule3, completionTimeKPrime);
                    }
                }
            }

            // ---------------- Result
            return value1 + value2 + value3;
        }
    }

    public int computeTardiness(Schedule s, int startTime) {
        return s.fixTardiness(startTime).getTardiness();
    }
}
