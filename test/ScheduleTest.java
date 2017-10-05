import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Olivier on 10/5/2017.
 */
class ScheduleTest {
    @Test
    void removeID() {
        Schedule input0 = new Schedule(null, 0, 67, 253);
        Schedule input1 = new Schedule(input0, 1, 85, 256);
        Schedule input2 = new Schedule(input1, 2, 82, 285);
        Schedule output = input2.removeID(1);
        output.printer();
    }

}