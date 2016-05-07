package cn.georgeyang.recognizer.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import wpam.recognizer.DataBlock;
import wpam.recognizer.Recognizer;
import wpam.recognizer.Spectrum;
import wpam.recognizer.SpectrumFragment;
import wpam.recognizer.StatelessRecognizer;

/**
 * Created by george.yang on 16/5/6.
 */
public class RecoShowVIew extends ImageView {
    public RecoShowVIew(Context context) {
        super(context);
        init();
    }

    public RecoShowVIew(Context context, AttributeSet attrs) {
        super(context, attrs);init();
    }

    public RecoShowVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecoShowVIew(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);init();
    }

    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    private void init () {
        bitmap = Bitmap.createBitmap((int) 512, (int) 100, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        this.setImageBitmap(bitmap);
    }


    private void drawSpectrum(Spectrum spectrum) {
        canvas.drawColor(Color.BLACK);

        for (int i = 0; i < spectrum.length(); ++i)
        {
            int downy = (int) (100 - (spectrum.get(i) * 100));

            int upy = 100;

            if(i >= 40 && i <= 65)
                paint.setColor(Color.rgb(130, 130, 130));
            else if(i >= 75 && i <= 100)
                paint.setColor(Color.rgb(130, 130, 130));
            else
                paint.setColor(Color.WHITE);

            canvas.drawLine(i, downy, i, upy, paint);
//			canvas.drawLine(startX, startY, stopX, stopY, paint)
        }

        paint.setColor(Color.RED);

        SpectrumFragment fragment = new SpectrumFragment(80, 200, spectrum);
        boolean[] distincts = fragment.getDistincts();

        int averageLineLevel = (int)(100 - fragment.getAverage() * 2 * 100);
        canvas.drawLine(0, averageLineLevel, 500, averageLineLevel, paint);

        invalidate();
    }


    private boolean running;
    private BlockingQueue<DataBlock> blockingQueue;//新数据

    //录音频率
    int frequency = 16000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;//单声到
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int blockSize = 1024;

    private AudioRecord audioRecord;

    public int getAudioSource()
    {
        TelephonyManager telephonyManager = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager.getCallState() != TelephonyManager.PHONE_TYPE_NONE)
            return MediaRecorder.AudioSource.VOICE_DOWNLINK;

        return MediaRecorder.AudioSource.MIC;
    }


    private Recognizer recognizer;
    private boolean isLooping;
    public void start() {
        new Thread() {
            @Override
            public void run() {
                Log.i("test","isLooping:" + isLooping);

                if (isLooping) {
                    return;
                }

                //init var
                blockingQueue = new LinkedBlockingQueue<DataBlock>();
                recognizer = new Recognizer();
                running = true;

                Log.i("test","isLoopshdgfg");
                if (audioRecord!=null) {
                    int stat = audioRecord.getState();
                    if (AudioRecord.STATE_UNINITIALIZED==stat) {
                        return;
                    }
                }

                Log.i("test","isLoossssssspshdgfg");


                int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

                audioRecord = new AudioRecord(getAudioSource(), frequency, channelConfiguration, audioEncoding, bufferSize);

                try {

                    Log.i("test","is327546753Loossssssspshdgfg");
                    short[] buffer = new short[blockSize];

                    audioRecord.startRecording();

                    newLoopThead().start();
                    while (running)
                    {
                        Log.i("test","loop get");
                        int bufferReadSize = audioRecord.read(buffer, 0, blockSize);

                        DataBlock dataBlock = new DataBlock(buffer, blockSize, bufferReadSize);

                        blockingQueue.put(dataBlock);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                    Log.i("test",Log.getStackTraceString(t));
                }

                try {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Thread newLoopThead() {
        return new Thread() {
            @Override
            public void run() {
                isLooping = true;

                while (running) {
                    try {
                        DataBlock dataBlock = blockingQueue.take();

                        Spectrum spectrum = dataBlock.FFT();

                        spectrum.normalize();

                        StatelessRecognizer statelessRecognizer = new StatelessRecognizer(spectrum);

                        Character key = recognizer.getRecognizedKey(statelessRecognizer.getRecognizedKey());

                        Message msg = Message.obtain();
                        msg.obj = spectrum;
                        msg.what = key.charValue();
                        handler.sendMessage(msg);

//				SpectrumFragment spectrumFragment = new SpectrumFragment(75, 100, spectrum);
//				publishProgress(spectrum, spectrumFragment.getMax());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                isLooping = false;
            }
        };
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (listener!=null) {
                listener.onGetter((char) msg.what);
            }
            Spectrum spectrum = (Spectrum) msg.obj;
            drawSpectrum(spectrum);
        }
    };

    private OnPhoneKeyDown listener;

    public void setListener(OnPhoneKeyDown listener) {
        this.listener = listener;
    }

    public interface OnPhoneKeyDown {
        void onGetter(char chr);
    }

    public void stop () {
        running = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }
}
