package com.company;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public static void main(String[] args) throws IOException {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your username for the group chat");
        String username = scan.nextLine();

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket,username);
        client.listenMsg();
        client.sendMsg();
    }

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeOut(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMsg() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scan = new Scanner(System.in);
            while (socket.isConnected()) {
                String msgToSend = scan.nextLine();
                bufferedWriter.write(username + ": " + msgToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeOut(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenMsg() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroup;

                while (socket.isConnected()) {
                    try {
                        msgFromGroup = bufferedReader.readLine();
                        System.out.println(msgFromGroup);
                    } catch (IOException e) {
                        closeOut(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    private void closeOut(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
