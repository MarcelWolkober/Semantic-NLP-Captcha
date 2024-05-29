package com.nlpcaptcha.captcha.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "pair_challenges")
public class PairChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pair_challenge_id")
    @JsonView(Views.Public.class)
    private Long id;

    @Column(name = "identifier", nullable = false, unique = true)
    @JsonView(Views.Public.class)
    private String identifier;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "pair_challenge_usage_pair",
            joinColumns = @JoinColumn(name = "pair_challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "usage_pair_id"))
    @JsonView(Views.Public.class)
    private final Set<UsagePair> usagePairs = new HashSet<>();


    @OneToMany(mappedBy = "pairChallenge")
    private final Set<StudyCombinedChallenge> studyCombinedChallenges = new HashSet<>();

    protected PairChallenge() {
    }

    public PairChallenge(String identifier, Set<UsagePair> usagePairs) {
        this.identifier = identifier;

        for (UsagePair usagePair : usagePairs) {
            addUsagePair(usagePair);
        }
    }

    private void addUsagePair(UsagePair usagePair) {
        this.usagePairs.add(usagePair);
        usagePair.addPairChallenge(this);
    }

    private void removeUsagePair(UsagePair usagePair) {
        this.usagePairs.remove(usagePair);
        usagePair.removePairChallenge(this);
    }

    public Set<StudyCombinedChallenge> getStudyCombinedChallenges() {
        return studyCombinedChallenges;
    }

    public void addStudyCombinedChallenge(StudyCombinedChallenge studyCombinedChallenge) {
        this.studyCombinedChallenges.add(studyCombinedChallenge);
        studyCombinedChallenge.setPairChallenge(this);
    }

    public void removeStudyCombinedChallenge(StudyCombinedChallenge studyCombinedChallenge) {
        this.studyCombinedChallenges.remove(studyCombinedChallenge);
        studyCombinedChallenge.setPairChallenge(null);
    }


    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<UsagePair> getUsagePairs() {
        return usagePairs;
    }

    @Override
    public String toString() {
        return "PairChallenge{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", usagePairs=" + usagePairs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairChallenge pairChallenge = (PairChallenge) o;
        return Objects.equals(this.id, pairChallenge.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }


}
