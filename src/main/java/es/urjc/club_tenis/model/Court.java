package es.urjc.club_tenis.model;


import jakarta.persistence.*;
import java.util.*;
import java.time.*;

@Entity
public class Court {

    @Id
    @GeneratedValue
    private long id;

    @ManyToMany
    @JoinTable(
            name = "court_matches",
            joinColumns = @JoinColumn(name = "court_id"),
            inverseJoinColumns = @JoinColumn(name = "match_id")
    )
    public List<TennisMatch> matches;

    private String name;

    private float price;

    private LocalTime start;
    private LocalTime end;

    public Court(String name, float price, LocalTime start, LocalTime end) {
        this.name = name;
        this.price = price;
        this.start = start;
        this.end = end;
    }

    public Court(){

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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