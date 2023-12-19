package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class ClientRmiTask2 {
    private ClientRmiTask2() {}

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            EventManagerInterface stub = (EventManagerInterface) registry.lookup("EventManager");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter command (create, read, update, delete, filter, exit): ");
                String command = scanner.nextLine();
                String[] parts = command.split(" ", 2);
                String action = parts[0].toLowerCase();

                switch (action) {
                    case "create":
                        String[] createArgs = parts[1].split(",");
                        stub.createEvent(createArgs[0], createArgs[1], createArgs[2], Boolean.parseBoolean(createArgs[3]));
                        System.out.println("Event created.");
                        break;
                    case "read":
                        System.out.println(stub.readEvent(parts[1]));
                        break;
                    case "update":
                        String[] updateArgs = parts[1].split(",");
                        stub.updateEvent(updateArgs[0], updateArgs[1], updateArgs[2], Boolean.parseBoolean(updateArgs[3]));
                        System.out.println("Event updated.");
                        break;
                    case "delete":
                        stub.deleteEvent(parts[1]);
                        System.out.println("Event deleted.");
                        break;
                    case "filter":
                        List<String> filteredEvents = stub.filterEvents(parts[1]);
                        filteredEvents.forEach(System.out::println);
                        break;
                    case "exit":
                        return;
                    default:
                        System.out.println("Unknown command.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
