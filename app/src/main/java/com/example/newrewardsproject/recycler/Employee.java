package com.example.newrewardsproject.recycler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Employee implements Serializable {
    private String fName;
    private String lName;
    private String userName;
    private String dept;
    private String story;
    private String position;
    private String imageBytes;
    private String points;
    private boolean isUser;

    public Employee(String fName, String lName, String userName, String dept, String story,
                    String position, String imageBytes, String points) {
        this.fName = fName;
        this.lName = lName;
        this.userName = userName;
        this.dept = dept;
        this.story = story;
        this.position = position;
        this.imageBytes = imageBytes;
        this.points = points;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getUserName() {
        return userName;
    }

    public String getDept() {
        return dept;
    }

    public String getStory() {
        return story;
    }

    public String getPosition() {
        return position;
    }


    public String getImageBytes() {
        return imageBytes;
    }

    public String getPoints() {return points;}

    public int getPointInt() {return Integer.parseInt(points);}

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public void setImageBytes(String imageBytes) {
        this.imageBytes = imageBytes;
    }

    public void setPoint(String points) {
        this.points = points;
    }



}
