package com.gold.kds517.funmedia_new.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.adapter.MainListAdapter;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.apps.MyApp;
import com.gold.kds517.funmedia_new.dialog.PackageDlg;
import com.gold.kds517.funmedia_new.dialog.PinDlg;
import com.gold.kds517.funmedia_new.dialog.SearchDlg;
import com.gold.kds517.funmedia_new.listner.SimpleGestureFilter;
import com.gold.kds517.funmedia_new.models.EPGChannel;
import com.gold.kds517.funmedia_new.models.EPGEvent;
import com.gold.kds517.funmedia_new.models.FullModel;
import com.gold.kds517.funmedia_new.utils.Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static java.lang.Integer.parseInt;

public class PreviewChannelExoActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, View.OnClickListener,
        SimpleGestureFilter.SimpleGestureListener, AdapterView.OnItemLongClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener{

    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
    public static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties";
    public static final String DRM_MULTI_SESSION_EXTRA = "drm_multi_session";
    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";

    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";

    public static final String ABR_ALGORITHM_EXTRA = "abr_algorithm";
    private static final String ABR_ALGORITHM_DEFAULT = "default";
    private static final String ABR_ALGORITHM_RANDOM = "random";

    private static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";

    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private PlayerView playerView;
    private FrameLayout frameLayout;
    private TextView debugTextView;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private FrameworkMediaDrm mediaDrm;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private DebugTextViewHelper debugViewHelper;
    private TrackGroupArray lastSeenTrackGroupArray;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    private AdsLoader adsLoader;
    private Uri loadedAdTagUri;
    private ViewGroup adUiViewGroup;


    private int mVideoWidth;
    private int mVideoHeight;
    private SimpleGestureFilter detector;
    Context context = null;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat time = new SimpleDateFormat("hh:mm");
    LinearLayout def_lay,ly_bottom,ly_resolution,ly_audio,ly_subtitle;
    RelativeLayout ly_header;

    boolean first = true;

    ImageView btn_back,btn_guide,image_clock,image_star,channel_logo,logo,image_icon;
    RelativeLayout ly_surface,main_lay;
    List<FullModel> full_datas;
    List<EPGChannel> channels;
    List<String> pkg_datas;
    EPGChannel sel_model;
    List<EPGEvent> epgModelList;
    int channel_pos,sub_pos=0,epg_pos,preview_pos,move_pos = 0,osd_time,pro,msg_time = 0;
    MainListAdapter adapter;
    ListView channel_list;
    TextView txt_time,txt_category,txt_title,txt_dec,txt_channel,txt_date,txt_time_passed,txt_remain_time,txt_last_time,txt_current_dec,txt_next_dec,
            firstTime,firstTitle,secondTime,secondTitle,thirdTime,thirdTitle,fourthTime,fourthTitle,txt_progress,num_txt;
    TextView txt_rss;
    SeekBar seekbar;
    String contentUri,mStream_id,key = "",rss = "";
    Handler mHandler = new Handler();
    Handler moveHandler = new Handler();
    Handler mEpgHandler = new Handler();
    Handler rssHandler = new Handler();
    Handler removeHamdler = new Handler();
    Runnable mTicker,moveTicker,mEpgTicker,rssTicker;
    boolean is_full = false,is_up = false,is_create= true,is_rss = false,is_msg = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        setContentView(R.layout.activity_preview_exo_channel);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mediaDataSourceFactory = buildDataSourceFactory(true);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        MyApp.is_welcome = false;
        osd_time = (int) MyApp.instance.getPreference().get(Constants.OSD_TIME);
        detector = new SimpleGestureFilter(this, PreviewChannelExoActivity.this);
        context = this;
        pkg_datas = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.package_list).length; i++) {
            pkg_datas.add(getResources().getStringArray(R.array.package_list)[i]);
        }
        main_lay = (RelativeLayout)findViewById(R.id.main_lay);
        main_lay.setOnClickListener(this);
        MyApp.is_first = true;
        full_datas = MyApp.fullModels_filter;
        channel_pos = (int) MyApp.instance.getPreference().get(Constants.getCHANNEL_POS());

        ly_bottom = findViewById(R.id.ly_bottom);
        channel_list = findViewById(R.id.channel_list);
        channel_list.setOnItemClickListener(this);
        channel_list.setOnItemLongClickListener(this);
        btn_back = findViewById(R.id.btn_back);
        btn_guide = findViewById(R.id.btn_guide);
        image_clock = findViewById(R.id.image_clock);
        image_star = findViewById(R.id.image_star);
        channel_logo = findViewById(R.id.channel_logo);
        btn_back.setOnClickListener(this);
        btn_guide.setOnClickListener(this);
        txt_time = findViewById(R.id.txt_time);
        txt_category = findViewById(R.id.txt_category);
        txt_category.setText(MyApp.maindatas.get(channel_pos));
        txt_title = findViewById(R.id.txt_title);
        txt_dec = findViewById(R.id.txt_dec);
        txt_channel = findViewById(R.id.txt_channel);
        txt_date = findViewById(R.id.txt_date);
        txt_time_passed = findViewById(R.id.txt_time_passed);
        txt_remain_time = findViewById(R.id.txt_remain_time);
        txt_last_time = findViewById(R.id.txt_last_time);
        txt_current_dec = findViewById(R.id.txt_current_dec);
        txt_next_dec = findViewById(R.id.txt_next_dec);
        firstTime = findViewById(R.id.txt_firstTime);
        firstTitle = findViewById(R.id.txt_firstTitle);
        secondTime  = findViewById(R.id.secondTime);
        secondTitle = findViewById(R.id.secondTitle);
        thirdTime = findViewById(R.id.thirdTime);
        thirdTitle = findViewById(R.id.thirdTitle);
        fourthTime = findViewById(R.id.fourthTime);
        fourthTitle = findViewById(R.id.fourthTitle);
        txt_progress = findViewById(R.id.txt_progress);
        num_txt = findViewById(R.id.txt_num);
        txt_progress.setVisibility(View.GONE);
        seekbar = findViewById(R.id.seekbar);
        seekbar.setMax(100);
        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();
        def_lay = findViewById(R.id.def_lay);
        ly_surface = findViewById(R.id.ly_surface);
        ly_surface.setOnClickListener(this);

        ly_header = findViewById(R.id.ly_header);
        ly_audio = findViewById(R.id.ly_audio);
        ly_resolution = findViewById(R.id.ly_resolution);
        ly_subtitle = findViewById(R.id.ly_subtitle);

        ly_subtitle.setOnClickListener(this);
        ly_resolution.setOnClickListener(this);
        ly_audio.setOnClickListener(this);

        txt_rss = findViewById(R.id.txt_rss);
        txt_rss.setSingleLine(true);

        findViewById(R.id.ly_fav).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.ly_back).setOnClickListener(this);
        findViewById(R.id.ly_guide).setOnClickListener(this);
        findViewById(R.id.ly_tv_schedule).setOnClickListener(this);
        logo = findViewById(R.id.logo);
        Picasso.with(this).load(Constants.GetIcon(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.icon)
                .into(logo);

        logo.setVisibility(View.GONE);
        image_icon = findViewById(R.id.image_icon);

        Picasso.with(this).load(Constants.GetIcon(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.icon)
                .into(image_icon);
        ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
        params.height = MyApp.SURFACE_HEIGHT;
        params.width = MyApp.SURFACE_WIDTH;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mVideoHeight = displayMetrics.heightPixels;
        mVideoWidth = displayMetrics.widthPixels;

        Log.e("height", String.valueOf(MyApp.SCREEN_HEIGHT));
        if(MyApp.SCREEN_HEIGHT==720){
            setMargins(ly_surface,0,MyApp.top_margin- Utils.dp2px(this,5),MyApp.right_margin+Utils.dp2px(this,5),0);
        }else if(MyApp.SCREEN_HEIGHT==1440){
            params.height = (int) (MyApp.SURFACE_HEIGHT*0.9);
            params.width = (int) (MyApp.SURFACE_WIDTH*0.9);
            setMargins(ly_surface,0,MyApp.top_margin-Utils.dp2px(this,10),MyApp.right_margin,0);
        }else {
            setMargins(ly_surface,0,MyApp.top_margin,MyApp.right_margin,0);
        }
        frameLayout = findViewById(R.id.rootVideoPlayerMovieActivity);
        playerView = findViewById(R.id.player_view);
        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
        playerView.setOnClickListener(this);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        ly_surface.setOnTouchListener((view1, motionEvent) -> {
            showFullScreen();
            return false;
        });

        debugTextView = findViewById(R.id.debug_text_view);

//        mVideoWidth = (int) (MyApp.SCREEN_WIDTH*0.6*0.86);
//        mVideoHeight = (int) (MyApp.SCREEN_HEIGHT*0.6);
//        changeVideoViewSize(mVideoWidth,mVideoHeight);
        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }
        ly_surface.setLayoutParams(params);
        FullScreencall();
        channels = full_datas.get(channel_pos).getChannels();
        if(channels==null || channels.size()==0){
            Toast.makeText(this,"This category do not have channels", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter = new MainListAdapter(this,channels);
        channel_list.setAdapter(adapter);
        channel_list.requestFocus();
        mStream_id = channels.get(sub_pos).getStream_id();
//        new Thread(this::getEpg).start();
        showEpg(channels.get(sub_pos));
        playChannel();
        new Thread(this::getRespond).start();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.SDK_INT > 23) {
//            playChannel();
//        }
//    }

    private void showEpg(EPGChannel epgChannel) {
        int now_id = Constants.findNowEvent(epgChannel.getEvents());
        Log.e("printdata",epgChannel.getName()+" "+epgChannel.getEvents().size()+" "+now_id);
        epgModelList = new ArrayList<>();
        if (now_id!=-1){
            epgModelList.addAll(epgChannel.getEvents().subList(now_id,now_id+4));
        }
        printEpgData();
    }

    private void getRespond(){
        String url = "";
        switch (MyApp.firstServer){
            case first:
                url=Constants.GetUrl1(this);
                break;
            case second:
                url=Constants.GetUrl2(this);
                break;
            case third:
                url=Constants.GetUrl3(this);
                break;
        }
        try{
            String response = MyApp.instance.getIptvclient().login(url);
            Log.e("response",response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data_obj = object.getJSONObject("data");
                    String msg=data_obj.getString("message");
                    try {
                        msg_time = Integer.parseInt(data_obj.getString("message_time"));
                    }catch (Exception e){
                        msg_time = 20;
                    }
                    is_msg = !data_obj.getString("message_on_off").isEmpty() && data_obj.getString("message_on_off").equalsIgnoreCase("1");
                    if (msg.equals("")) msg=getString(R.string.app_name);
                    String finalMsg = msg;
                    runOnUiThread(()->{
                        String rss_feed = "                 "+ finalMsg +"                 ";
                        if(rss.equalsIgnoreCase(rss_feed)){
                            ly_header.setVisibility(View.GONE);
    //                            image_icon.setVisibility(View.GONE);
    //                            txt_rss.setVisibility(View.GONE);
                            is_rss = false;
                        }else {
                            rss =rss_feed;
                            is_rss = true;
                            ly_header.setVisibility(View.VISIBLE);
                        }

                        if(is_msg){
                            ly_header.setVisibility(View.VISIBLE);
                            txt_rss.setText(rss);
                            Animation bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top);
                            txt_rss.clearAnimation();
                            txt_rss.startAnimation(bottomToTop);
                        }else {
                            ly_header.setVisibility(View.GONE);
                        }
                        rssTimer();
                    });
                } else {
                    Toast.makeText(this, "Server Error!", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }catch (Exception e){

        }

    }
    int rss_time;
    private void rssTimer() {
        rss_time = msg_time;
        rssTicker = () -> {
            if (rss_time < 1) {
                txt_rss.setText("");
                txt_rss.setBackgroundResource(R.color.trans_parent);
                ly_header.setVisibility(View.GONE);
                logo.setVisibility(View.VISIBLE);
                return;
            }
            runRssTicker();
        };
        rssTicker.run();
    }

    private void runRssTicker() {
        rss_time --;
        long next = SystemClock.uptimeMillis() + 1000;
        rssHandler.postAtTime(rssTicker, next);
    }


//    private void getEpg(){
//        mHandler.removeCallbacks(mUpdateTimeTask);
//        try {
//            String map = MyApp.instance.getIptvclient().getShortEPG(MyApp.user,MyApp.pass,
//                    mStream_id,4);
//            Log.e(getClass().getSimpleName(),map);
//            map=map.replaceAll("[^\\x00-\\x7F]", "");
//            if (!map.contains("null_error_response")){
//                JSONObject jsonObject = new JSONObject(map);
//                JSONArray maps = (JSONArray) jsonObject.get("epg_listings");
//                epgModelList = new ArrayList<>();
//                if(maps!=null && maps.length()>0){
//                    for(int i = 0;i<maps.length();i++){
//                        try {
//                            JSONObject e_p = (JSONObject) maps.get(i);
//                            EpgModel epgModel = new EpgModel();
//                            epgModel.setId((String)e_p.get("id"));
//                            epgModel.setCh_id((String)e_p.get("channel_id"));
//                            epgModel.setCategory((String)e_p.get("epg_id"));
//                            epgModel.setT_time((String)e_p.get("start"));
//                            epgModel.setT_time_to((String)e_p.get("end"));
//                            byte[] desc_byte = Base64.decode((String)e_p.get("description"), Base64.DEFAULT);
//                            String desc = new String(desc_byte);
//                            epgModel.setDescr(desc);
//                            byte[] title_byte = Base64.decode((String)e_p.get("title"), Base64.DEFAULT);
//                            String title = new String(title_byte);
//                            epgModel.setName(title);
//                            epgModel.setStart_timestamp(e_p.get("start_timestamp").toString());
//                            epgModel.setStop_timestamp(e_p.get("stop_timestamp").toString());
//                            int duration = ((Integer.parseInt(e_p.get("stop_timestamp").toString())) - (Integer.parseInt(e_p.get("start_timestamp").toString())));
//                            epgModel.setDuration(duration);
//                            if(e_p.has("has_archive")) {
//                                Double d = Double.parseDouble(e_p.get("has_archive").toString());
//                                epgModel.setMark_archive(d.intValue());
//                            }
//                            epgModelList.add(epgModel);
//                        }catch (Exception e){
//                            Log.e("error","epg_parse_error");
//                        }
//                    }
//                }
//                runOnUiThread(()->{
//                    if(is_full){
//                        MyApp.is_first = true;
//                        mHandler.removeCallbacks(mUpdateTimeTask);
//                        updateProgressBar();
//                        ly_bottom.setVisibility(View.VISIBLE);
//                        if(channels.get(sub_pos).getStream_icon()!=null && !channels.get(sub_pos).getStream_icon().isEmpty()){
//                            Picasso.with(this).load(channels.get(sub_pos).getStream_icon())
//                                    .into(channel_logo);
//                            channel_logo.setVisibility(View.VISIBLE);
//                        }else {
//                            channel_logo.setVisibility(View.GONE);
//                        }
//                        listTimer();
//                    }
//                    printEpgData();
//                });
//            }
//        }catch (Exception e){
//
//        }
//    }
//    int epg_time;
//    int i = 0;
    private void EpgTimer(){
//        epg_time = 1;
        mEpgTicker = () -> {
            mEpgHandler.removeCallbacks(mEpgTicker);
            showEpg(channels.get(sub_pos));
//            if (epg_time < 1) {
//                i++;
//                Log.e("count", String.valueOf(i));
////                new Thread(this::getEpg).start();
//                return;
//            }
            runNextEpgTicker();
        };
        mEpgTicker.run();
    }

    private void runNextEpgTicker() {
//        epg_time--;
        long next = SystemClock.uptimeMillis() + 60000;
        mEpgHandler.postAtTime(mEpgTicker, next);
    }

    private void showFullScreen(){
        if(!is_full && txt_progress.getVisibility()== View.GONE){
            is_full = true;
            ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
            params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(getApplicationContext(),50);
            params.width = MyApp.SCREEN_WIDTH+Utils.dp2px(getApplicationContext(),50);
            ly_surface.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
            setMargins(ly_surface,Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
            ly_surface.setLayoutParams(params);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    updateProgressBar();
                    ly_bottom.setVisibility(View.VISIBLE);
                    listTimer();
                }
            }, 2000);
        }else if(is_full){
            if(ly_bottom.getVisibility()== View.GONE){
                if(pro>99){
                    mEpgHandler.removeCallbacks(mEpgTicker);
                    EpgTimer();
                }
                ly_bottom.setVisibility(View.VISIBLE);
            }else {
                ly_bottom.setVisibility(View.GONE);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_surface:
                showFullScreen();
                break;
            case R.id.ly_back:
                if(!is_full){
                    MyApp.key = false;
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    releaseMediaPlayer();
                    finish();
                }
                break;
            case R.id.ly_guide:
                if(!is_full && txt_progress.getVisibility()== View.GONE){
                    if(channels==null || channels.size()==0){
                        return;
                    }
                    channel_list.setFocusable(true);
                    MyApp.key = false;
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    releaseMediaPlayer();
                    String channel_name = channels.get(sub_pos).getName();
                    mStream_id = channels.get(sub_pos).getStream_id();
                    Intent intent = new Intent(this,GuideActivity.class);
//                    intent.putExtra("channel",channels.get(sub_pos));
                    MyApp.epgChannel = channels.get(sub_pos);
                    startActivity(intent);
                }
                break;
            case R.id.ly_audio:
                showAudioOptions();
                break;

            case R.id.ly_subtitle:
                showSubtitlesOptions();
                break;
            case R.id.ly_resolution:

                break;
            case R.id.ly_fav:
                if (full_datas.get(channel_pos).getChannels().get(sub_pos).is_favorite()) {
                    pkg_datas.set(0, "Add to Fav");
                    image_star.setVisibility(View.GONE);
                    full_datas.get(channel_pos).getChannels().get(sub_pos).setIs_favorite(false);
                    boolean is_exist = false;
                    int pp = 0;
                    for (int i = 0; i < Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().size(); i++) {
                        if (Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().get(i).getName().equals(full_datas.get(channel_pos).getChannels().get(sub_pos).getName())) {
                            is_exist = true;
                            pp = i;
                        }
                    }
                    if (is_exist)
                        Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().remove(pp);
                    MyApp.instance.getPreference().put(Constants.getFavInfo(), Constants.getListStrFromListEpg(Constants.getFavFullModel(MyApp.fullModels_filter).getChannels()));
                } else {
                    image_star.setVisibility(View.VISIBLE);
                    full_datas.get(channel_pos).getChannels().get(sub_pos).setIs_favorite(true);
                    Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().add(full_datas.get(channel_pos).getChannels().get(sub_pos));
                    MyApp.instance.getPreference().put(Constants.getFavInfo(), Constants.getListStrFromListEpg(Constants.getFavFullModel(MyApp.fullModels_filter).getChannels()));
                    pkg_datas.set(0, "Remove from Fav");
                }
                channels = full_datas.get(channel_pos).getChannels();
                adapter = new MainListAdapter(PreviewChannelExoActivity.this,channels);
                channel_list.setAdapter(adapter);
                channel_list.setSelection(sub_pos);
                MyApp.is_first = true;
                adapter.selectItem(sub_pos);
                listTimer();
                break;
            case R.id.btn_search:
                SearchDlg searchDlg = new SearchDlg(PreviewChannelExoActivity.this, (dialog, sel_Channel) -> {
                    dialog.dismiss();
                    FullScreencall();
                    for(int i = 0;i<channels.size();i++){
                        if(channels.get(i).getName().equalsIgnoreCase(sel_Channel.getName())){
                            sub_pos = i;
                            break;
                        }
                    }
                    scrollToLast(channel_list, sub_pos);
                    epg_pos = sub_pos;
                    preview_pos = sub_pos;
                    adapter.selectItem(sub_pos);
                    MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                    mStream_id = channels.get(sub_pos).getStream_id();
                    mEpgHandler.removeCallbacks(mEpgTicker);
                    EpgTimer();
                    playChannel();
                });
                searchDlg.show();
                break;
            case R.id.ly_tv_schedule:
                startActivity(new Intent(this, WebViewActivity.class));
                break;
        }
    }

    private void scrollToLast(final ListView listView, final int position) {
        listView.post(() -> listView.setSelection(position));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(channels.get(preview_pos).getStream_id().equalsIgnoreCase(channels.get(position).getStream_id())){
            ly_surface.setVisibility(View.VISIBLE);
            is_full = true;
            ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
            params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(getApplicationContext(),50);
            params.width = MyApp.SCREEN_WIDTH+Utils.dp2px(getApplicationContext(),50);
            ly_surface.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
            setMargins(ly_surface,Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
            ly_surface.setLayoutParams(params);
            mHandler.removeCallbacks(mUpdateTimeTask);
            updateProgressBar();
            ly_bottom.setVisibility(View.VISIBLE);
            listTimer();
        }else {
            MyApp.is_first = true;
            sub_pos = position;
            epg_pos = sub_pos;
            preview_pos = sub_pos;
            adapter.selectItem(sub_pos);
            MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
            mStream_id = channels.get(sub_pos).getStream_id();
            mEpgHandler.removeCallbacks(mEpgTicker);
            EpgTimer();
            playChannel();
//            rssHandler.removeCallbacks(rssTicker);
//            new Thread(this::getRespond).start();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (playerView != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm a EEE MM/dd");
                long wrongMedialaanTime = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
                long totalDuration = System.currentTimeMillis()+wrongMedialaanTime;
                if(epgModelList!=null && epgModelList.size()>0){
                    try {
                        long millis =epgModelList.get(0).getStartTime().getTime()+wrongMedialaanTime+Constants.SEVER_OFFSET;
                        long mills_to = epgModelList.get(0).getEndTime().getTime()+wrongMedialaanTime+Constants.SEVER_OFFSET;
                        if(totalDuration>millis){
                            txt_title.setText(epgModelList.get(0).getTitle());
                            txt_dec.setText(epgModelList.get(0).getDec());
                            try {
                                txt_channel.setText(channels.get(sub_pos).getNum() + " " + channels.get(sub_pos).getName());
                            }catch (Exception e1){
                                txt_channel.setText("    ");
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        txt_channel.setText(channels.get(sub_pos).getNum() + " " + channels.get(sub_pos).getName());
                                    }
                                }, 5000);
                            }
                            txt_date.setText(dateFormat.format(new Date()));
                            int pass_min = (int) ((totalDuration - millis)/(1000*60));
                            int remain_min = (int)(mills_to-totalDuration)/(1000*60);
                            int progress = (int) pass_min*100/(pass_min+remain_min);
                            pro  = progress;
                            seekbar.setProgress(progress);
                            txt_time_passed.setText("Started " + pass_min +" mins ago");
                            txt_remain_time.setText("+"+remain_min+" min");
                            txt_last_time.setText(sdf.format(new Date(mills_to-wrongMedialaanTime)));
                            txt_current_dec.setText(epgModelList.get(Constants.findNowEvent(epgModelList)).getTitle());
                            txt_next_dec.setText(epgModelList.get(Constants.findNowEvent(epgModelList)+1).getTitle());
                            if(channels.get(sub_pos).is_favorite()){
                                image_star.setVisibility(View.VISIBLE);
                            }else {
                                image_star.setVisibility(View.GONE);
                            }
                            if(channels.get(sub_pos).getTv_archive().equalsIgnoreCase("1")){
                                image_clock.setVisibility(View.VISIBLE);
                            }else {
                                image_clock.setVisibility(View.GONE);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    txt_title.setText("No Information");
                    txt_dec.setText("No Information");
                    try {
                        txt_channel.setText(channels.get(sub_pos).getNum() + " " + channels.get(sub_pos).getName());
                    }catch (Exception e2){
                        txt_channel.setText("    ");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txt_channel.setText(channels.get(sub_pos).getNum() + " " + channels.get(sub_pos).getName());
                            }
                        }, 5000);
                    }
                    txt_date.setText(dateFormat.format(new Date()));
                    txt_time_passed.setText("      mins ago");
                    txt_remain_time.setText("      min");
                    txt_last_time.setText("         ");
                    seekbar.setProgress(0);
                    txt_current_dec.setText("No Information");
                    txt_next_dec.setText("No Information");
                }
            }
            mHandler.postDelayed(this, 500);
        }
    };


    @Override
    public void onSwipe(int direction) {

    }

    @Override
    public void onDoubleTap() {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    private void printEpgData(){
        Log.e("printdata","true "+epgModelList.size());
        if(txt_progress.getVisibility()== View.GONE){
            if(epgModelList.size()>0) {
                firstTime.setText(Constants.Offset(true,epgModelList.get(0).getStartTime()));
                firstTitle.setText(epgModelList.get(0).getTitle());
                if(epgModelList.size()>1){
                    secondTime.setText(Constants.Offset(true,epgModelList.get(1).getStartTime()));
                    secondTitle.setText(epgModelList.get(1).getTitle());
                }

                if(epgModelList.size()>2){
                    thirdTime.setText(Constants.Offset(true,epgModelList.get(2).getStartTime()));
                    thirdTitle.setText(epgModelList.get(2).getTitle());
                }

                if(epgModelList.size()>3){
                    fourthTime.setText(Constants.Offset(true,epgModelList.get(3).getStartTime()));
                    fourthTitle.setText(epgModelList.get(3).getTitle());
                }
            }else {
                firstTime.setText("");
                firstTitle.setText("");
                secondTime.setText("");
                secondTitle.setText("");
                thirdTime.setText("");
                thirdTitle.setText("");
                fourthTime.setText("");
                fourthTitle.setText("");
            }
        }
    }

    int maxTime;
    private void listTimer() {
        maxTime = osd_time;
        mTicker = new Runnable() {
            public void run() {
                if (maxTime < 1) {
                    ly_bottom.setVisibility(View.GONE);
                    return;
                }
                runNextTicker();
            }
        };
        mTicker.run();
    }
    private void runNextTicker() {
        maxTime--;
        long next = SystemClock.uptimeMillis() + 1000;
        removeHamdler.postAtTime(mTicker, next);
    }

    @Override
    public void preparePlayback() {

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    txt_time.setText(time.format(new Date()));
                } catch (Exception e) {
                }
            }
        });
    }
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void FullScreencall() {
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            Toast.makeText(this,""+event.getKeyCode(),Toast.LENGTH_SHORT).show();
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_MENU:
                    if (full_datas.get(channel_pos).getChannels().get(sub_pos).is_favorite()) {
                        pkg_datas.set(0,"Remove from Fav");
                    }else {
                        pkg_datas.set(0,"Add to Fav");
                    }
                    PackageDlg packageDlg = new PackageDlg(PreviewChannelExoActivity.this, pkg_datas, (dialog, position) -> {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                if (full_datas.get(channel_pos).getChannels().get(sub_pos).is_favorite()) {
                                    pkg_datas.set(0, "Add to Fav");
                                    image_star.setVisibility(View.GONE);
                                    full_datas.get(channel_pos).getChannels().get(sub_pos).setIs_favorite(false);
                                    boolean is_exist = false;
                                    int pp = 0;
                                    for (int i = 0; i < Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().size(); i++) {
                                        if (Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().get(i).getName().equals(full_datas.get(channel_pos).getChannels().get(sub_pos).getName())) {
                                            is_exist = true;
                                            pp = i;
                                        }
                                    }
                                    if (is_exist)
                                        Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().remove(pp);
                                    MyApp.instance.getPreference().put(Constants.getFavInfo(), Constants.getListStrFromListEpg(Constants.getFavFullModel(MyApp.fullModels_filter).getChannels()));
                                } else {
                                    image_star.setVisibility(View.VISIBLE);
                                    full_datas.get(channel_pos).getChannels().get(sub_pos).setIs_favorite(true);
                                    Constants.getFavFullModel(MyApp.fullModels_filter).getChannels().add(full_datas.get(channel_pos).getChannels().get(sub_pos));
                                    MyApp.instance.getPreference().put(Constants.getFavInfo(), Constants.getListStrFromListEpg(Constants.getFavFullModel(MyApp.fullModels_filter).getChannels()));
                                    pkg_datas.set(0, "Remove from Fav");
                                }
                                listTimer();
                                break;
                            case 1:
                                SearchDlg searchDlg = new SearchDlg(PreviewChannelExoActivity.this, (dialog1, sel_Channel) -> {
                                    dialog1.dismiss();
                                    FullScreencall();
                                    for(int i = 0;i<channels.size();i++){
                                        if(channels.get(i).getName().equalsIgnoreCase(sel_Channel.getName())){
                                            sub_pos = i;
                                            break;
                                        }
                                    }
                                    scrollToLast(channel_list, sub_pos);
                                    epg_pos = sub_pos;
                                    preview_pos = sub_pos;
                                    adapter.selectItem(sub_pos);
                                    MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                                    mStream_id = channels.get(sub_pos).getStream_id();
                                    mEpgHandler.removeCallbacks(mEpgTicker);
                                    EpgTimer();
                                    playChannel();
                                });
                                searchDlg.show();
                                break;
                            case 2:
                                showSubtitlesOptions();
                                break;
                            case 3:
                                showAudioOptions();
                                break;
                            case 4:

                                break;
                            case 5:
                                startActivity(new Intent(PreviewChannelExoActivity.this,WebViewActivity.class));
                                break;
                        }
                    });
                    packageDlg.show();
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if(ly_bottom.getVisibility()== View.VISIBLE){
                        ly_bottom.setVisibility(View.GONE);
                        return true;
                    }
                    if(is_full){
                        is_full = false;
                        ly_surface.setVisibility(View.VISIBLE);
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
                        params.height = MyApp.SURFACE_HEIGHT;
                        params.width = MyApp.SURFACE_WIDTH;
                        if(MyApp.SCREEN_HEIGHT==720){
                            setMargins(ly_surface,0,MyApp.top_margin-Utils.dp2px(this,5),MyApp.right_margin+Utils.dp2px(this,5),0);
                        }else if(MyApp.SCREEN_HEIGHT==1440){
                            params.height = (int) (MyApp.SURFACE_HEIGHT*0.9);
                            params.width = (int) (MyApp.SURFACE_WIDTH*0.9);
                            setMargins(ly_surface,0,MyApp.top_margin-Utils.dp2px(this,10),MyApp.right_margin,0);
                        }else {
                            setMargins(ly_surface,0,MyApp.top_margin,MyApp.right_margin,0);
                        }
//                        ly_surface.setPadding(15,15,15,15);
                        ly_surface.setLayoutParams(params);
                        ly_bottom.setVisibility(View.GONE);
                        return true;
                    }
                    releaseMediaPlayer();
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(!is_full){
                        if(channels == null || channels.size()==0){
                            return true;
                        }
                        channel_list.setFocusable(true);
                        MyApp.key = false;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        releaseMediaPlayer();
//                        Log.e("sub",String .valueOf(sub_pos));
                        String channel_name = channels.get(sub_pos).getName();
                        mStream_id = channels.get(sub_pos).getStream_id();
                        Intent intent = new Intent(this,GuideActivity.class);
//                        intent.putExtra("channel",channels.get(sub_pos));
                        MyApp.epgChannel = channels.get(sub_pos);
                        startActivity(intent);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(!is_full){
                        MyApp.key = false;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        releaseMediaPlayer();
                        finish();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
//                    Log.e("sub",String .valueOf(sub_pos));
                    removeHamdler.removeCallbacks(mTicker);
                    if(is_full){
                        is_up = true;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()== View.GONE){
                             if(sub_pos>0){
                                sub_pos--;
                                 MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                                mStream_id = channels.get(sub_pos).getStream_id();
                                 mEpgHandler.removeCallbacks(mEpgTicker);
                                 EpgTimer();
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                MyApp.is_first = true;
                                channel_list.setSelection(sub_pos);
                                adapter.selectItem(sub_pos);
                                playChannel();
//                                 rssHandler.removeCallbacks(rssTicker);
//                                 new Thread(this::getRespond).start();
                                 return true;
                              }else {
                                 sub_pos = channels.size()-1;
                                 MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                                 mStream_id = channels.get(sub_pos).getStream_id();
                                 mEpgHandler.removeCallbacks(mEpgTicker);
                                 EpgTimer();
                                 mHandler.removeCallbacks(mUpdateTimeTask);
                                 MyApp.is_first = true;
                                 channel_list.setSelection(sub_pos);
                                 adapter.selectItem(sub_pos);
                                 playChannel();
//                                 rssHandler.removeCallbacks(rssTicker);
//                                 new Thread(this::getRespond).start();
                                 return true;
                             }
                        }
                        return true;
                    }
//                    MyApp.key = false;
                    if(view ==channel_list){
                        is_up = false;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()== View.GONE){
                            if(sub_pos<channels.size()-1){
                                sub_pos++;
                                MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                                mStream_id = channels.get(sub_pos).getStream_id();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                            }
//                            channel_list.setSelection(sub_pos);
//                            adapter.selectItem(sub_pos);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    removeHamdler.removeCallbacks(mTicker);
                    if(is_full){
                        is_up = false;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()== View.GONE){
                            if(sub_pos<channels.size()-1){
                                sub_pos++;
                                MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                                mStream_id = channels.get(sub_pos).getStream_id();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                MyApp.is_first = true;
                                channel_list.setSelection(sub_pos);
                                adapter.selectItem(sub_pos);
                                playChannel();
//                                rssHandler.removeCallbacks(rssTicker);
//                                new Thread(this::getRespond).start();
                                return true;
                            }else {
                                sub_pos = 0;
                                MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                                mStream_id = channels.get(sub_pos).getStream_id();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                MyApp.is_first = true;
                                channel_list.setSelection(sub_pos);
                                adapter.selectItem(sub_pos);
                                playChannel();
//                                rssHandler.removeCallbacks(rssTicker);
//                                new Thread(this::getRespond).start();
                                return true;
                            }
                        }
                        return true;
                    }
                    if(view == channel_list){
                        is_up = true;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()== View.GONE){
                            if(sub_pos>0){
                                sub_pos--;
                                MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                                mStream_id = channels.get(sub_pos).getStream_id();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                            }
//                            channel_list.setSelection(sub_pos);
//                            adapter.selectItem(sub_pos);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(is_full){
                        if(ly_bottom.getVisibility()== View.VISIBLE){
                            ly_bottom.setVisibility(View.GONE);
                            if(pro>99){
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                            }
                        }else {
                            ly_bottom.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_0:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    if (!key.isEmpty()) {
                        key += "0";
                        move_pos = parseInt(key);
                        if (move_pos > MyApp.channel_size) {
                            num_txt.setText("");
                            key = "";
                            move_pos = 0;
                            moveHandler.removeCallbacks(moveTicker);
                        } else {
                            moveHandler.removeCallbacks(moveTicker);
                            num_txt.setText(key);
                            moveTimer();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_1:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "1";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();

                    }
                    break;
                case KeyEvent.KEYCODE_2:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "2";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_3:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "3";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_4:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "4";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_5:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "5";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_6:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "6";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_7:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "7";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_8:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "8";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_9:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "9";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    int moveTime;
    private void moveTimer() {
        moveTime = 2;
        moveTicker = () -> {
            if(moveTime==1){
                if(is_full){
                    for(int i = 0;i<channels.size();i++){
                        if (parseInt(channels.get(i).getNum()) == move_pos) {
                            sel_model = channels.get(i);
                            sub_pos = i;
                            MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                            break;
                        }
                    }
                    if (sel_model == null) {
                        MyApp.key = false;
                        key = "";
                        num_txt.setText("");
                        num_txt.setVisibility(View.GONE);
                        Toast.makeText(PreviewChannelExoActivity.this,"This category do not have this channel", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        key = "";
                        num_txt.setText("");
                        num_txt.setVisibility(View.GONE);
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        mStream_id = sel_model.getStream_id();
                        MyApp.is_first = true;
                        channel_list.setSelection(sub_pos);
                        adapter.selectItem(sub_pos);
//                        new Thread(()->getEpg()).start();
                        showEpg(channels.get(sub_pos));
                        playChannel();
                        listTimer();
                    }
                }else {
                    for(int i = 0;i<channels.size();i++){
                        if (parseInt(channels.get(i).getNum()) == move_pos) {
                            sel_model = channels.get(i);
                            sub_pos = i;
                            MyApp.instance.getPreference().put(Constants.getSubPos(),sub_pos);
                            break;
                        }
                    }
                    if (sel_model == null) {
                        MyApp.key = false;
                        key = "";
                        num_txt.setText("");
                        num_txt.setVisibility(View.GONE);
                        Toast.makeText(PreviewChannelExoActivity.this,"This category do not have this channel", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        key = "";
                        num_txt.setText("");
                        num_txt.setVisibility(View.GONE);
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        mStream_id = sel_model.getStream_id();
                        MyApp.is_first = true;
                        channel_list.setSelection(sub_pos);
                        adapter.selectItem(sub_pos);
//                        new Thread(()->getEpg()).start();
                        showEpg(channels.get(sub_pos));
                        playChannel();
                        listTimer();
                    }
                }
                return;
            }
            moveNextTicker();
        };
        moveTicker.run();
    }
    private void moveNextTicker() {
        moveTime--;
        long next = SystemClock.uptimeMillis() + 1000;
        moveHandler.postAtTime(moveTicker, next);
    }

    private void playChannel(){
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mVideoHeight = displayMetrics.heightPixels;
        mVideoWidth = displayMetrics.widthPixels;
        mStream_id = channels.get(sub_pos).getStream_id();

        EPGChannel showmodel = channels.get(sub_pos);
        checkAddedRecent(showmodel);
        Constants.getRecentFullModel(MyApp.fullModels_filter).getChannels().add(0,showmodel);
        //get recent series names list
        List<String> recent_series_names = new ArrayList<>();
        for (EPGChannel channel:Constants.getRecentFullModel(MyApp.fullModels_filter).getChannels()){
            recent_series_names.add(channel.getName());
        }

        //set
        MyApp.instance.getPreference().put(Constants.getRecentChannels(), recent_series_names);
        contentUri = MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user, MyApp.pass,
                mStream_id,"ts");
        if(channels.get(sub_pos).getStream_icon()!=null && !channels.get(sub_pos).getStream_icon().isEmpty()){
            Picasso.with(PreviewChannelExoActivity.this).load(channels.get(sub_pos).getStream_icon())
                    .into(channel_logo);
            channel_logo.setVisibility(View.VISIBLE);
        }else {
            channel_logo.setVisibility(View.GONE);
        }
        if(channels.get(sub_pos).is_locked() && (channel_pos==1 || channel_pos==0)){
            PinDlg pinDlg = new PinDlg(this, new PinDlg.DlgPinListener() {
                @Override
                public void OnYesClick(Dialog dialog, String pin_code) {
                    dialog.dismiss();
                    String pin = (String)MyApp.instance.getPreference().get(Constants.PIN_CODE);
                    if(pin_code.equalsIgnoreCase(pin)){
                        dialog.dismiss();
                        playVideo();
                    }else {
                        dialog.dismiss();
                        Toast.makeText(PreviewChannelExoActivity.this, "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void OnCancelClick(Dialog dialog, String pin_code) {
                    dialog.dismiss();
                }
            });
            pinDlg.show();
        }else {
            playVideo();
        }

        channel_list.requestFocus();
    }

    private void checkAddedRecent(EPGChannel showModel) {
        Iterator<EPGChannel> iter =  Constants.getRecentFullModel(MyApp.fullModels_filter).getChannels().iterator();
        while(iter.hasNext()){
            EPGChannel movieModel = iter.next();
            if (movieModel.getName().equals(showModel.getName()))
                iter.remove();
        }
    }

    private void playVideo() {
        toggleFullscreen(true);
        Log.e("url",contentUri);
        releaseMediaPlayer();
        if(def_lay.getVisibility()== View.VISIBLE)def_lay.setVisibility(View.GONE);
        try {
            if (player == null) {
                Intent intent = getIntent();

                Uri[] uris = new Uri[1];
                String[] extensions = new String[1];

                uris[0] = Uri.parse(contentUri);

                DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
                if (intent.hasExtra(DRM_SCHEME_EXTRA) || intent.hasExtra(DRM_SCHEME_UUID_EXTRA)) {
                    String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL_EXTRA);
                    String[] keyRequestPropertiesArray =
                            intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA);
                    boolean multiSession = intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA, false);
                    int errorStringId = R.string.error_drm_unknown;
                    if (Util.SDK_INT < 18) {
                        errorStringId = R.string.error_drm_not_supported;
                    } else {
                        try {
                            String drmSchemeExtra = intent.hasExtra(DRM_SCHEME_EXTRA) ? DRM_SCHEME_EXTRA
                                    : DRM_SCHEME_UUID_EXTRA;
                            UUID drmSchemeUuid = Util.getDrmUuid(intent.getStringExtra(drmSchemeExtra));
                            if (drmSchemeUuid == null) {
                                errorStringId = R.string.error_drm_unsupported_scheme;
                            } else {
                                drmSessionManager =
                                        buildDrmSessionManagerV18(
                                                drmSchemeUuid, drmLicenseUrl, keyRequestPropertiesArray, multiSession);
                            }
                        } catch (UnsupportedDrmException e) {
                            errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                                    ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
                        }
                    }
                    if (drmSessionManager == null) {
                        showToast(errorStringId);
                        releaseMediaPlayer();
                        return;
                    }
                }

                TrackSelection.Factory trackSelectionFactory;
                String abrAlgorithm = intent.getStringExtra(ABR_ALGORITHM_DEFAULT);
                if (abrAlgorithm == null || ABR_ALGORITHM_DEFAULT.equals(abrAlgorithm)) {
                    trackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
                } else if (ABR_ALGORITHM_RANDOM.equals(abrAlgorithm)) {
                    trackSelectionFactory = new RandomTrackSelection.Factory();
                } else {
                    showToast(R.string.error_unrecognized_abr_algorithm);
                    releaseMediaPlayer();
                    return;
                }

                boolean preferExtensionDecoders =
                        intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false);
                @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                        MyApp.instance.useExtensionRenderers()
                                ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                                : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
                DefaultRenderersFactory renderersFactory =
                        new DefaultRenderersFactory(this, extensionRendererMode);

                trackSelector = new DefaultTrackSelector(trackSelectionFactory);
                trackSelector.setParameters(trackSelectorParameters);
                lastSeenTrackGroupArray = null;

                player = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector, drmSessionManager);
                player.addListener(new PlayerEventListener());
                player.setPlayWhenReady(startAutoPlay);
                player.addAnalyticsListener(new EventLogger(trackSelector));
                playerView.setPlayer(player);
                playerView.setPlaybackPreparer(this);

                debugViewHelper = new DebugTextViewHelper(player, debugTextView);
                debugViewHelper.start();

                MediaSource[] mediaSources = new MediaSource[uris.length];
                for (int i = 0; i < uris.length; i++) {
                    mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
                }
                mediaSource =
                        mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
                String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);
                if (adTagUriString != null) {
                    Uri adTagUri = Uri.parse(adTagUriString);
                    if (!adTagUri.equals(loadedAdTagUri)) {
                        releaseAdsLoader();
                        loadedAdTagUri = adTagUri;
                    }
                    MediaSource adsMediaSource = createAdsMediaSource(mediaSource, Uri.parse(adTagUriString));
                    if (adsMediaSource != null) {
                        mediaSource = adsMediaSource;
                    } else {
                        showToast(R.string.ima_not_loaded);
                    }
                } else {
                    releaseAdsLoader();
                }
            }
            boolean haveStartPosition = startWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                player.seekTo(startWindow, startPosition);
            }
            player.prepare(mediaSource, !haveStartPosition, false);

        } catch (Exception e) {
            Toast.makeText(this, "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }
    }
    @Override
    protected void onUserLeaveHint() {
        releaseMediaPlayer();
        super.onUserLeaveHint();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!is_create) {
//            if (Util.SDK_INT <= 23 || player == null) {
//                playChannel();
//            }
            releaseMediaPlayer();
            playChannel();
            channel_list.requestFocus();
        } else {
            is_create = false;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releaseMediaPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releaseMediaPlayer();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAdsLoader();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void toggleFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }
    private void releaseMediaPlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            debugViewHelper.stop();
            debugViewHelper = null;
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
        releaseMediaDrm();
        mVideoWidth = 0;
        mVideoHeight = 0;
    }


    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return MyApp.instance.buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.decoderName == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.decoderName);
                    }
                }
            }
            return Pair.create(0, errorString);
        }
    }

    private void changeVideoViewSize(int witdth,int height){
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            ViewGroup.LayoutParams paramsPlayerView=frameLayout.getLayoutParams();
            paramsPlayerView.height = height;
            paramsPlayerView.width = witdth;
            frameLayout.setLayoutParams(paramsPlayerView);
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private void releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader.release();
            adsLoader = null;
            loadedAdTagUri = null;
            playerView.getOverlayFrameLayout().removeAllViews();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            releaseMediaPlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);

        super.onSaveInstanceState(outState);
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void releaseMediaDrm() {
        if (mediaDrm != null) {
            mediaDrm.release();
            mediaDrm = null;
        }
    }

    private List<?> getOfflineStreamKeys(Uri uri) {
        return MyApp.instance.getDownloadTracker().getOfflineStreamKeys(uri);
    }

    private DefaultDrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(
            UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray, boolean multiSession)
            throws UnsupportedDrmException {
        HttpDataSource.Factory licenseDataSourceFactory =
                MyApp.instance.buildHttpDataSourceFactory(/* listener= */ null);
        HttpMediaDrmCallback drmCallback =
                new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        releaseMediaDrm();
        mediaDrm = FrameworkMediaDrm.newInstance(uuid);
        return new DefaultDrmSessionManager<>(uuid, mediaDrm, drmCallback, null, multiSession);
    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        private  final String LOG_TAG =  PlayerEventListener.class.getSimpleName();

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            super.onPlaybackParametersChanged(playbackParameters);
            Log.d(LOG_TAG,"onPlaybackParametersChanged");
        }

        @Override
        public void onSeekProcessed() {
            super.onSeekProcessed();
            Log.d(LOG_TAG,"onSeekProcessed");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                showControls();
            }
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (player.getPlaybackError() != null) {
                updateStartPosition();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                playVideo();
            } else {
                updateStartPosition();
                showControls();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            if (trackGroups != lastSeenTrackGroupArray) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_video);
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_audio);
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }

    private void showControls() {
        if (is_full)
            ly_bottom.setVisibility(View.VISIBLE);
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }
    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .setManifestParser(
                                new FilteringManifestParser<>(
                                        new DashManifestParser(), (List<StreamKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .setManifestParser(
                                new FilteringManifestParser<>(
                                        new SsManifestParser(), (List<StreamKey>) getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .setPlaylistParserFactory(new HlsPlaylistParserFactory() {
                            @Override
                            public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser() {
                                return null;
                            }

                            @Override
                            public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser(HlsMasterPlaylist masterPlaylist) {
                                return null;
                            }
                        })
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private @Nullable
    MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) {
        try {
            Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
            if (adsLoader == null) {
                Constructor<? extends AdsLoader> loaderConstructor =
                        loaderClass
                                .asSubclass(AdsLoader.class)
                                .getConstructor(Context.class, Uri.class);
                adsLoader = loaderConstructor.newInstance(this, adTagUri);
                adUiViewGroup = new FrameLayout(this);
                playerView.getOverlayFrameLayout().addView(adUiViewGroup);
            }
            AdsMediaSource.MediaSourceFactory adMediaSourceFactory =
                    new AdsMediaSource.MediaSourceFactory() {
                        @Override
                        public MediaSource createMediaSource(Uri uri) {
                            return buildMediaSource(uri);
                        }

                        @Override
                        public int[] getSupportedTypes() {
                            return new int[] {C.TYPE_DASH, C.TYPE_SS, C.TYPE_HLS, C.TYPE_OTHER};
                        }
                    };
            return new AdsMediaSource(mediaSource, adMediaSourceFactory, adsLoader, (AdsLoader.AdViewProvider) adUiViewGroup);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showAudioOptions(){
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            CharSequence title = "Audio";
            int rendererIndex = 1;
            /*int rendererType = mappedTrackInfo.getRendererType(rendererIndex);
            boolean allowAdaptiveSelections =
                    rendererType == C.TRACK_TYPE_VIDEO
                            || (rendererType == C.TRACK_TYPE_AUDIO
                            && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);*/
            Pair<AlertDialog, TrackSelectionView> dialogPair =
                    TrackSelectionView.getDialog(this, title, trackSelector, rendererIndex);
            dialogPair.second.setShowDisableOption(false);
            dialogPair.second.setAllowAdaptiveSelections(false);
            dialogPair.first.show();
        }
    }

    private void showSubtitlesOptions(){
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            CharSequence title = "Subtitles";
            int rendererIndex = 2;
            /*int rendererType = mappedTrackInfo.getRendererType(rendererIndex);
            boolean allowAdaptiveSelections =
                    rendererType == C.TRACK_TYPE_VIDEO
                            || (rendererType == C.TRACK_TYPE_AUDIO
                            && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);*/
            Pair<AlertDialog, TrackSelectionView> dialogPair =
                    TrackSelectionView.getDialog(this, title, trackSelector, rendererIndex);
            dialogPair.second.setShowDisableOption(true);
            dialogPair.second.setAllowAdaptiveSelections(false);
            dialogPair.second.removeViewAt(4);
            dialogPair.first.show();
        }
    }



}
