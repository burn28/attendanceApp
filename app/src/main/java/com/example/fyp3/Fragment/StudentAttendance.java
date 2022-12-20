package com.example.fyp3.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.media.MediaRecorder.VideoSource.CAMERA;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.fyp3.Adapter.StudentAttendanceAdp;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.example.fyp3.StudentActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.ImagePickerActivity;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentAttendance extends Fragment {

    private List<String> courses;
    private RecyclerView recyclerView;
    private List<StudentClass> classList;
    private List<StudentClass> doneList;
    RecyclerView.LayoutManager linearLayoutManager;
    private StudentAttendanceAdp attendanceAdp;
    String day;
    String week;
    private TextView weekText;
    private String time;
    private static final String CHANNEL_ID = "com.example.fyp3.channel_1";
    public String currentPhotoPath;
    Uri imageUri;
    Context fContext;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);
        week = pref.getString("week", "1");
        String dWeek = "Week " + week;
        weekText = view.findViewById(R.id.textWeek);
        weekText.setText(dWeek);

        SharedPreferences pref2 = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref2.edit();
        editor.clear();
        editor.apply();


        courses = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);


        classList = new ArrayList<>();
        attendanceAdp = new StudentAttendanceAdp(getContext(), classList, StudentAttendance.this);
        recyclerView.setAdapter(attendanceAdp);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
//        day = "Monday";
        time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
//        Toast.makeText(getContext(), time, Toast.LENGTH_SHORT).show();
//        day = "Wednesday";


        showList();

//        popUpNotification();

        return view;
    }

    private void showList() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Enrolment");
        query.whereEqualTo("studentId", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    courses.clear();
                    classList.clear();
                    for (ParseObject obj : objects) {
                        courses.add(obj.getString("courseId"));
                    }
                    for (String course : courses) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("course");
                        query.whereEqualTo("courseId", course);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject obj : objects) {
                                        if (obj.getString("day").equals(day)) {
                                            StudentClass mClass = new StudentClass();
                                            mClass.setCourseId(course);
                                            mClass.setTitle(obj.getString("title"));
                                            mClass.setDay(day);
                                            mClass.setStartTime(obj.getString("startTime"));
                                            classList.add(mClass);
//                                            Toast.makeText(getContext(), course, Toast.LENGTH_SHORT).show();
                                        }

                                        attendanceAdp.notifyDataSetChanged();
                                    }
                                    //check ticked attendance and remove
                                    SharedPreferences pref = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    Gson gson = new Gson();
                                    String json = pref.getString("done_class", null);
                                    Type type = new TypeToken<ArrayList<StudentClass>>() {
                                    }.getType();

//                                    editor.remove("done_class");
//                                    editor.apply();
                                    doneList = gson.fromJson(json, type);
                                    if (doneList == null) {
                                        doneList = new ArrayList<>();
                                        attendanceAdp.notifyDataSetChanged();
//                                        popUpNotification();
                                    } else if (!doneList.isEmpty() && !doneList.get(0).getDay().equals(day)) {
                                        editor.remove("done_class");
                                        editor.apply();
                                        doneList = null;
//                                        Toast.makeText(getContext(), "DELETE", Toast.LENGTH_SHORT).show();
                                    } else {
                                        for (int i = 0; i < classList.size(); i++) {
                                            for (int j = 0; j < doneList.size(); j++) {
                                                if (classList.get(i).getCourseId().equals(doneList.get(j).getCourseId())) {
//                                                    Toast.makeText(getContext(), doneList.get(j).getCourseId(), Toast.LENGTH_SHORT).show();
                                                    classList.remove(i);
                                                    attendanceAdp.notifyDataSetChanged();
//                                                    popUpNotification();
                                                    if (classList.size() == 0) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
//                                        Toast.makeText(getContext(), doneList.get(0).getCourseId(), Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getContext(), doneList.get(0).getDay(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("QUERY2", "Error: " + e.getMessage());
                                }
                            }
                        });
