package com.mmjang.ankihelper.domain;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.mmjang.ankihelper.data.Settings;

import java.util.HashMap;
import java.util.Locale;

public class PlayAudioManager {
    private static final String TAG = "PlayAudioManager";
    private static MediaPlayer mediaPlayer;
    private static TextToSpeech tts;

    private static void playAudio(final Context context, final String url) throws Exception {
//        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer.create(context, Uri.parse(url));
//        }
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                killMediaPlayer();
//            }
//        });
//        mediaPlayer.start();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                        }
                    }
            );
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(context, Uri.parse(url));
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });

        mediaPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mp.reset();
                        return false;
                    }
                }
        );
    }

    private static void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void playPronounceVoice(final Context context, final String word) {
        int lastPronounceLanguage = Settings.getInstance(context).getLastPronounceLanguage();
        String youdaoLanguageType = PronounceManager.getYoudaoTypeFromLanguageIndex(lastPronounceLanguage);
        try {
            PlayAudioManager.playAudio(context, "https://dict.youdao.com/dictvoice?audio=" + word + "&le=" + youdaoLanguageType);
        } catch (Exception e) {
            Toast.makeText(context, "获取发音失败,请检查网络设置或单词拼写。", Toast.LENGTH_SHORT).show();
        }
    }


    public static final int EN_PRONOUNCE_BRITISH = 1;
    public static final int EN_PRONOUNCE_AMERICAN = 2;

    /**
     * 播放英文发音
     *
     * @param word
     * @param voiceType 1 英音 2 美音
     */
    public static void playEngPronounceVoice(final Context context, final String word, final int voiceType) {
        try {
            PlayAudioManager.playAudio(context, "https://dict.youdao.com/dictvoice?audio=" + word + "&type=" + voiceType);
        } catch (Exception e) {
            Toast.makeText(context, "获取发音失败,请检查网络设置或单词拼写。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放中文语音
     *
     * @param context
     * @param chinese
     */
    public static void playCNPronVoice(final Context context, final String chinese) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            killTTS();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Toast.makeText(context, "发音失败", Toast.LENGTH_SHORT).show();
                            killTTS();
                        }
                    });
                    int result = tts.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {
                        ConvertTextToSpeech(context, chinese);
                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });
    }

    private static void killTTS() {
        if (tts != null) {

            tts.stop();
            tts.shutdown();
        }
    }

    private static void ConvertTextToSpeech(Context context, String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(context, s);
        } else {
            ttsUnder20(s);
        }
    }

    @SuppressWarnings("deprecation")
    private static void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void ttsGreater21(Context context, String text) {
        String utteranceId = context.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
