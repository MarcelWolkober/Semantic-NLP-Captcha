package com.nlpcaptcha.captcha.model;

import jakarta.persistence.*;

@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "start_index")
    private int startIndex;

    @Column(nullable = false, name = "position")
    private int endIndex;


    protected Position() {
    }

    public Position(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }


    public Long getId() {
        return id;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
