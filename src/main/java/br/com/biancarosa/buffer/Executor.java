package br.com.biancarosa.buffer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Executor {


    public static void main(String[] args) {
        int port = 0;
        int max = 0;
        try {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Porta precisa ser um número");
                System.exit(1);
            }
            try {
                max = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Número máximo de números no buffer precisa ser um numero");
                System.exit(1);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Passe o número da porta e a quantidade de números no buffer como argumentos");
            System.exit(1);
        }

        Buffer buffer = new Buffer(max);
        Semaphore consumerSemaphore = new Semaphore(max);
        Object lock = new Object();
        Boolean running = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = in.readLine();
                if (inputLine.contains("Produtor")) {
                    handleProducerInput(buffer, consumerSemaphore, lock, clientSocket, inputLine);
                } else if (inputLine.contains("Consumidor")) {
                    handleConsumerInput(buffer, consumerSemaphore, lock, clientSocket, inputLine);
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void handleConsumerInput(Buffer buffer, Semaphore sem, Object lock, Socket clientSocket, String consumerName) throws IOException {
        synchronized (lock) {
            String message;
            if (buffer.isEmpty()) {
                message = "Number?empty";
            } else {
                int number = buffer.consumeNumber();
                message = "Number?"+number;
                System.out.println("Valor " + number + " retirado de Buffer pelo " + consumerName);
                sem.release();
            }
            OutputStream os = clientSocket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(message);
            bw.flush();
        }
    }

    private static void handleProducerInput(Buffer buffer, Semaphore sem, Object lock, Socket clientSocket, String inputLine) throws IOException {
        boolean canAdd;
        boolean added = false;
        String[] input = inputLine.split(",");
        String name = input[0];
        Integer number = Integer.parseInt(input[1]);
        synchronized (lock) {
            canAdd = sem.tryAcquire(); //checks if full
            if (canAdd) {
                buffer.addNumber(number);
                System.out.println("Valor " + number + " adicionado em Buffer pelo " + name);
                added = true;
                if (!buffer.hasMoreSpace()) {
                    canAdd = false;
                }
            }
        }

        OutputStream os = clientSocket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write("Full?"+!canAdd+"?Added?"+added);
        bw.flush();
    }

}

