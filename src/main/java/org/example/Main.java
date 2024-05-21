package org.example;

import org.example.Client.Client;
import org.example.Dao.InMemoryContactDao;
import org.example.Model.Contact;
import org.example.Server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 12345;
        int timeout = 10000; // 10 seconds timeout

        // Start the server
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                InMemoryContactDao inMemoryContactDao = new InMemoryContactDao();
                Server server = new Server(serverSocket, inMemoryContactDao);
                server.start(timeout);

                // This is to simulate server running for a while before shutdown
                Thread.sleep(30000); // Run server for 30 seconds
                server.shutDown();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // Give the server a moment to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Start the client
        try {
            Socket socket = new Socket("localhost", port);
            Client client = new Client(socket);

            // Test echo command
            String echoResponse = client.echo("Hello, World!");
            System.out.println("Client: Echo response: " + echoResponse);

            // Test get command
            Contact contact = client.get("1");
            System.out.println("Client: Contact response: " + contact);

            // Exit the client
            client.exit();
            client.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}