package RMI;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private Date date;
    private String category;
    private boolean isPeriodic;

    public Event(String title, Date date, String category, boolean isPeriodic) {
        this.title = title;
        this.date = date;
        this.category = category;
        this.isPeriodic = isPeriodic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPeriodic() {
        return isPeriodic;
    }

    public void setPeriodic(boolean periodic) {
        isPeriodic = periodic;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", category='" + category + '\'' +
                ", isPeriodic=" + isPeriodic +
                '}';
    }
}
