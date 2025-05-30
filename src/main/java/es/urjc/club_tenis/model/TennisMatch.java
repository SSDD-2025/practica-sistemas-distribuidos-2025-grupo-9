package es.urjc.club_tenis.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class TennisMatch {

    public static final int PAGE_SIZE = 5;

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @ManyToOne
    @JoinColumn(name = "local_id")
    private User local;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private User visitor;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;

    private String result;

    public TennisMatch(){}

    public TennisMatch(User owner, User local, User visitor, User winner, String result, Court court){
        this.owner = owner;
        this.local = local;
        this.visitor = visitor;
        this.winner = winner;
        this.result = result.isEmpty() ? "Aun no se ha jugado." : result;
        this.court = court;
    }

    public TennisMatch(User local, User visitor, Court court, User winner, String result){
        this.local = local;
        this.owner = local;
        this.visitor = visitor;
        this.court = court;
        this.winner = winner;
        this.result = result.isEmpty() ? "Aun no se ha jugado." : result;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public User getLocal() {
        return local;
    }

    public void setLocal(User local) {
        this.local = local;
    }

    public User getVisitor() {
        return visitor;
    }

    public void setVisitor(User visitor) {
        this.visitor = visitor;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TennisMatch that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TennisMatch{" +
                "id=" + id +
                ", owner=" + owner +
                ", winner=" + winner +
                ", local=" + local +
                ", visitor=" + visitor +
                ", court=" + court +
                ", result='" + result + '\'' +
                '}';
    }
}

