package com.example.fyp3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;

import java.util.List;

public class StudentAttendanceAdp extends RecyclerView.Adapter<StudentAttendanceAdp.ViewHolder> {

    public Context mContext;
    public List<StudentClass> mAttendanceList;

    public StudentAttendanceAdp(Context mContext, List<StudentClass> mAttendanceList) {
        this.mContext = mContext;
        this.mAttendanceList = mAttendanceList;
    }

    @NonNull
    @Override
    public StudentAttendanceAdp.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.student_attendance_item,parent,false);
        return new StudentAttendanceAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAttendanceAdp.ViewHolder holder, int position) {
        StudentClass mClass = mAttendanceList.get(position);
        holder.courseId.setText(mClass.getCourseId());
        holder.courseTitle.setText(mClass.getTitle());


    }

    @Override
    public int getItemCount() {
        return mAttendanceList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView courseId, courseTitle;
        public ImageButton presentBtn, absentBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            courseId = itemView.findViewById(R.id.courseId);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            presentBtn = itemView.findViewById(R.id.presentBtn);
            absentBtn = itemView.findViewById(R.id.absentBtn);

        }
    }


}
