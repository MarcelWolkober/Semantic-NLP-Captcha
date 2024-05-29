package com.nlpcaptcha.captcha.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "list_ranking_challenges")
public class ListRankingChallenge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "list_ranking_challenge_id")
    @JsonView(Views.Public.class)
    private Long id;

    @Column(nullable = false)
    @JsonView(Views.Public.class)
    private String lemma;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonView(Views.Public.class)
    private Usage referenceUsage;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "list_ranking_challenge_usages",
            joinColumns = @JoinColumn(name = "list_ranking_challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "usage_id"))
    @JsonView(Views.Public.class)
    private final List<Usage> listUsages = new ArrayList<>();


    @OneToMany(mappedBy = "listRankingChallenge")
    private final Set<StudyCombinedChallenge> studyCombinedChallenges = new HashSet<>();

    protected ListRankingChallenge() {
    }

    public ListRankingChallenge(String lemma, Usage referenceUsage, List<Usage> listUsages) {
        this.lemma = lemma;
        this.referenceUsage = referenceUsage;
        for (Usage usage : listUsages) {
            addListUsage(usage);
        }
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

    public List<Usage> getListUsages() {
        return listUsages;
    }

    public void addListUsage(Usage usage) {
        this.listUsages.add(usage);
    }

    public Long getId() {
        return id;
    }

    public Set<StudyCombinedChallenge> getStudyCombinedChallenges() {
        return studyCombinedChallenges;
    }

    public void addStudyCombinedChallenge(StudyCombinedChallenge studyCombinedChallenge) {
        this.studyCombinedChallenges.add(studyCombinedChallenge);
        studyCombinedChallenge.setListRankingChallenge(this);
    }

    public void removeStudyCombinedChallenge(StudyCombinedChallenge studyCombinedChallenge) {
        this.studyCombinedChallenges.remove(studyCombinedChallenge);
        studyCombinedChallenge.setListRankingChallenge(null);
    }

    @Override
    public String toString() {
        return "ListRankingChallenge{" +
                "id=" + id +
                ", lemma='" + lemma + '\'' +
                ", referenceUsage=" + referenceUsage +
                ", listUsages=" + listUsages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListRankingChallenge that = (ListRankingChallenge) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
