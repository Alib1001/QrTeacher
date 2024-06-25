package com.app.qrteachernavigation.API;

import com.app.qrteachernavigation.models.News;
import com.app.qrteachernavigation.models.Room;
import com.app.qrteachernavigation.models.Student;
import com.app.qrteachernavigation.models.TimeTable;
import com.app.qrteachernavigation.models.TurnstileHistory;
import com.app.qrteachernavigation.models.UserDTO;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/users")
    Call<List<UserDTO>> getUsers();

    @GET("/api/timetable")
    Call<List<TimeTable>> getSchedule();

    @GET("/api/students")
    Call<List<Student>> getStudents();

    @GET("/api/users/{id}")
    Call<UserDTO> getUserById(@Path("id") Long id);

    @FormUrlEncoded
    @POST("/api/users/login")
    Call<Map<String, String>> loginUser(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/timetable/addtimetable")
    Call<TimeTable> addTimetable(
            @Field("group") String group,
            @Field("subjectName") String subjectName,
            @Field("dayOfWeek") String dayOfWeek,
            @Field("startTime") String startTime,
            @Field("endTime") String endTime
    );


    @PUT("/api/timetable/edittimetable/{id}")
    Call<TimeTable> editTimeTableEntry(@Path("id") Long id, @Body TimeTable updatedTimeTable);


    @PUT("/api/users/{id}")
    Call<UserDTO> updateUser(@Path("id") Long id, @Body UserDTO updatedUserDTO);

    @POST("/api/turnstile/scan/{userId}")
    Call<TurnstileHistory> scanUser(@Path("userId") long userId);
    @GET("/api/timetable/getStudents/{timetableId}")
    Call<List<Integer>> getStudentsFromTimetable(@Path("timetableId") Long timetableId);

    @Multipart
    @POST("/api/users/uploadImage/{id}")
    Call<Void> uploadImage(@Path("id") Integer id, @Part MultipartBody.Part image);

    @PUT("/api/timetable/update-scanable/{timeTableId}")
    Call<Void> updateScanable(@Path("timeTableId") Long timeTableId, @Body Boolean scanable);
    @GET("/api/rooms/free")
    Call<List<Room>> getFreeRooms();

    @GET("/api/news/")
    Call<List<News>> getAllNews();

    @GET("/api/students/{id}")
    Call<Student> getStudentById(@Path("id") Long id);

    @POST("api/users/saveFCMToken/{userId}")
    Call<Void> sendFcmToken(@Path("userId") long userId, @Query("fcmToken") String fcmToken);

    @GET("/api/guests")
    Call<List<UserDTO>> getGuests();
    @POST("/api/students/verifyguest")
    Call<Void> verifyGuest(@Query("guestId") Long guestId);

    @GET("/api/turnstile/getqrcode")
    Call<String> getDynamicQRCode();
}