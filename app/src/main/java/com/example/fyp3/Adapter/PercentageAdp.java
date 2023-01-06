package com.example.fyp3.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Fragment.AbsentDetails;
import com.example.fyp3.Fragment.LecturerAttendance;
import com.example.fyp3.Model.AttendanceClass;
import com.example.fyp3.Model.AttendanceClassNew;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PercentageAdp extends RecyclerView.Adapter<PercentageAdp.ViewHolder> {

    public Context mContext;
    public List<AttendanceClass> mPercentList;
    public List<AttendanceClassNew> mPercentList2;

    View view;

    public PercentageAdp(Context mContext, List<AttendanceClassNew> mPercentList2) {
        this.mContext = mContext;
        this.mPercentList2 = mPercentList2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.lecturer_percentage_item, parent, false);
        return new PercentageAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        AttendanceClassNew student = mPercentList2.get(position);

        holder.name.setText(student.getStudentName());
        holder.id.setText(student.getStudentId());
        holder.percent.setText(student.getPercentage() + "%");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                List<String> absentWeek = new ArrayList<>();
                absentWeek = student.getWeeks();
                String json = gson.toJson(absentWeek);

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("studentId", student.getStudentId());
                editor.putString("studentName", student.getStudentName());
                editor.putInt("percentage", student.getPercentage());
                editor.putString("weeks", json);
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new AbsentDetails())
                        .addToBackStack("details")
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPercentList2.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView name, id, percent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview);
            name = itemView.findViewById(R.id.studentName);
            id = itemView.findViewById(R.id.studentId);
            percent = itemView.findViewById(R.id.percentage);
        }
    }
}
