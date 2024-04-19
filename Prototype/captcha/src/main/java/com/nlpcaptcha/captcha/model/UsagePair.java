package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

@Entity
@Table(name= "usage_pairs")
public class UsagePair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Usage usage1;

    @OneToOne(cascade = CascadeType.ALL)
    private Usage usage2;

    @Column(name = "label")
    private Float label;

    public Usage getUsage1() {
        return usage1;
    }

    public void setUsage1(Usage usage1) {
        this.usage1 = usage1;
    }

    public Usage getUsage2() {
        return usage2;
    }

    public void setUsage2(Usage usage2) {
        this.usage2 = usage2;
    }

    public Float getLabel() {
        return label;
    }

    public void setLabel(Float label) {
        this.label = label;
    }

    protected UsagePair(){}

    public UsagePair(Usage usage1, Usage usage2, Float label) {
        this.usage1 = usage1;
        this.usage2 = usage2;
        this.label = label;
    }
    
}
