package com.mmjang.duckmemo.algo;

import com.mmjang.duckmemo.data.card.SM2Card;

public interface MemoAlgorithm {
    void calculate(SM2Card card, int quality, long reviewTime);
}
