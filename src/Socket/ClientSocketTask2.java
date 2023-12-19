package Socket;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientSocketTask2 {
    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server");

            while (true) {
                System.out.print("Enter command (create, read, update, delete, filter, exit): ");
                String command = scanner.nextLine();

                if ("exit".equalsIgnoreCase(command)) {
                    break;
                }

                out.writeObject(command);
                out.flush();

                try {
                    Object response = in.readObject();
                    System.out.println("Server response: " + response);
                } catch (ClassNotFoundException e) {
                    System.out.println("Error in response from server");
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Host not found: " + HOST);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't connect to " + HOST);
            e.printStackTrace();
            System.exit(1);
        }
    }
}