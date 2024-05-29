package com.nlpcaptcha.captcha.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "usage_pairs")
public class UsagePair implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonView(Views.Public.class)
    private Long id;

    @Column(name = "identifier", nullable = false, unique = true)
    @JsonView(Views.Public.class)
    private String identifier;

//    @OneToMany(mappedBy = "usagePair", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    private final List<Usage> usages = new ArrayList<>();//maybe as javafx Pair

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "usage_pair_usage",
            joinColumns = @JoinColumn(name = "usage_pair_id"),
            inverseJoinColumns = @JoinColumn(name = "usage_id"))
    @JsonView(Views.Public.class)
    private final Set<Usage> usages = new HashSet<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pair_challenge_id")
    @JsonView(Views.Public.class)
    private PairChallenge pairChallenge;

    @Column(name = "label")
    @JsonView(Views.Public.class)
    private float label;

    public UsagePair(String identifier, Usage usage1, Usage usage2, Float label) {
        this.identifier = identifier;
        addUsage(usage1);
        addUsage(usage2);
        this.label = label;
    }

    public void addUsage(Usage usage) {
        this.usages.add(usage);
        usage.addUsagePair(this);
    }

    public Float getLabel() {
        return label;
    }

    public void setLabel(Float label) {
        this.label = label;
    }

    protected UsagePair() {
    }

    public PairChallenge getPairChallenge() {
        return pairChallenge;
    }

    public void setPairChallenge(PairChallenge pairChallenge) {
        this.pairChallenge = pairChallenge;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<Usage> getUsages() {
        return usages;
    }

    @Override
    public String toString() {
        return "UsagePair{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", usages=" + usages +
                ", pairChallenge=" + pairChallenge +
                ", label=" + label +
                '}';
    }


}
