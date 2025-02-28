package es.urjc.club_tenis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;


@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;
    @Column(unique=true)
    public String username;
    public String name;
    public String password;
    public String statistics;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    public List<TennisMatch> playedTennisMatches;

    @ManyToMany(mappedBy = "participants")
    public List<Tournament> tournaments;

    public User(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.statistics = "Aun no ha jugado ningun partido";
    }


    public User() {

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatistics() {
        return statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    public List<TennisMatch> getPlayedMatches() {
        return playedTennisMatches;
    }

    public void setPlayedMatches(List<TennisMatch> playedTennisMatches) {
        this.playedTennisMatches = playedTennisMatches;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }
}
