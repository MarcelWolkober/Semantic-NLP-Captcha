package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name= "usages")
public class Usage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "lemma")
    private String lemma;

    @OneToOne(cascade = CascadeType.ALL)
    private Position pos;

    @Column(nullable = false, name = "context")
    private String context;

    protected Usage(){}

    public Usage(String lemma, Position pos, String context) {
        this.lemma = lemma;
        this.pos = pos;
        this.context = context;
    }


    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
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
}
