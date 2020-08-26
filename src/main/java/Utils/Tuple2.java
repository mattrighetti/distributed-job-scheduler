package Utils;

public class Tuple2<T1, T2> implements Tuple {
    public T1 item1;
    public T2 item2;

    public Tuple2(final T1 item_1, final T2 item_2) {
        item1 = item_1;
        item2 = item_2;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Tuple2)) {
            return false;
        }

        return ((Tuple2<?, ?>) obj).item1 == item1 &&
                ((Tuple2<?, ?>) obj).item2 == item2;
    }
}
