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
@Table(name = "study_challenges")
public class StudyCombinedChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "study_challenge_id")
    @JsonView(Views.Public.class)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonView(Views.Public.class)
    private PairChallenge pairChallenge;

    @ManyToOne(cascade ={CascadeType.PERSIST, CascadeType.MERGE})
    @JsonView(Views.Public.class)
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
        return Objects.equals(this.id, studyCombinedChallenge.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}
