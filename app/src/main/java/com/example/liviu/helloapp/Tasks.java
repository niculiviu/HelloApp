package com.example.liviu.helloapp;

import java.util.Date;

/**
 * Created by liviu on 01.05.2015.
 */
public class Tasks {
    String task_name;
    String task_desc;
    String task_volunteers_num;
    String points;
    String status;
    Date deadline;
    String id;

    public Tasks(String task_name, String task_volunteers_num, String points, String status, String id) {
        super();
        this.task_name = task_name;
        this.task_volunteers_num = task_volunteers_num;
        this.points = points;
        this.status = status;
        this.id = id;
    }

    public String getTask_volunteers_num() {
        return task_volunteers_num;
    }

    public void setTask_volunteers_num(String task_volunteers_num) {
        this.task_volunteers_num = task_volunteers_num;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
