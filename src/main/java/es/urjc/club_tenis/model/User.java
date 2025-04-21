package es.urjc.club_tenis.model;

import java.io.IOException;
import java.sql.Blob;
import jakarta.persistence.*;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.web.multipart.MultipartFile;

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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @Lob
    private Blob profilePicture;


    @ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_match",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "match_id") }
    )
    public Set<TennisMatch> playedTennisMatches;

    public boolean admin;

    public User(String username, String name, String password, MultipartFile profilePicture) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.admin = false;
        this.roles = new ArrayList<>();
        if(profilePicture!=null && !profilePicture.isEmpty()) {
            try {
                this.profilePicture = (BlobProxy.generateProxy(profilePicture.getInputStream(),
                        profilePicture.getSize()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            this.profilePicture = null;
        }
    }

    public User(String username, String name, String password, boolean admin) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.admin = true;
        this.roles = new ArrayList<>();
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

    public Set<TennisMatch> getPlayedMatches() {
        return playedTennisMatches;
    }

    public void setPlayedMatches(Set<TennisMatch> playedTennisMatches) {
        this.playedTennisMatches = playedTennisMatches;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Blob getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Blob profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id == user.id && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", admin=" + admin +
                '}';
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }
}
