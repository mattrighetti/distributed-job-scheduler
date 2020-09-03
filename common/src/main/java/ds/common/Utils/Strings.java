package ds.common.Utils;

public enum Strings {
    NULL("NULL");

    private final String value;

    Strings(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}
