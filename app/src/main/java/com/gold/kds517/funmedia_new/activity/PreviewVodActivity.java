package com.gold.kds517.funmedia_new.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.adapter.VodListAdapter;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.apps.MyApp;
import com.gold.kds517.funmedia_new.dialog.SearchVodDlg;
import com.gold.kds517.funmedia_new.models.CategoryModel;
import com.gold.kds517.funmedia_new.models.MovieInfoModel;
import com.gold.kds517.funmedia_new.models.MovieModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PreviewVodActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    List<CategoryModel>categoryModels;
    List<MovieModel>movieModels;
    MovieInfoModel movieInfoModel;
    VodListAdapter adapter;
    RelativeLayout main_lay;
    ImageView btn_back,image_movie,btn_about_movie;
    TextView txt_category,txt_time,txt_progress,txt_rating,txt_age,txt_length,txt_director,txt_genre;
    ListView vod_list;
    Button btn_play,btn_trailer;
    String sort_by = "";
    String category_id,vod_title,vod_image,mStream_id,vod_url,type;
    int category_pos,sub_pos=0,sort=0,current_player=0;
    boolean is_create = true;
    Handler mEpgHandler = new Handler();
    Runnable mEpgTicker;
    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_vod);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        txt_category = (TextView)findViewById(R.id.txt_category);
        category_pos = (int) MyApp.instance.getPreference().get(Constants.getVodPos());
        categoryModels = MyApp.vod_categories_filter;
        category_id = categoryModels.get(category_pos).getId();
        txt_category.setText(categoryModels.get(category_pos).getName());

