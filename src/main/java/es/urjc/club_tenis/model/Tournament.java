package es.urjc.club_tenis.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tournament_participants",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public Set<User> participants;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "tournament_matches",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "match_id")
    )
    public Set<TennisMatch> matches;

    public Tournament(String name, LocalDate initDate, LocalDate endDate, int price) {
        this.name = name;
        this.initDate = initDate;
        this.endDate = endDate;
        this.price = price;
        this.matches = new HashSet<>();
        this.participants = new HashSet<>();
    }

    public Tournament() {

    }

    public TennisMatch addMatch(TennisMatch match) {
        Set<TennisMatch> matches = this.getMatches();
        if(matches == null){
            matches = new HashSet<>();
        }
        if(matches.isEmpty()){
            matches.add(match);
        }else if(!matches.contains(match)) {
            matches.add(match);
            if(!participants.contains(match.getLocal())) {
                participants.add(match.getLocal());
            }
            if(!participants.contains(match.getVisitor())) {
                participants.add(match.getVisitor());
            }
        }
        match.setTournament(this);
        return match;
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

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Set<TennisMatch> getMatches() {
        return matches;
    }

    public void setMatches(Set<TennisMatch> matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", initDate=" + initDate +
                ", endDate=" + endDate +
                ", price=" + price +
                ", matchesSize=" + matches.size()+
                ", participantSize=" + participants.size()+
                '}';
    }
}