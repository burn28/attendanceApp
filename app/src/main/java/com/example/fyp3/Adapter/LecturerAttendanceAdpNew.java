package com.example.fyp3.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Model.AttendanceClass;
import com.example.fyp3.Model.AttendanceClassNew;
import com.example.fyp3.R;

import java.util.List;

public class LecturerAttendanceAdpNew extends RecyclerView.Adapter<LecturerAttendanceAdpNew.ViewHolder>{

    public Context mContext;
    public List<AttendanceClassNew> mAttendanceList;
    View view;
    public String week;

    public LecturerAttendanceAdpNew (Context mContext, List<AttendanceClassNew> mAttendanceList, String week){
        this.mContext = mContext;
        this.mAttendanceList = mAttendanceList;
        this.week = week;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.lecturer_attendance_item,parent,false);
        return new LecturerAttendanceAdpNew.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LecturerAttendanceAdpNew.ViewHolder holder, int position) {
        AttendanceClassNew attendance = mAttendanceList.get(position);
        holder.matrix.setText(attendance.getStudentId());
        holder.name.setText(attendance.getStudentName());

        if(attendance.getStatus(week)!= null && attendance.getStatus(week).equals("no record")){
//            Drawable backgroundOff = view.getBackground(); //v is a view
//            backgroundOff.setTint(Color.parseColor("#F35555")); //defaultColor is an int
//            view.setBackground(backgroundOff);
            holder.cardView.setCardBackgroundColor(Color.rgb(240, 167, 20));

        }else if(attendance.getStatus(week)!= null && attendance.getStatus(week).equals("present")){
//            Drawable backgroundOff = view.getBackground(); //v is a view
//            backgroundOff.setTint(Color.parseColor("#30C77B")); //defaultColor is an int
//            view.setBackground(backgroundOff);
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.green));
        }else {
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.red));
        }

    }

    @Override
    public int getItemCount() {
        return mAttendanceList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView matrix,name;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview);
            matrix = itemView.findViewById(R.id.matrix);
            name = itemView.findViewById(R.id.name);
        }
    }
}
