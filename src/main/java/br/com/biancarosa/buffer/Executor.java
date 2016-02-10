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
        Semaphore full = new Semaphore(1);
        Object lock = new Object();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = null;
                boolean canAdd = true;
                boolean added = false;
                try {
                    inputLine = in.readLine();
                    //If producer
                    String[] input = inputLine.split(",");
                    String name = input[0];
                    Integer number = Integer.parseInt(input[1]);
                    System.out.println("Received number " + number + " from producer named " + name);
                    synchronized (lock) {
                        try {
                            canAdd = full.tryAcquire(); //checks if full
                            if (canAdd) {
                                buffer.addNumber(number);
                                System.out.println("Added number");
                                added = true;
                                full.release();
                                if (!buffer.hasMoreSpace()) {
                                    System.out.println("Acquiring lock");
                                    full.acquire();
                                    canAdd = false;
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }  catch (IOException e1) {
                    e1.printStackTrace();
                }

                System.out.println("Sending the response back to the client.");
                OutputStream os = clientSocket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write("Full?"+!canAdd+"?Added?"+added);
                bw.flush();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}