//        if(!getApplicationContext().getPackageName().equalsIgnoreCase(new String(Base64.decode(new String (Base64.decode("LmdvbGQua2RTG1kdmJHUXVhMlJZMjl0TG1kdmJHUXVhMlJ6TlRFM0xuTm9hWHA2WDI1bGR3PT0=".substring(11),Base64.DEFAULT)).substring(11),Base64.DEFAULT)))){
//            return;
//        }

        main_lay = findViewById(R.id.main_lay);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_about_movie = findViewById(R.id.btn_about_movie);
        btn_about_movie.setOnClickListener(this);
        image_movie = findViewById(R.id.image_movie);

        txt_time = findViewById(R.id.txt_time);
        txt_progress = findViewById(R.id.txt_progress);
        txt_rating = findViewById(R.id.txt_rating);
        txt_age = findViewById(R.id.txt_age);
        txt_length = findViewById(R.id.txt_length);
        txt_director = findViewById(R.id.txt_director);
        txt_genre = findViewById(R.id.txt_genre);

        vod_list = findViewById(R.id.vod_list);
        vod_list.setOnItemClickListener(this);

        btn_play = findViewById(R.id.btn_play);
        btn_trailer = findViewById(R.id.btn_trailer);
        btn_play.setOnClickListener(this);
        btn_trailer.setOnClickListener(this);

        findViewById(R.id.ly_back).setOnClickListener(this);
        findViewById(R.id.ly_guide).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);

        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();

        sort = (int) MyApp.instance.getPreference().get(Constants.getSORT());
        current_player = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
        if(category_pos==2){
            if(MyApp.instance.getPreference().get(Constants.getMovieFav())!=null){
                movieModels = (List<MovieModel>) MyApp.instance.getPreference().get(Constants.getMovieFav());
                MyApp.movieModels0 = movieModels;
                if(movieModels.size()>0){
//                    GetSortModel(movieModels);
                    txt_category.setText(categoryModels.get(category_pos).getName());
                    adapter = new VodListAdapter(this,movieModels);
                    vod_list.setAdapter(adapter);
                    new Thread(this::getMovieInfo).start();
                }
            }
        }else if(category_pos ==0){
            movieModels  = new ArrayList<>(Constants.getRecentCatetory(MyApp.vod_categories).getMovieModels());
            if(movieModels.size()>0){
//                GetSortModel(movieModels);
                txt_category.setText(categoryModels.get(category_pos).getName());
                adapter = new VodListAdapter(this,movieModels);
                vod_list.setAdapter(adapter);
                new Thread(this::getMovieInfo).start();
            }
        }else {
            new Thread(this::getMovies).start();
        }
        FullScreencall();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!is_create) {
            if(category_pos==2){
                if(MyApp.instance.getPreference().get(Constants.getMovieFav())!=null){
                    movieModels = (List<MovieModel>) MyApp.instance.getPreference().get(Constants.getMovieFav());
                    MyApp.movieModels0 = movieModels;
                    if(movieModels.size()>0){
//                    GetSortModel(movieModels);
                        txt_category.setText(categoryModels.get(category_pos).getName());
                        adapter = new VodListAdapter(this,movieModels);
                        vod_list.setAdapter(adapter);
                        new Thread(this::getMovieInfo).start();
                    }
                }
            }
        } else {
            is_create = false;
        }
    }

    private void getMovies(){
        String map = "";
//        txt_progress.setVisibility(View.VISIBLE);
        if(category_id.equalsIgnoreCase("100")){
            try {
                long startTime = System.nanoTime();
                map = MyApp.instance.getIptvclient().getMovies(MyApp.user,MyApp.pass);
                long endTime = System.nanoTime();
                long MethodeDuration = (endTime - startTime);
                Log.e(getClass().getSimpleName(),map);
                Log.e("BugCheck","getMovies success "+MethodeDuration);
            }catch (Exception e){
                map = "";
            }
        }else {
            try {
                long startTime = System.nanoTime();
                map = MyApp.instance.getIptvclient().getMoviesForCategory(MyApp.user,MyApp.pass,category_id);
                long endTime = System.nanoTime();
                long MethodeDuration = (endTime - startTime);
                Log.e(getClass().getSimpleName(),map);
                Log.e("BugCheck","getMovies success "+MethodeDuration);
            }catch (Exception e){
                map = "";
            }

        }
        map = map.replaceAll("[^\\x00-\\x7F]", "");
        try {
            JSONArray jsonArray = new JSONArray(map);
            movieModels = new ArrayList<>();
            if(jsonArray.length() > 0){
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject m_m = (JSONObject) jsonArray.get(i);
                    MovieModel movieModel = new MovieModel();
                    try {
                        movieModel.setNum(m_m.get("num").toString());
                        movieModel.setName(m_m.get("name").toString());
                        movieModel.setStream_id(String .valueOf(m_m.get("stream_id")));
                        movieModel.setExtension(m_m.get("container_extension").toString());
                        try {
                            movieModel.setRating(Double.parseDouble(m_m.get("rating").toString()));
                        }catch (Exception e){
                            movieModel.setRating(0.0);
                        }
                        try {
                            movieModel.setAdded(m_m.get("added").toString());
                        }catch (Exception e){
                            movieModel.setAdded("0");
                        }
                        try {
                            movieModel.setStream_icon(m_m.get("stream_icon").toString());
                        }catch (Exception e){
                            movieModel.setStream_icon("");
                        }
                        movieModel.setStream_type(m_m.get("stream_type").toString());
                        movieModels.add(movieModel);
                    }catch (Exception e){
                        Log.e("error", i +"vod_parse_error");
                    }
                }
                runOnUiThread(()->{
                    if(category_id.equalsIgnoreCase("100")){
                        GetSortModel(movieModels);
                        txt_category.setText(categoryModels.get(category_pos).getName()+" /"+sort_by);
                    }else {
                        txt_category.setText(categoryModels.get(category_pos).getName());
                    }
                    adapter = new VodListAdapter(this,movieModels);
                    MyApp.movieModels0 = movieModels;
                    vod_list.setAdapter(adapter);
                    new Thread(this::getMovieInfo).start();
                });
            }
        }catch (Exception e){

        }
    }
    private  List<MovieModel> GetSortModel(List<MovieModel>movieModels){
        switch (sort){
            case 0:
                sort_by = "By Number";
                Collections.sort(movieModels,
                        (o1, o2) -> {
                            if (Long.parseLong(o1.getAdded()) ==
                                    Long.parseLong(o2.getNum()))
                            {
                                return 0;
                            }
                            else if (Long.parseLong(o1.getNum()) <
                                    Long.parseLong(o2.getNum()))
                            {
                                return -1;
                            }
                            return 1;
                        });
                break;
            case 1:
                sort_by = "By Added";
                Collections.sort(movieModels,
                        (o1, o2) -> {
                            if (Long.parseLong(o1.getAdded()) ==
                                    Long.parseLong(o2.getAdded()))
                            {
                                return 0;
                            }
                            else if (Long.parseLong(o1.getAdded()) >
                                    Long.parseLong(o2.getAdded()))
                            {
                                return -1;
                            }
                            return 1;
                        });
                break;
            case 2:
                sort_by = "By Name";
                Collections.sort(movieModels, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                break;
        }
        return movieModels;
    }
    int epg_time;
    int i = 0;
    private void EpgTimer(){
        epg_time = 1;
        mEpgTicker = () -> {
            if (epg_time < 1) {
                i++;
                Log.e("count",String .valueOf(i));
                new Thread(this::getMovieInfo).start();
                return;
            }
            runNextEpgTicker();
        };
        mEpgTicker.run();
    }
    private void runNextEpgTicker() {
        epg_time--;
        long next = SystemClock.uptimeMillis() + 1000;
        mEpgHandler.postAtTime(mEpgTicker, next);
    }

    private void getMovieInfo(){
//        txt_progress.setVisibility(View.GONE);
        mStream_id = movieModels.get(sub_pos).getStream_id();
        try {
            String response = MyApp.instance.getIptvclient().getVodInfo(MyApp.user,MyApp.pass, mStream_id);
            movieInfoModel = new MovieInfoModel();
            try{
                Log.e(getClass().getSimpleName(),response);
                JSONObject map = new JSONObject(response);
                JSONObject info_obj = (JSONObject) map.get("info");
                try {
                    movieInfoModel.setMovie_img(info_obj.get("movie_image").toString());
                }catch (Exception e){
                    movieInfoModel.setMovie_img("");
                }
                try {
                    movieInfoModel.setGenre(info_obj.get("genre").toString());
                }catch (Exception e){
                    movieInfoModel.setGenre("");
                }
                try {
                    movieInfoModel.setPlot(info_obj.get("plot").toString());
                }catch (Exception e){
                    movieInfoModel.setPlot("");
                }
                try {
                    movieInfoModel.setCast(info_obj.get("cast").toString());
                }catch (Exception e){
                    movieInfoModel.setCast("");
                }
                try {
                    movieInfoModel.setRating(Double.parseDouble(info_obj.get("rating").toString()));
                }catch (Exception e1){
                    movieInfoModel.setRating(0.0);
                }
                try {
                    movieInfoModel.setYoutube(info_obj.get("youtube_trailer").toString());
                }catch (Exception e){
                    movieInfoModel.setYoutube("");
                }
                try {
                    movieInfoModel.setDirector(info_obj.get("director").toString());
                }catch (Exception e){
                    movieInfoModel.setDirector("");
                }
                try {
                    movieInfoModel.setDuration(Integer.parseInt(info_obj.get("duration").toString()));
                }catch (Exception e){
                    movieInfoModel.setDuration(0);
                }
                try {
                    movieInfoModel.setActors(info_obj.get("actors").toString());
                }catch (Exception e){
                    movieInfoModel.setActors("");
                }
                try {
                    movieInfoModel.setDescription(info_obj.get("description").toString());
                }catch (Exception e){
                    movieInfoModel.setDescription("");
                }
                try {
                    movieInfoModel.setAge(info_obj.get("age").toString());
                }catch (Exception e){
                    movieInfoModel.setAge("");
                }
                try {
                    movieInfoModel.setCountry(info_obj.get("country").toString());
                }catch (Exception e){
                    movieInfoModel.setCountry("");
                }
                try {
                    Map movie_data = (Map) map.get("movie_data");
                    try {
                        movieInfoModel.setStream_id(Integer.parseInt(movie_data.get("stream_id").toString()));
                    }catch (Exception e){
                        movieInfoModel.setStream_id(1);
                    }
                    try {
                        movieInfoModel.setExtension(movie_data.get("container_extension").toString());
                    }catch (Exception e){
                        movieInfoModel.setExtension("");
                    }
                    try {
                        movieInfoModel.setName(movie_data.get("name").toString());
                    }catch (Exception e){
                        movieInfoModel.setName("");
                    }
                }catch (Exception e){

                }

            }catch (Exception e){
                Log.e("error","info_parse_error");
            }
            runOnUiThread(this::printDetailData);
        }catch (Exception e){
            runOnUiThread(()->{
                txt_rating.setText(String .valueOf(movieModels.get(sub_pos).getRating()));
                try {
                    Picasso.with(PreviewVodActivity.this).load(movieModels.get(sub_pos).getStream_icon())
                            .placeholder(R.drawable.icon_default)
                            .error(R.drawable.icon_default)
                            .into(image_movie);
                }catch (Exception e0){
                    Picasso.with(PreviewVodActivity.this).load(R.drawable.icon_default).into(image_movie);
                }
            });
        }
    }

    private void printDetailData(){
        if(movieInfoModel.getYoutube()!=null && !movieInfoModel.getYoutube().isEmpty()){
            btn_trailer.setVisibility(View.VISIBLE);
        }else {
            btn_trailer.setVisibility(View.GONE);
        }
        txt_rating.setText(String .valueOf(movieInfoModel.getRating()));
        txt_age.setText(movieInfoModel.getAge());
        txt_genre.setText(movieInfoModel.getGenre());
        int hour = movieInfoModel.getDuration()/3600;
        int min = (movieInfoModel.getDuration()%3600)/60;
        int sec = (movieInfoModel.getDuration()%3600)%60;
        txt_length.setText(hour +":"+min+":"+sec);
        txt_director.setText(movieInfoModel.getDirector());
        try {
            Picasso.with(PreviewVodActivity.this).load(movieInfoModel.getMovie_img())
                    .placeholder(R.drawable.icon_default)
                    .error(R.drawable.icon_default)
                    .into(image_movie);
        }catch (Exception e){
            Picasso.with(PreviewVodActivity.this).load(R.drawable.icon_default).into(image_movie);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                MovieModel showmodel = movieModels.get(sub_pos);
                checkAddedRecent(showmodel);
                Constants.getRecentCatetory(MyApp.vod_categories).getMovieModels().add(0,showmodel);
                //get recent channel names list
                List<MovieModel> recent_channel_models = new ArrayList<>(Constants.getRecentCatetory(MyApp.vod_categories).getMovieModels());
                //set
                MyApp.instance.getPreference().put(Constants.getRecentMovies(), recent_channel_models);
                vod_title = movieModels.get(sub_pos).getName();
                vod_image = movieModels.get(sub_pos).getStream_icon();
                type = movieModels.get(sub_pos).getExtension();
                MyApp.vod_model = movieModels.get(sub_pos);
                vod_url = MyApp.instance.getIptvclient().buildMovieStreamURL(MyApp.user,MyApp.pass,mStream_id,type);
                Intent intent = new Intent();
                switch (current_player){
                    case 0:
                        intent = new Intent(this,VideoPlayActivity.class);
                        break;
                    case 1:
                        intent = new Intent(this,VideoIjkPlayActivity.class);
                        break;
                    case 2:
                        intent = new Intent(this,VideoExoPlayActivity.class);
                        break;
                }
                intent.putExtra("title",vod_title);
                intent.putExtra("img",vod_image);
                intent.putExtra("url",vod_url);
                startActivity(intent);
                break;
            case R.id.btn_trailer:
                String content_Uri = movieInfoModel.getYoutube();
                if(content_Uri.isEmpty()){
                    Toast.makeText(getApplicationContext(),"This movie do not have trailer",Toast.LENGTH_LONG).show();
                }else {
                    String newstr = content_Uri;
                    if(content_Uri.contains("=")){
                        int endIndex = content_Uri.lastIndexOf("=");
                        if (endIndex != -1)
                        {
                            newstr = content_Uri.substring(endIndex+1); // not forgot to put check if(endIndex != -1)
                        }
                    }else {
                        int endIndex = content_Uri.lastIndexOf("/");
                        if (endIndex != -1)
                        {
                            newstr = content_Uri.substring(endIndex+1); // not forgot to put check if(endIndex != -1)
                        }
                    }
                    Intent intent1 = new Intent(this, TrailerActivity.class);
                    intent1.putExtra("content_Uri",newstr);
                    startActivity(intent1);
                }
                break;
            case R.id.ly_back:
                finish();
                break;
            case R.id.ly_guide:
                if(movieModels==null || movieModels.size()==0 || movieInfoModel==null){
                    return ;
                }
                Intent intent1 = new Intent(PreviewVodActivity.this,VodInFoActivity.class);
                intent1.putExtra("title",movieModels.get(sub_pos).getName());
                intent1.putExtra("year",movieInfoModel.getCountry());
                intent1.putExtra("genre",movieInfoModel.getGenre());
                intent1.putExtra("length",movieInfoModel.getDuration());
                intent1.putExtra("director",movieInfoModel.getDirector());
                intent1.putExtra("cast",movieInfoModel.getActors());
                intent1.putExtra("age",movieInfoModel.getAge());
                if(movieInfoModel.getDescription()==null || movieInfoModel.getDescription().isEmpty() || movieInfoModel.getDescription().equalsIgnoreCase("No Information")){
                    intent1.putExtra("dec",movieInfoModel.getPlot());
                }else {
                    intent1.putExtra("dec",movieInfoModel.getDescription());
                }
                intent1.putExtra("img",movieInfoModel.getMovie_img());
                startActivity(intent1);
                break;
            case R.id.btn_search:
                SearchVodDlg searchVodDlg = new SearchVodDlg(PreviewVodActivity.this, (dialog, sel_Channel) -> {
                    FullScreencall();
                    dialog.dismiss();
                    for(int i=0;i<movieModels.size();i++){
                        if(movieModels.get(i).getName().equalsIgnoreCase(sel_Channel.getName())){
                            sub_pos = i;
                            break;
                        }
                    }
                    scrollToLast(vod_list, sub_pos);
                    mEpgHandler.removeCallbacks(mEpgTicker);
                    EpgTimer();
                    MyApp.touch = true;
                    adapter.selectItem(sub_pos);
                });
                searchVodDlg.show();
                break;
        }
    }
    private void checkAddedRecent(MovieModel showMovieModel) {
        Iterator<MovieModel> iter = Constants.getRecentCatetory(MyApp.vod_categories).getMovieModels().iterator();
        while(iter.hasNext()){
            MovieModel movieModel = iter.next();
            if (movieModel.getName().equals(showMovieModel.getName()))
                iter.remove();
        }
    }
    private void scrollToLast(final ListView listView, final int position) {
        listView.post(() -> listView.setSelection(position));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView==vod_list){
            if(movieModels.get(sub_pos).getName().equalsIgnoreCase(movieModels.get(i).getName())){
                MovieModel showmodel = movieModels.get(sub_pos);
                checkAddedRecent(showmodel);
                Constants.getRecentCatetory(MyApp.vod_categories).getMovieModels().add(0,showmodel);
                //get recent channel names list
                List<MovieModel> recent_channel_models = new ArrayList<>(Constants.getRecentCatetory(MyApp.vod_categories).getMovieModels());
                //set
                MyApp.instance.getPreference().put(Constants.getRecentMovies(), recent_channel_models);
                vod_title = movieModels.get(i).getName();
                vod_image = movieModels.get(i).getStream_icon();
                type = movieModels.get(i).getExtension();
                MyApp.vod_model = movieModels.get(sub_pos);
                vod_url = MyApp.instance.getIptvclient().buildMovieStreamURL(MyApp.user,MyApp.pass,mStream_id,type);
                Intent intent = new Intent();
                switch (current_player){
                    case 0:
                        intent = new Intent(this,VideoPlayActivity.class);
                        break;
                    case 1:
                        intent = new Intent(this,VideoIjkPlayActivity.class);
                        break;
                    case 2:
                        intent = new Intent(this,VideoExoPlayActivity.class);
                        break;
                }
                intent.putExtra("title",vod_title);
                intent.putExtra("img",vod_image);
                intent.putExtra("url",vod_url);
                startActivity(intent);
            }else {
                sub_pos = i;
                mEpgHandler.removeCallbacks(mEpgTicker);
                EpgTimer();
                MyApp.touch = true;
                adapter.selectItem(sub_pos);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_MENU:
                    SearchVodDlg searchVodDlg = new SearchVodDlg(PreviewVodActivity.this, (dialog, sel_Channel) -> {
                        FullScreencall();
                        dialog.dismiss();
                        for(int i=0;i<movieModels.size();i++){
                            if(movieModels.get(i).getName().equalsIgnoreCase(sel_Channel.getName())){
                                sub_pos = i;
                                break;
                            }
                        }
                        scrollToLast(vod_list, sub_pos);
                        mEpgHandler.removeCallbacks(mEpgTicker);
                        EpgTimer();
                        MyApp.touch = true;
                        adapter.selectItem(sub_pos);
                    });
                    searchVodDlg.show();
                    break;
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(movieModels==null || movieModels.size()==0 || movieInfoModel==null){
                        return true;
                    }
                    Intent intent1 = new Intent(PreviewVodActivity.this,VodInFoActivity.class);
                    intent1.putExtra("title",movieModels.get(sub_pos).getName());
                    intent1.putExtra("year",movieInfoModel.getCountry());
                    intent1.putExtra("genre",movieInfoModel.getGenre());
                    intent1.putExtra("length",movieInfoModel.getDuration());
                    intent1.putExtra("director",movieInfoModel.getDirector());
                    intent1.putExtra("cast",movieInfoModel.getActors());
                    intent1.putExtra("age",movieInfoModel.getAge());
                    if(movieInfoModel.getDescription()==null || movieInfoModel.getDescription().isEmpty() || movieInfoModel.getDescription().equalsIgnoreCase("No Information")){
                        intent1.putExtra("dec",movieInfoModel.getPlot());
                    }else {
                        intent1.putExtra("dec",movieInfoModel.getDescription());
                    }
                    intent1.putExtra("img",movieInfoModel.getMovie_img());
                    startActivity(intent1);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(view==vod_list){
                        if(sub_pos>0){
                            sub_pos--;
                            mEpgHandler.removeCallbacks(mEpgTicker);
                            EpgTimer();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(view==vod_list){
                        if(sub_pos<movieModels.size()-1){
                            sub_pos++;
                            mEpgHandler.removeCallbacks(mEpgTicker);
                            EpgTimer();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
//                    if(view == vod_list){
//                        vod_title = movieModels.get(sub_pos).getName();
//                        vod_image = movieModels.get(sub_pos).getStream_icon();
//                        type = movieModels.get(sub_pos).getExtension();
//                        MyApp.vod_model = movieModels.get(sub_pos);
//                        vod_url = Constants.GetAppDomain(this) + "movie/" + MyApp.user + "/" + MyApp.pass + "/" + mStream_id + "."+type;
//                        Intent intent = new Intent(this,VideoPlayActivity.class);
//                        intent.putExtra("title",vod_title);
//                        intent.putExtra("img",vod_image);
//                        intent.putExtra("url",vod_url);
//                        startActivity(intent);
//                    }
                    break;
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
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void doWork() {
        runOnUiThread(() -> {
            try {
                txt_time.setText(time.format(new Date()));
            } catch (Exception ignored) {
            }
        });
    }
    public void FullScreencall() {
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
