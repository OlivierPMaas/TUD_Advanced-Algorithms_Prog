import org.junit.jupiter.api.Test;

public class RuntimeTest {
    @Test
    void main() {
        String[] values = {"0.2", "0.4", "0.6", "0.8", "1.0"};
        String[] fiveToHundred = {"5","10"};//"15","20","25", "30","35","40","45","50",
        //"55","60","65","70","75","80", "85","90","95","100"};
        for (String value1 : values) {
            for (String value2 : values) {
                for (String value3 : fiveToHundred) {
                    String[] arg = {String.format(String.format(String.format("instances/random_RDD=%s", value1) + "_TF=%s", value2) + "_#%s.dat", value3)};
                    //System.out.println("RDD=" + value1 + ", TF=" + value2 + ", #" + value3);
                    //System.out.println(arg[0]);
                    ComputeTardiness.main(arg);
                    //System.out.println("");
                }
            }
        }
    }
}


