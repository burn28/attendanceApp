package com.example.fyp3.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Fragment.LecturerAttendance;
import com.example.fyp3.Model.LecturerClass;
import com.example.fyp3.R;

import java.util.List;

public class LecturerClassAdp extends RecyclerView.Adapter<LecturerClassAdp.ViewHolder> {

    public Context mContext;
    public List<LecturerClass> mClassList;

    public LecturerClassAdp(Context mContext, List<LecturerClass> mClassList){
        this.mContext = mContext;
        this.mClassList = mClassList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lecturer_class_item,parent,false);
        return new LecturerClassAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LecturerClass Class = mClassList.get(position);
        holder.title.setText(Class.getCourseId()+" " +Class.getTitle());
        holder.day.setText(Class.getDay());
        holder.time.setText(Class.getStartTime()+" - "+Class.getEndTime());
        holder.location.setText(Class.getLocation());
        if(Class.getType().equals("lab")){
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.green));
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("courseId",Class.getCourseId());
                editor.putString("title",Class.getCourseId()+" " +Class.getTitle());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new LecturerAttendance())
                        .addToBackStack("attendance")
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title,day,time,location;
        public LinearLayout linearLayout;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview);
            linearLayout = itemView.findViewById(R.id.classLinear);
            title = itemView.findViewById(R.id.classTitle);
            day = itemView.findViewById(R.id.classDay);
            time = itemView.findViewById(R.id.classTime);
            location = itemView.findViewById(R.id.classLocation);

        }
    }
}
