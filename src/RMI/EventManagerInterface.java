package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EventManagerInterface extends Remote {
    void createEvent(String title, String date, String category, boolean isPeriodic) throws RemoteException;
    String readEvent(String title) throws RemoteException;
    void updateEvent(String title, String date, String category, boolean isPeriodic) throws RemoteException;
    void deleteEvent(String title) throws RemoteException;
    List<String> filterEvents(String filterType) throws RemoteException;
}