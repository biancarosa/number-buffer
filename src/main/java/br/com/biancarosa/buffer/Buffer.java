package br.com.biancarosa.buffer;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class Buffer {

    int max;
    Deque<Integer> numbers;

    public Buffer(int max) {
        this.max = max;
        this.numbers = new LinkedBlockingDeque<Integer>(max);
    }

    public Deque<Integer> getNumbers() {
        return numbers;
    }
}
