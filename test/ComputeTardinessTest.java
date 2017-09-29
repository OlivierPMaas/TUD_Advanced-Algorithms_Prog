import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Olivier on 9/29/2017.
 */
class ComputeTardinessTest {
    @Test
    void main() {
        String[] arg = {"instances/random_RDD=0.2_TF=0.2_#5.dat"};
        String[] arg2 = {"instances/random_RDD=0.2_TF=0.2_#10.dat"};
        String[] arg3 = {"instances/random_RDD=0.2_TF=0.2_#15.dat"};
        ComputeTardiness.main(arg);
        ComputeTardiness.main(arg2);
        ComputeTardiness.main(arg3);
    }

}