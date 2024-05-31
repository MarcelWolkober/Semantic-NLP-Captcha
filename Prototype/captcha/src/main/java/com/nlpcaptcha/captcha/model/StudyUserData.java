package com.nlpcaptcha.captcha.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "study_user_data")
public class StudyUserData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonView(Views.Public.class)
    private Long id;

    @OneToOne(mappedBy = "studyUserData")
    private StudyCombinedChallenge studyCombinedChallenge;

    @Column(name = "pair_challenge_results")
    private String pairChallengeResults;

    @Column(name = "list_ranking_challenge_results")
    private String listRankingChallengeResults;

    public StudyUserData(StudyCombinedChallenge studyCombinedChallenge) {
        this.studyCombinedChallenge = studyCombinedChallenge;
    }

    protected StudyUserData() {
    }


    public Long getId() {
        return id;
    }

    public StudyCombinedChallenge getStudyCombinedChallenges() {
        return studyCombinedChallenge;
    }

    public String getPairChallengeResults() {
        return pairChallengeResults;
    }

    public String getListRankingChallengeResults() {
        return listRankingChallengeResults;
    }

    public void setPairChallengeResults(String pairChallengeResults) {
        this.pairChallengeResults = pairChallengeResults;
    }

    public void setListRankingChallengeResults(String listRankingChallengeResults) {
        this.listRankingChallengeResults = listRankingChallengeResults;
    }
}
