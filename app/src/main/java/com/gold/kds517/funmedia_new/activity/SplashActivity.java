package com.gold.kds517.funmedia_new.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.apps.MyApp;
import com.gold.kds517.funmedia_new.apps.PlayGifView;
import com.gold.kds517.funmedia_new.dialog.ConnectionDlg;
import com.gold.kds517.funmedia_new.models.CategoryModel;
import com.gold.kds517.funmedia_new.models.EPGChannel;
import com.gold.kds517.funmedia_new.models.EPGEvent;
import com.gold.kds517.funmedia_new.models.FullModel;
import com.gold.kds517.funmedia_new.models.LoginModel;
import com.gold.kds517.funmedia_new.models.MovieModel;
import com.gold.kds517.funmedia_new.models.SeriesModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SplashActivity extends AppCompatActivity{
    String user,password;
    List<CategoryModel> categories;
    LoginModel loginModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        PlayGifView playGifView = findViewById(R.id.splash_gif);
        playGifView.setImageResource(R.raw.start);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playGifView.getLayoutParams();
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        playGifView.setLayoutParams(params);
        loginModel = (LoginModel) MyApp.instance.getPreference().get(Constants.getLoginInfo());
        user = loginModel.getUser_name().replaceAll("\\s","");
        password = loginModel.getPassword().replaceAll("\\s","");

        new Thread(this::callLogin).start();
        FullScreencall();

    }

    private void callLogin() {
        try {
            long startTime = System.nanoTime();
            String responseBody = MyApp.instance.getIptvclient().authenticate(user,password);
            long endTime = System.nanoTime();
            long MethodeDuration = (endTime - startTime);
            Log.e(getClass().getSimpleName(),responseBody);
            Log.e("BugCheck","authenticate success "+MethodeDuration);
            try {
                JSONObject map = new JSONObject(responseBody);
                MyApp.user = user;
                MyApp.pass = password;
                JSONObject u_m;
                u_m = map.getJSONObject("user_info");
                if (!u_m.has("username")) {
                    Toast.makeText(getApplicationContext(), "Username is incorrect", Toast.LENGTH_LONG).show();
                } else {
                    MyApp.created_at = u_m.getString("created_at");
                    MyApp.status = u_m.getString("status");
                    if(!MyApp.status.equalsIgnoreCase("Active")){
                        Intent intent =new Intent(this,EmptyActivity.class);
                        intent.putExtra("msg","Your account is Expired");
                        startActivity(intent);
                        return;
                    }
                    MyApp.is_trail = u_m.getString("is_trial");
                    MyApp.active_cons = u_m.getString("active_cons");
                    MyApp.max_cons = u_m.getString("max_connections");
                    String exp_date;
                    try{
                        exp_date = u_m.getString("exp_date");
                    }catch (Exception e){
                        exp_date = "unlimited";
                    }
                    LoginModel loginModel = new LoginModel();
                    loginModel.setUser_name(MyApp.user);
                    loginModel.setPassword(MyApp.pass);
                    try{
                        loginModel.setExp_date(exp_date);
                    }catch (Exception e){
                        loginModel.setExp_date("unlimited");
                    }
                    MyApp.loginModel = loginModel;
                    MyApp.instance.getPreference().put(Constants.getLoginInfo(), loginModel);

                    JSONObject serverInfo= map.getJSONObject("server_info");
                    String  my_timestamp= serverInfo.getString("timestamp_now");
                    String server_timestamp= serverInfo.getString("time_now");
                    Constants.setServerTimeOffset(my_timestamp,server_timestamp);
                    callVodCategory();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() ->{
                    Toast.makeText(getApplicationContext(), "Username is incorrect", Toast.LENGTH_LONG).show();
                } );
            }
        } catch (Exception e0) {
            e0.printStackTrace();
            runOnUiThread(() -> {
                ConnectionDlg connectionDlg = new ConnectionDlg(this, new ConnectionDlg.DialogConnectionListener() {
                    @Override
                    public void OnYesClick(Dialog dialog) {
                        dialog.dismiss();
                        new Thread(() -> callLogin()).start();
                    }

                    @Override
                    public void OnNoClick(Dialog dialog) {
                        startActivity(new Intent(SplashActivity.this, ConnectionErrorActivity.class));
                    }
                },"LOGIN UNSUCCESSFUL PLEASE CHECK YOUR LOGIN DETAILS OR CONTACT YOUR PROVIDER", null, null);
                connectionDlg.show();
            });
        }
    }

    private void callVodCategory(){
        try {
            long startTime = System.nanoTime();
//api call here
            String map = MyApp.instance.getIptvclient().getMovieCategories(user,password);
            long endTime = System.nanoTime();
            long MethodeDuration = (endTime - startTime);

            Gson gson=new Gson();
            map = map.replaceAll("[^\\x00-\\x7F]", "");
            categories = new ArrayList<>();
            categories.add(getRecentMovies());
            categories.add(new CategoryModel(Constants.all_id,Constants.All,""));
            categories.add(new CategoryModel(Constants.fav_id,Constants.Favorites,"aa"));
            try {
                categories.addAll(gson.fromJson(map, new TypeToken<List<CategoryModel>>(){}.getType()));
            }catch (Exception e){

            }
            MyApp.vod_categories = categories;
            callLiveCategory();
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(() -> {
                ConnectionDlg connectionDlg = new ConnectionDlg(SplashActivity.this, new ConnectionDlg.DialogConnectionListener() {
                    @Override
                    public void OnYesClick(Dialog dialog) {
                        dialog.dismiss();
                        new Thread(() -> callVodCategory()).start();
                    }

                    @Override
                    public void OnNoClick(Dialog dialog) {
                        startActivity(new Intent(SplashActivity.this, ConnectionErrorActivity.class));
                    }
                },"LOGIN SUCCESSFUL LOADING DATA", null, null);
                connectionDlg.show();
            });
        }
    }

    private void callLiveCategory(){
        try {
            long startTime = System.nanoTime();
//api call here
            String map = MyApp.instance.getIptvclient().getLiveCategories(user,password);
            long endTime = System.nanoTime();

            long MethodeDuration = (endTime - startTime);
            Log.e(getClass().getSimpleName(),map);
            Log.e("BugCheck","getLiveCategories success "+MethodeDuration);
            Gson gson=new Gson();
            map = map.replaceAll("[^\\x00-\\x7F]", "");
            List<CategoryModel> categories;
            categories = new ArrayList<>();
            categories.add(new CategoryModel(Constants.recent_id,Constants.Recently_Viewed,""));
            categories.add(new CategoryModel(Constants.all_id,Constants.All,""));
            categories.add(new CategoryModel(Constants.fav_id,Constants.Favorites,""));
            try {
                categories.addAll(gson.fromJson(map, new TypeToken<List<CategoryModel>>(){}.getType()));
            }catch (Exception e){
            }
            MyApp.live_categories = categories;
            for (CategoryModel categoryModel: categories){
                String category_name = categoryModel.getName().toLowerCase();
                if(category_name.contains("adult")||category_name.contains("xxx")){
                    Constants.xxx_category_id = categoryModel.getId();
                    Log.e("LoginActivity","xxx_category_id: "+Constants.xxx_category_id);
                }
            }
            callSeriesCategory();
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(() -> {
                ConnectionDlg connectionDlg = new ConnectionDlg(SplashActivity.this, new ConnectionDlg.DialogConnectionListener() {
                    @Override
                    public void OnYesClick(Dialog dialog) {
                        dialog.dismiss();
                        new Thread(() -> callLiveCategory()).start();
                    }

                    @Override
                    public void OnNoClick(Dialog dialog) {
                        startActivity(new Intent(SplashActivity.this, ConnectionErrorActivity.class));
                    }
                },"LOGIN SUCCESSFUL LOADING DATA", null, null);
                connectionDlg.show();
            });
        }
    }

    private void callSeriesCategory(){
        try {
            long startTime = System.nanoTime();
//api call here
            String map = MyApp.instance.getIptvclient().getSeriesCategories(user,password);
            long endTime = System.nanoTime();

            long MethodeDuration = (endTime - startTime);
            Log.e(getClass().getSimpleName(),map);
            Log.e("BugCheck","getSeriesCategories success "+MethodeDuration);
            Gson gson=new Gson();

            map = map.replaceAll("[^\\x00-\\x7F]", "");
            List<CategoryModel> categories;
            categories = new ArrayList<>();
            categories.add(getRecentSeries());
            categories.add(new CategoryModel(Constants.all_id,Constants.All,""));
            try {
                categories.addAll(gson.fromJson(map, new TypeToken<List<CategoryModel>>(){}.getType()));
            }catch (Exception e){
                e.printStackTrace();
            }
            MyApp.series_categories = categories;
            callLiveStreams();
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(() -> {
                ConnectionDlg connectionDlg = new ConnectionDlg(SplashActivity.this, new ConnectionDlg.DialogConnectionListener() {
                    @Override
                    public void OnYesClick(Dialog dialog) {
                        dialog.dismiss();
                        new Thread(() -> callSeriesCategory()).start();
                    }

                    @Override
                    public void OnNoClick(Dialog dialog) {
                        startActivity(new Intent(SplashActivity.this, ConnectionErrorActivity.class));
                    }
                },"LOGIN SUCCESSFUL LOADING DATA", null, null);
                connectionDlg.show();
            });
        }
    }

    private void callLiveStreams(){
        try{
            long startTime = System.nanoTime();
//api call here
            String map = MyApp.instance.getIptvclient().getLiveStreams(user,password);
            long endTime = System.nanoTime();

            long MethodeDuration = (endTime - startTime);
            Log.e(getClass().getSimpleName(),map);
            Log.e("BugCheck","getLiveStreams success "+MethodeDuration);
            try {
                map = map.replaceAll("[^\\x00-\\x7F]", "");
                List<EPGChannel> channelModels = new ArrayList<>();
                Gson gson=new Gson();
                try{
                    channelModels = new ArrayList<>(gson.fromJson(map, new TypeToken<List<EPGChannel>>() {}.getType()));
                    Log.e("SplashActivity","gson get model success");
                    for (EPGChannel epgChannel:channelModels) {
                        if (epgChannel.getCategory_id()!=null && epgChannel.getCategory_id().equals(Constants.xxx_category_id)) {
                            epgChannel.setIs_locked(true);
                            Log.e("SplashActivity","set lock success "+epgChannel.getName());
                        }
                        else epgChannel.setIs_locked(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    JSONArray response;
                    try {
                        response=new JSONArray(map);
                        for (int i=0;i<response.length();i++){
                            JSONObject jsonObject=response.getJSONObject(i);
                            EPGChannel epgChannel=new EPGChannel();
                            try{
                                epgChannel.setNum(jsonObject.getString("num"));
                            }catch (JSONException e1){
                                epgChannel.setNum("");
                            }
                            try{
                                epgChannel.setName(jsonObject.getString("name"));
                            }catch (JSONException e2){
                                epgChannel.setName("");
                            }
                            try{
                                epgChannel.setStream_type(jsonObject.getString("stream_type"));
                            }catch (JSONException e3){
                                epgChannel.setStream_type("");
                            }
                            try{
                                epgChannel.setStream_id(jsonObject.getString("stream_id"));
                            }catch (JSONException e4){
                                epgChannel.setStream_id("-1");
                            }
                            try{
                                epgChannel.setStream_icon(jsonObject.getString("stream_icon"));
                            }catch (JSONException e5){
                                epgChannel.setStream_icon("");
                            }
                            try{
                                epgChannel.setChannelID(jsonObject.getInt("epg_channel_id"));
                            }catch (JSONException e1){
                                epgChannel.setChannelID(-1);
                            }
                            try{
                                epgChannel.setAdded(jsonObject.getString("added"));
                            }catch (JSONException e1){
                                epgChannel.setAdded("");
                            }
                            try{
                                epgChannel.setCategory_id(jsonObject.getString("category_id"));
                                if (epgChannel.getCategory_id()!=null && epgChannel.getCategory_id().equals(Constants.xxx_category_id))
                                    epgChannel.setIs_locked(true);
                                else epgChannel.setIs_locked(false);
                            }catch (JSONException e1){
                                epgChannel.setCategory_id("-1");
                            }
                            try{
                                epgChannel.setCustom_sid(jsonObject.getString("custom_sid"));
                            }catch (JSONException e1){
                                epgChannel.setCustom_sid("");
                            }
                            try{
                                epgChannel.setTv_archive(jsonObject.getString("tv_archive"));
                            }catch (JSONException e1){
                                epgChannel.setTv_archive("0");
                            }
                            try{
                                epgChannel.setDirect_source(jsonObject.getString("direct_source"));
                            }catch (JSONException e1){
                                epgChannel.setDirect_source("");
                            }
                            try{
                                epgChannel.setTv_archive_duration(jsonObject.getString("tv_archive_duration"));
                            }catch (JSONException e1){
                                epgChannel.setTv_archive_duration("0");
                            }
                            channelModels.add(epgChannel);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                MyApp.channel_size = channelModels.size();
                Map back_map = new HashMap();
                back_map.put("channels", channelModels);
                MyApp.backup_map = back_map;
                List<FullModel> fullModels = new ArrayList<>();
                fullModels.add(new FullModel(Constants.recent_id, getRecentChannels(channelModels), Constants.Recently_Viewed));
                fullModels.add(new FullModel(Constants.all_id, channelModels,"All Channel"));
                if(MyApp.instance.getPreference().get(Constants.getFavInfo())==null){
                    fullModels.add(new FullModel(Constants.fav_id, new ArrayList<EPGChannel>(),"My Favorites"));
                }else {
                    List<EPGChannel> epgChannels = new ArrayList<>();
                    for(int i = 0;i<fullModels.get(0).getChannels().size();i++){
                        List<String> fav = (List<String>) MyApp.instance.getPreference().get(Constants.getFavInfo());
                        for(int j=0;j< fav.size();j++){
                            if(fullModels.get(0).getChannels().get(i).getName().equals(fav.get(j))){
                                fullModels.get(0).getChannels().get(i).setIs_favorite(true);
                                epgChannels.add(fullModels.get(0).getChannels().get(i));
                            }else {
                                fullModels.get(0).getChannels().get(i).setIs_favorite(false);
                            }
                        }
                    }
                    fullModels.add(new FullModel(Constants.fav_id, epgChannels,"My Favorites"));
                }

                List<String> datas = new ArrayList<>();
                datas.add(Constants.Recently_Viewed);
                datas.add("All Channel");
                datas.add("My Favorites");
                for(int i = 3; i< MyApp.live_categories.size(); i++){
                    String category_id = MyApp.live_categories.get(i).getId();
                    String category_name = MyApp.live_categories.get(i).getName();
                    int count =0;
                    List<EPGChannel> chModels = new ArrayList<>();
                    for(int j = 0;j<channelModels.size();j++){
                        if(category_id.equals(channelModels.get(j).getCategory_id())){
                            EPGChannel chModel = channelModels.get(j);
                            chModels.add(chModel);
                        }
                    }
//                    if(chModels.size()<1){
//                        continue;
//                    }
                    datas.add(MyApp.live_categories.get(i).getName());
                    fullModels.add(new FullModel(MyApp.live_categories.get(i).getName(),chModels,category_name));
                }
                MyApp.fullModels = fullModels;
                MyApp.maindatas = datas;
            }catch (Exception e){
                Log.e("catch","catch");
            }
            new Thread(this::callAllEpg).start();
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(() -> {
                ConnectionDlg connectionDlg = new ConnectionDlg(SplashActivity.this, new ConnectionDlg.DialogConnectionListener() {
                    @Override
                    public void OnYesClick(Dialog dialog) {
                        dialog.dismiss();
                        new Thread(() -> callLiveStreams()).start();
                    }

                    @Override
                    public void OnNoClick(Dialog dialog) {
                        startActivity(new Intent(SplashActivity.this, ConnectionErrorActivity.class));
                    }
                },"LOGIN SUCCESSFUL LOADING DATA", null, null);
                connectionDlg.show();
            });
        }
    }

    private boolean is_data_loaded = false;

    private void callAllEpg() {
        try {
            Log.e("BugCheck","getAllEPG start ");
            long startTime = System.nanoTime();
            //api call here
            String inputStream = MyApp.instance.getIptvclient().getAllEPG(MyApp.user,MyApp.pass);
            long endTime = System.nanoTime();

            long MethodeDuration = (endTime - startTime);
            //            Log.e(getClass().getSimpleName(),inputStream);
            Log.e("BugCheck","getAllEPG success "+MethodeDuration);
            if (inputStream==null || inputStream.length()==0) return;
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = null;
            //        Log.e("xml result","received");
            try {
                parser = parserFactory.newSAXParser();
                DefaultHandler handler = new DefaultHandler(){
                    String currentValue = "";
                    boolean currentElement = false;
                    EPGEvent prevEvent=null;
                    EPGEvent currentEvent=null;
                    String channel="";
                    List<EPGChannel> currentChannelList;
                    ArrayList<EPGEvent> epgModels=new ArrayList<>();
                    public void startElement(String uri, String localName,String qName, Attributes attributes) {
                        currentElement = true;
                        currentValue = "";
//                    Log.e("response","received");
                        if(localName.equals("programme")){
//                        Log.e("response","started programs list");
                            currentEvent = new EPGEvent();
                            String start=attributes.getValue(0);
                            String end=attributes.getValue(1);
                            currentEvent.setStart_timestamp(start);//.split(" ")[0]
                            currentEvent.setStop_timestamp(end);//.split(" ")[0]
                            if (!channel.equals(attributes.getValue(2))) {
                                if (currentChannelList !=null && !currentChannelList.isEmpty()) {
                                    Collections.sort(epgModels, new Comparator<EPGEvent>(){
                                        public int compare(EPGEvent o1, EPGEvent o2){
                                            return o1.getStart_timestamp().compareTo(o2.getStart_timestamp());
                                        }
                                    });
                                    for (EPGChannel epgChannel:currentChannelList)
                                        epgChannel.setEvents(epgModels);
                                }
                                epgModels=new ArrayList<>();
                                channel=attributes.getValue(2);
                                currentChannelList =findChannelByid(channel);
                            }
                        }
                    }
                    public void endElement(String uri, String localName, String qName) {
                        currentElement = false;
                        if (localName.equalsIgnoreCase("title"))
                            currentEvent.setTitle(currentValue);
                        else if (localName.equalsIgnoreCase("desc"))
                            currentEvent.setDec(currentValue);
                        else if (localName.equalsIgnoreCase("programme")) {
                            if (currentChannelList !=null && !currentChannelList.isEmpty())
                                currentEvent.setChannel(currentChannelList.get(0));
                            if (prevEvent!=null){
                                currentEvent.setPreviousEvent(prevEvent);
                                prevEvent.setNextEvent(currentEvent);
                            }
                            prevEvent=currentEvent;
                            for (EPGEvent epgEvent:epgModels){
                                if (epgEvent.getTitle().equals(currentEvent.getTitle()) &&
                                        epgEvent.getDec().equals(currentEvent.getDec()) &&
                                        epgEvent.getStart_timestamp().equals(currentEvent.getStart_timestamp()) &&
                                        epgEvent.getStop_timestamp().equals(currentEvent.getStop_timestamp()))
                                    return;
                            }
                            epgModels.add(currentEvent);
                        }
                        else if (localName.equalsIgnoreCase("tv")){
                            //
                            is_data_loaded=true;
                            Constants.getLiveFilter();
                            getAuthorization();
                        }
                    }
                    @Override
                    public void characters(char[] ch, int start, int length) {
                        if (currentElement) {
                            currentValue = currentValue +  new String(ch, start, length);
                        }
                    }
                };

                parser.parse(new InputSource(new StringReader(inputStream)),handler);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                Log.e("exceptionname",e.getStackTrace()[0].getMethodName());
                if (e.getStackTrace()[0].getMethodName().equalsIgnoreCase("finish")){
                    is_data_loaded=true;
                    Constants.getLiveFilter();
                    getAuthorization();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                ConnectionDlg connectionDlg = new ConnectionDlg(SplashActivity.this, new ConnectionDlg.DialogConnectionListener() {
                    @Override
                    public void OnYesClick(Dialog dialog) {
                        dialog.dismiss();
                        new Thread(() -> callAllEpg()).start();
                    }

                    @Override
                    public void OnNoClick(Dialog dialog) {
                        startActivity(new Intent(SplashActivity.this, ConnectionErrorActivity.class));
                    }
                },"LOGIN SUCCESSFUL LOADING DATA", null, null);
                connectionDlg.show();
            });
        }
    }

    private List<EPGChannel> findChannelByid(String channel_id){
        List<EPGChannel> channelList = new ArrayList<>();
        List<EPGChannel> entireChannels =Constants.getAllFullModel(MyApp.fullModels).getChannels();
        Log.e("allfullmodel",MyApp.fullModels.size()+" "+channel_id+" "+entireChannels.size());
        for (EPGChannel epgChannel : entireChannels) {
            try{
                if (epgChannel.getId()!=null && epgChannel.getId().equals(channel_id)) {
                    Log.e("findId",epgChannel.getId());
                    channelList.add(epgChannel);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return channelList;
    }

    private List<EPGChannel> getRecentChannels(List<EPGChannel> epgChannels){
        List<EPGChannel> recentChannels=new ArrayList<>();
        if(MyApp.instance.getPreference().get(Constants.getRecentChannels())!=null){
            List<String> recent_channel_names=(List<String>) MyApp.instance.getPreference().get(Constants.getRecentChannels());
            for(int j=0;j< recent_channel_names.size();j++){
                for(int i = 0;i<epgChannels.size();i++){
                    if(epgChannels.get(i).getName().equals(recent_channel_names.get(j))){
                        recentChannels.add(epgChannels.get(i));
                    }
                }
            }
        }
        return recentChannels;
    }
    private void getAuthorization(){
        StringRequest request = new StringRequest(Constants.GetAutho1(this), string -> {
            try {
                JSONObject object = new JSONObject(string);
                if (((String) object.get("status")).equalsIgnoreCase("success")) {
                    startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(SplashActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }, volleyError -> Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show());

        RequestQueue rQueue = Volley.newRequestQueue(SplashActivity.this);
        rQueue.add(request);
    }

    private CategoryModel getRecentMovies() {
        CategoryModel recentCategory = new CategoryModel(Constants.recent_id,Constants.Recently_Viewed,"");
        List<MovieModel> recentMovies=(List<MovieModel>) MyApp.instance.getPreference().get(Constants.getRecentMovies());
        if (recentMovies!=null){
            MyApp.recentMovieModels=recentMovies;
            recentCategory.setMovieModels(recentMovies);
        }
        else {
            MyApp.recentMovieModels=new ArrayList<>();
            recentCategory.setMovieModels(new ArrayList<>());
        }
        return recentCategory;
    }
    private CategoryModel getRecentSeries() {
        CategoryModel recentCategory = new CategoryModel(Constants.recent_id,Constants.Recently_Viewed,"");
        List<SeriesModel> recentMovies=(List<SeriesModel>) MyApp.instance.getPreference().get(Constants.getRecentSeries());
        if (recentMovies!=null){
            MyApp.recentSeriesModels=recentMovies;
            recentCategory.setSeriesModels(recentMovies);
        }
        else {
            MyApp.recentSeriesModels=new ArrayList<>();
            recentCategory.setSeriesModels(new ArrayList<>());
        }
        return recentCategory;
    }
    public void FullScreencall() {
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
