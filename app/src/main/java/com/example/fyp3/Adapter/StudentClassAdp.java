package com.example.fyp3.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Fragment.AbsentDetails;
import com.example.fyp3.Fragment.LecturerAttendance;
import com.example.fyp3.Model.LecturerClass;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class StudentClassAdp extends RecyclerView.Adapter<StudentClassAdp.ViewHolder>{

    public Context mContext;
    public List<StudentClass> mCourseList;
    public List<String> colorList;

    public StudentClassAdp(Context mContext, List<StudentClass> mCourseList){
        this.mContext = mContext;
        this.mCourseList = mCourseList;
        colorList = new ArrayList<>();
        colorList.add("#4378DB");
        colorList.add("#30C77B");
        colorList.add("#D54949");
        colorList.add("#F2BF40");
        colorList.add("#4378DB");
        colorList.add("#D54949");
        colorList.add("#30C77B");
        colorList.add("#FDCD55");
    }

    public void setFilteredList(List<StudentClass> filteredList){
        this.mCourseList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentClassAdp.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.student_course_item,parent,false);
        return new StudentClassAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentClassAdp.ViewHolder holder, int position) {
        StudentClass Class = mCourseList.get(position);
        holder.title.setText(Class.getCourseId()+" "+Class.getTitle());
        holder.percent.setText(Class.getPercentage() + "%");
        holder.progressBar.setProgress(Class.getPercentage());
        holder.cardView.setCardBackgroundColor(Color.parseColor(colorList.get(position)));
//        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.anim_fall_down));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("courseId",Class.getCourseId());
                editor.putString("title",Class.getCourseId()+" "+Class.getTitle());
                editor.putInt("percentage", Class.getPercentage());

                Gson gson = new Gson();
                List<String> weekList = new ArrayList<String>(Class.getWeeks());
                String jsonText = gson.toJson(weekList);
                editor.putString("weeks", jsonText);
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new AbsentDetails())
                        .addToBackStack("details")
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title,percent;
        public LinearLayout linearLayout;
        public ProgressBar progressBar;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.classTitle);
            percent = itemView.findViewById(R.id.percent);
            linearLayout = itemView.findViewById(R.id.classLinear);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progressBar.setMax(100);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
