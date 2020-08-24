package Utils;

public class Tuple2<T1, T2> implements Tuple {
    public final T1 item1;
    public final T2 item2;

    public Tuple2(final T1 item_1, final T2 item_2) {
        item1 = item_1;
        item2 = item_2;
    }

    @Override
    public int size() {
        return 2;
    }
}
