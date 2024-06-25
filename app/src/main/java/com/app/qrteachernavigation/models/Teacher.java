package com.app.qrteachernavigation.models;

public class Teacher extends UserDTO {
    private String TeacherTaughtSubject;



    public Teacher(long id, String username, String password, String firstName, String lastName, String userType,
                   String TeacherTaughtSubject) {
        super(id, username, password, firstName, lastName,userType);
        this.TeacherTaughtSubject = TeacherTaughtSubject;
    }

    public String getTeacherTaughtSubject() {
        return TeacherTaughtSubject;
    }

    public void setTeacherTaughtSubject(String teacherTaughtSubject) {
        this.TeacherTaughtSubject = teacherTaughtSubject;
    }
}
