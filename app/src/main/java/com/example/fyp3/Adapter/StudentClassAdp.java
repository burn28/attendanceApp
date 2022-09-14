package com.example.fyp3.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Fragment.LecturerAttendance;
import com.example.fyp3.Model.LecturerClass;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;

import java.util.List;

public class StudentClassAdp extends RecyclerView.Adapter<StudentClassAdp.ViewHolder>{

    public Context mContext;
    public List<StudentClass> mCourseList;

    public StudentClassAdp(Context mContext, List<StudentClass> mCourseList){
        this.mContext = mContext;
        this.mCourseList = mCourseList;
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
//        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
//                editor.putString("courseId",Class.getCourseId());
//                editor.apply();
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                                new LecturerAttendance())
//                        .addToBackStack("profile")
//                        .commit();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title,percent;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.classTitle);
            percent = itemView.findViewById(R.id.percent);
            linearLayout = itemView.findViewById(R.id.classLinear);

        }
    }
}