package es.urjc.club_tenis.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import java.util.*;
import java.time.*;


@Entity
public class Tournament {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private LocalDate initDate;
    private LocalDate endDate;
    private int price;

    //Ordered by ranking
    @ManyToMany
    @JoinTable(
            name = "tournament_participants",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public List<User> participants;

    @ManyToMany
    @JoinTable(
            name = "tournament_matches",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "match_id")
    )
    public List<TennisMatch> matches;

    public Tournament(String name, LocalDate initDate, LocalDate endDate, int price) {
        this.name = name;
        this.initDate = initDate;
        this.endDate = endDate;
        this.price = price;
    }

    public Tournament() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public void setInitDate(LocalDate initDate) {
        this.initDate = initDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public List<TennisMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<TennisMatch> matches) {
        this.matches = matches;
    }
}