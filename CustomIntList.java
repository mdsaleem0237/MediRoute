package project;

public class CustomIntList {
    private int[] data = new int[8];
    private int count = 0;

    public void add(int val) {
        if (count == data.length) {
            int[] nxt = new int[data.length * 2];
            System.arraycopy(data, 0, nxt, 0, data.length);
            data = nxt;
        }
        data[count++] = val;
    }

    public int get(int idx) {
        return data[idx];
    }

    public int size() {
        return count;
    }
}
