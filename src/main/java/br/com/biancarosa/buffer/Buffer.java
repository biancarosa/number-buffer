package br.com.biancarosa.buffer;


public class Buffer {

    private int max;
    private int[] numbers;
    private int current = 0;

    public Buffer(int max) {
        this.max = max;
        this.numbers = new int[max];
    }

    public void addNumber(int n) {
        numbers[current++] = n;
        printBuffer();
    }
    public boolean hasMoreSpace() {
        return current != max;
    }

    public void printBuffer() {
        for (int i = 0; i < max; ++i) {
            System.out.print(numbers[i] + " - ");
        }
        System.out.println(" In current buffer");
    }
}
