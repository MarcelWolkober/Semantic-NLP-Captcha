package com.nlpcaptcha.captcha.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;



@Entity
@Table(name = "usages")
public class Usage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonView(Views.Public.class)
    private Long id;

    @Column(nullable = false, name = "identifier", unique = true)
    @JsonView(Views.Public.class)
    private String identifier;

    @Column(nullable = false, name = "lemma")
    @JsonView(Views.Public.class)
    private String lemma;

    @Column(nullable = false, name = "context", length = 2048)
    @JsonView(Views.Public.class)
    private String context;

    @Column(nullable = false, name = "start_index")
    @JsonView(Views.Public.class)
    private int posStartIndex;

    @Column(nullable = false, name = "end_index")
    @JsonView(Views.Public.class)
    private int posEndIndex;

    @ManyToMany(mappedBy = "usages")
    private final Set<UsagePair> usagePairs = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "referenceUsage")
    private final Set<ListRankingChallenge> pairChallengesAsReferenceUsage = new HashSet<>();

    @JsonBackReference
    @ManyToMany(mappedBy = "listUsages")
    private final Set<ListRankingChallenge> pairChallengesAsListUsage = new HashSet<>();


    protected Usage() {
    }

    public Usage(String lemma, String identifier, String context, int posStartIndex, int posEndIndex) {
        this.lemma = lemma;
        this.identifier = identifier;
        this.context = context;
        this.posStartIndex = posStartIndex;
        this.posEndIndex = posEndIndex;
    }


    public Set<UsagePair> getUsagePairs() {
        return usagePairs;
    }

    public void addUsagePair(UsagePair usagePair) {
        this.usagePairs.add(usagePair);
        usagePair.getUsages().add(this);
    }

    public void removeUsagePair(UsagePair usagePair) {
        this.usagePairs.remove(usagePair);
        usagePair.getUsages().remove(this);
    }

    public Set<ListRankingChallenge> getPairChallengesAsReferenceUsage() {
        return pairChallengesAsReferenceUsage;
    }

    public Set<ListRankingChallenge> getPairChallengesAsListUsage() {
        return pairChallengesAsListUsage;
    }

    public void addListChallengeAsReferenceUsage(ListRankingChallenge listRankingChallenge) {
        this.pairChallengesAsReferenceUsage.add(listRankingChallenge);

        if (!listRankingChallenge.getReferenceUsage().equals(this)) {
            listRankingChallenge.setReferenceUsage(this);
        }
    }

    public void addListChallengeAsListUsage(ListRankingChallenge listRankingChallenge) {
        this.pairChallengesAsListUsage.add(listRankingChallenge);
        if (!listRankingChallenge.getListUsages().contains(this)) {
            listRankingChallenge.addListUsage(this);
        }

    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Long getId() {
        return id;
    }

    public int getPosStartIndex() {
        return posStartIndex;
    }

    public void setPosStartIndex(int posStartIndex) {
        this.posStartIndex = posStartIndex;
    }

    public int getPosEndIndex() {
        return posEndIndex;
    }

    public void setPosEndIndex(int posEndIndex) {
        this.posEndIndex = posEndIndex;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usage usage = (Usage) o;
        return Objects.equals(identifier, usage.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "Usage{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", lemma='" + lemma + '\'' +
                ", context='" + context + '\'' +
                ", posStartIndex=" + posStartIndex +
                ", posEndIndex=" + posEndIndex +
                '}';
    }


}
