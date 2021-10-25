package com.company;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket socket;
    private String clientUserName;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMsg(clientUserName + " has connected");
        } catch (IOException e) {
            closeOut(socket, bufferedReader, bufferedWriter);
        }
    }

    // run the client handler
    @Override
    public void run() {

        String msgToClient;

        while(socket.isConnected()) {
            try {
                msgToClient = bufferedReader.readLine();
                broadcastMsg(msgToClient);
            } catch (IOException e) {
                closeOut(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    //broadcast a message to all users except for the one who sent it
    private void broadcastMsg(String msg) {
        for(ClientHandler client: clientHandlers) {
            try {
                if(!client.clientUserName.equals(clientUserName)) {
                    client.bufferedWriter.write(msg);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeOut(socket,bufferedReader,bufferedWriter);
            }
        }
    }

    //exit out of everything

    private void closeOut(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if(socket != null) {
                socket.close();
            }
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //remove the client handler

    private void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMsg("The user: " + clientUserName + " has left the chat.");
    }
}
