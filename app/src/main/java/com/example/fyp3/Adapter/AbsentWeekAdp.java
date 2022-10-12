package com.example.fyp3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Model.LecturerClass;
import com.example.fyp3.R;

import java.util.List;

public class AbsentWeekAdp extends RecyclerView.Adapter<AbsentWeekAdp.ViewHolder>{

    public Context mContext;
    public List<String> mWeekList;

    public AbsentWeekAdp(Context mContext, List<String> mWeekList){
        this.mContext = mContext;
        this.mWeekList = mWeekList;
    }

    @NonNull
    @Override
    public AbsentWeekAdp.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.attendance_week_item,parent,false);
        return new AbsentWeekAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String week = mWeekList.get(position);
        holder.textWeek.append(week);
    }

    @Override
    public int getItemCount() {
        return mWeekList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textWeek;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textWeek = itemView.findViewById(R.id.textWeek);
        }
    }
}
