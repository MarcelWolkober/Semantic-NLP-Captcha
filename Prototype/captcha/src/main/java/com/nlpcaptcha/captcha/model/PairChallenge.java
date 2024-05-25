package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "pair_challenges")
public class PairChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pair_challenge_id")
    private Long id;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pairChallenge", cascade = CascadeType.ALL)
    private List<UsagePair> listUsagePairs;

    protected PairChallenge() {
    }

    public PairChallenge(String identifier, List<UsagePair> listUsagePairs) {
        this.identifier = identifier;
        this.listUsagePairs = listUsagePairs;
    }

    public List<UsagePair> getListUsagePairs() {
        return listUsagePairs;
    }

    public void setListUsagePairs(List<UsagePair> listUsagePairs) {

        this.listUsagePairs = listUsagePairs;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }
}
