package es.urjc.club_tenis.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    private long id;
    public String username;
    public String name;
    public String password;
    public String statistics;
    @OneToMany
    public List<User> followedBy;
    @OneToMany
    public List<User> followedUsers;

    public User(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.followedBy = new ArrayList<User>();
        this.followedUsers = new ArrayList<User>();
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
}
