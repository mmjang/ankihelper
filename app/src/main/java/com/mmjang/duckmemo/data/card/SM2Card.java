package com.mmjang.duckmemo.data.card;

public interface SM2Card {
    long getNextReviewTime();
    int getRepetitions();
    int getInterval(); // < 0 not graduated minutes, > 0 graduated days
    float getEasinessFactor();
    int[] getInitialSteps();//in data base this separated by comma

    void setNextReviewTime(long time);
    void setRepetitions(int repetitions);
    void setInterval(int interval);
    void setEasinessFactor(float easinessFactor);
}
