package org.example.Client;


import org.example.Model.Contact;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;

    // Constructor to initialize the client with a socket connection
    public Client(Socket socket) {
        this.socket = socket;
    }

    // Method to send an echo command to the server and receive the response
    public String echo(String echo) throws IOException, ClassNotFoundException {
        // Create an input stream to receive data from the server
        ObjectInputStream inp = new ObjectInputStream(socket.getInputStream());
        // Create an output stream to send data to the server
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        // Send the echo command to the server
        out.println("echo " + echo);
        // Flush the output stream to ensure the command is sent immediately
        out.flush();

        // Read the response from the server
        String res = (String) inp.readObject();

        // Close the input and output streams
        inp.close();
        out.close();

        // Return the response received from the server
        return res;
    }

    // Method to send an exit command to the server, indicating the client is done
    public void exit() throws IOException {
        // Create an output stream to send data to the server
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        // Send the exit command to the server
        out.println("exit");
        // Flush the output stream to ensure the command is sent immediately
        out.flush();

        // Close the output stream
        out.close();
    }

    // Method to request a Contact object from the server using an ID
    public Contact get(String id) throws IOException, ClassNotFoundException {
        // Create an input stream to receive data from the server
        ObjectInputStream inp = new ObjectInputStream(socket.getInputStream());
        // Create an output stream to send data to the server
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        // Send the get command with the specified ID to the server
        out.println("get " + id);
        // Flush the output stream to ensure the command is sent immediately
        out.flush();

        // Read the Contact object from the server response
        Contact res = (Contact) inp.readObject();

        // Close the input and output streams
        inp.close();
        out.close();

        // Return the Contact object received from the server
        return res;
    }

    // Method to close the client socket connection
    public void close() throws IOException {
        // Close the socket connection
        socket.close();
        // Set the socket to null to indicate it's no longer connected
        socket = null;
    }
}
