package com.example.fyp3.Adapter;

import android.content.Context;
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

public class PercentageAdp extends RecyclerView.Adapter<PercentageAdp.ViewHolder>{

    public Context mContext;
    public List<AttendanceClass> mPercentList;
    View view;

    public PercentageAdp(Context mContext, List<AttendanceClass> mPercentList){
        this.mContext = mContext;
        this.mPercentList = mPercentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.lecturer_percentage_item,parent,false);
        return new PercentageAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceClass student = mPercentList.get(position);
        holder.name.setText(student.getStudentName());
        holder.id.setText(student.getStudentId());
        holder.percent.setText(student.getPercentage()+"%");

    }

    @Override
    public int getItemCount() {
        return mPercentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView name,id,percent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview);
            name = itemView.findViewById(R.id.studentName);
            id = itemView.findViewById(R.id.studentId);
            percent = itemView.findViewById(R.id.percentage);
        }
    }
}
