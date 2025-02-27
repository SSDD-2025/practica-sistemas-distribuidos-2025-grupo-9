package es.urjc.club_tenis.model;

import jakarta.persistence.*;
import java.util.*;
import java.time.*;


@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private LocalDate initDate;
    private LocalDate endDate;
    private LocalDate limitDate;
    private int price;

    @OneToMany
    private List <Match> matches;

    @OneToMany
    private List<User> participants;

    @ManyToOne
    private User winner;

    public Tournament(String name, LocalDate initDate, LocalDate endDate, int price) {

        this.name = name;
        this.initDate = initDate;
        this.endDate = endDate;
        this.limitDate = initDate.minusDays(3);
        this.price = price;
        this.matches = new ArrayList<Match>();
        this.participants = new ArrayList<User>();
        this.winner = null;
    }

    public Tournament() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(LocalDate limitDate) {
        this.limitDate = limitDate;
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
}
