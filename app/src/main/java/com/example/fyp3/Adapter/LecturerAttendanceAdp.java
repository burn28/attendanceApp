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
import com.example.fyp3.R;

import java.util.List;

public class LecturerAttendanceAdp extends RecyclerView.Adapter<LecturerAttendanceAdp.ViewHolder>{

    public Context mContext;
    public List<AttendanceClass> mAttendanceList;
    View view;

    public LecturerAttendanceAdp (Context mContext, List<AttendanceClass> mAttendanceList){
        this.mContext = mContext;
        this.mAttendanceList = mAttendanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.lecturer_attendance_item,parent,false);
        return new LecturerAttendanceAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LecturerAttendanceAdp.ViewHolder holder, int position) {
        AttendanceClass attendance = mAttendanceList.get(position);
        holder.matrix.setText(attendance.getStudentId());
        holder.name.setText(attendance.getStudentName());
        if(attendance.getStatus().equals("absent")){
//            Drawable backgroundOff = view.getBackground(); //v is a view
//            backgroundOff.setTint(Color.parseColor("#F35555")); //defaultColor is an int
//            view.setBackground(backgroundOff);
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.red));
        }else{
//            Drawable backgroundOff = view.getBackground(); //v is a view
//            backgroundOff.setTint(Color.parseColor("#30C77B")); //defaultColor is an int
//            view.setBackground(backgroundOff);
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.green));
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
