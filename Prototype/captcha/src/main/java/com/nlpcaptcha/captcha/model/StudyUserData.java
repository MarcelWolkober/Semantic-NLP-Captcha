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
    @JsonView(Views.Public.class)
    private String pairChallengeResults;

    @Column(name = "list_ranking_challenge_results")
    @JsonView(Views.Public.class)
    private String listRankingChallengeResults;

    @Column(name = "start_time")
    @JsonView(Views.Public.class)
    private long startTime;

    @Column(name = "pair_challenge_end_time")
    @JsonView(Views.Public.class)
    private long pairChallengeEndTime;

    @Column(name = "end_time")
    @JsonView(Views.Public.class)
    private long endTime;

    @Column(name = "feedback")
    @JsonView(Views.Public.class)
    private String feedback;


    public StudyUserData(StudyCombinedChallenge studyCombinedChallenge, String pairChallengeResults,
                         String listRankingChallengeResults, long startTime, long pairChallengeEndTime, long endTime,
                         String feedback) {
        this.studyCombinedChallenge = studyCombinedChallenge;
        this.pairChallengeResults = pairChallengeResults;
        this.listRankingChallengeResults = listRankingChallengeResults;
        this.startTime = startTime;
        this.pairChallengeEndTime = pairChallengeEndTime;
        this.endTime = endTime;
        this.feedback = feedback;
        studyCombinedChallenge.setStudyUserData(this);
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

    public void setStudyCombinedChallenge(StudyCombinedChallenge studyCombinedChallenge) {
        this.studyCombinedChallenge = studyCombinedChallenge;
    }
}
