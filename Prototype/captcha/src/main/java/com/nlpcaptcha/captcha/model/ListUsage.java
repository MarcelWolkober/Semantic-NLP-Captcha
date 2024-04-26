package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name= "list_usages")
public class ListUsage implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_ranking_challenge_id", nullable = false)
    private ListRankingChallenge listRankingChallenge;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "lemma")
    private String lemma;


    @Column(nullable = false, name = "context")
    private String context;

    @Column(nullable = false, name = "start_index")
    private int posStartIndex;

    @Column(nullable = false, name = "end_index")
    private int posEndIndex;

    @Column(nullable = false, name = "ranking_position")
    private int rankingPosition;

    /*    @OneToOne(cascade = CascadeType.ALL)
    private Position pos; */

    protected ListUsage(){}

    public ListUsage(String lemma, String context, int posStartIndex, int posEndIndex) {
        this.lemma = lemma;
        this.context = context;
        this.posStartIndex = posStartIndex;
        this.posEndIndex = posEndIndex;
    }

    public ListRankingChallenge getListChallenge() {
        return listRankingChallenge;
    }

    public void setListChallenge(ListRankingChallenge listRankingChallenge) {
        this.listRankingChallenge = listRankingChallenge;
    }


    public ListRankingChallenge getListRankingChallenge() {
        return listRankingChallenge;
    }

    public void setListRankingChallenge(ListRankingChallenge listRankingChallenge) {
        this.listRankingChallenge = listRankingChallenge;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getPosStartIndex() {
        return posStartIndex;
    }

    public void setPosStartIndex(int posStartIndex) {
        this.posStartIndex = posStartIndex;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getPosEndIndex() {
        return posEndIndex;
    }

    public void setPosEndIndex(int posEndIndex) {
        this.posEndIndex = posEndIndex;
    }

    public Long getId() {
        return id;
    }

    public int getRankingPosition() {
        return rankingPosition;
    }

    public void setRankingPosition(int rankingPosition) {
        this.rankingPosition = rankingPosition;
    }
}
