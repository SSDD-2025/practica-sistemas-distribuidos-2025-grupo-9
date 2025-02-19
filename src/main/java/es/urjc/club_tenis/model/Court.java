package es.urjc.club_tenis.model;


import jakarta.persistence.*;
import java.util.*;
import java.time.*;

@Entity
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ElementCollection
    private ArrayList<LocalDateTime> schedule = new ArrayList<>();

    private LocalTime start;
    private LocalTime end;

    public void setLimits(LocalTime start , LocalTime end) {

        if (start.isAfter(end) || end.isBefore(start)) {
            //error
        }
        this.start = start;
        this.end = end;
    }

    public long getId() {
        return id;
    }

    public ArrayList<LocalDateTime> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<LocalDateTime> schedule) {
        this.schedule = schedule;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }
}
