package com.nlpcaptcha.captcha.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "study_challenges")
public class StudyCombinedChallenge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "study_challenge_id")
    @JsonView(Views.Public.class)
    private Long id;

    @Column(name = "identifier", unique = true, nullable = false)
    @JsonView(Views.Public.class)
    private String identifier;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonView(Views.Public.class)
    private PairChallenge pairChallenge;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonView(Views.Public.class)
    private ListRankingChallenge listRankingChallenge;


    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonView(Views.Public.class)
    private StudyUserData studyUserData;


    protected StudyCombinedChallenge() {
    }

    public StudyCombinedChallenge(PairChallenge pairChallenge, ListRankingChallenge listRankingChallenge) {
        this.identifier = pairChallenge.getIdentifier() + "|||" + listRankingChallenge.getIdentifier();
        setPairChallenge(pairChallenge);
        setListRankingChallenge(listRankingChallenge);
    }

    public Long getId() {
        return id;
    }

    public ListRankingChallenge getListRankingChallenge() {
        return listRankingChallenge;
    }

    public void setListRankingChallenge(ListRankingChallenge listRankingChallenge) {
        this.listRankingChallenge = listRankingChallenge;
        listRankingChallenge.addStudyCombinedChallenge(this);
    }

    public PairChallenge getPairChallenge() {
        return pairChallenge;
    }

    public void setPairChallenge(PairChallenge pairChallenge) {
        this.pairChallenge = pairChallenge;
        pairChallenge.addStudyCombinedChallenge(this);
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setStudyUserData(StudyUserData studyUserData) {
        this.studyUserData = studyUserData;

    }

    public StudyUserData getStudyUserData() {
        return studyUserData;
    }

    @Override
    public String toString() {
        return "StudyCombinedChallenge{" +
                "id=" + id +
                ", pairChallenge=" + pairChallenge +
                ", listRankingChallenge=" + listRankingChallenge +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StudyCombinedChallenge studyCombinedChallenge = (StudyCombinedChallenge) obj;
        return Objects.equals(this.identifier, studyCombinedChallenge.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
