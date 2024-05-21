package org.example.Server;

import org.example.Model.Contact;
import org.example.Dao.ContactDao;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private final ServerSocket serverSocket;
    private final ContactDao contacts;
    private boolean shutdown;
    private Thread main;

    // Constructor to initialize the server with a ServerSocket and ContactDao
    public Server(ServerSocket serverSocket, ContactDao contacts) {
        this.serverSocket = serverSocket;
        this.contacts = contacts;
        this.shutdown = false;
    }

    // Method to start the server with a specified timeout
    public void start(int timeout) {
        main = new Thread(() -> {
            try {
                // Set the server socket timeout
                serverSocket.setSoTimeout(timeout);
            } catch (SocketException e) {
                e.printStackTrace();
                return;
            }
            while (!shutdown) {
                Socket socket = null;
                try {
                    System.out.println("Server: Waiting for connection");
                    // Accept a new client connection
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    continue;
                }
                if (socket != null) {
                    System.out.println("Server: New client connection detected");
                    // Handle the client connection in a new thread
                    handleClient(socket);
                }
            }
            try {
                // Close the server socket when shutting down
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // Start the main server thread
        main.start();
    }

    // Method to handle communication with a connected client
    private void handleClient(Socket socket) {
        new Thread(() -> {
            BufferedReader inp;
            ObjectOutputStream out;
            try {
                // Initialize input and output streams for the socket
                inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (true) {
                String command = null;
                try {
                    // Read a command from the client
                    command = inp.readLine();
                } catch (IOException e) {
                    break;
                }
                try {
                    // Handle different commands from the client
                    if (command == null || command.equals("exit")) {
                        // Exit the loop if the command is "exit" or null
                        break;
                    } else if (command.startsWith("echo ")) {
                        // Handle the "echo" command by sending back the echoed message
                        out.writeObject(command.substring(5));
                    } else if (command.startsWith("get ")) {
                        // Handle the "get" command by retrieving the Contact and sending it to the client
                        Contact contact = contacts.get(command.substring(4));
                        out.writeObject(contact);
                    }
                    // Flush the output stream to ensure the response is sent immediately
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // Close the client socket when done
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server: Client connection closed");
        }).start();
    }

    // Method to shut down the server
    public void shutDown() {
        shutdown = true;
        try {
            // Wait for the main server thread to terminate
            main.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
