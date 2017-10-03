/**
 * Created by Olivier on 10/3/2017.
 */
public class EDP {
    private int numJobs;
    private int[][] jobs;

    public EDP(ProblemInstance instance) {
        numJobs = instance.getNumJobs();
        jobs = instance.getJobs();
        Greedy greedy = new Greedy(instance);
        //build up schedule, sorted in nondecreasing deadline order
        Schedule greedySchedule = greedy.getSchedule();
    }

    public int findOptimalTardiness() {

        //return computeOptimalTardiness(s,0);
        return 4;
    }

    public int computeTardiness(Schedule s, int startTime) {
        return s.getTardiness() + startTime;
    }

    public int computeOptimalTardiness(Schedule s, int startTime) {
        int i = 0;
        int j = s.getDepth()-1;
        int t = startTime;
        Schedule k = s.findK();
        return 6;


        //value1 = ;
        //value2 = ;
        //value3 = ;
    }

}
