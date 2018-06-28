package com.cauliflower.danielt.fab;

import android.app.Activity;
import android.graphics.Outline;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ImageButton mFab;
    TextView mMarqueeText;
    boolean mIsPlay;
    String mArticle;
    Marquee mMarquee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFab = (ImageButton) findViewById(R.id.fab);
        mMarqueeText = (TextView) findViewById(R.id.textView1);
        mArticle = getString(R.string.description_1);

        //實作試圖外觀
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        };

        //套用視圖外觀到 ImageButton
        mFab.setOutlineProvider(viewOutlineProvider);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFab.setImageResource(mIsPlay ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
                mIsPlay = !mIsPlay;
                try {
                    //第一次跑馬燈
                    if (mMarquee == null) {
                        mMarquee = new Marquee();
                        mMarquee.start();
                        //跑馬燈結束後
                    } else if (mMarquee.isMarqueeFinished()) {
                        mMarquee.start();
                        //暫停後繼續播放
                    } else if (mIsPlay) {
                        mMarquee.run();
                    }
                } catch (IllegalThreadStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * By accumulating an int to show visual effect of mMarquee.
     * For example,show one text at the 0.1 second and show two text at the 0.2 seconds
     */
    class Marquee extends Thread {
        private int length = 0;
        private int maxLength = mArticle.length();
        //The smaller the fast
        private int speed = 50;

        private Handler handler = new Handler();
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (length <= maxLength && mIsPlay) {
                    mMarqueeText.setText(mArticle.subSequence(0, length));
                    length++;
                    play();
                } else if (length > maxLength) {
                    mFab.setImageResource(android.R.drawable.ic_media_play);
                    length = 0;
                    mIsPlay = false;
                }
            }
        };

        @Override
        public void run() {
            if (mIsPlay) {
                play();
            }
        }

        private void play() {
            handler.postDelayed(runnable, speed);
        }

        //Check
        public boolean isMarqueeFinished() {
            if (length > maxLength && !mIsPlay &&
                    mFab.getDrawable() == getResources().getDrawable(android.R.drawable.ic_media_pause)) {
                return true;
            }
            return false;
        }

    }
}
