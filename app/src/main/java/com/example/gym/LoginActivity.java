package com.example.gym;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.gym.DB.AppDataBase;
import com.example.gym.DB.GymLogDAO;


public class LoginActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mButton;

    private GymLogDAO mGymLogDAO;
    private String mUsernameString;
    private String mPasswordString;
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        wiredUpDisplay();
        getDatabase();

    }

    private void getDatabase() {
        mGymLogDAO = Room.databaseBuilder(this, AppDataBase.class, AppDataBase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .GymLogDAO();
    }

    public static Intent intentFactory(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    private void wiredUpDisplay(){
        mUsername = findViewById(R.id.editTextTextLoginUsername);
        mPassword = findViewById(R.id.editTextTextLoginPassword);
        mButton = findViewById(R.id.buttonlogin);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValuesFromDisplay();
                if(checkForUserInDatabase()){
                    if (!validatePassword()){
                        Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = MainActivity.intentFactory(getApplicationContext(), mUser.getUserId());
                        startActivity(intent);
                    }
                }


            }
        });
    }

    private boolean validatePassword() {
        return mUser.getPassword().equals(mPasswordString);
    }

    private boolean checkForUserInDatabase() {
        mUser = mGymLogDAO.getUserByUsername(mUsernameString);
        if(mUser==null){
            Toast.makeText(this, "no user " + mUsernameString + " found", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void getValuesFromDisplay() {
        mUsernameString = mUsername.getText().toString();
        mPasswordString = mPassword.getText().toString();
    }


}
