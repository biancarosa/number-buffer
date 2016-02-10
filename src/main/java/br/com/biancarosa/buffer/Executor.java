package br.com.biancarosa.buffer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Executor {


    public static void main(String[] args) {
        int port = 8000;
        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Wrong number of args provided");
            System.exit(1);
        }

        System.out.println("Running on port " + port);

        Buffer buffer = new Buffer(10);
        Semaphore consumerSemaphore = new Semaphore(10);
        Object lock = new Object();
        Boolean running = true;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = in.readLine();
                if (inputLine.contains("Producer")) {
                    handleProducerInput(buffer, consumerSemaphore, lock, clientSocket, inputLine);
                } else if (inputLine.contains("Consumer")) {
                    handleConsumerInput(buffer, consumerSemaphore, lock, clientSocket, inputLine);
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void handleConsumerInput(Buffer buffer, Semaphore sem, Object lock, Socket clientSocket, String consumerName) throws IOException {
        System.out.println(consumerName + " asked for a number");
        synchronized (lock) {
            String message;
            if (buffer.isEmpty()) {
                message = "Number?empty";
            } else {
                message = "Number?"+buffer.consumeNumber();
                sem.release();
            }
            System.out.println("Sending the response back to the client.");
            OutputStream os = clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(message);
            bw.flush();
        }
    }

    private static void handleProducerInput(Buffer buffer, Semaphore sem, Object lock, Socket clientSocket, String inputLine) throws IOException {

        boolean canAdd = true;
        boolean added = false;

        String[] input = inputLine.split(",");
        String name = input[0];
        Integer number = Integer.parseInt(input[1]);
        System.out.println("Received number " + number + " from producer named " + name);
        synchronized (lock) {
            canAdd = sem.tryAcquire(); //checks if full
            if (canAdd) {
                buffer.addNumber(number);
                System.out.println("Added number");
                added = true;
                if (!buffer.hasMoreSpace()) {
                    canAdd = false;
                }
            }
        }

        System.out.println("Sending the response back to the client.");
        OutputStream os = clientSocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write("Full?"+!canAdd+"?Added?"+added);
        bw.flush();
    }

}

