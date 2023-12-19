package RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerRmiTask2 implements EventManagerInterface {
    private final Map<String, Event> events = new ConcurrentHashMap<>();

    public ServerRmiTask2() {
        super();
    }

    public static void main(String[] args) {
        try {
            ServerRmiTask2 server = new ServerRmiTask2();
            EventManagerInterface stub = (EventManagerInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("EventManager", stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void createEvent(String title, String date, String category, boolean isPeriodic) throws RemoteException {
        try {
            Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Event event = new Event(title, eventDate, category, isPeriodic);
            events.put(title, event);
            System.out.println("Created event: " + event);
        } catch (ParseException e) {
            throw new RemoteException("Error parsing date. Please use the format yyyy-MM-dd", e);
        }
    }

    @Override
    public String readEvent(String title) throws RemoteException {
        Event event = events.get(title);
        return event != null ? event.toString() : "Event not found: " + title;
    }

    @Override
    public void updateEvent(String title, String date, String category, boolean isPeriodic) throws RemoteException {
        try {
            Event event = events.get(title);
            if (event == null) {
                throw new RemoteException("Event not found: " + title);
            }
            Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            event.setDate(eventDate);
            event.setCategory(category);
            event.setPeriodic(isPeriodic);
            System.out.println("Updated event: " + event);
        } catch (ParseException e) {
            throw new RemoteException("Error parsing date. Please use the format yyyy-MM-dd", e);
        }
    }

    @Override
    public void deleteEvent(String title) throws RemoteException {
        if (events.remove(title) != null) {
            System.out.println("Deleted event: " + title);
        } else {
            throw new RemoteException("Event not found: " + title);
        }
    }

    @Override
    public List<String> filterEvents(String filterType) throws RemoteException {
        List<Event> filteredEvents = new ArrayList<>(events.values());

        switch (filterType.toLowerCase()) {
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
                throw new RemoteException("Invalid filter type");
        }

        return filteredEvents.stream().map(Event::toString).collect(Collectors.toList());
    }
}

