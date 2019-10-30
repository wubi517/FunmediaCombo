package com.gold.kds517.funmedia_new.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.adapter.EpisodeListAdapter;
import com.gold.kds517.funmedia_new.adapter.SeasonListAdapter;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.apps.MyApp;
import com.gold.kds517.funmedia_new.models.CategoryModelSeries;
import com.gold.kds517.funmedia_new.models.MovieModel;
import com.gold.kds517.funmedia_new.models.SeriesFullModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;

public class SeriesCatActivity extends AppCompatActivity implements   View.OnClickListener,AdapterView.OnItemClickListener,it.sephiroth.android.library.widget.AdapterView.OnItemClickListener {
    List<CategoryModelSeries> categories;
    List<SeriesFullModel> seriesFullModels;
    List<MovieModel> movieModels;
    HListView season_list;
    EpisodeListAdapter episodeListAdapter;
    SeasonListAdapter seasonListAdapter;
    ListView episode_list;
    TextView title_txt, rating_txt, cat_txt, view_txt, desc_txt,actor_txt,txt_category,txt_release;
    ImageView image;
    RatingBar ratingBar;
    MovieModel movieModel;
    int ses_pos=0,episode_pos = 0;
    String image_url,series_id,title,star,cast,genre,plot,cat_id;
    LinearLayout seri_lay;
    float rating;
    boolean is_sub = true;

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
        setContentView(R.layout.activity_series_cat);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LinearLayout linearLayout = findViewById(R.id.activity_series_cat);


        seri_lay = findViewById(R.id.seri_lay);
        categories = new ArrayList<>();
        movieModels = new ArrayList<>();

        season_list = findViewById(R.id.season_list);
        episode_list = findViewById(R.id.episode_list);
        title_txt = findViewById(R.id.vod_detail_title);
        rating_txt = findViewById(R.id.vod_detail_rating_val);
        cat_txt = findViewById(R.id.vod_detail_category);
        view_txt = findViewById(R.id.vod_detail_views);
        desc_txt = findViewById(R.id.vod_detail_desc);
        ratingBar = findViewById(R.id.vod_detail_ratingbar);
        actor_txt = findViewById(R.id.vod_detail_actors);
        image = findViewById(R.id.land_image);
        txt_category = findViewById(R.id.txt_category);
        txt_release = findViewById(R.id.txt_release);

