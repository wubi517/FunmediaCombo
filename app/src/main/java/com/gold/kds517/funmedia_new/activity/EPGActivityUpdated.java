package com.gold.kds517.funmedia_new.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.apps.MyApp;
import com.gold.kds517.funmedia_new.listner.DoubleClickListener;
import com.gold.kds517.funmedia_new.models.Childsetget;
import com.gold.kds517.funmedia_new.models.Parentsetget;
import com.gold.kds517.funmedia_new.models.SetgetCategory;
import com.gold.kds517.funmedia_new.utils.Utils;
import com.objectsoltechnologies.xtreamcodeslib.XtreamCodeData.XtreamCodeAPICall;
import com.objectsoltechnologies.xtreamcodeslib.XtreamCodeData.XtreamCodeListner;
import com.objectsoltechnologies.xtreamcodeslib.XtreamCodeData.XtreamCodeListnerSeries;

import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by krishanu on 11/12/17.
 */
public class EPGActivityUpdated extends AppCompatActivity implements XtreamCodeListner, XtreamCodeListnerSeries,
        MediaPlayer.EventListener, IVLCVout.Callback,SurfaceHolder.Callback {

    SurfaceView player_homescreen;
    private SurfaceHolder holder;
    TextView time_1st_half_homescreen, time_2nd_half_homescreen, time_3rd_half_homescreen, time_4th_half_homescreen;
    TextView time_layout_homescreen, date_layout_homescreen;
    LinearLayout linear_epg_list_homescreen, linear_container_channel_categories;
    HorizontalScrollView container_channels_categories;
    TextView epg_time_homescreen, epg_name_homescreen, epg_duration_homescreen, epg_description_homescreen;
    ProgressDialog pDialog;

    ArrayList<Parentsetget> totalparentlist;
    ArrayList<Parentsetget> uncategorisedlist;
    ArrayList<Childsetget> totalchildlist;
    ArrayList<SetgetCategory> categoryList;

    int parentid = 0;
    int counter_epg_name = 0;
    int mWidth,mHeight;

    boolean is_disable_loader = false;
    boolean is_first_time = false;
    boolean is_first_time_loader = false,is_create = true;

    String cuurent_time_fragment = "";
    String current_date_global = "";
    String epg_staring_time_global = "", epg_ending_time_global = "";
    String current_playing_stream_name = "";
    String current_playing_category_id = "";
    String lower_date_limit = "", upper_date_limit = "";

    CountDownTimer time_countdown;
    CountDownTimer refresh_Epg;

    MediaPlayer mMediaPlayer;
    LibVLC mLibVLC;

    XtreamCodeAPICall objxtream;
    boolean is_first_channel = false, is_last_channel = false,is_start = false,doubleBackToExitPressedOnce = false;
    int category_focus_id = 0;

    static String liveplayUrl = "";
    static String stream_id_current="";
    SharedPreferences logindetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        logindetails = getSharedPreferences("logindetails", MODE_PRIVATE);
        SharedPreferences.Editor rem = logindetails.edit();
        rem.putString("username", MyApp.user);
        rem.putString("password", MyApp.pass);
        rem.apply();

        objxtream = new XtreamCodeAPICall(Constants.GetAppDomain(EPGActivityUpdated.this), this);
        if (isTablet(this)) {
            setContentView(R.layout.layout_homescreen_tab);
        } else {
            setContentView(R.layout.layout_homescreen);
        }

        InitView();
        Setplayer();

        totalparentlist = new ArrayList<>();
        totalchildlist = new ArrayList<>();
        categoryList = new ArrayList<>();
        uncategorisedlist = new ArrayList<Parentsetget>();


        Settime(TimeZone.getDefault().getID());

        current_date_global = Constants.GetCurrentDateByTimeZone("yyyy-MM-dd", TimeZone.getDefault().getID());
        date_layout_homescreen.setText(Constants.GetCurrentDate("yyyy-MM-dd"));

        time_countdown = new CountDownTimer(24 * 3600 * 1000, 60 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_layout_homescreen.setText(Constants.GetCurrentTime("HH:mm"));
            }

            @Override
            public void onFinish() {

            }
        };

        time_countdown.start();

        objxtream.GetAllChanneldata();
        ShowProgressDilog(this);

        player_homescreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
                if(isTablet(EPGActivityUpdated.this)){
                    params.height = MyApp.SCREEN_HEIGHT+ Utils.dp2px(EPGActivityUpdated.this,50);
                }else {
                    params.height = MyApp.SCREEN_HEIGHT+ Utils.dp2px(EPGActivityUpdated.this,50);
                }
                params.width = MyApp.SCREEN_WIDTH;
