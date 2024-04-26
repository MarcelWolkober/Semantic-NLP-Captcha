package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "usages")
public class Usage implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "usage_id")
    private Long id;

    @Column(nullable = false, name = "lemma")
    private String lemma;

    @Column(nullable = false, name = "context", length = 2048)
    private String context;

    @Column(nullable = false, name = "start_index")
    private int posStartIndex;

    @Column(nullable = false, name = "end_index")
    private int posEndIndex;

    /*    @OneToOne(cascade = CascadeType.ALL)
    private Position pos; */

    protected Usage() {
    }

    public Usage(String lemma, String context, int posStartIndex, int posEndIndex) {
        this.lemma = lemma;
        this.context = context;
        this.posStartIndex = posStartIndex;
        this.posEndIndex = posEndIndex;
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

}