//                        popUpNotification();
                    }

                } else {
                    Log.d("QUERY1", "Error: " + e.getMessage());
                }
            }
        });


    }

    public void recordAttendance(byte[] imageByte) {
        SharedPreferences pref = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String action = pref.getString("record", null);
        String courseId = pref.getString("courseId", null);
        String courseTitle = pref.getString("courseTitle", null);
        String start = pref.getString("startTime", null);


        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date currentTime = null;
        Date startTime = null;
        try {
            currentTime = format.parse(time);
            startTime = format.parse(start);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
//        Log.d("END", "D:"+currentTime);
        long mills = currentTime.getTime() - startTime.getTime();
        int diff = (int) (mills / (1000 * 60 * 60));

        if (diff < 0) {
            Toast.makeText(getContext(), "Class has not started yet!", Toast.LENGTH_SHORT).show();
            Log.e("NOTI", "start: " + startTime + " current:" + currentTime);
            Log.e("NOTI", "diff" + diff);
        } else {
            Gson gson = new Gson();
            StudentClass Class = new StudentClass();
            Class.setCourseId(courseId);
            Class.setTitle(courseTitle);
            Class.setDay(day);
            Class.setStartTime(start);
            doneList.add(Class);
            String json = gson.toJson(doneList);
            editor.putString("done_class", json);
            editor.apply();

            if (action.equals("absent")) {
                ParseFile file = new ParseFile(imageByte);
                ParseObject parseObject = new ParseObject(courseId);
                parseObject.put("studentId", ParseUser.getCurrentUser().getUsername());
                parseObject.put("week", week);
                parseObject.put("evidence", file);
                parseObject.saveInBackground();
            }
            showList();
//        for(int i=0; i<classList.size(); i++){
//            if(classList.get(i).getCourseId().equals(courseId)){
//                classList.remove(i);
//                attendanceAdp.notifyDataSetChanged();
//            }
//        }
        }
    }

    //Android native to takePicture
    public void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Pair<Uri, String> p = createImage();
        Log.e("PIC","error");
        Uri finalUri = p.first;
        String imageFileName = p.second;
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, finalUri);
//        mGetContent.launch(takePictureIntent);
        startActivityForResult(takePictureIntent, CAMERA);
        String imagePath = "Pictures/Student Attendance/"+imageFileName;
    }

    //takePicture with external lib
    public void pickFromGallery() {
        ImagePicker.with(this)
                .cropSquare()    			//Crop image(Optional), Check Customization for more option
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(720, 720)//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    //save img file and return imagePath
    public Pair<Uri, String> createImage(){
        Uri uri = null;
        ContentResolver resolver = getContext().getContentResolver();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Log.d("URI", "first");
        }else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Log.d("URI", "second");
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName+".jpg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/"+"Student Attendance/");
        Uri finalUri = resolver.insert(uri, contentValues);
//        Uri finalUri = resolver.insert(nUri, contentValues);
        String imagePath = "/storage/emulated/0/"+"Pictures/Student Attendance/"+imageFileName+".jpg";
        imageUri = finalUri;

        return new Pair<>(finalUri, imagePath);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA){
            if(resultCode == RESULT_OK){
                Toast.makeText(getContext(), "ADDED", Toast.LENGTH_SHORT).show();
                Pair<Uri, String> p = createImage();
                recordAttendance(getBytes(p.second));

            }
        }
//        else if(requestCode == 2404){
//            if(resultCode == RESULT_OK){
//                Toast.makeText(getContext(), "ADDED ADDED", Toast.LENGTH_SHORT).show();
//                Pair<Uri, String> p = createImage(data.getData());
//                Log.e("image", p.second);
////                recordAttendance(getBytes(p.second));
//            }
//        }else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
//        }
    }

    //get image byte[] to save into cloud db
    public byte[] getBytes(String imagePath){
//        //Only decode image size. Not whole image
//        BitmapFactory.Options option = new BitmapFactory.Options();
//        option.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(imagePath, option);
//
//        // Minimum width and height are > NEW_SIZE (e.g. 380 * 720)
//        final int NEW_SIZE = 480;
//
//        //Now we have image width and height. We should find the correct scale value. (power of 2)
//        int width = option.outWidth;
//        int height = option.outHeight;
//        int scale = 1;
//        while (width / 2 > NEW_SIZE || height / 2 > NEW_SIZE) {
//            width /= 2;//  ww w . j  a  va  2  s.co  m
//            height /= 2;
//            scale++;
//        }
//        //Decode again with inSampleSize
//        option = new BitmapFactory.Options();
//        option.inSampleSize = scale;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        bitmap.recycle();

        return stream.toByteArray();
    }



    public void popUpNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_power)
                .setContentTitle("Student Attendance")
                .setContentText("There is upcoming class")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 8);

        Intent intent = new Intent(getActivity(), StudentActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        if (!classList.isEmpty()) {
            Log.e("NOTI", "popUpNotification: 1st");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date currentTime = null;
            Date startTime = null;
            Date test = null;
            for (StudentClass obj : classList) {
                String start = obj.getStartTime();
                try {
                    currentTime = format.parse(time);
                    startTime = format.parse(start);
                    test = format.parse("22:49");
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
//        Log.d("END", "D:"+currentTime);
//                long mills = startTime.getTime() - currentTime.getTime();
                long mills = test.getTime() - currentTime.getTime();
                int diff = (int) (mills / (1000 * 60));
                Log.e("NOTI", "popUpNotification: " + diff);
                if (diff > 4 && diff < 7) {
                    notificationManager.notify(200, builder.build());
//                    Log.e("NOTI", "test: "+test + " current:"+currentTime);
                }
            }


        }
    }

}
