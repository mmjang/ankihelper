package com.mmjang.duckmemo.ui.review;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.algo.SOMemoAlgorithm;
import com.mmjang.duckmemo.algo.Scheduler;
import com.mmjang.duckmemo.data.card.Card;
import com.mmjang.duckmemo.data.card.CardHtmlGenerator;
import com.mmjang.duckmemo.data.card.CardType;
import com.mmjang.duckmemo.data.news.NewsEntryPosition;
import com.mmjang.duckmemo.domain.PlayAudioManager;
import com.mmjang.duckmemo.filter.CardFilter;
import com.mmjang.duckmemo.ui.news.NewsReaderActivity;
import com.mmjang.duckmemo.util.Constant;
import com.mmjang.duckmemo.util.Utils;

import java.util.List;

public class ReviewerActivity extends AppCompatActivity implements View.OnClickListener{

    //views
    CardView mCardViewFlashCard;
    TextView mTextViewFlashCard;
    CardView mCardViewButtonPanel;
    LinearLayout mPanelShowAnswer;
    LinearLayout mPanelNewCard;
    LinearLayout mPanelOldCard;
    TextView mPanelButtonShowAnswer;
    TextView mPanelButtonNewFail;
    TextView mPanelButtonNewGood;
    TextView mPanelButtonOldFail;
    TextView mPanelButtonOldGood;
    TextView mPanelButtonOldEasy;
    ProgressBar mProgressBar;
    Button mBtnJumpSource;
    ///////
    Scheduler mScheduler;
    CardFilter mCardFilter;
    Card mCurrentCard;
    String[] mCurrentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewer);
        assaignViews();
        initViews();
        if(loadCards()){
            showCard();
            showNextCard();
        }
    }

    private void assaignViews() {
        mCardViewFlashCard = findViewById(R.id.flash_card_holder);
        mTextViewFlashCard = findViewById(R.id.flash_card_textView);
        mCardViewButtonPanel = findViewById(R.id.reviewer_bottom_panel);
        mPanelShowAnswer = findViewById(R.id.reviewer_panel_show_answer);
        mPanelNewCard = findViewById(R.id.reviewer_panel_new_card);
        mPanelOldCard = findViewById(R.id.reviewer_panel_old_card);
        mPanelButtonShowAnswer = findViewById(R.id.review_button_show_answer);
        mPanelButtonNewFail = findViewById(R.id.review_button_failed_new);
        mPanelButtonNewGood = findViewById(R.id.review_button_good_new);
        mPanelButtonOldFail = findViewById(R.id.review_button_failed_old);
        mPanelButtonOldGood = findViewById(R.id.review_button_good_old);
        mPanelButtonOldEasy = findViewById(R.id.review_button_easy_old);
        mProgressBar = findViewById(R.id.reviewer_progress_bar);
        mBtnJumpSource = findViewById(R.id.btn_jump_to_source);
    }

    private void initViews() {
        showProgressBar();
        mPanelButtonShowAnswer.setOnClickListener(this);
        mPanelButtonNewFail.setOnClickListener(this);
        mPanelButtonNewGood.setOnClickListener(this);
        mPanelButtonOldFail.setOnClickListener(this);
        mPanelButtonOldGood.setOnClickListener(this);
        mPanelButtonOldEasy.setOnClickListener(this);
        mBtnJumpSource.setOnClickListener(this);
    }

    private boolean loadCards(){
        mCardFilter = new CardFilter();
        List<Card> cardList = mCardFilter.getCardList(System.currentTimeMillis());
        mScheduler = new Scheduler(cardList, new SOMemoAlgorithm());
        if(cardList.size() > 0){
            return true;
        }else{
            mProgressBar.setVisibility(View.GONE);
            Utils.showMessage(this, "no card to review");
            return false;
        }
    }

    private void showNextCard(){
        mCurrentCard = mScheduler.getCard();
        Toast.makeText(this, "new:" + mScheduler.getNumberOfNewCard() + "old:" + mScheduler.getNumberOfOldCard(), Toast.LENGTH_SHORT).show();
        if(mCurrentCard != null) {
            if(mCurrentCard.getNote().getNewsEntryPosition() != null){
                mBtnJumpSource.setEnabled(true);
            }else{
                mBtnJumpSource.setEnabled(false);
            }
            mCurrentContent = CardHtmlGenerator.getCard(mCurrentCard.getNote(), mCurrentCard.getCardType());
            setCardHtml(mCurrentContent[0]);
            showShowAnswerPanel();
            if(mCurrentCard.getNote().getLanguage().equals("en") && mCurrentCard.getCardType() == CardType.SENTENCE_DEFINITION){
                PlayAudioManager.playEngPronounceVoice(this, mCurrentCard.getNote().getWord(), 2);
            }
        }else{
            mCardViewButtonPanel.setVisibility(View.GONE);
            Utils.showMessage(this,"review finished");
        }
    }

    private void flipCard(){
        //float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        //float distance = mCardViewFlashCard.getCameraDistance() * (scale + (scale / 3));
        //mCardViewFlashCard.setCameraDistance(distance);
        mCardViewFlashCard.animate()
                .alpha(0)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {
                                setCardHtml(mCurrentContent[1]);
                                //mCardViewFlashCard.setAlpha(1);
                                mCardViewFlashCard.animate().withLayer()
                                        .alpha(1)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setDuration(300)
                                        .start();
                            }
                        }
                ).start();
        //setCardHtml(mCurrentContent[1]);
        if(mCurrentCard.getInterval() < 0){
            showNewCardPanel();
        }else{
            showOldCardPanel();
        }

        if(mCurrentCard.getNote().getLanguage().equals("en") && mCurrentCard.getCardType() == CardType.CLOZE){
            PlayAudioManager.playEngPronounceVoice(this, mCurrentCard.getNote().getWord(), 2);
        }
    }

    private void setCardHtml(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTextViewFlashCard.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT));
        }
        else{
            mTextViewFlashCard.setText(Html.fromHtml(s));
        }
    }

    private void showProgressBar(){
        mCardViewFlashCard.setVisibility(View.GONE);
        mCardViewButtonPanel.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void showCard(){
        mCardViewFlashCard.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mCardViewButtonPanel.setVisibility(View.VISIBLE);
    }

    private void showShowAnswerPanel(){
        mPanelShowAnswer.setVisibility(View.VISIBLE);
        mPanelOldCard.setVisibility(View.GONE);
        mPanelNewCard.setVisibility(View.GONE);
    }

    private void showNewCardPanel(){
        mPanelShowAnswer.setVisibility(View.GONE);
        mPanelOldCard.setVisibility(View.GONE);
        mPanelNewCard.setVisibility(View.VISIBLE);
    }

    private void showOldCardPanel(){
        mPanelShowAnswer.setVisibility(View.GONE);
        mPanelOldCard.setVisibility(View.VISIBLE);
        mPanelNewCard.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.review_button_show_answer:
                flipCard();
                break;
            case R.id.review_button_failed_new:
                mScheduler.doReview(mCurrentCard, 1);
                showNextCard();
                break;
            case R.id.review_button_good_new:
                mScheduler.doReview(mCurrentCard, 3);
                showNextCard();
                break;
            case R.id.review_button_failed_old:
                mScheduler.doReview(mCurrentCard, 1);
                showNextCard();
                break;
            case R.id.review_button_good_old:
                mScheduler.doReview(mCurrentCard, 3);
                showNextCard();
                break;
            case R.id.review_button_easy_old:
                mScheduler.doReview(mCurrentCard, 5);
                showNextCard();
                break;
            case R.id.btn_jump_to_source:
                Intent intent = new Intent(this, NewsReaderActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                NewsEntryPosition newsEntryPosition = mCurrentCard.getNote().getNewsEntryPosition();
                intent.putExtra(Constant.INTENT_DUCKMEMO_NEWS_ID, newsEntryPosition.getNewsEntry().getId());
                intent.putExtra(Constant.INTENT_DUCKMEMO_NEWS_POSITION_INDEX, newsEntryPosition.getSentenceIndex());
                startActivity(intent);
        }
    }
}
