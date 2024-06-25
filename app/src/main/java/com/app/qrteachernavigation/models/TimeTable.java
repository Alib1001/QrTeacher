package com.app.qrteachernavigation.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TimeTable implements Serializable {

    @SerializedName("id")
    private Long id;
    @SerializedName("dayOfWeek")
    private String dayOfWeek;
    @SerializedName("roomId")
    private Integer roomId;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("endTime")
    private String endTime;

    @SerializedName("scheduleGroupId")
    private Integer scheduleGroupId;

    @SerializedName("date")
    private String date;
    @SerializedName("classroom")
    private Room classroom;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("subjectName")
    private String subjectName;

    @SerializedName("weekNumber")
    private int weekNumber;

    @SerializedName("studentIds")
    private List<String> studentIds;

    @SerializedName("teacher")
    private Teacher teacher;

    @SerializedName("teacherId")
    private Integer teacherId;

    @SerializedName("scanable")
    private Boolean scanable;
    public TimeTable() {
    }

    public TimeTable(String groupName, String subjectName, String dayOfWeek,
                     String startTime, String endTime, Room classroom , int weekNumber, List<String> studentIds) {
        this.groupName = groupName;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroom = classroom;
        this.weekNumber = weekNumber;
        this.studentIds = studentIds;
    }

    public TimeTable(String groupName, String subjectName, String dayOfWeek, String startTime,
                     String endTime, Room classroom, List<String> presentStudents, Integer teacherId) {
        this.groupName = groupName;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroom = classroom;
        this.weekNumber = weekNumber;
        this.studentIds = presentStudents;
        this.teacherId = teacherId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Room getClassroom() {
        return classroom;
    }

    public void setClassroom(Room classroom) {
        this.classroom = classroom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getScheduleGroupId() {
        return scheduleGroupId;
    }

    public void setScheduleGroupId(Integer scheduleGroupId) {
        this.scheduleGroupId = scheduleGroupId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Boolean getScanable() {
        return scanable;
    }

    public void setScanable(Boolean scanable) {
        this.scanable = scanable;
    }


}
