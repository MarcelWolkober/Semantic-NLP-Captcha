package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "list_ranking_challenges")
public class ListRankingChallenge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "list_ranking_challenge_id")
    private Long id;

    @Column(nullable = false)
    private String lemma;

    @OneToOne(cascade = CascadeType.ALL)
    private Usage referenceUsage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listRankingChallenge", cascade = CascadeType.ALL)
    private List<ListUsage> listUsages;


    protected ListRankingChallenge() {
    }

    public ListRankingChallenge(String lemma, Usage referenceUsage, List<ListUsage> listUsages) {
        this.lemma = lemma;
        this.referenceUsage = referenceUsage;
        this.listUsages = listUsages;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public Usage getReferenceUsage() {
        return referenceUsage;
    }

    public void setReferenceUsage(Usage referenceUsage) {
        this.referenceUsage = referenceUsage;
    }

    public List<ListUsage> getListUsages() {
        return listUsages;
    }

    public void setListUsages(List<ListUsage> listUsages) {
        this.listUsages = listUsages;
    }

    public Long getId() {
        return id;
    }
}
