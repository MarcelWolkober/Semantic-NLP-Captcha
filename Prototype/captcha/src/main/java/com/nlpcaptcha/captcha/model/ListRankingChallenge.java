package com.nlpcaptcha.captcha.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;



@Entity
@Table(name = "list_ranking_challenges")
public class ListRankingChallenge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "list_ranking_challenge_id")
    @JsonView(Views.Public.class)
    private Long id;

    /**
     * Unique identifier of the challenge by combining the identifiers of the usages with "||" as separator
     */
    @Column(nullable = false, name = "identifier", unique = true, length = 512)
    @JsonView(Views.Public.class)
    private String identifier;

    @Column(nullable = false)
    @JsonView(Views.Public.class)
    private String lemma;

    @JsonManagedReference
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonView(Views.Public.class)
    private Usage referenceUsage;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "list_ranking_challenge_usages",
            joinColumns = @JoinColumn(name = "list_ranking_challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "usage_id"))
    @JsonView(Views.Public.class)
    private final List<Usage> listUsages = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "list_ranking_challenge_id"))
    @Column(name = "order_item")
    private List<String> order;


    @OneToMany(mappedBy = "listRankingChallenge")
    private final Set<StudyCombinedChallenge> studyCombinedChallenges = new HashSet<>();

    protected ListRankingChallenge() {
    }

    public ListRankingChallenge(String identifier, String lemma, Usage referenceUsage, List<Usage> listUsages, List<String> order) {
        this.identifier = identifier;
        this.lemma = lemma;
        setReferenceUsage(referenceUsage);
        for (Usage usage : listUsages) {
            addListUsage(usage);
        }
        this.order = order;
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
        referenceUsage.addListChallengeAsReferenceUsage(this);
    }

    public List<Usage> getListUsages() {
        return listUsages;
    }

    public void addListUsage(Usage usage) {
        this.listUsages.add(usage);
        usage.addListChallengeAsListUsage(this);
    }

    public Long getId() {
        return id;
    }

    public Set<StudyCombinedChallenge> getStudyCombinedChallenges() {
        return studyCombinedChallenges;
    }

    public void addStudyCombinedChallenge(StudyCombinedChallenge studyCombinedChallenge) {
        this.studyCombinedChallenges.add(studyCombinedChallenge);

        if (!getStudyCombinedChallenges().contains(studyCombinedChallenge)) {
            studyCombinedChallenge.setListRankingChallenge(this);
        }
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
        return Objects.equals(this.identifier, that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }


    public String getIdentifier() {
        return identifier;
    }

    public List<String> getOrder() {
        return order;
    }
}
