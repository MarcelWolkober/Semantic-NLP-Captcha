package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name= "list_usages")
public class ListUsage implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_challenges_id", nullable = false)
    private ListChallenge listChallenge;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "lemma")
    private String lemma;

    @OneToOne(cascade = CascadeType.ALL)
    private Position pos;

    @Column(nullable = false, name = "context")
    private String context;


    protected ListUsage(){}

    public ListUsage(String lemma, Position pos, String context) {
        this.lemma = lemma;
        this.pos = pos;
        this.context = context;
    }

    public ListChallenge getListChallenge() {
        return listChallenge;
    }

    public void setListChallenge(ListChallenge listChallenge) {
        this.listChallenge = listChallenge;
    }




}
