package com.example.fyp3.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Fragment.AbsentDetails;
import com.example.fyp3.Fragment.Evidence;
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
        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.anim_fall_down));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PROOF", Context.MODE_PRIVATE).edit();
                editor.putString("week", week);
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new Evidence())
                        .addToBackStack("evidence")
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mWeekList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textWeek;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textWeek = itemView.findViewById(R.id.textWeek);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