//                    player_homescreen.setPadding(Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0));
//                ViewGroup.LayoutParams params1 = ly_surface.getLayoutParams();
                params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(getApplicationContext(),50);
                params.width = MyApp.SCREEN_WIDTH+Utils.dp2px(getApplicationContext(),50);
                player_homescreen.setPadding(Utils.dp2px(getApplicationContext(),0),Utils.dp2px(getApplicationContext(),0),Utils.dp2px(getApplicationContext(),0),Utils.dp2px(getApplicationContext(),0));
                setMargins(player_homescreen,Utils.dp2px(getApplicationContext(),0),Utils.dp2px(getApplicationContext(),0),Utils.dp2px(getApplicationContext(),0),Utils.dp2px(getApplicationContext(),0));
                player_homescreen.setLayoutParams(params);

            }
        });

        refresh_Epg = new CountDownTimer(24 * 3600 * 1000, 60 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (Constants.GetCurrentTimeByTimeZone("HH:mm", TimeZone.getDefault().getID())
                        .equalsIgnoreCase(time_2nd_half_homescreen.getText().toString()) ||
                        Constants.checktimings(time_2nd_half_homescreen.getText().toString(), Constants.GetCurrentTimeByTimeZone("HH:mm", TimeZone.getDefault().getID()), "HH:mm")) {
                    linear_epg_list_homescreen.removeAllViews();
                    SettimeEpg(time_2nd_half_homescreen.getText().toString());
                }

            }

            @Override
            public void onFinish() {

            }
        };
        findViewById(R.id.btn_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        FullScreencall();

    }

    void Setplayer() {
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

            mLibVLC = new LibVLC(this, options);

            mMediaPlayer = new MediaPlayer(mLibVLC);
            mMediaPlayer.setEventListener(this);


            // Seting up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(player_homescreen);
            vout.setWindowSize(mWidth, mHeight);
            vout.addCallback(this);
            vout.attachViews();

        } catch (Exception e) {
            Toast.makeText(this, "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }
    }

    void InitView() {
        player_homescreen = (SurfaceView) findViewById(R.id.player_homescreen);
        holder = player_homescreen.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBX_8888);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;
        int SCREEN_HEIGHT = displayMetrics.heightPixels;
        int SCREEN_WIDTH = displayMetrics.widthPixels;
        holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        time_1st_half_homescreen = (TextView) findViewById(R.id.time_1st_half_homescreen);
        time_2nd_half_homescreen = (TextView) findViewById(R.id.time_2nd_half_homescreen);
        time_3rd_half_homescreen = (TextView) findViewById(R.id.time_3rd_half_homescreen);
        time_4th_half_homescreen = (TextView) findViewById(R.id.time_4th_half_homescreen);
        time_layout_homescreen = (TextView) findViewById(R.id.time_layout_homescreen);
        date_layout_homescreen = (TextView) findViewById(R.id.date_layout_homescreen);
        linear_epg_list_homescreen = (LinearLayout) findViewById(R.id.linear_epg_list_homescreen);
        linear_container_channel_categories = (LinearLayout) findViewById(R.id.linear_container_channel_categories);
        container_channels_categories = (HorizontalScrollView) findViewById(R.id.container_channels_categories);
        epg_time_homescreen = (TextView) findViewById(R.id.epg_time_homescreen);
        epg_name_homescreen = (TextView) findViewById(R.id.epg_name_homescreen);
        epg_duration_homescreen = (TextView) findViewById(R.id.epg_duration_homescreen);
        epg_description_homescreen = (TextView) findViewById(R.id.epg_description_homescreen);

        if(isTablet(this)){
            ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
            params.height = MyApp.EPG_HEIGHT-Utils.dp2px(EPGActivityUpdated.this,20);
            params.width = MyApp.EPG_WIDTH;
            setMargins(player_homescreen,0,MyApp.EPG_TOP-Utils.dp2px(EPGActivityUpdated.this,30),MyApp.EPG_RIGHT-Utils.dp2px(EPGActivityUpdated.this,30),0);
            player_homescreen.setLayoutParams(params);
        }else {
            ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
            params.height = MyApp.EPG_HEIGHT;
            params.width = MyApp.EPG_WIDTH;
            setMargins(player_homescreen,0,MyApp.EPG_TOP,MyApp.EPG_RIGHT,0);
            player_homescreen.setLayoutParams(params);
        }
    }
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
    private void PlayChannel(){
        try {
            int channel_pos = 0,sub_pos = 0;
            if(MyApp.instance.getPreference().get(Constants.getCHANNEL_POS())!=null)
                channel_pos = (int) MyApp.instance.getPreference().get(Constants.getCHANNEL_POS());
            else
                channel_pos = 0;
            if(MyApp.instance.getPreference().get(Constants.getSubPos())!=null)
                sub_pos = (int) MyApp.instance.getPreference().get(Constants.getSubPos());
            else
                sub_pos = 0;

            String stream_id = MyApp.fullModels.get(channel_pos).getChannels().get(sub_pos).getStream_id();
            String stream_name =  MyApp.fullModels.get(channel_pos).getChannels().get(sub_pos).getName();
            liveplayUrl =  MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user, MyApp.pass,
                    stream_id,"ts");
            stream_id_current=stream_id;

            System.out.println("Live play url - " + liveplayUrl);
            PlayerRelease();
            Setplayer();
            Media m = new Media(mLibVLC, Uri.parse(liveplayUrl));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
            current_playing_stream_name = stream_name;
            SetEpgInfoChannelName(stream_name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ShowProgressDilog(Context c) {

        pDialog = new ProgressDialog(c);
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setContentView(R.layout.layout_progress_dilog);

    }

    void DismissProgress(Context c) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }


    public String GetDecodedString(String text) {
        return text;
    }

    public void Settime(String time_zone) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone(time_zone));

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        calendar.add(Calendar.MINUTE, mod < 29 ? -mod : (30 - mod));


        cuurent_time_fragment = dateFormat.format(calendar.getTime()) + ":00";

        time_1st_half_homescreen.setText(dateFormat.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, 30);

        time_2nd_half_homescreen.setText(dateFormat.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, 30);

        time_3rd_half_homescreen.setText(dateFormat.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, 30);

        time_4th_half_homescreen.setText(dateFormat.format(calendar.getTime()));

        calendar.add(Calendar.MINUTE, 30);

        epg_staring_time_global = time_1st_half_homescreen.getText().toString() + ":00";
        epg_ending_time_global = dateFormat.format(calendar.getTime()) + ":00";

    }

    public void SettimeEpg(String current_time) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));

        try {
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(dateFormat.parse(current_time));
            int unroundedMinutes = calendar.get(Calendar.MINUTE);
            int mod = unroundedMinutes % 30;
            calendar.add(Calendar.MINUTE, mod < 29 ? -mod : (30 - mod));


            cuurent_time_fragment = dateFormat.format(calendar.getTime()) + ":00";

            time_1st_half_homescreen.setText(dateFormat.format(calendar.getTime()));

            calendar.add(Calendar.MINUTE, 30);

            time_2nd_half_homescreen.setText(dateFormat.format(calendar.getTime()));

            calendar.add(Calendar.MINUTE, 30);

            time_3rd_half_homescreen.setText(dateFormat.format(calendar.getTime()));

            calendar.add(Calendar.MINUTE, 30);

            time_4th_half_homescreen.setText(dateFormat.format(calendar.getTime()));

            calendar.add(Calendar.MINUTE, 30);

            epg_staring_time_global = time_1st_half_homescreen.getText().toString() + ":00";
            epg_ending_time_global = dateFormat.format(calendar.getTime()) + ":00";

            ChangeEPGByTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private boolean checktimings(String current_time, String endtime) {

        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(current_time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getdiff(String start_time, String end_time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        long diffMinutes = 0;
        try {
            d1 = format.parse(start_time);
            d2 = format.parse(end_time);

            long diff = d2.getTime() - d1.getTime();

            if (isTablet(this)) {
                diffMinutes = diff / (60 * 1000) * 6;
            } else {
                diffMinutes = diff / (60 * 1000) * 3;
            }
            return diffMinutes;

        } catch (Exception e) {
            e.printStackTrace();
            return diffMinutes;
        }
    }


    @Override
    protected void onPause() {

        //mMediaPlayer.stop();
        PlayerRelease();

        objxtream.CancelAllRequest();
        time_countdown.cancel();
        refresh_Epg.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {

        refresh_Epg.start();
        time_countdown.start();

        date_layout_homescreen.setText(Constants.GetCurrentDateByTimeZone("EEE dd/MM", TimeZone.getDefault().getID()));
        if (!is_create) {
            PlayChannel();
        } else {
            is_create = false;
        }

        super.onResume();
    }

    void SetEpgInfo(String name, String start_time, long duration, String description) {
        epg_name_homescreen.setText(name);
        epg_time_homescreen.setText(start_time);
        epg_duration_homescreen.setText(String.valueOf(duration) + " min");
        epg_description_homescreen.setText(description);

    }

    void SetEpgNoInfo() {
        epg_name_homescreen.setText("No Info");
        epg_time_homescreen.setText("No Info");
        epg_duration_homescreen.setText("No Info");
        epg_description_homescreen.setText("No Info");

    }

    public void SetEpgInfoChannelName(String channel_name) {

        for (int i = 0; i < totalparentlist.size(); i++) {

            if (totalparentlist.get(i).getName().equalsIgnoreCase(channel_name)) {

                if (totalparentlist.get(i).getChildlist().size() == 0) {

                    SetEpgNoInfo();

                } else {

                    for (int j = 0; j < totalparentlist.get(i).getChildlist().size(); j++) {

                        String current_date = totalparentlist.get(i).getChildlist().get(j).getStart().substring(0, 10);

                        if (current_date_global.equalsIgnoreCase(current_date)) {

                            String start_time = totalparentlist.get(i).getChildlist().get(j).getStart().substring(
                                    totalparentlist.get(i).getChildlist().get(j).getStart().length() - 8,
                                    totalparentlist.get(i).getChildlist().get(j).getStart().length() - 3);

                            String end_time = totalparentlist.get(i).getChildlist().get(j).getEnd().substring(
                                    totalparentlist.get(i).getChildlist().get(j).getEnd().length() - 8,
                                    totalparentlist.get(i).getChildlist().get(j).getEnd().length() - 3);

                            if (start_time.concat(":00").equalsIgnoreCase(cuurent_time_fragment)) {
                                long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");
                                SetEpgInfo(Constants.GetDecodedString(totalparentlist.get(i).getChildlist().get(j).getTitle()), start_time, duration,
                                        Constants.GetDecodedString(totalparentlist.get(i).getChildlist().get(j).getDescription()));
                                break;
                            } else if (Constants.checktimings(cuurent_time_fragment, end_time, "HH:mm") &&
                                    Constants.checktimings(start_time, cuurent_time_fragment, "HH:mm")) {

                                long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");
                                SetEpgInfo(Constants.GetDecodedString(totalparentlist.get(i).getChildlist().get(j).getTitle()), start_time, duration,
                                        Constants.GetDecodedString(totalparentlist.get(i).getChildlist().get(j).getDescription()));

                            }
                        }
                    }
                }
            }

        }

    }


    @Override
    public void onEvent(MediaPlayer.Event event) {

    }


    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }


    @Override
    public void onReceiveSeriesCaregories(ArrayList<HashMap> arrayList) {

    }

    @Override
    public void onReceiveSeriesList(ArrayList<HashMap> arrayList) {

    }

    @Override
    public void onReceiveSeriesSessions(ArrayList<HashMap> arrayList) {

    }

    public String encodedString(String channel_id) {

        StringBuilder str = new StringBuilder();

        if (!channel_id.equalsIgnoreCase("")) {

            for (int i = 0; i < channel_id.length(); i++) {

                char word = channel_id.charAt(i);
                if (word == '1') {
                    str.append("a");
                } else if (word == '2') {
                    str.append("b");
                } else if (word == '3') {
                    str.append("c");
                } else if (word == '4') {
                    str.append("d");
                } else if (word == '5') {
                    str.append("e");
                } else if (word == '6') {
                    str.append("f");
                } else if (word == '7') {
                    str.append("g");
                } else if (word == '8') {
                    str.append("h");
                } else if (word == '9') {
                    str.append("i");
                } else if (word == '0') {
                    str.append("j");
                }

            }

        }
        return str.toString();

    }

    @Override
    protected void onDestroy() {

        /*player_homescreen.stopPlayback();
        player_homescreen.release();*/
        PlayerRelease();
        super.onDestroy();
    }


    public void PlayerRelease() {
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        mLibVLC.release();
    }


    public void ChangeEPGByTime() {

        for (int j = 0; j < totalparentlist.size(); j++) {

            LinearLayout linearLayout_epglist_total = new LinearLayout(this);
            linearLayout_epglist_total.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout_epglist_total.setOrientation(LinearLayout.HORIZONTAL);


            LinearLayout linearLayout_epglist_channel_name = new LinearLayout(this);
            linearLayout_epglist_channel_name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            linearLayout_epglist_channel_name.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout linearLayout_epglist_epg_name = new LinearLayout(this);
            linearLayout_epglist_epg_name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f));
            linearLayout_epglist_epg_name.setOrientation(LinearLayout.HORIZONTAL);


            LinearLayout.LayoutParams param_channelname = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param_channelname.setMargins(4, 4, 4, 4);
            TextView tv_channelname = new TextView(EPGActivityUpdated.this);
            tv_channelname.setLayoutParams(param_channelname);
            tv_channelname.setText(totalparentlist.get(j).getName());
            try {
                JSONObject object = new JSONObject();
                object.put("stream_id", totalparentlist.get(j).getId());
                object.put("stream_type", totalparentlist.get(j).getStream_type());
                object.put("stream_name", totalparentlist.get(j).getName());
                tv_channelname.setTag(object);

            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_channelname.setPadding(10, 10, 10, 10);
            if (isTablet(this)) {
                tv_channelname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            } else {
                tv_channelname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            }
            tv_channelname.setTextColor(Color.parseColor("#FFFFFF"));
            tv_channelname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
            tv_channelname.setSingleLine(true);
            tv_channelname.setEllipsize(TextUtils.TruncateAt.END);
            tv_channelname.setFocusable(true);
            tv_channelname.setClickable(true);

            tv_channelname.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    try {
                        is_start = true;
                        JSONObject object = (JSONObject) v.getTag();
                        liveplayUrl =  MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user, MyApp.pass,
                                object.getString("stream_id"),"ts");
                        stream_id_current=object.getString("stream_id");

                        System.out.println("Live play url - " + liveplayUrl);

                        PlayerRelease();

                        Setplayer();
                        Media m = new Media(mLibVLC, Uri.parse(liveplayUrl));
                        mMediaPlayer.setMedia(m);
                        mMediaPlayer.play();
                        current_playing_stream_name = object.getString("stream_name");

                        SetEpgInfoChannelName(object.getString("stream_name"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onDoubleClick(View v) {
//                    if (liveplayUrl.equalsIgnoreCase("")) {
//                        Toast.makeText(EPGActivityUpdated.this, "Please Select A Channel", Toast.LENGTH_SHORT).show();
//                    } else {
//                        mMediaPlayer.stop();
//                        VlcPlayerActivity.stream_id_from_epg=stream_id_current;
//                        startActivity(new Intent(EPGActivityUpdated.this, VlcPlayerActivity.class));
//                    }
                    ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
                    if(isTablet(EPGActivityUpdated.this)){
                        params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(EPGActivityUpdated.this,50);
                    }else {
                        params.height = MyApp.SCREEN_HEIGHT;
                    }
                    params.width = MyApp.SCREEN_WIDTH;
//                    player_homescreen.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                    setMargins(player_homescreen,Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0));
                    player_homescreen.setLayoutParams(params);
                }
            });


            linearLayout_epglist_channel_name.addView(tv_channelname);

            if (totalparentlist.get(j).getChildlist().size() == 0) {

                LinearLayout.LayoutParams param_epgname = new LinearLayout.LayoutParams(360 * 4, ViewGroup.LayoutParams.WRAP_CONTENT);
                param_epgname.setMargins(4, 4, 4, 4);
                TextView tv_epgname = new TextView(EPGActivityUpdated.this);
                tv_epgname.setLayoutParams(param_epgname);
                tv_epgname.setPadding(10, 10, 10, 10);
                if (isTablet(this)) {
                    tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else {
                    tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                }
                tv_epgname.setTextColor(Color.parseColor("#FFFFFF"));
                tv_epgname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                tv_epgname.setSingleLine(true);
                tv_epgname.setEllipsize(TextUtils.TruncateAt.END);
                tv_epgname.setFocusable(true);

                tv_epgname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetEpgNoInfo();
                    }
                });

                linearLayout_epglist_epg_name.addView(tv_epgname);

            } else {
                String temp_time = cuurent_time_fragment;
                for (int i = 0; i < totalparentlist.get(j).getChildlist().size(); i++) {

                    String current_date = totalparentlist.get(j).getChildlist().get(i).getStart().substring(0, 10);

                    if (current_date_global.equalsIgnoreCase(current_date)) {

                        final String start_time = totalparentlist.get(j).getChildlist().get(i).getStart().substring(
                                totalparentlist.get(j).getChildlist().get(i).getStart().length() - 8,
                                totalparentlist.get(j).getChildlist().get(i).getStart().length());

                        final String end_time = totalparentlist.get(j).getChildlist().get(i).getEnd().substring(
                                totalparentlist.get(j).getChildlist().get(i).getEnd().length() - 8,
                                totalparentlist.get(j).getChildlist().get(i).getEnd().length());


                        if (start_time.equalsIgnoreCase(temp_time)) {

                            counter_epg_name++;
                            LinearLayout.LayoutParams param_epgname = new LinearLayout.LayoutParams(dpToPx((int) getdiff(start_time, end_time)), ViewGroup.LayoutParams.WRAP_CONTENT);
                            param_epgname.setMargins(4, 4, 4, 4);
                            TextView tv_epgname = new TextView(EPGActivityUpdated.this);
                            tv_epgname.setLayoutParams(param_epgname);
                            tv_epgname.setText(GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));

                            try {
                                JSONObject object = new JSONObject();
                                object.put("name", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));
                                object.put("description", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getDescription()));
                                object.put("start_time", totalparentlist.get(j).getChildlist().get(i).getStart());
                                object.put("end_time", totalparentlist.get(j).getChildlist().get(i).getEnd());

                                tv_epgname.setTag(object);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            tv_epgname.setPadding(10, 10, 10, 10);
                            if (isTablet(this)) {
                                tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            } else {
                                tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            }
                            tv_epgname.setTextColor(Color.parseColor("#FFFFFF"));
                            tv_epgname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                            tv_epgname.setSingleLine(true);
                            tv_epgname.setEllipsize(TextUtils.TruncateAt.END);
                            tv_epgname.setFocusable(true);

                            tv_epgname.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    try {
                                        JSONObject object = (JSONObject) v.getTag();

                                        String start_time = object.getString("start_time").substring(
                                                object.getString("start_time").length() - 8,
                                                object.getString("start_time").length() - 3);

                                        String end_time = object.getString("end_time").substring(
                                                object.getString("end_time").length() - 8,
                                                object.getString("end_time").length() - 3);

                                        long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                        SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                            linearLayout_epglist_epg_name.addView(tv_epgname);

                            temp_time = end_time;

                            if (checktimings(epg_ending_time_global, end_time)) {

                                LinearLayout.LayoutParams param_epgname1 = new LinearLayout.LayoutParams(dpToPx((int) getdiff(start_time, epg_ending_time_global)), ViewGroup.LayoutParams.WRAP_CONTENT);
                                param_epgname.setMargins(4, 4, 4, 4);
                                TextView tv_epgname1 = new TextView(EPGActivityUpdated.this);
                                tv_epgname1.setLayoutParams(param_epgname1);
                                tv_epgname1.setText(GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));

                                try {

                                    JSONObject object = new JSONObject();
                                    object.put("name", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));
                                    object.put("description", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getDescription()));
                                    object.put("start_time", totalparentlist.get(j).getChildlist().get(i).getStart());
                                    object.put("end_time", totalparentlist.get(j).getChildlist().get(i).getEnd());

                                    tv_epgname1.setTag(object);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                tv_epgname1.setPadding(10, 10, 10, 10);
                                if (isTablet(this)) {
                                    tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                } else {
                                    tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                }
                                tv_epgname1.setTextColor(Color.parseColor("#FFFFFF"));
                                tv_epgname1.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                                tv_epgname1.setSingleLine(true);
                                tv_epgname1.setEllipsize(TextUtils.TruncateAt.END);
                                tv_epgname1.setFocusable(true);

                                tv_epgname1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        try {
                                            JSONObject object = (JSONObject) v.getTag();

                                            String start_time = object.getString("start_time").substring(
                                                    object.getString("start_time").length() - 8,
                                                    object.getString("start_time").length() - 3);

                                            String end_time = object.getString("end_time").substring(
                                                    object.getString("end_time").length() - 8,
                                                    object.getString("end_time").length() - 3);

                                            long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                            SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                linearLayout_epglist_epg_name.addView(tv_epgname1);

                                break;
                            }

                        } else {

                            if (checktimings(temp_time, end_time)) {


                                LinearLayout.LayoutParams param_epgname = new LinearLayout.LayoutParams(dpToPx((int) getdiff(temp_time, start_time)), ViewGroup.LayoutParams.WRAP_CONTENT);
                                param_epgname.setMargins(4, 4, 4, 4);
                                TextView tv_epgname = new TextView(EPGActivityUpdated.this);
                                tv_epgname.setLayoutParams(param_epgname);
                                tv_epgname.setText(GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));

                                try {

                                    JSONObject object = new JSONObject();
                                    object.put("name", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));
                                    object.put("description", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getDescription()));
                                    object.put("start_time", totalparentlist.get(j).getChildlist().get(i).getStart());
                                    object.put("end_time", totalparentlist.get(j).getChildlist().get(i).getEnd());

                                    tv_epgname.setTag(object);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                tv_epgname.setPadding(10, 10, 10, 10);
                                if (isTablet(this)) {
                                    tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                } else {
                                    tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                }
                                tv_epgname.setTextColor(Color.parseColor("#FFFFFF"));
                                tv_epgname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                                tv_epgname.setSingleLine(true);
                                tv_epgname.setEllipsize(TextUtils.TruncateAt.END);
                                tv_epgname.setFocusable(true);

                                tv_epgname.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        try {
                                            JSONObject object = (JSONObject) v.getTag();

                                            String start_time = object.getString("start_time").substring(
                                                    object.getString("start_time").length() - 8,
                                                    object.getString("start_time").length() - 3);

                                            String end_time = object.getString("end_time").substring(
                                                    object.getString("end_time").length() - 8,
                                                    object.getString("end_time").length() - 3);

                                            long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                            SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                linearLayout_epglist_epg_name.addView(tv_epgname);

                                temp_time = end_time;

                                if (checktimings(epg_ending_time_global, end_time)) {

                                    LinearLayout.LayoutParams param_epgname1 = new LinearLayout.LayoutParams(dpToPx((int) getdiff(start_time, epg_ending_time_global)), ViewGroup.LayoutParams.WRAP_CONTENT);
                                    param_epgname.setMargins(4, 4, 4, 4);
                                    TextView tv_epgname1 = new TextView(EPGActivityUpdated.this);
                                    tv_epgname1.setLayoutParams(param_epgname1);
                                    tv_epgname1.setText(GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));

                                    try {

                                        JSONObject object = new JSONObject();
                                        object.put("name", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getTitle()));
                                        object.put("description", GetDecodedString(totalparentlist.get(j).getChildlist().get(i).getDescription()));
                                        object.put("start_time", totalparentlist.get(j).getChildlist().get(i).getStart());
                                        object.put("end_time", totalparentlist.get(j).getChildlist().get(i).getEnd());

                                        tv_epgname1.setTag(object);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    tv_epgname1.setPadding(10, 10, 10, 10);
                                    if (isTablet(this)) {
                                        tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                    } else {
                                        tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                    }
                                    tv_epgname1.setTextColor(Color.parseColor("#FFFFFF"));
                                    tv_epgname1.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                                    tv_epgname1.setSingleLine(true);
                                    tv_epgname1.setEllipsize(TextUtils.TruncateAt.END);
                                    tv_epgname1.setFocusable(true);

                                    tv_epgname1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try {
                                                JSONObject object = (JSONObject) v.getTag();


                                                String start_time = object.getString("start_time").substring(
                                                        object.getString("start_time").length() - 8,
                                                        object.getString("start_time").length() - 3);

                                                String end_time = object.getString("end_time").substring(
                                                        object.getString("end_time").length() - 8,
                                                        object.getString("end_time").length() - 3);

                                                long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                                SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                    linearLayout_epglist_epg_name.addView(tv_epgname1);

                                    break;
                                }

                            }
                        }
                    }

                }
            }


            linearLayout_epglist_total.addView(linearLayout_epglist_channel_name);
            linearLayout_epglist_total.addView(linearLayout_epglist_epg_name);

            linear_epg_list_homescreen.addView(linearLayout_epglist_total);
        }

    }


    @Override
    public void onSuccess(SharedPreferences sharedPreferences, ArrayList<HashMap> LiveChannelCategory, ArrayList<HashMap> Moviecategory, ArrayList<HashMap> seriescategory) {
        DismissProgress(this);

        for (int x = 0; x < LiveChannelCategory.size(); x++) {

            SetgetCategory setgetCategory1 = new SetgetCategory();
            setgetCategory1.setCategory_name(LiveChannelCategory.get(x).get("cate_name").toString());
            setgetCategory1.setCategory_id(LiveChannelCategory.get(x).get("cat_id").toString());
            setgetCategory1.setChannels(new ArrayList<Parentsetget>());

            for (int y = 0; y < ((ArrayList<HashMap>) LiveChannelCategory.get(x).get("channels")).size(); y++) {
                Parentsetget parentsetget = new Parentsetget();
                parentsetget.setName(((ArrayList<HashMap>) LiveChannelCategory.get(x).get("channels")).get(y).get("Name").toString());
                parentsetget.setId(Integer.parseInt(((ArrayList<HashMap>) LiveChannelCategory.get(x).get("channels")).get(y).get("ID").toString()));
                parentsetget.setStream_icon(((ArrayList<HashMap>) LiveChannelCategory.get(x).get("channels")).get(y).get("KeyPicture").toString());
                parentsetget.setEpg_channel_id(((ArrayList<HashMap>) LiveChannelCategory.get(x).get("channels")).get(y).get("epg_channel_id").toString());

                parentsetget.setStream_type("live");
                setgetCategory1.getChannels().add(parentsetget);
            }

            if (((ArrayList<HashMap>) LiveChannelCategory.get(x).get("channels")).size() > 0)
                categoryList.add(setgetCategory1);
        }

        for (int a = 0; a < categoryList.size(); a++) {
            categoryList.get(a).setShow_flag("true");
        }


        for (int i = 0; i < categoryList.size(); i++) {

            if (categoryList.get(i).getShow_flag().equalsIgnoreCase("true")) {

                if (categoryList.get(i).getChannels() != null) {
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    tv.setText(categoryList.get(i).getCategory_name());
                    tv.setTag(categoryList.get(i).getCategory_name());
                    if (isTablet(this)) {
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    } else {
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    }
                    tv.setTextColor(getResources().getColor(R.color.white));
                    tv.setBackground(getResources().getDrawable(R.drawable.bg_epglist_categories));
                    tv.setSingleLine(true);
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    tv.setFocusable(true);
                    tv.setPadding(20, 10, 20, 10);
                    if (i == 0) {
                        tv.setId(i);
                        tv.setNextFocusRightId(i + 1);
                        tv.setSelected(true);
                    } else if (i == categoryList.size() - 1) {
                        tv.setId(i);
                        tv.setNextFocusRightId(0);
                        tv.setNextFocusLeftId(i - 1);
                    } else {
                        tv.setId(i);
                        tv.setNextFocusRightId(i + 1);
                        tv.setNextFocusLeftId(i - 1);
                    }
                    final int finalI = i;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < linear_container_channel_categories.getChildCount(); i++) {
                                View view = (View) linear_container_channel_categories.getChildAt(i);
                                if (view.getTag().toString().equalsIgnoreCase(v.getTag().toString())) {
                                    linear_container_channel_categories.getChildAt(i).setSelected(true);
                                } else {
                                    linear_container_channel_categories.getChildAt(i).setSelected(false);
                                }

                            }
                            category_focus_id = v.getId();

                            lower_date_limit = "";
                            upper_date_limit = "";
                            is_first_time = false;
                            is_first_time_loader = false;

                            current_date_global = Constants.GetCurrentDateByTimeZone("yyyy-MM-dd", TimeZone.getDefault().getID());
                            current_playing_category_id = categoryList.get(finalI).getCategory_id();
                            objxtream.CancelAllRequest();
                            linear_epg_list_homescreen.removeAllViews();
                            parentid = 0;
                            is_disable_loader = false;
                            try {
                                if (finalI == 0) {
                                    totalparentlist = categoryList.get(finalI).getChannels();
                                    is_first_channel = false;
                                    is_last_channel = false;
                                    objxtream.GetEpgListEachChannel(totalparentlist.get(0).getEpg_channel_id(), is_first_channel, is_last_channel);

                                } else {
                                    totalparentlist = categoryList.get(finalI).getChannels();
                                    is_first_channel = false;
                                    is_last_channel = false;
                                    objxtream.GetEpgListEachChannel(totalparentlist.get(0).getEpg_channel_id(), is_first_channel, is_last_channel);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(2, 15, 2, 15);

                    View v = new View(this);
                    v.setLayoutParams(lp);
                    v.setTag(categoryList.get(i).getCategory_name());
                    v.setBackgroundColor(Color.parseColor("#00000000"));
                    v.setPadding(5, 5, 5, 0);
                    if (i == categoryList.size() - 1) {
                        linear_container_channel_categories.addView(tv);
                    } else {
                        linear_container_channel_categories.addView(tv);
                        linear_container_channel_categories.addView(v);
                    }
                }
            }
        }


        for (int i = 0; i < categoryList.size(); i++) {

            if (categoryList.get(i).getShow_flag().equalsIgnoreCase("true")) {

                totalparentlist = categoryList.get(i).getChannels();
                is_first_channel = false;
                is_last_channel = false;
                objxtream.GetEpgListEachChannel(totalparentlist.get(parentid).getEpg_channel_id(), is_first_channel, is_last_channel);
                break;
            }
        }
        PlayChannel();
    }


    @Override
    public void onMACAuthenticationSuccess(String username, String password) {
    }

    @Override
    public void ErrorOnApiCall() {
        DismissProgress(this);
        Log.e("epgresponse","error");
    }

    @Override
    public void onEPGDataSuccess(SharedPreferences sharedPreferences, ArrayList<HashMap> EPGList) {
        try {
            totalchildlist = new ArrayList<>();
            Log.e("epgresponse",EPGList.toString());
            Childsetget child;
            for (int i = 0; i < EPGList.size(); i++) {
                child = new Childsetget();
                child.setChannel_id(EPGList.get(i).get("channel_id").toString());
                child.setStart(EPGList.get(i).get("start").toString());
                child.setEnd(EPGList.get(i).get("end").toString());
                child.setTitle(EPGList.get(i).get("title").toString());
                child.setDescription(EPGList.get(i).get("description").toString());
                child.setParent_streamid(String.valueOf(totalparentlist.get(parentid).getId()));
                totalchildlist.add(child);
            }
            totalparentlist.get(parentid).setChildlist(totalchildlist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (totalparentlist.get(parentid).getChildlist().size() != 0) {
            if (!is_first_time) {
                try {
                    lower_date_limit = totalparentlist.get(parentid).getChildlist().get(0).getEnd().substring(0, 10);
                    upper_date_limit = totalparentlist.get(parentid).getChildlist()
                            .get(totalparentlist.get(parentid).getChildlist().size() - 1).getEnd().substring(0, 10);
                    is_first_time = true;
                } catch (Exception e) {
                    Toast.makeText(EPGActivityUpdated.this, "Date-Time Error", Toast.LENGTH_SHORT).show();
                    is_first_time = true;
                }
            }
        }
        LinearLayout linearLayout_epglist_total = new LinearLayout(EPGActivityUpdated.this);
        linearLayout_epglist_total.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout_epglist_total.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayout_epglist_channel_name = new LinearLayout(EPGActivityUpdated.this);
        linearLayout_epglist_channel_name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
        linearLayout_epglist_channel_name.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayout_epglist_epg_name = new LinearLayout(EPGActivityUpdated.this);
        linearLayout_epglist_epg_name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f));
        linearLayout_epglist_epg_name.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams param_channelname = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param_channelname.setMargins(4, 4, 4, 4);
        TextView tv_channelname = new TextView(EPGActivityUpdated.this);
        tv_channelname.setLayoutParams(param_channelname);
        tv_channelname.setText(totalparentlist.get(parentid).getName());
        try {
            JSONObject object = new JSONObject();
            object.put("stream_id", totalparentlist.get(parentid).getId());
            object.put("stream_type", totalparentlist.get(parentid).getStream_type());
            object.put("stream_name", totalparentlist.get(parentid).getName());
            tv_channelname.setTag(object);

        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_channelname.setPadding(10, 10, 10, 10);
        if (isTablet(this)) {
            tv_channelname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            tv_channelname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
        tv_channelname.setTextColor(Color.parseColor("#FFFFFF"));
        tv_channelname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
        tv_channelname.setSingleLine(true);
        tv_channelname.setEllipsize(TextUtils.TruncateAt.END);
        tv_channelname.setFocusable(true);
        tv_channelname.setClickable(true);

        tv_channelname.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

                try {
                    is_start = true;
                    JSONObject object = (JSONObject) v.getTag();
                    liveplayUrl =  MyApp.instance.getIptvclient().buildLiveStreamURL(MyApp.user, MyApp.pass,
                            object.getString("stream_id"),"ts");
                    stream_id_current=object.getString("stream_id");

                    System.out.println("Live play url - " + liveplayUrl);

                    PlayerRelease();

                    Setplayer();
                    Media m = new Media(mLibVLC, Uri.parse(liveplayUrl));
                    mMediaPlayer.setMedia(m);
                    mMediaPlayer.play();

                    current_playing_stream_name = object.getString("stream_name");
                    SetEpgInfoChannelName(object.getString("stream_name"));

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onDoubleClick(View v) {
                ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
                if(isTablet(EPGActivityUpdated.this)){
                    params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(EPGActivityUpdated.this,50);
                }else {
                    params.height = MyApp.SCREEN_HEIGHT;
                }
                params.width = MyApp.SCREEN_WIDTH;
//                    player_homescreen.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                setMargins(player_homescreen,Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0),Utils.dp2px(EPGActivityUpdated.this,0));
                player_homescreen.setLayoutParams(params);
            }
        });

        linearLayout_epglist_channel_name.addView(tv_channelname);

        if (totalparentlist.get(parentid).getChildlist().size() == 0) {

            LinearLayout.LayoutParams param_epgname = new LinearLayout.LayoutParams(360 * 4, ViewGroup.LayoutParams.WRAP_CONTENT);
            param_epgname.setMargins(4, 4, 4, 4);
            TextView tv_epgname = new TextView(EPGActivityUpdated.this);
            tv_epgname.setLayoutParams(param_epgname);
            tv_epgname.setPadding(10, 10, 10, 10);
            if (isTablet(this)) {
                tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            } else {
                tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            }
            tv_epgname.setTextColor(Color.parseColor("#FFFFFF"));
            tv_epgname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
            tv_epgname.setSingleLine(true);
            tv_epgname.setEllipsize(TextUtils.TruncateAt.END);
            tv_epgname.setFocusable(true);

            tv_epgname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetEpgNoInfo();
                }
            });

            linearLayout_epglist_epg_name.addView(tv_epgname);

        } else {
            String temp_time = cuurent_time_fragment;
            for (int i = 0; i < totalparentlist.get(parentid).getChildlist().size(); i++) {

                try {

                    String current_date = totalparentlist.get(parentid).getChildlist().get(i).getStart().substring(0, 10);

                    if (current_date_global.equalsIgnoreCase(current_date)) {

                        String start_time = totalparentlist.get(parentid).getChildlist().get(i).getStart().substring(
                                totalparentlist.get(parentid).getChildlist().get(i).getStart().length() - 8,
                                totalparentlist.get(parentid).getChildlist().get(i).getStart().length());

                        String end_time = totalparentlist.get(parentid).getChildlist().get(i).getEnd().substring(
                                totalparentlist.get(parentid).getChildlist().get(i).getEnd().length() - 8,
                                totalparentlist.get(parentid).getChildlist().get(i).getEnd().length());

                        if (start_time.equalsIgnoreCase(temp_time)) {

                            counter_epg_name++;
                            LinearLayout.LayoutParams param_epgname = new LinearLayout.LayoutParams(dpToPx((int) getdiff(start_time, end_time)), ViewGroup.LayoutParams.WRAP_CONTENT);
                            param_epgname.setMargins(4, 4, 4, 4);
                            TextView tv_epgname = new TextView(EPGActivityUpdated.this);
                            tv_epgname.setLayoutParams(param_epgname);
                            tv_epgname.setText(GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));

                            try {

                                JSONObject object = new JSONObject();
                                object.put("name", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));
                                object.put("description", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getDescription()));
                                object.put("start_time", totalparentlist.get(parentid).getChildlist().get(i).getStart());
                                object.put("end_time", totalparentlist.get(parentid).getChildlist().get(i).getEnd());

                                tv_epgname.setTag(object);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            tv_epgname.setPadding(10, 10, 10, 10);
                            if (isTablet(this)) {
                                tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            } else {
                                tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                            }
                            tv_epgname.setTextColor(Color.parseColor("#FFFFFF"));
                            tv_epgname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                            tv_epgname.setSingleLine(true);
                            tv_epgname.setEllipsize(TextUtils.TruncateAt.END);
                            tv_epgname.setFocusable(true);

                            tv_epgname.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    try {
                                        JSONObject object = (JSONObject) v.getTag();

                                        String start_time = object.getString("start_time").substring(
                                                object.getString("start_time").length() - 8,
                                                object.getString("start_time").length() - 3);

                                        String end_time = object.getString("end_time").substring(
                                                object.getString("end_time").length() - 8,
                                                object.getString("end_time").length() - 3);

                                        long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                        SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                            linearLayout_epglist_epg_name.addView(tv_epgname);

                            temp_time = end_time;

                            if (checktimings(epg_ending_time_global, end_time)) {

                                LinearLayout.LayoutParams param_epgname1 = new LinearLayout.LayoutParams(dpToPx((int) getdiff(start_time, epg_ending_time_global)), ViewGroup.LayoutParams.WRAP_CONTENT);
                                param_epgname.setMargins(4, 4, 4, 4);
                                TextView tv_epgname1 = new TextView(EPGActivityUpdated.this);
                                tv_epgname1.setLayoutParams(param_epgname1);
                                tv_epgname1.setText(GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));

                                try {

                                    JSONObject object = new JSONObject();
                                    object.put("name", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));
                                    object.put("description", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getDescription()));
                                    object.put("start_time", totalparentlist.get(parentid).getChildlist().get(i).getStart());
                                    object.put("end_time", totalparentlist.get(parentid).getChildlist().get(i).getEnd());

                                    tv_epgname1.setTag(object);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                tv_epgname1.setPadding(10, 10, 10, 10);
                                if (isTablet(this)) {
                                    tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                } else {
                                    tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                }
                                tv_epgname1.setTextColor(Color.parseColor("#FFFFFF"));
                                tv_epgname1.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                                tv_epgname1.setSingleLine(true);
                                tv_epgname1.setEllipsize(TextUtils.TruncateAt.END);
                                tv_epgname1.setFocusable(true);

                                tv_epgname1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        try {
                                            JSONObject object = (JSONObject) v.getTag();

                                            String start_time = object.getString("start_time").substring(
                                                    object.getString("start_time").length() - 8,
                                                    object.getString("start_time").length() - 3);

                                            String end_time = object.getString("end_time").substring(
                                                    object.getString("end_time").length() - 8,
                                                    object.getString("end_time").length() - 3);

                                            long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                            SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                linearLayout_epglist_epg_name.addView(tv_epgname1);

                                break;
                            }

                        } else {

                            if (checktimings(temp_time, end_time)) {


                                LinearLayout.LayoutParams param_epgname = new LinearLayout.LayoutParams(dpToPx((int) getdiff(temp_time, start_time)), ViewGroup.LayoutParams.WRAP_CONTENT);
                                param_epgname.setMargins(4, 4, 4, 4);
                                TextView tv_epgname = new TextView(EPGActivityUpdated.this);
                                tv_epgname.setLayoutParams(param_epgname);
                                tv_epgname.setText(GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));

                                try {

                                    JSONObject object = new JSONObject();
                                    object.put("name", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));
                                    object.put("description", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getDescription()));
                                    object.put("start_time", totalparentlist.get(parentid).getChildlist().get(i).getStart());
                                    object.put("end_time", totalparentlist.get(parentid).getChildlist().get(i).getEnd());

                                    tv_epgname.setTag(object);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                tv_epgname.setPadding(10, 10, 10, 10);
                                if (isTablet(this)) {
                                    tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                } else {
                                    tv_epgname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                }
                                tv_epgname.setTextColor(Color.parseColor("#FFFFFF"));
                                tv_epgname.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                                tv_epgname.setSingleLine(true);
                                tv_epgname.setEllipsize(TextUtils.TruncateAt.END);
                                tv_epgname.setFocusable(true);

                                tv_epgname.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        try {
                                            JSONObject object = (JSONObject) v.getTag();

                                            String start_time = object.getString("start_time").substring(
                                                    object.getString("start_time").length() - 8,
                                                    object.getString("start_time").length() - 3);

                                            String end_time = object.getString("end_time").substring(
                                                    object.getString("end_time").length() - 8,
                                                    object.getString("end_time").length() - 3);

                                            long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                            SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                linearLayout_epglist_epg_name.addView(tv_epgname);

                                temp_time = end_time;

                                if (checktimings(epg_ending_time_global, end_time)) {

                                    LinearLayout.LayoutParams param_epgname1 = new LinearLayout.LayoutParams(dpToPx((int) getdiff(start_time, epg_ending_time_global)), ViewGroup.LayoutParams.WRAP_CONTENT);
                                    param_epgname.setMargins(4, 4, 4, 4);
                                    TextView tv_epgname1 = new TextView(EPGActivityUpdated.this);
                                    tv_epgname1.setLayoutParams(param_epgname1);
                                    tv_epgname1.setText(GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));

                                    try {

                                        JSONObject object = new JSONObject();
                                        object.put("name", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getTitle()));
                                        object.put("description", GetDecodedString(totalparentlist.get(parentid).getChildlist().get(i).getDescription()));
                                        object.put("start_time", totalparentlist.get(parentid).getChildlist().get(i).getStart());
                                        object.put("end_time", totalparentlist.get(parentid).getChildlist().get(i).getEnd());

                                        tv_epgname1.setTag(object);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    tv_epgname1.setPadding(10, 10, 10, 10);
                                    if (isTablet(this)) {
                                        tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                    } else {
                                        tv_epgname1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                                    }
                                    tv_epgname1.setTextColor(Color.parseColor("#FFFFFF"));
                                    tv_epgname1.setBackground(getResources().getDrawable(R.drawable.bg_epglist));
                                    tv_epgname1.setSingleLine(true);
                                    tv_epgname1.setEllipsize(TextUtils.TruncateAt.END);
                                    tv_epgname1.setFocusable(true);

                                    tv_epgname1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            try {
                                                JSONObject object = (JSONObject) v.getTag();

                                                String start_time = object.getString("start_time").substring(
                                                        object.getString("start_time").length() - 8,
                                                        object.getString("start_time").length() - 3);

                                                String end_time = object.getString("end_time").substring(
                                                        object.getString("end_time").length() - 8,
                                                        object.getString("end_time").length() - 3);

                                                long duration = Constants.getTimeDiffMinutes(start_time, end_time, "HH:mm");

                                                SetEpgInfo(object.getString("name"), start_time, duration, object.getString("description"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                    linearLayout_epglist_epg_name.addView(tv_epgname1);

                                    break;
                                }

                            }

                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }

        linearLayout_epglist_total.addView(linearLayout_epglist_channel_name);
        linearLayout_epglist_total.addView(linearLayout_epglist_epg_name);

        linear_epg_list_homescreen.addView(linearLayout_epglist_total);

        if (parentid < totalparentlist.size() - 1) {
            parentid = parentid + 1;
            is_first_channel = false;
            is_last_channel = false;
            objxtream.GetEpgListEachChannel(totalparentlist.get(parentid).getEpg_channel_id(), is_first_channel, is_last_channel);

        }
    }

    @Override
    public void onReceiveVODInfo(JSONObject jsonObject) {

    }

    int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (!doubleBackToExitPressedOnce) {
                        this.doubleBackToExitPressedOnce = true;
                        if(isTablet(this)){
                            ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
                            params.height = MyApp.EPG_HEIGHT-Utils.dp2px(EPGActivityUpdated.this,20);
                            params.width = MyApp.EPG_WIDTH;
                            setMargins(player_homescreen,0,MyApp.EPG_TOP-Utils.dp2px(EPGActivityUpdated.this,30),MyApp.EPG_RIGHT-Utils.dp2px(EPGActivityUpdated.this,30),0);
                            player_homescreen.setLayoutParams(params);
                        }else {
                            ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
                            params.height = MyApp.EPG_HEIGHT;
                            params.width = MyApp.EPG_WIDTH;
                            setMargins(player_homescreen,0,MyApp.EPG_TOP,MyApp.EPG_RIGHT,0);
                            player_homescreen.setLayoutParams(params);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);
                        return true;
                    }
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    is_start = false;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    is_start = false;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(is_start){
                        ViewGroup.LayoutParams params = player_homescreen.getLayoutParams();
                        if(isTablet(this)){
                            params.height = MyApp.SCREEN_HEIGHT + Utils.dp2px(this,50);
                        }else {
                            params.height = MyApp.SCREEN_HEIGHT;
                        }
                        params.width = MyApp.instance.SCREEN_WIDTH;
                        player_homescreen.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                        setMargins(player_homescreen,Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                        player_homescreen.setLayoutParams(params);
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void FullScreencall() {
        if(Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else  {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
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


