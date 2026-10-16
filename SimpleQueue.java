package project;

public class SimpleQueue {
    private int[] buf = new int[1024];
    private int head = 0, tail = 0;

    public void push(int x) {
        buf[tail++] = x;
    }

    public int pop() {
        return buf[head++];
    }

    public boolean isEmpty() {
        return head == tail;
    }

    public void reset() {
        head = tail = 0;
    }
}
