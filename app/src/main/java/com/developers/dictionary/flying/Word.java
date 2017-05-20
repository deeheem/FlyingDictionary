package com.developers.dictionary.flying;

/**
 * Created by gurtej on 23/1/17.
 */

public class Word {
    String name;
    String dateTime;

    public Word(String name, String dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public String getDateTime() {
        return dateTime;
    }
}
