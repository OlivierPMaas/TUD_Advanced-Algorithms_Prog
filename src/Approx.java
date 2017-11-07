public class Approx {
    private int numJobs;
    private double[][] jobs;
    private Schedule greedyScheduleFixed;
    public double Tmax; // Assigned based on epsilon everytime.

    public Approx(ProblemInstance instance) {
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

    public Schedule resetIDs(Schedule s, double[][] jobs) {
        int depth = s.getDepth();
        double jobDueTime = jobs[s.jobID][1];
        // depth - 1 because we start counting at 0.
        if(s.previous == null) {
            return new Schedule(null, depth - 1, s.jobLength, jobDueTime);
        }
        return new Schedule(resetIDs(s.previous, jobs), depth - 1, s.jobLength, jobDueTime);
    }

    public double ApproximateOptimalTardiness(double epsilon) {
        this.Tmax = greedyScheduleFixed.getMaxIndividualTardiness(0);
        if(Tmax == 0) {
            return 0;
        }
        else {
            double K = Tmax*(2*epsilon)/(numJobs * (numJobs+1));
            // Q: Use greedyScheduleFixed or some un-processed schedule?
            Schedule rescaledSchedule = this.greedyScheduleFixed.rescale(K);
            EDP edp = new EDP(rescaledSchedule.makeProblemInstance());
            return edp.findOptimalTardiness()*K;
        }
    }

}
