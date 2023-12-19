package Socket;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ServerSocketTask2 {
    private static final int PORT = 1234;
    private Map<String, Event> events = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        ServerSocketTask2 server = new ServerSocketTask2();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(server.new ClientHandler(clientSocket));
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                Object inputObject;
                try {
                    while ((inputObject = in.readObject()) != null) {
                        if (inputObject instanceof String) {
                            String input = (String) inputObject;
                            String[] parts = input.split(" ", 2);
                            String command = parts[0].toLowerCase();
                            String argument = parts.length > 1 ? parts[1] : "";

                            switch (command.toLowerCase()) {
                                case "create":
                                    createEvent(argument);
                                    out.writeObject("Event created");
                                    break;
                                case "read":
                                    out.writeObject(readEvent(parts[1]));
                                    break;
                                case "update":
                                    updateEvent(argument);
                                    out.writeObject("Event updated");
                                    break;
                                case "delete":
                                    deleteEvent(argument);
                                    out.writeObject("Event deleted");
                                    break;
                                case "filter":
                                    out.writeObject(filterEvents(argument));
                                    break;
                                default:
                                    out.writeObject("Invalid command");
                            }
                        }
                    }
                } catch (EOFException e) {
                    System.out.println("End of stream reached (Client connection closed, server still running)");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void createEvent(String eventData) {
            String[] data = eventData.split(",");
            if (data.length < 4) {
                System.out.println("Insufficient data for creating an event");
                return;
            }

            try {
                String title = data[0].trim();
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(data[1].trim());
                String category = data[2].trim();
                boolean isPeriodic = Boolean.parseBoolean(data[3].trim());

                Event event = new Event(title, date, category, isPeriodic);
                events.put(title, event);
                System.out.println("Created event: " + event);
            } catch (ParseException e) {
                System.out.println("Error parsing date. Please use the format yyyy-MM-dd");
            }
        }

        private String readEvent(String title) {
            Event event = events.get(title);
            return event != null ? event.toString() : "Event not found: " + title;
        }

        private void updateEvent(String eventData) {
            String[] data = eventData.split(",");
            if (data.length < 4) {
                System.out.println("Insufficient data for updating an event");
                return;
            }

            String title = data[0].trim();
            Event event = events.get(title);

            if (event == null) {
                System.out.println("Event not found: " + title);
                return;
            }

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(data[1].trim());
                String category = data[2].trim();
                boolean isPeriodic = Boolean.parseBoolean(data[3].trim());

                event.setDate(date);
                event.setCategory(category);
                event.setPeriodic(isPeriodic);
                System.out.println("Updated event: " + event);
            } catch (ParseException e) {
                System.out.println("Error parsing date. Date in format yyyy-MM-dd");
            }
        }

        private void deleteEvent(String title) {
            if (events.remove(title) != null) {
                System.out.println("Deleted event: " + title);
            } else {
                System.out.println("Event not found: " + title);
            }
        }

        private String filterEvents(String filterCriteria) {
            String[] criteria = filterCriteria.split(",");
            if (criteria.length < 1) {
                return "Missing filtering type";
            }

            String filterType = criteria[0].trim().toLowerCase();
            List<Event> filteredEvents = new ArrayList<>(events.values());

            switch (filterType) {
                case "alphabet":
                    filteredEvents.sort(Comparator.comparing(Event::getTitle));
                    break;
                case "date":
                    filteredEvents.sort(Comparator.comparing(Event::getDate));
                    break;
                case "type":
                    filteredEvents.sort(Comparator.comparing(Event::isPeriodic));
                    break;
                default:
                    return "Invalid filter type";
            }

            return filteredEvents.stream().map(Event::toString).collect(Collectors.joining("\n"));
        }
    }
}
