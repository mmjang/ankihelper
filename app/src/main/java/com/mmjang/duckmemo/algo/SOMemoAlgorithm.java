package com.mmjang.duckmemo.algo;

import com.google.common.primitives.Ints;
import com.mmjang.duckmemo.data.card.SM2Card;
import com.mmjang.duckmemo.util.DateUtils;

import java.util.Arrays;

/**
 * modified implementattion from
 * https://stackoverflow.com/questions/49047159/spaced-repetition-algorithm-from-supermemo-sm-2
 */
public class SOMemoAlgorithm implements MemoAlgorithm {

    private static final int QUALITY_T = 3;
    private int mFirstInteval;
    private int mSecondInteval;

    public SOMemoAlgorithm(int firstInteval, int secondInteval){
        mFirstInteval = firstInteval;
        mSecondInteval = secondInteval;
    }

    public SOMemoAlgorithm(){
        mFirstInteval = 1; //default
        mSecondInteval = 3;
    }

    @Override
    public void calculate(SM2Card card, int quality, long reviewTime) {
        if (quality < 0 || quality > 5) {
            throw new IllegalArgumentException("quality is between 1 and 5");
        }

        // retrieve the stored values (default values if new cards)
        int repetitions = card.getRepetitions();
        float easiness = card.getEasinessFactor();
        int interval = card.getInterval();

        int[] initialSteps = card.getInitialSteps();

        if(interval > 0) {//interval < 0, minutes, interval > 0 days;
            //the card has graduated
            // easiness factor
            easiness = (float) Math.max(1.3, easiness + 0.1 - (5.0 - quality) * (0.08 + (5.0 - quality) * 0.02));

            // repetitions
            if (quality < QUALITY_T) {
                repetitions = 0;
            } else {
                repetitions += 1;
            }

            // interval
            if (repetitions <= 1) {
                interval = mFirstInteval;
            } else if (repetitions == 2) {
                interval = mSecondInteval;
            } else {
                interval = Math.round(interval * easiness);
            }

        }else{
            //1, 10 as the default step
            int positiveMinuteInteval = -interval;
            int currentPosition = Ints.indexOf(initialSteps, positiveMinuteInteval);
            if(currentPosition < 0){
                //not found
                card.setInterval(-initialSteps[0]);
            }else{
                if(currentPosition == initialSteps.length - 1){
                    //last interval
                    card.setInterval(mFirstInteval);
                }else{
                    if(quality < QUALITY_T){
                        //failed, set interval to initial state
                        card.setInterval(-initialSteps[0]);
                    }else{
                        int nextStep = -initialSteps[currentPosition + 1];
                        card.setInterval(nextStep);
                    }
                }
            }
        }

        if(card.getInterval() > 0) {
            // next practice
            int millisInADay = 60 * 60 * 24 * 1000;
            long startOfToday = DateUtils.fromMillisToStartOfDay(reviewTime);
            long nextPracticeDate = startOfToday + millisInADay * interval;
            card.setEasinessFactor(easiness);
            card.setInterval(interval);
            card.setRepetitions(repetitions);
            card.setNextReviewTime(nextPracticeDate);
        }else{
            int millisInAMinute = 60 * 1000;
            long nextPracticeDate = reviewTime + millisInAMinute * (-interval);
            card.setNextReviewTime(nextPracticeDate);
        }
    }
}
