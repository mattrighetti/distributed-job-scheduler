package Utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Tuple2Test {

    @Test
    public void checkEquals() {
        Tuple2<Integer, Integer> tupleOne = new Tuple2<>(0, 1);
        Tuple2<Integer, Integer> tupleTwo = new Tuple2<>(0, 1);
        Tuple2<Integer, Integer> tupleThree = new Tuple2<>(1, 1);
        Tuple2<Integer, Integer> tupleFour = new Tuple2<>(0, 1);
        Tuple2<String, Integer> tupleFive = new Tuple2<>("Hello", 1);
        assertEquals(tupleOne, tupleTwo);
        assertEquals(tupleFour, tupleOne);
        assertNotEquals(tupleThree, tupleFour);
        assertNotEquals(tupleFive, tupleFour);
    }

}
