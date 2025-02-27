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
    private LocalDateTime initDate;
    private LocalDateTime endDate;
    private LocalDateTime limitDate;
    private int price;

    @OneToMany
    private List <Match> matches;

    @OneToMany
    private List<User> participants;

    @ManyToOne
    private User winner;

    public Tournament(String name, LocalDateTime initDate, LocalDateTime endDate, int price) {

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

    public LocalDateTime getInitDate() {
        return initDate;
    }

    public void setInitDate(LocalDateTime initDate) {
        this.initDate = initDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
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
}
