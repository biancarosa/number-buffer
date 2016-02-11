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
    }

    public boolean hasMoreSpace() {
        return current != max;
    }

    public boolean isEmpty() {
        return current == 0;
    }

    public int consumeNumber() {
        int number = numbers[0];
        //reorganize array
        for (int i = 0; i < max-1; ++i) {
            numbers[i] = numbers[i+1];
        }
        current--;
        return number;
    }

    public void printBuffer() {
        for (int i = 0; i < max; ++i) {
            System.out.print(numbers[i] + " - ");
        }
    }


}
