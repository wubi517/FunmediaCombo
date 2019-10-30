package com.gold.kds517.funmedia_new.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.gold.kds517.funmedia_new.R;
import com.gold.kds517.funmedia_new.apps.Constants;
import com.gold.kds517.funmedia_new.models.EPGChannel;
import com.gold.kds517.funmedia_new.models.EPGEvent;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function4;

public class EpgAdapter extends RecyclerView.Adapter<EpgAdapter.HomeListViewHolder> {
    private List<EPGChannel> list ;
    private Context context;
    private Function4<Integer,Integer, EPGChannel, EPGEvent, Unit> onClickListener;
    private Function4<Integer, Integer, EPGChannel, EPGEvent, Unit> onFocusListener;
    private int channelPos=-1;
    private boolean is_header_focused = true;
    private RecyclerView recyclerView;
    public EpgAdapter(RecyclerView recyclerView,List<EPGChannel> list, Context context, Function4<Integer, Integer, EPGChannel, EPGEvent, Unit> onClickListener,
                      Function4<Integer, Integer, EPGChannel, EPGEvent, Unit> onFocusListener) {
        this.recyclerView = recyclerView;
        this.list = list;
        this.context = context;
        this.onClickListener = onClickListener;
        this.onFocusListener = onFocusListener;
    }

    public void setChannelPos(int channelPos){
        this.channelPos=channelPos;
        notifyItemChanged(channelPos);
    }
    public void setList(List<EPGChannel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public boolean getIs_Header_focused(){
        return is_header_focused;
    }
    @NonNull
    @Override
    public HomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.epg_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeListViewHolder holder, final int position) {
        final EPGChannel epgChannel = list.get(position);
        if (epgChannel.getStream_icon()!=null && !epgChannel.getStream_icon().equals("")) {
            Picasso.with(holder.itemView.getContext()).load(epgChannel.getStream_icon())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.ad1)
                    .into(holder.image);
            Log.e("url",epgChannel.getStream_icon());
        }
        holder.programs_recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
        holder.programs_recyclerview.setAdapter(new EpgProgramsListAdapter(epgChannel.getEvents(), (integer, epgEvent) -> {
            //onClickListener
            onClickListener.invoke(position, integer, epgChannel, epgEvent);
            return null;
        }, (integer, epgEvent) -> {
            //onFocusListener
            onFocusListener.invoke(position, integer, epgChannel, epgEvent);
//            Log.e("epgadapter","program focused "+position+" "+epgEvent.getTitle());
//            recyclerView.post(()-> checkLastPosition(position));
            is_header_focused = false;
            return null;
        }));
        holder.image.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                holder.image.setBackgroundColor(Color.parseColor("#2962FF"));
                onFocusListener.invoke(position, -1, epgChannel, null);
                Log.e("epgadapter","header focused "+position);
//                recyclerView.post(()-> checkLastPosition(position));
                is_header_focused = true;
            }else{
                holder.image.setBackgroundColor(Color.parseColor("#ababab"));
            }
        });
        holder.image.setOnClickListener(v -> onClickListener.invoke(position, -1, epgChannel, null));
        if (channelPos==position) {
            holder.image.requestFocus();
            channelPos=-1;
        }else {
            int now_i= Constants.findNowEvent(epgChannel.getEvents());
            if (now_i!=-1) holder.programs_recyclerview.scrollToPosition(now_i);
        }
    }

    private void checkLastPosition(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastPos = layoutManager.findLastVisibleItemPosition();
                if (position==lastPos-1) {
                    recyclerView.smoothScrollToPosition(lastPos+1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeListViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        RecyclerView programs_recyclerview;
        public HomeListViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            programs_recyclerview= itemView.findViewById(R.id.programs_recyclerview);
        }
    }
}