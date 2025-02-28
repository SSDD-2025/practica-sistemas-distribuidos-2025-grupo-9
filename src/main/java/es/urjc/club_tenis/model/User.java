package es.urjc.club_tenis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique=true)
    public String username;
    public String name;
    public String password;
    public String statistics;
    @OneToMany
    @JsonIgnore
    public List<User> followedBy;
    @OneToMany
    @JsonIgnore
    public List<User> followedUsers;
    @OneToMany
    public List<Match> playedMatches;

    private boolean admin;

    public User(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.statistics = "Aún no ha jugado ningún partido";
        this.followedBy = new ArrayList<User>();
        this.followedUsers = new ArrayList<User>();
        this.playedMatches = new ArrayList<Match>();
    }

    public User() {}

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

    public List<User> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(List<User> followedBy) {
        this.followedBy = followedBy;
    }

    public List<User> getFollowing() {
        return followedUsers;
    }

    public void setFollowing(List<User> following) {
        this.followedUsers = following;
    }

    public List<Match> getPlayedMatches() {
        return playedMatches;
    }

    public void setPlayedMatches(List<Match> playedMatches) {
        this.playedMatches = playedMatches;
    }

    public List<User> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(List<User> followedUsers) {
        this.followedUsers = followedUsers;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
