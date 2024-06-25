package com.app.qrteachernavigation.models;

import java.io.Serializable;


public class Student extends UserDTO implements Serializable {

    public Student(long id, String username, String password, String firstName, String lastName,
                   String groupName, String specialization, String userType) {
        super(id, username, password, firstName, lastName, userType);
        setGroup(groupName);
        setSpecialization(specialization);
    }
}
