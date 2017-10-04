import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Olivier on 9/29/2017.
 */
class ComputeTardinessTest {
    @Test
    void main() {
        String[] arg = {"instances/random_RDD=0.2_TF=0.6_#90.dat"};
        ComputeTardiness.main(arg);
    }

}