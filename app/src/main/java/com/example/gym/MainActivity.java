package com.example.gym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gym.DB.AppDataBase;
import com.example.gym.DB.GymLogDAO;
import com.example.gym.databinding.ActivityMainBinding;

import org.w3c.dom.Text;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.daclink.gymlog_v_sp22.userIdKey";
    private static final String PREFENCES_KEY = "com.daclink.gymlog_v_sp22.PREFENCES_KEY";
    ActivityMainBinding binding;
    private TextView mMainDisplay;
    private EditText mExercise;
    private EditText mWeight;
    private EditText mReps;

    private Button mSubmit;

    private  GymLogDAO mGymLogDAO;

    List<GymLog> mGymLogList;
    private int mUserId = -1;
    private User mUser;
    private SharedPreferences mPrefernces = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item1){
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDataBase();
        checkForUser();
        addUserToPreference(mUserId);
        loginUser(mUserId);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mMainDisplay = binding.mainGymLogDisplay;
        mExercise = binding.mainExerciseEditText;
        mWeight = binding.mainWeightEditText;
        mReps = binding.mainRepsEditText;
        mSubmit = binding.mainSubmitButton;

        mMainDisplay.setMovementMethod(new ScrollingMovementMethod());


        refreshDisplay();

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitGymLog();
                refreshDisplay();
            }
        });
    }

    private void loginUser(int userId) {
        mUser = mGymLogDAO.getUserByUserId(userId);
        invalidateOptionsMenu();


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mUser != null){
            MenuItem item = menu.findItem(R.id.item1);
            item.setTitle(mUser.getUserName());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void getDataBase() {
        mGymLogDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .GymLogDAO();
    }

    private void checkForUser() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY,-1);
        if(mUserId != -1){
            return;
        }
        if(mPrefernces == null){
            getPrefs();
        }
        mUserId = mPrefernces.getInt(USER_ID_KEY,-1);

        if(mUserId != 1){
            return;
        }

        List<User>users = mGymLogDAO .getAllUsers();
        if(users.size() <= 0){
            User defaultUser = new User("jerry","jerry123");
            User altUser = new User("gerardo","jerry123");
            mGymLogDAO.insert(defaultUser,altUser);
        }

        Intent intent = LoginActivity.intentFactory(this);
        startActivity(intent);

    }

    private void getPrefs() {
        mPrefernces = this.getSharedPreferences(PREFENCES_KEY, Context.MODE_PRIVATE);
    }

    private void logoutUser(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.logout);
        alertBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        clearUserFromIntent();
                        clearUserFromPrefs();
                        mUserId =-1;
                        checkForUser();

                    }
                });
        alertBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }

                });
        alertBuilder.create().show();

    }

    private void clearUserFromIntent() {
        getIntent().putExtra(USER_ID_KEY,-1);
    }

    private void addUserToPreference(int userId) {
        if(mPrefernces == null){
            getPrefs();
        }
        SharedPreferences.Editor editor = mPrefernces.edit();
        editor.putInt(USER_ID_KEY,userId);
    }

    private void clearUserFromPrefs() {
        addUserToPreference(-1);
    }


    private void submitGymLog(){
        String exercise = mExercise.getText().toString();
        double weight = Double.parseDouble(mWeight.getText().toString());
        int reps = Integer.parseInt(mReps.getText().toString());

        GymLog log = new GymLog(exercise,weight,reps,mUserId);

        mGymLogDAO.insert(log);
    }

    private void refreshDisplay(){
        mGymLogList = mGymLogDAO.getGymLogsById(mUserId);
        if(!mGymLogList.isEmpty()){
            StringBuilder sb = new StringBuilder();
            for(GymLog log: mGymLogList){
                sb.append(log.toString());
            }
            mMainDisplay.setText(sb.toString());
        }else{
            mMainDisplay.setText(R.string.no_logs_msg);
        }
    }

    public static Intent intentFactory(Context context,int userId){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }
}