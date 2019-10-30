package com.gold.kds517.funmedia_new.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.adapter.CategoryAdapter;
import com.gold.kds517.funmedia_new.adapter.EpgAdapter;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.apps.MyApp;
import com.gold.kds517.funmedia_new.dialog.PinDlg;
import com.gold.kds517.funmedia_new.models.CategoryModel;
import com.gold.kds517.funmedia_new.models.EPGChannel;
import com.gold.kds517.funmedia_new.models.EPGEvent;
import com.gold.kds517.funmedia_new.models.FullModel;
import com.gold.kds517.funmedia_new.utils.PreCachingLayoutManager;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;

public class TvGuideActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback, IVLCVout.Callback {
    private String TAG=getClass().getSimpleName();
    private EpgAdapter epgAdapter;
    private RecyclerView category_recycler_view,epg_recyclerview;
    private TextView duration, title, content, channel_name;
    private int categoryPos=0, channelPos=0, programPos;
    RelativeLayout ly_surface;
    public static SurfaceView surfaceView;
    SurfaceView remote_subtitles_surface;
    private SurfaceHolder holder;
    MediaPlayer.TrackDescription[] traks;
    MediaPlayer.TrackDescription[] subtraks;
    LinearLayout def_lay;
    String ratio;
    String[] resolutions ;
    int current_resolution = 0;
    private int mVideoWidth;
    private int mVideoHeight;
    private EPGChannel selectedEpgChannel;
    private MediaPlayer mMediaPlayer = null;
    LibVLC libvlc=null;
    private String contentUri;
    private TextView txt_time;
    SimpleDateFormat time = new SimpleDateFormat("d MMM hh:mm a");
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_UP){
            if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                if (!epgAdapter.getIs_Header_focused()) {
                    epgAdapter.setChannelPos(channelPos);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void doWork() {
        runOnUiThread(() -> {
            try {
                txt_time.setText(time.format(new Date()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_guide);
        epg_recyclerview = findViewById(R.id.epg_recyclerview);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setExtraLayoutSpace(MyApp.SCREEN_HEIGHT);

        epg_recyclerview.setLayoutManager(layoutManager);

        category_recycler_view = findViewById(R.id.category_recyclerview);
        category_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        duration = findViewById(R.id.textView4);
        title = findViewById(R.id.textView7);
        content = findViewById(R.id.textView8);
        channel_name = findViewById(R.id.channel_name);
        def_lay = findViewById(R.id.def_lay);
        surfaceView = findViewById(R.id.surface_view);
        txt_time = findViewById(R.id.txt_time);
        Thread myThread;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();
        setUI();
        FullScreencall();
    }

    public void FullScreencall() {
        //for new api versions.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setUI() {
        epgAdapter = new EpgAdapter(epg_recyclerview,MyApp.fullModels_filter.get(0).getChannels(), this, (integer, integer2, epgChannel, epgEvent) -> {
            //onclicklistener
            playVideo(epgChannel);
            setDescription(epgChannel,epgEvent);
            return null;
        }, (i_ch, i_pro, epgChannel, epgEvent) -> {
            //onfocuslistener
            channelPos=i_ch;
            programPos=i_pro;
            setDescription(epgChannel,epgEvent);
            return null;
        });
        epg_recyclerview.setAdapter(epgAdapter);

        ly_surface = findViewById(R.id.ly_surface);
        ly_surface.setOnClickListener(this);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBX_8888);

        remote_subtitles_surface = findViewById(R.id.remote_subtitles_surface);
        remote_subtitles_surface.setZOrderMediaOverlay(true);
        remote_subtitles_surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
        params.height = MyApp.SURFACE_HEIGHT;
        params.width = MyApp.SURFACE_WIDTH;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int SCREEN_HEIGHT = displayMetrics.heightPixels;
        int SCREEN_WIDTH = displayMetrics.widthPixels;
        mVideoHeight = displayMetrics.heightPixels;
        mVideoWidth = displayMetrics.widthPixels;
        holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        ratio = mVideoWidth + ":"+ mVideoHeight;
        resolutions =  new String[]{"16:9", "4:3", ratio};
        Log.e("height", String.valueOf(MyApp.SCREEN_HEIGHT));
        category_recycler_view.setAdapter(new CategoryAdapter(MyApp.live_categories_filter,  new Function3<CategoryModel,Integer,Boolean, Unit>() {
            @Override
            public Unit invoke(CategoryModel categoryModel, Integer position,Boolean is_clicked) {
                if (!is_clicked) return null;
                if (categoryModel.getId().equalsIgnoreCase(Constants.xxx_category_id)){
                    PinDlg pinDlg = new PinDlg(TvGuideActivity.this, new PinDlg.DlgPinListener() {
                        @Override
                        public void OnYesClick(Dialog dialog, String pin_code) {
                            dialog.dismiss();
                            String pin = (String)MyApp.instance.getPreference().get(Constants.PIN_CODE);
                            if(pin_code.equalsIgnoreCase(pin)){
                                dialog.dismiss();
                                categoryPos=position;
                                epgAdapter.setList(MyApp.fullModels_filter.get(position).getChannels());
                                initializeHeader(MyApp.fullModels_filter.get(position));
                            }else {
                                dialog.dismiss();
                                Toast.makeText(TvGuideActivity.this, "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void OnCancelClick(Dialog dialog, String pin_code) {
                            dialog.dismiss();
                        }
                    });
                    pinDlg.show();
                }else {
                    categoryPos=position;
                    epgAdapter.setList(MyApp.fullModels_filter.get(position).getChannels());
                    initializeHeader(MyApp.fullModels_filter.get(position));
                }
                return null;
            }
        }));
        initializeHeader(MyApp.fullModels_filter.get(0));
    }

    private void initializeHeader(FullModel fullModel) {
        Log.e(TAG,"initialize header by changing category");
        if (fullModel.getChannels()==null || fullModel.getChannels().size()==0) {
            setDescription(null,null);
            return;
        }
        playChannel(fullModel.getChannels().get(0));
        if (fullModel.getChannels().get(0).getEvents()==null || fullModel.getChannels().get(0).getEvents().size()==0){
            setDescription(fullModel.getChannels().get(0),null);
            return;
        }
        setDescription(fullModel.getChannels().get(0),fullModel.getChannels().get(0).getEvents().get(0));//Constants.findNowEvent(fullModel.getChannels().get(0).getEvents())
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedEpgChannel!=null){
            if (libvlc != null) {
                releaseMediaPlayer();
            }
            holder = surfaceView.getHolder();
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.addCallback(this);
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int SCREEN_HEIGHT = displayMetrics.heightPixels;
            int SCREEN_WIDTH = displayMetrics.widthPixels;
            holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
            mVideoHeight = displayMetrics.heightPixels;
            mVideoWidth = displayMetrics.widthPixels;
            if (!mMediaPlayer.isPlaying())playChannel(selectedEpgChannel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    private void playVideo(EPGChannel epgChannel) {
        Log.e(TAG,"Start Video");
        if (selectedEpgChannel!=null && selectedEpgChannel.getName().equals(epgChannel.getName())){
            goVideoActivity(selectedEpgChannel);
        }else {
            if(epgChannel.is_locked() && (categoryPos==1 || categoryPos==0)){
                PinDlg pinDlg = new PinDlg(this, new PinDlg.DlgPinListener() {
                    @Override
                    public void OnYesClick(Dialog dialog, String pin_code) {
                        dialog.dismiss();
                        String pin = (String)MyApp.instance.getPreference().get(Constants.PIN_CODE);
                        if(pin_code.equalsIgnoreCase(pin)){
                            dialog.dismiss();
                            playChannel(epgChannel);
                        }else {
                            dialog.dismiss();
                            Toast.makeText(TvGuideActivity.this, "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void OnCancelClick(Dialog dialog, String pin_code) {
                        dialog.dismiss();
                    }
                });
                pinDlg.show();
            }else {
                playChannel(epgChannel);
            }
        }

    }

    private void playChannel(EPGChannel epgChannel) {
        selectedEpgChannel = epgChannel;
        contentUri = MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user, MyApp.pass,
                epgChannel.getStream_id()+"","ts");
        Log.e("url",contentUri);
        if(def_lay.getVisibility()== View.VISIBLE) def_lay.setVisibility(View.GONE);
        releaseMediaPlayer();
        try {

            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            options.add("0");//this option is used to show the first subtitle track
            options.add("--subsdec-encoding");

            libvlc = new LibVLC(this, options);

            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);
            mMediaPlayer.setAspectRatio(MyApp.SCREEN_WIDTH+":"+MyApp.SCREEN_HEIGHT);

            // Seting up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(surfaceView);
            if (remote_subtitles_surface != null)
                vout.setSubtitlesView(remote_subtitles_surface);
            vout.setWindowSize(mVideoWidth, mVideoHeight);
            vout.addCallback(this);
            vout.attachViews();


            Media m = new Media(libvlc, Uri.parse(contentUri));
            mMediaPlayer.setMedia(m);
            m.release();
            mMediaPlayer.play();

        } catch (Exception e) {
            Toast.makeText(this, "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }
    }

    private MediaPlayer.EventListener mPlayerListener = new MediaPlayerListener(this);

    @Override
    public void onSurfacesCreated(IVLCVout ivlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout ivlcVout) {

    }

    private static class MediaPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<TvGuideActivity> mOwner;

        public MediaPlayerListener(TvGuideActivity owner) {
            mOwner = new WeakReference<TvGuideActivity>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            TvGuideActivity player = mOwner.get();

            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    player.releaseMediaPlayer();
                    player.onResume();
                    break;
                case MediaPlayer.Event.Playing:
                    break;
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
                    break;
                case MediaPlayer.Event.Buffering:
                    break;
                case MediaPlayer.Event.EncounteredError:
                    player.def_lay.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.Event.TimeChanged:
                    break;
                case MediaPlayer.Event.PositionChanged:
                    break;
                default:
                    break;
            }
        }
    }

    private void releaseMediaPlayer() {
        if (libvlc == null)
            return;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.removeCallback(this);
            vout.detachViews();
        }
        holder = null;

        libvlc.release();
        libvlc = null;

    }

    private void goVideoActivity(EPGChannel epgChannel) {
        String url = MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user, MyApp.pass,
                epgChannel.getStream_id()+"","ts");
        Log.e(getClass().getSimpleName(),url);
        int current_player = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
        Intent intent;
        switch (current_player){
            case 0:
            default:
                intent = new Intent(this,LivePlayActivity.class);
                break;
            case 1:
                intent = new Intent(this,LiveIjkPlayActivity.class);
                break;
            case 2:
                intent = new Intent(this,LiveExoPlayActivity.class);
                break;
        }
        MyApp.epgChannel = epgChannel;
        intent.putExtra("title",epgChannel.getName());
        intent.putExtra("img",epgChannel.getStream_icon());
        intent.putExtra("url",url);
        intent.putExtra("stream_id",epgChannel.getStream_id());
        intent.putExtra("is_live",true);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void setDescription(EPGChannel epgChannel, EPGEvent epgEvent) {
        Log.e(TAG,"initialize header by changing program");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM d, hh:mm");
        if (epgEvent!=null){
            Date that_date = new Date();
            that_date.setTime(epgEvent.getStartTime().getTime()+ Constants.SEVER_OFFSET);
            Date end_date = new Date();
            end_date.setTime(epgEvent.getEndTime().getTime()+ Constants.SEVER_OFFSET);
            duration.setText(dateFormat1.format(that_date)+" - "+dateFormat.format(end_date));
            title.setText(epgEvent.getTitle());
            content.setText(epgEvent.getDec());
        }else {
            duration.setText("-");
            title.setText(this.getString(R.string.no_information));
            content.setText("");
        }
        if (epgChannel!=null){
            //video play
            channel_name.setText(epgChannel.getName());
        }else {
            //video stop
            channel_name.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_surface:
                goVideoActivity(selectedEpgChannel);
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
