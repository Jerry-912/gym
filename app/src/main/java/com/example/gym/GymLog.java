package com.example.gym;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.gym.DB.AppDataBase;

import java.util.Date;

@Entity (tableName = AppDataBase.GYMLOG_TABLE)
public class GymLog {

    @PrimaryKey(autoGenerate = true)
    private int mLogId;

    private String mExercise;
    private double mWeight;
    private int mReps;

    private Date mDate;

    private int mUserId;

    public GymLog(String exercise, double weight, int reps, int userId){
        mExercise = exercise;
        mWeight = weight;
        mReps = reps;
        mDate = new Date();
        mUserId = userId;
    }



    @Override
    public String toString() {
        String out;
        out = mExercise + " " + mWeight + " " + mReps;
        out+="\n";
        out+=getDate();
        out+="\n";
        out+="userId == " + mUserId;

        return out;

    }
    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }
    public int getLogId() {
        return mLogId;
    }

    public void setLogId(int logId) {
        mLogId = logId;
    }

    public String getExercise() {
        return mExercise;
    }

    public void setExercise(String exercise) {
        mExercise = exercise;
    }

    public double getWeight() {
        return mWeight;
    }

    public void setWeight(double weight) {
        mWeight = weight;
    }

    public int getReps() {
        return mReps;
    }

    public void setReps(int reps) {
        mReps = reps;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
