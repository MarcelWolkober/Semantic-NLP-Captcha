package com.nlpcaptcha.captcha.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.Objects;

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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private StudyCombinedChallenge studyCombinedChallenge;

    @Column(name = "pair_challenge_results", length = 4000)
    @JsonView(Views.Public.class)
    private String pairChallengeResults;

    @Column(name = "list_ranking_challenge_results", length = 4000)
    @JsonView(Views.Public.class)
    private String listRankingChallengeResults;

    @Column(name = "start_time")
    @JsonView(Views.Public.class)
    private long startTime;

    @Column(name = "pair_challenge_end_time")
    @JsonView(Views.Public.class)
    private long firstChallengeEndTime;

    @Column(name = "end_time")
    @JsonView(Views.Public.class)
    private long endTime;

    @Column(name = "feedback", length = 8000)
    @JsonView(Views.Public.class)
    private String feedback;


    public StudyUserData(StudyCombinedChallenge studyCombinedChallenge, String pairChallengeResults,
                         String listRankingChallengeResults, long startTime, long firstChallengeEndTime, long endTime,
                         String feedback) {
        this.studyCombinedChallenge = studyCombinedChallenge;
        this.pairChallengeResults = pairChallengeResults;
        this.listRankingChallengeResults = listRankingChallengeResults;
        this.startTime = startTime;
        this.firstChallengeEndTime = firstChallengeEndTime;
        this.endTime = endTime;
        this.feedback = feedback;
        studyCombinedChallenge.addStudyUserData(this);
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

    @Override
    public String toString() {
        return "StudyUserData{" +
                "id=" + id +
                ", studyCombinedChallenge=" + studyCombinedChallenge.getId() +
                ", pairChallengeResults='" + pairChallengeResults + '\'' +
                ", listRankingChallengeResults='" + listRankingChallengeResults + '\'' +
                ", startTime=" + startTime +
                ", pairChallengeEndTime=" + firstChallengeEndTime +
                ", endTime=" + endTime +
                ", feedback='" + feedback + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudyUserData that)) return false;
        return this.getId().equals(that.getId()) &&
                this.getPairChallengeResults().equals(that.getPairChallengeResults()) &&
                this.getListRankingChallengeResults().equals(that.getListRankingChallengeResults()) &&
                this.getStartTime() == that.getStartTime() &&
                this.getFirstChallengeEndTime() == that.getFirstChallengeEndTime() &&
                this.getEndTime() == that.getEndTime() &&
                this.getFeedback().equals(that.getFeedback());

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getPairChallengeResults(), this.getListRankingChallengeResults(), this.getStartTime(), this.getFirstChallengeEndTime(), this.getEndTime(), this.getFeedback());
    }

    public StudyCombinedChallenge getStudyCombinedChallenge() {
        return studyCombinedChallenge;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFirstChallengeEndTime() {
        return firstChallengeEndTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getFeedback() {
        return feedback;
    }
}
