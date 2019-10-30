package com.gold.kds517.funmedia_new.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gold.kds517.funmedia_new.models.CategoryModel;
import com.gold.kds517.funmedia_new.models.EPGChannel;
import com.gold.kds517.funmedia_new.models.EPGEvent;
import com.gold.kds517.funmedia_new.models.FullModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Constants {
    public static final String APP_INFO = "app_info";
    public static final String LOGIN_INFO = "login_info";
    public static final String FAV_INFO = "user_info";
    public static final String MEDIA_POSITION = "media_position";
    public static final String SERIS_POSITION = "seris_position";
    public static final String MAC_ADDRESS = "mac_addr";
    public static final String MOVIE_FAV = "movie_app";

    public static final String CHANNEL_POS = "channel_pos";
    public static final String SUB_POS ="sub_pos";
    public static final String SERIES_POS = "series_pos";
    public static final String VOD_POS = "vod_pos2";
    public static final String PIN_CODE = "pin_code";
    public static final String OSD_TIME = "osd_time";
    public static final String IS_PHONE = "is_phone";

    public static final String EPG_OFFSET1 = "epg_offset1";
    public static final String EPG_OFFSET2 = "epg_offset2";
    public static final String EPG_OFFSET3 = "epg_offset3";
    public static final String INVISIBLE_LIVE_CATEGORIES0 = "invisible_vod_categories";
    public static final String INVISIBLE_VOD_CATEGORIES0 = "invisible_live_categories";
    public static final String INVISIBLE_SERIES_CATEGORIES0 = "invisible_series_categories";
    public static SimpleDateFormat epgFormat = new SimpleDateFormat("yyyyMMddHHmmss Z");
    public static String xxx_category_id ="-1";

    public static SimpleDateFormat stampFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat clockFormat=new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat clock12Format=new SimpleDateFormat("hh:mm");
    public static long SEVER_OFFSET;
    public static String all_id = "100";
    public static String fav_id = "200";
    public static String All="All";
    public static String Favorites="Favorites";
    public static final String CURRENT_PLAYER0 = "current_player";
    private static final String RECENT_CHANNELS="RECENT_CHANNELS";
    private static final String RECENT_MOVIES="RECENT_MOVIES";
    private static final String RECENT_SERIES="RECENT_SERIES";
    public static String  recent_id = "1000";
    public static String Recently_Viewed = "Recently Viewed";
    public static String SORT = "sort";

    public static SimpleDateFormat catchupFormat = new SimpleDateFormat("yyyy-MM-dd:HH-mm");

    public static int findNowEvent(List<EPGEvent> epgEvents){
        Date now = new Date();
        now.setTime(now.getTime()-SEVER_OFFSET);
        for (int i=0;i<epgEvents.size();i++){
            EPGEvent epgEvent=epgEvents.get(i);
            if (now.after(epgEvent.getStartTime()) && now.before(epgEvent.getEndTime())) {
                return i;
            }
        }
        return -1;
    }

    public static FullModel getFavFullModel(List<FullModel> fullModels){
        for (FullModel fullModel:fullModels){
            if (fullModel.getCategory_id()==Constants.fav_id)
                return fullModel;
        }
        return null;
    }

    public static String getSORT(){
        return SORT+MyApp.firstServer.getValue();
    }
    public static String getRecentChannels(){
        return RECENT_CHANNELS+MyApp.firstServer.getValue();
    }
    public static String getRecentMovies(){
        return RECENT_MOVIES+MyApp.firstServer.getValue();
    }

    public static String getCHANNEL_POS() {
        return CHANNEL_POS +MyApp.firstServer.getValue();
    }
    public static String getLoginInfo(){
        return LOGIN_INFO+MyApp.firstServer.getValue();
    }
    public static String getFavInfo(){
        return FAV_INFO+MyApp.firstServer.getValue();
    }
    public static String getMovieFav(){
        return MOVIE_FAV+MyApp.firstServer.getValue();
    }

    public static String getSubPos(){
        return SUB_POS+MyApp.firstServer.getValue();
    }

    public static String getVodPos(){
        return VOD_POS+MyApp.firstServer.getValue();
    }

    public static String getSeriesPos(){
        return SERIES_POS+MyApp.firstServer.getValue();
    }

    public static String getInvisibleLiveCategories(){
        return INVISIBLE_LIVE_CATEGORIES0+MyApp.firstServer.getValue();
    }

    public static String getInvisibleVodCategories(){
        return INVISIBLE_VOD_CATEGORIES0+MyApp.firstServer.getValue();
    }

    public static String getInvisibleSeriesCategories(){
        return INVISIBLE_SERIES_CATEGORIES0+MyApp.firstServer.getValue();
    }

    public static String getCurrentPlayer(){
        return CURRENT_PLAYER0+MyApp.firstServer.getValue();
    }

    public static String getRecentSeries(){
        return RECENT_SERIES+MyApp.firstServer.getValue();
    }

    public static FullModel getRecentFullModel(List<FullModel> fullModels){
        for (FullModel fullModel:fullModels){
            if (fullModel.getCategory_id().equals(Constants.recent_id))
                return fullModel;
        }
        return null;
    }

    public static List<String> getListStrFromListEpg(List<EPGChannel> epgChannels){
        List<String> stringList = new ArrayList<>();
        for (EPGChannel epgChannel:epgChannels){
            stringList.add(epgChannel.getName());
        }
        return stringList;
    }

    public static CategoryModel getAllCategory(List<CategoryModel> categoryModels){
        for (CategoryModel categoryModel:categoryModels){
            if (categoryModel.getId()==Constants.all_id)
                return categoryModel;
        }
        return null;
    }

    public static FullModel getAllFullModel(List<FullModel> fullModels){
        for (FullModel fullModel:fullModels){
            if (fullModel.getCategory_id().equals(Constants.all_id))
                return fullModel;
        }
        return null;
    }

    public static void getVodFilter() {
        List<Integer> selectedIds=(List<Integer>) MyApp.instance.getPreference().get(getINVISIBLE_VOD_CATEGORIES());
        MyApp.vod_categories_filter=new ArrayList<>();
        MyApp.vod_categories_filter.addAll(MyApp.vod_categories);
        if (selectedIds!=null && selectedIds.size()!=0) {
            for (CategoryModel categoryModel: MyApp.vod_categories){
                for (int string:selectedIds){
                    if (string == Integer.valueOf(categoryModel.getId()))
                        MyApp.vod_categories_filter.remove(categoryModel);
                }
            }
        }
    }

    public static void getLiveFilter() {
        List<Integer> selectedIds=(List<Integer>) MyApp.instance.getPreference().get(getINVISIBLE_LIVE_CATEGORIES());
        MyApp.live_categories_filter = new ArrayList<>();
        MyApp.live_categories_filter.addAll(MyApp.live_categories);
        MyApp.fullModels_filter=new ArrayList<>();
        MyApp.fullModels_filter.addAll(MyApp.fullModels);
        if (selectedIds!=null && selectedIds.size()!=0) {
            for (int i = 0; i< MyApp.live_categories.size(); i++){
                CategoryModel categoryModel= MyApp.live_categories.get(i);
                for (int string:selectedIds){
                    if (string == Integer.valueOf(categoryModel.getId())){
                        MyApp.live_categories_filter.remove(categoryModel);
                        MyApp.fullModels_filter.remove(MyApp.fullModels.get(i));
                    }
                }
            }
        }
        Log.e("live filter",MyApp.live_categories_filter.size()+"");
    }

    public static void getSeriesFilter() {
        List<Integer> selectedIds=(List<Integer>) MyApp.instance.getPreference().get(getINVISIBLE_SERIES_CATEGORIES());
        MyApp.series_categories_filter=new ArrayList<>();
        MyApp.series_categories_filter.addAll(MyApp.series_categories);
        if (selectedIds!=null && selectedIds.size()!=0) {
            for (CategoryModel categoryModel: MyApp.series_categories){
                for (int string:selectedIds){
                    if (string == Integer.valueOf(categoryModel.getId()))
                        MyApp.series_categories_filter.remove(categoryModel);
                }
            }
        }
    }
    private static final String INVISIBLE_VOD_CATEGORIES = "invisible_vod_categories";
    private static final String INVISIBLE_LIVE_CATEGORIES = "invisible_live_categories";
    private static final String INVISIBLE_SERIES_CATEGORIES = "invisible_series_categories";

    public static String getINVISIBLE_SERIES_CATEGORIES() {
        return INVISIBLE_SERIES_CATEGORIES+MyApp.firstServer.getValue();
    }

    public static String getINVISIBLE_LIVE_CATEGORIES() {
        return INVISIBLE_LIVE_CATEGORIES+MyApp.firstServer.getValue();
    }

    public static String getINVISIBLE_VOD_CATEGORIES() {
        return INVISIBLE_VOD_CATEGORIES+MyApp.firstServer.getValue();
    }

    public static CategoryModel getRecentCatetory(List<CategoryModel> categoryModels){
        for (CategoryModel categoryModel:categoryModels){
            if (categoryModel.getId().equals(Constants.recent_id))
                return categoryModel;
        }
        return null;
    }

    public static void setServerTimeOffset(String my_timestamp,String server_timestamp) {
        Log.e("server_timestamp",server_timestamp);
        try {
            long my_time= Long.parseLong(my_timestamp);
            Date date_server= stampFormat.parse(server_timestamp);
            my_time=my_time*1000;
            SEVER_OFFSET=my_time-date_server.getTime();
            Log.e("offset",String.valueOf(SEVER_OFFSET));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String Offset(boolean is_12,String string){
        try {
            Date that_date=stampFormat.parse(string);
            that_date.setTime(that_date.getTime()+ Constants.SEVER_OFFSET);
            if (is_12)return clock12Format.format(that_date);
            else return clockFormat.format(that_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String Offset(boolean is_12,Date string){
        string.setTime(string.getTime()+ Constants.SEVER_OFFSET);
        if (is_12)return clock12Format.format(string);
        else return clockFormat.format(string);
    }

    public static String GetBaseURL(Context mcontext)
    {
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        String base_url=serveripdetails.getString("ip","");
        base_url=base_url+"player_api.php?";
        return  base_url;
    }

    public static String GetAppDomain(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("ip","");
        return  base_url;
    }

    public static String GetIcon(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("i","");
        return  app_icon;
    }

    public static String GetLoginImage(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("l","");
        return  app_icon;
    }

    public static String GetMainImage(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("m","");
        return  app_icon;
    }

    public static String GetAd1(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("d1","");
        return  base_url;
    }

    public static String GetAd2(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("d2","");
        return  base_url;
    }
    public static String GetAutho1(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("autho1","");
        return  app_icon;
    }

    public static String GetPin4(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("four_way_screen","");
        return  app_icon;
    }

    public static String GetPin3(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("tri_screen","");
        return  app_icon;
    }

    public static String GetPin2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("dual_screen","");
        return  app_icon;
    }
    public static String GetAutho2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("autho2","");
        return  app_icon;
    }
    public static String GetList(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("list","");
        return  app_icon;
    }
    public static String GetUrl3(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("url3","");
        return  app_icon;
    }
    public static String GetUrl2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("url2","");
        return  app_icon;
    }
    public static String GetUrl1(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("url1","");
        return  app_icon;
    }
    public static String GetUrl(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("url","");
        return  app_icon;
    }

    public static String GetKey(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("key","");
        return  app_icon;
    }

    public static String Get0(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a0","");
        return  app_icon;
    }
    public static String Get1(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a1","");
        return  app_icon;
    }
    public static String Get2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a2","");
        return  app_icon;
    }
    public static String Get3(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a3","");
        return  app_icon;
    }

    public static String Get4(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a4","");
        return  app_icon;
    }
    public static String Get5(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a5","");
        return  app_icon;
    }
    public static String Get6(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a6","");
        return  app_icon;
    }
    public static String Get7(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("a7","");
        return  app_icon;
    }

    public static String GetCurrentTimeByTimeZone(String pattern,String time_zone) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone(time_zone)); //
        return df.format(c.getTime());
    }

    public static String GetCurrentDateByTimeZone(String pattern,String time_zone) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone(time_zone)); //
        return df.format(c.getTime());
    }


    public static String GetCurrentTime(String pattern) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(c.getTime());
    }

    public static String GetCurrentDate(String pattern) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(c.getTime());
    }

    public static long getTimeDiffMinutes(String start_time, String end_time, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date d1 = null;
        Date d2 = null;
        long diffMinutes = 0;
        try {
            d1 = format.parse(start_time);
            d2 = format.parse(end_time);

            long diff = d2.getTime() - d1.getTime();
            diffMinutes = diff / (60 * 1000);
            return diffMinutes;

        } catch (Exception e) {
            e.printStackTrace();
            return diffMinutes;
        }
    }

    public static boolean checktimings(String current_time, String endtime, String pattern) {

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

    public static String GetDecodedString(String text) {
        return text;
    }
}