        seasonListAdapter = new SeasonListAdapter(this, categories);
        season_list.setAdapter(seasonListAdapter);
        season_list.setOnItemClickListener(this);
        episodeListAdapter = new EpisodeListAdapter(this,movieModels);
        episode_list.setAdapter(episodeListAdapter);
        episode_list.setOnItemClickListener(this);
        initData();
        FullScreencall();

    }
    protected void initData(){
        Intent intent = getIntent();
        series_id = intent.getStringExtra("series_id");
        title = intent.getStringExtra("title");
        star = intent.getStringExtra("star");
        cast = intent.getStringExtra("cast");
        genre = intent.getStringExtra("genre");
        plot = intent.getStringExtra("plot");
        cat_id = intent.getStringExtra("cat_id");
        image_url = intent.getStringExtra("img_url");
        title_txt.setText(title);
        cat_txt.setText(cat_id);
        desc_txt.setText(plot);
        actor_txt.setText(cast);
        view_txt.setText(genre);
        txt_category.setText("Category : ");
        txt_release.setText("Genre");
        if(image_url==null || image_url.isEmpty()){
            Picasso.with(this).load(R.drawable.icon_default).into(image);
        }else {
            Picasso.with(this).load(image_url).into(image);
        }
        if(star==null || star.equals("n/A") || star.isEmpty()){
            rating = 0;
        }else {
            rating = Float.valueOf(star).floatValue();
        }
        if(rating>0){
            rating_txt.setText(star);
            ratingBar.setRating(rating);
        }else {
            rating_txt.setText("0");
            ratingBar.setRating(0);
        }
        new Thread(this::getSeriesInfo).start();
    }

    private void getSeriesInfo(){
        try {
            String requestBody = MyApp.instance.getIptvclient().getSeriesInfo(MyApp.user,MyApp.pass,series_id);
            Log.e(getClass().getSimpleName(),series_id + " "+ requestBody);
            JSONObject map = new JSONObject(requestBody);
            try {
                JSONObject s_m = (JSONObject) map.get("episodes");
                if(s_m.length()>0){
                    seriesFullModels = new ArrayList<>();
                    Iterator<String > key_array = s_m.keys();
                    while (key_array.hasNext()){
                        String key = key_array.next();
                        try {
                            JSONArray e_m = (JSONArray) s_m.get(key);
                            List<MovieModel> movieModels = new ArrayList<>();
                            for(int j = 0;j<e_m.length();j++){
                                try {
                                    JSONObject m_m = e_m.getJSONObject(j);
                                    movieModel = new MovieModel();
                                    movieModel.setStream_id(String.valueOf(m_m.get("id")));
                                    movieModel.setName((String)m_m.get("title"));
                                    movieModel.setType((String)m_m.get("container_extension"));
                                    movieModels.add(movieModel);
                                }catch (Exception e){
                                    Log.e("error","model_error");
                                }
                            }
                            seriesFullModels.add(new SeriesFullModel(key,movieModels));
                        }catch (Exception e){
                            Log.e("error","parse_error");
                        }
                    }
                    runOnUiThread(this::LoadData);
                }
            }catch (Exception e){
                Log.e("error","parse_error0");
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == episode_list){
            episode_pos = position;
            movieModels = seriesFullModels.get(ses_pos).getChannels();
            MyApp.movieModels = movieModels;
            MyApp.episode_pos = position;
            int current_player = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());

            Intent intent = new Intent();
            switch (current_player){
                case 0:
                    intent = new Intent(this,SeriesPlayActivity.class);
                    break;
                case 1:
                    intent = new Intent(this,SeriesIjkPlayActivity.class);
                    break;
                case 2:
                    intent = new Intent(this,SeriesExoPlayActivity.class);
                    break;
            }
            startActivity(intent);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (is_sub) {
            if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN &&
                    view == episode_list){
                episode_list.requestFocus();
                if(episode_pos < seriesFullModels.get(ses_pos).getChannels().size()-1){
                    episode_pos++;
                    LoadInfo();
                }
            }else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP &&
                    view == episode_list){
                if(episode_pos > 0){
                    episode_pos--;
                }else if(episode_pos == 0){
                    season_list.requestFocus();
                }
                LoadInfo();
            }else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT &&
                    view == season_list){
                if(ses_pos > 0){
                    ses_pos--;
                }
                seasonListAdapter.selectItem(ses_pos);
                season_list.post(() -> {
                    season_list.smoothScrollToPosition(ses_pos);
                    season_list.setSelection(ses_pos);
                });
                season_list.requestFocus();
            }else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT &&
                    view == season_list){
                if(ses_pos < categories.size()-1){
                    ses_pos++;
                }
                seasonListAdapter.selectItem(ses_pos);
                season_list.post(() -> {
                    season_list.smoothScrollToPosition(ses_pos);
                    season_list.setSelection(ses_pos);
                });
                season_list.requestFocus();
            }else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT){
                season_list.requestFocus();
                LoadSeason();
            }else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                finish();
            }else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && view == season_list){
                episode_pos = 0;
                LoadInfo();
            }
        } else {

        }
        return super.dispatchKeyEvent(event);
    }

    private void LoadData(){
        for(int i = 0; i <seriesFullModels.size();i++){
            CategoryModelSeries categoryModelSeries = new CategoryModelSeries();
            categoryModelSeries.setId(seriesFullModels.get(i).getCategory());
            categoryModelSeries.setName("Season"+seriesFullModels.get(i).getCategory());
            categories.add(categoryModelSeries);
        }
        seasonListAdapter = new SeasonListAdapter(this, categories);
        season_list.setAdapter(seasonListAdapter);
        movieModels = seriesFullModels.get(0).getChannels();
        episodeListAdapter = new EpisodeListAdapter(this,movieModels);
        episode_list.setAdapter(episodeListAdapter);
        episode_list.setOnItemClickListener(this);
    }
    private void LoadInfo(){
        try {
            movieModels = seriesFullModels.get(ses_pos).getChannels();
            movieModel = movieModels.get(episode_pos);
            title_txt.setText(movieModel.getName());
        }catch (Exception e){
        }
    }

    private void LoadSeason(){
        Intent intent = getIntent();
        series_id = intent.getStringExtra("series_id");
        title = intent.getStringExtra("title");
        star = intent.getStringExtra("star");
        cast = intent.getStringExtra("cast");
        genre = intent.getStringExtra("genre");
        plot = intent.getStringExtra("plot");
        cat_id = intent.getStringExtra("cat_id");
        image_url = intent.getStringExtra("img_url");
        txt_category.setText("Category : ");
        txt_release.setText("Genre");
        try {
            title_txt.setText(title);
            cat_txt.setText(cat_id);
            desc_txt.setText(plot);
            actor_txt.setText(cast);
            view_txt.setText(genre);
            if(image_url==null || image_url.isEmpty()){
                Picasso.with(this).load(R.drawable.icon_default).into(image);
            }else {
                Picasso.with(this).load(image_url).into(image);
            }
            if(star.equals("n/A") || star.isEmpty()){
                rating = 0;
            }else {
                rating = Float.valueOf(star).floatValue();
            }
            if(rating>0){
                rating_txt.setText(star);
                ratingBar.setRating(rating);
            }else {
                rating_txt.setText("0");
                ratingBar.setRating(0);
            }
        }catch (NullPointerException e){
        }
    }
    @Override
    public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> parent, View view, int position, long id) {
        View v = getCurrentFocus();
        if(parent == season_list){
            ses_pos = position;
            seasonListAdapter.selectItem(position);
            movieModels = seriesFullModels.get(position).getChannels();
            episodeListAdapter = new EpisodeListAdapter(this,movieModels);
            episode_list.setAdapter(episodeListAdapter);
            LoadSeason();
        }
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
}
