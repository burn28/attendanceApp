package com.example.fyp3.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Fragment.StudentAttendance;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.google.gson.Gson;

import java.util.List;

public class StudentAttendanceAdp extends RecyclerView.Adapter<StudentAttendanceAdp.ViewHolder> {

    public Context mContext;
    public List<StudentClass> mAttendanceList;
    public StudentAttendance fragment;

    public StudentAttendanceAdp(Context mContext, List<StudentClass> mAttendanceList, StudentAttendance fragment) {
        this.mContext = mContext;
        this.mAttendanceList = mAttendanceList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public StudentAttendanceAdp.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.student_attendance_item, parent, false);
        return new StudentAttendanceAdp.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAttendanceAdp.ViewHolder holder, int position) {
        StudentClass mClass = mAttendanceList.get(position);
        holder.courseId.setText(mClass.getCourseId());
        holder.courseTitle.setText(mClass.getTitle());
        holder.presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit();
                editor.putString("courseId", mClass.getCourseId());
                editor.putString("courseTitle", mClass.getTitle());
                editor.putString("record","present");
                editor.putString("startTime",mClass.getStartTime());
                editor.apply();
                byte[] data = null;
                fragment.loadingDialog.startLoadingDialog(fragment.getLayoutInflater());
                fragment.getLocation();
//                fragment.recordAttendance(data);
            }
        });
        holder.absentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit();
                editor.putString("courseId", mClass.getCourseId());
                editor.putString("courseTitle", mClass.getTitle());
                editor.putString("record","absent");
                editor.putString("startTime",mClass.getStartTime());
                editor.apply();
                byte[] data = null;
//                fragment.chooseFile();
                fragment.showOptDialog();
//                fragment.recordAttendance(data);
//                fragment.takePicture();
//                fragment.pickFromGallery();
//                fragment.dispatchTakePictureIntent();
            }
        });


    }

//    private void recordAttendance(){
//        SharedPreferences pref = mContext.getSharedPreferences("DATA", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        String action = pref.getString("record",null);
//        String courseId = pref.getString("courseId", null);
//        if(action.equals("present")){
//
//            Gson gson = new Gson();
//            StudentClass attendance = new StudentClass();
//            attendance.setCourseId(courseId);
//        }
//    }



    @Override
    public int getItemCount() {
        return mAttendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
