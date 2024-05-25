package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

@Entity
@Table(name = "study_challenges")
public class StudyCombinedChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "study_challenge_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private PairChallenge pairChallenge;

    @OneToOne(cascade = CascadeType.ALL)
    private ListRankingChallenge listRankingChallenge;

    protected StudyCombinedChallenge() {
    }

    public StudyCombinedChallenge(PairChallenge pairChallenge, ListRankingChallenge listRankingChallenge) {
        this.pairChallenge = pairChallenge;
        this.listRankingChallenge = listRankingChallenge;
    }

    public Long getId() {
        return id;
    }

    public ListRankingChallenge getListRankingChallenge() {
        return listRankingChallenge;
    }

    public void setListRankingChallenge(ListRankingChallenge listRankingChallenge) {
        this.listRankingChallenge = listRankingChallenge;
    }

    public PairChallenge getPairChallenge() {
        return pairChallenge;
    }

    public void setPairChallenge(PairChallenge pairChallenge) {
        this.pairChallenge = pairChallenge;
    }
}
