package es.urjc.club_tenis.model;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;

@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDateTime date;

    @ManyToOne
    private User owner;
    @ManyToOne
    private User local;
    @ManyToOne
    private User visitor;

    @ManyToOne
    private Court court;

    @ManyToOne
    private User winner = null;

    private int bestOf;

    @ElementCollection
    private ArrayList<Integer> result = new ArrayList<>();

    public void createMatch(User owner, int bestOf, LocalDateTime date){
        if(bestOf == 1 ||
                bestOf == 3 ||
                bestOf == 5) {
            this.bestOf = bestOf;
        }else {
            //Error
        }
        this.date = date;
    }

    public void calculateWinner(){
        int res = 0;

        for(int i = 0; i < result.size(); i++){
            if( result.get(i) >  result.get(i+1) ) res++;
            else if( result.get(i) <  result.get(i+1) ) res--;
        }

        if(res>0) this.winner = this.local;
        else if(res<0) this.winner = this.visitor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public int getBestOf() {
        return bestOf;
    }

    public void setBestOf(int bestOf) {
        this.bestOf = bestOf;
    }

    public ArrayList<Integer> getResult() {
        return result;
    }

    public void setResult(ArrayList<Integer> result) {
        this.result = result;
    }
}

