package com.example.fyp3.Fragment;

import com.example.fyp3.FileUtil;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static android.view.View.GONE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atwa.filepicker.core.FilePicker;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.fyp3.Adapter.StudentAttendanceAdp;

import com.example.fyp3.LoadingDialog;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.example.fyp3.StudentActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.ImagePickerActivity;
import com.github.dhaval2404.imagepicker.constant.ImageProvider;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
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

    File finalFile;
    Button confirmBtn;
    String selectedOpt;
    String reason;

    public static final int LOCATION_REQUEST_CODE = 100;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    public LoadingDialog loadingDialog;


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
//        editor.clear();
//        editor.apply();

        confirmBtn = view.findViewById(R.id.confirm_button);
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
//        day = "Thursday";
        time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
//        Toast.makeText(getContext(), time, Toast.LENGTH_SHORT).show();
//        day = "Wednesday";

        loadingDialog = new LoadingDialog(getActivity());
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = new byte[0];
                try {
                    bytes = Files.readAllBytes(finalFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Confirm", e.getMessage());
                }
                Log.e("Bytes", String.valueOf(bytes.length));
                recordAttendance(bytes);
            }
        });
        showList();
//        popUpNotification();
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        turnOnGPS();
//        showOptDialog();
//        getLocation();
        return view;
    }

    private void showList() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Enrollment");
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
                                            mClass.setEndTime(obj.getString("endTime"));
                                            mClass.setLocation(obj.getString("location"));

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

    public void recordAttendance(byte[] fileByte) {
        SharedPreferences pref = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String action = pref.getString("record", null);
        String courseId = pref.getString("courseId", null);
        String courseTitle = pref.getString("title", null);
        String start = pref.getString("startTime", null);
        String end = pref.getString("endTime", null);


        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date currentTime = null;
        Date startTime = null;
        Date endTime = null;

        try {
            currentTime = format.parse(time);
            startTime = format.parse(start);
            endTime = format.parse(end);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
//        Log.d("END", "D:"+currentTime);
        long mills = currentTime.getTime() - startTime.getTime();
        int diffStart = (int) (mills / (1000 * 60 * 60));

        long mills2 = currentTime.getTime() - endTime.getTime();
        int diffEnd = (int) (mills2 / (1000 * 60 * 60));


        if (diffStart < 0) {
            Toast.makeText(getContext(), "Class has not started yet!", Toast.LENGTH_SHORT).show();
            Log.e("NOTI", "start: " + startTime + " current:" + currentTime);
            Log.e("NOTI", "diff" + diffStart);
        }
        else if (diffEnd > 0) {
            Toast.makeText(getContext(), "Class was finished!", Toast.LENGTH_SHORT).show();
            Log.e("NOTI", "end: " + endTime + " current:" + currentTime);
            Log.e("NOTI", "diff" + diffEnd);
        }
        else {
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

            String studentId = ParseUser.getCurrentUser().getUsername();

//            if (action.equals("absent")) {
//                Log.e("CourseId", courseId);
//                Log.e("student", ParseUser.getCurrentUser().getUsername());
//                Log.e("week", week);
//
//
//                ParseObject parseObject = new ParseObject(courseId);
//                parseObject.put("studentId", ParseUser.getCurrentUser().getUsername());
//                parseObject.put("week", week);
//                if (reason != null) {
//                    parseObject.put("reason", reason);
//                    parseObject.saveInBackground();
//                    Toast.makeText(getContext(), "Record save successfully.", Toast.LENGTH_SHORT).show();
//                } else if (fileByte.length != 0) {
//                    ParseFile file = new ParseFile(finalFile.getName(), fileByte);
//                    file.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                parseObject.put("evidence", file);
//                                parseObject.saveInBackground(new SaveCallback() {
//                                    @Override
//                                    public void done(ParseException e) {
//                                        if (e == null) {
//                                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//                                            confirmBtn.setVisibility(GONE);
//                                        } else {
//                                            Log.e("parse", e.getMessage());
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                            } else {
//                                Log.e("parseFile", e.getMessage());
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
            query.whereEqualTo("studentId", studentId);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (action.equals("present")) {
                        object.put("week" + week, "present");
                        object.saveInBackground();
                    } else {
                        if (reason != null) {
                            ParseObject parseObject = new ParseObject("reason");
                            parseObject.put("sentence", reason);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        String objectId = parseObject.getObjectId();
                                        object.put("week" + week, objectId);
                                        object.saveInBackground();
                                        Toast.makeText(getContext(), "Record save successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } else if (fileByte.length != 0) {
                            String extension = FilenameUtils.getExtension(finalFile.getName());
                            Log.e("file", extension);
                            ParseFile file = new ParseFile(ParseUser.getCurrentUser().getUsername() + "." + extension, fileByte);
                            file.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseObject evidence = new ParseObject("reason");
                                        evidence.put("document", file);
                                        evidence.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    String objectId = evidence.getObjectId();
                                                    object.put("week" + week, objectId);
                                                    object.saveInBackground();
                                                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                                    confirmBtn.setVisibility(GONE);
                                                } else {
                                                    Log.e("parse", e.getMessage());
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("parseFile", e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                }
            });
//            else if(action.equals("present")){
//
//            }
            showList();
        }
    }

    public void askLocationPermission(String classLocation) {
        ActivityCompat.requestPermissions((Activity) getContext(), new String[]
                {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(classLocation);
        }

    }

    public void turnOnGPS() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(3000).build();
        LocationSettingsRequest.Builder locationSetting = new LocationSettingsRequest.Builder();
        locationSetting.addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSetting.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("LSR", "Success");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LSR", "Fail:" + e);
                e.printStackTrace();
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);


                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void getLocation(String Location) {

        LatLng upperLeft1 = new LatLng(2.8525253649524265, 101.76843307238708);
        LatLng upperRight1 = new LatLng(2.8525188709429625, 101.76858181060345);
        LatLng lowerLeft1 = new LatLng(2.851545195760625, 101.7684395629876);
        LatLng lowerRight1 = new LatLng(2.851541433082251, 101.76855982326943);
        List<LatLng> polygonPts = new ArrayList<>();
        polygonPts.add(upperLeft1);
        polygonPts.add(upperRight1);
        polygonPts.add(lowerRight1);
        polygonPts.add(lowerLeft1);

        LatLng upperLeft2 = new LatLng(2.8520676948409656, 101.76766283267942);
        LatLng upperRight2 = new LatLng(2.8520703041939037, 101.76840649993802);
        LatLng lowerLeft2 = new LatLng(2.851731705481608, 101.76774971322024);
        LatLng lowerRight2 = new LatLng(2.8517101881802343, 101.76841249625413);
        List<LatLng> polygonPts2 = new ArrayList<>();
        polygonPts2.add(upperLeft2);
        polygonPts2.add(upperRight2);
        polygonPts2.add(lowerRight2);
        polygonPts2.add(lowerLeft2);

        LatLng upperLeft3 = new LatLng(2.841886354725435, 101.77934442720063);
        LatLng upperRight3 = new LatLng(2.8416559684191376, 101.77990500888474);
        LatLng lowerLeft3 = new LatLng(2.8409380200999306, 101.77895818910245);
        LatLng lowerRight3 = new LatLng(2.8406969179522834, 101.77949731311448);
        List<LatLng> polygonPtsFEM = new ArrayList<>();
        polygonPtsFEM.add(upperLeft3);
        polygonPtsFEM.add(upperRight3);
        polygonPtsFEM.add(lowerRight3);
        polygonPtsFEM.add(lowerLeft3);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getContext(), "getLocation", Toast.LENGTH_SHORT).show();

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getContext(), "Please turn On GPS", Toast.LENGTH_SHORT).show();
            } else {
                fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    double latitude = addresses.get(0).getLatitude();
                                    double longitude = addresses.get(0).getLongitude();

//                                    double latitude = 2.8518691171806414;
//                                    double longitude = 101.7680472556706;
                                    //FEM test
//                                    double latitude = 2.841388077307472;
//                                    double longitude = 101.77924786767608;
                                    Log.e("GPS", "latitude: " + addresses.get(0).getLatitude());
                                    Log.e("GPS", "longitude: " + addresses.get(0).getLongitude());
                                    Log.e("GPS", "address: " + addresses.get(0).getAddressLine(0));
                                    Log.e("GPS", "city: " + addresses.get(0).getLocality());
                                    Log.e("GPS", "country: " + addresses.get(0).getCountryName());
//                                    Toast.makeText(getContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                                    boolean isWithin1 = PolyUtil.containsLocation(latitude, longitude, polygonPts, true);
                                    boolean isWithin2 = PolyUtil.containsLocation(latitude, longitude, polygonPts2, true);
                                    boolean isWithin3 = PolyUtil.containsLocation(latitude, longitude, polygonPtsFEM, true);

                                    if(Location.toUpperCase().contains("FST")){
                                        if (isWithin1 || isWithin2) {
//                                        Toast.makeText(getContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getContext(), "Within the area", Toast.LENGTH_SHORT).show();
                                            byte[] bytes = new byte[0];
                                            recordAttendance(bytes);
                                        } else {
                                            Toast.makeText(getContext(), "Not within the area", Toast.LENGTH_SHORT).show();
                                        }
                                    }else if(Location.toUpperCase().contains("FEM")){
                                        if (isWithin3) {
//                                        Toast.makeText(getContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getContext(), "Within the area", Toast.LENGTH_SHORT).show();
                                            byte[] bytes = new byte[0];
                                            recordAttendance(bytes);
                                        } else {
                                            Toast.makeText(getContext(), "Not within the area", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                loadingDialog.dismissDialog();

                            } else {
//                                Toast.makeText(getContext(), "getLocation: NULL", Toast.LENGTH_SHORT).show();
                                getLocation(Location);
                            }
                        }
                    });
            }
        } else {
            askLocationPermission(Location);
        }

    }


    public void showOptDialog() {
        final String[] opt = {"Sick Leave", "Programme Leave", "Others"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Reason");
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("State your reason");
        final EditText input = new EditText(getContext());
        builder1.setView(input);
        builder1.setPositiveButton("Sumbit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reason = input.getText().toString().trim();
                byte[] bytes = new byte[0];
                recordAttendance(bytes);
                Log.e("REASON", reason);
                dialogInterface.dismiss();
            }
        });
        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.setSingleChoiceItems(opt, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedOpt = opt[i];

            }
        });
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                selectedOpt = opt[i];
                if (selectedOpt.equals("Sick Leave") || selectedOpt.equals("Programme Leave")) {
                    dialogInterface.dismiss();

                    chooseFile();
                } else {
                    //popup to fill the reason
                    dialogInterface.dismiss();
                    builder1.create().show();
                    Log.e("INDEX", selectedOpt);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 28);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == CAMERA){
//            if(resultCode == RESULT_OK){
//                Toast.makeText(getContext(), "ADDED", Toast.LENGTH_SHORT).show();
//                Pair<Uri, String> p = createImage();
//                recordAttendance(getBytes(p.second));
//                Log.e("Pic","activityResult");
//
//            }
//        }
//        else if(requestCode == 2404){
//            if(resultCode == RESULT_OK){
//                Toast.makeText(getContext(), "ADDED 2404", Toast.LENGTH_SHORT).show();
//                Pair<Uri, String> p = createImage();
//                Log.e("image", p.second);
//                recordAttendance(getBytes(p.second));
//            }
//        }else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
//        }
        if (requestCode == 28) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                } else {
                    Uri u = data.getData();
//                    String path = getFilePath(data);
                    try {
                        finalFile = FileUtil.from(getContext(), data.getData());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("ERROR: ", e.getMessage());
                    }
//                    finalFile = new File(data.getData().getPath());
                    Log.e("Result: ", data.getData().toString());
//                    recordAttendance(getBytes(data.getData()));
                    if (finalFile != null) {
                        Log.e("FILE", "NOT NULL");
                        confirmBtn.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("FILE", "NULL");
                    }
                }
            }
        }
    }

    public String getFilePath(Intent data) {
        Uri selectedFileUri = data.getData();
        File file = new File(selectedFileUri.getPath());
        String[] split = file.getPath().split(":");
        String selectedPath = split[1];
        String stringPath = file.getAbsolutePath();
        Log.e("PATH", stringPath);
        return stringPath;
    }


    //get image byte[] to save into cloud db
    public byte[] getBytes(String imagePath) {
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
        Log.e("Pic", "getByte");

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

    //    //Android native to takePicture
//    public void takePicture(){
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Pair<Uri, String> p = createImage();
//        Log.e("PIC","error");
//        Uri finalUri = p.first;
//        String imageFileName = p.second;
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, finalUri);
////        mGetContent.launch(takePictureIntent);
//        startActivityForResult(takePictureIntent, CAMERA);
//        String imagePath = "Pictures/Student Attendance/"+imageFileName;
//    }
//
//    //takePicture with external lib
//    public void pickFromGallery() {
//        ImagePicker.with(this)
//                .cropSquare()    			//Crop image(Optional), Check Customization for more option
////                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(720, 720)//Final image resolution will be less than 1080 x 1080(Optional)
//                .start();
//    }
//
//    //save img file and return imagePath
//    public Pair<Uri, String> createImage(){
//        Uri uri = null;
//        ContentResolver resolver = getContext().getContentResolver();
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//            Log.d("URI", "first");
//        }else {
//            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            Log.d("URI", "second");
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName+".jpg");
//        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/"+"Student Attendance/");
//        Uri finalUri = resolver.insert(uri, contentValues);
////        Uri finalUri = resolver.insert(nUri, contentValues);
//        String imagePath = "/storage/emulated/0/"+"Pictures/Student Attendance/"+imageFileName+".jpg";
//        imageUri = finalUri;
//        Log.e("Pic","createImage");
//        return new Pair<>(finalUri, imagePath);
//    }

}
