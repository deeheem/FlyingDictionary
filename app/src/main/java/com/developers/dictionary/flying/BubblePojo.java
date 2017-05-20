package com.developers.dictionary.flying;

/**
 * Created by deeheem on 6/2/17.
 */

public class BubblePojo {

    String word;
    boolean check;

    public BubblePojo(String word, boolean check) {
        this.word = word;
        this.check = check;
    }

    public String getWord() {
        return word;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
