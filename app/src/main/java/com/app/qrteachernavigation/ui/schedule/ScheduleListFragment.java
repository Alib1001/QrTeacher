package com.app.qrteachernavigation.ui.schedule;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.qrteachernavigation.API.ApiService;
import com.app.qrteachernavigation.API.RetrofitClient;
import com.app.qrteachernavigation.Adapters.CalendarAdapter;
import com.app.qrteachernavigation.R;
import com.app.qrteachernavigation.models.TimeTable;
import com.app.qrteachernavigation.models.UserDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleListFragment extends Fragment implements ScheduleListAdapter.ScheduleItemClickListener {
    private com.app.qrteachernavigation.databinding.FragmentScheduleBinding binding;
    private List<TimeTable> timeTableList = new ArrayList<>();
    private RecyclerView scheduleRecyclerView;
    private ScheduleListAdapter adapter;
    private ApiService apiService;
    private String groupName;
    private List<String> calendarItems = new ArrayList<>();
    private List<String> calendarDays = new ArrayList<>();
    private RecyclerView calendarRecyclerView;

    private TextView currentDateTextView;

    private final static int INDEX_ADDICTION = 3 ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        updateToolbarText();

        currentDateTextView = rootView.findViewById(R.id.textViewCurrentDate);
        calendarRecyclerView = rootView.findViewById(R.id.calendarRecyclerView);
        scheduleRecyclerView = rootView.findViewById(R.id.scheduleRecyclerView);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleListAdapter(timeTableList);
        scheduleRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        calendarRecyclerView.setLayoutManager(layoutManager);
        setupCalendarItems();

        CalendarAdapter calendarAdapter = new CalendarAdapter(calendarDays, calendarItems);
        calendarRecyclerView.setAdapter(calendarAdapter);

        // Find today's date index
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(Calendar.getInstance().getTime());
        int todayIndex = calendarItems.indexOf(todayDate) - 3;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        String formattedDay = dayFormat.format(calendar.getTime());
        if (!formattedDay.equalsIgnoreCase("Sun") &&
                !formattedDay.equalsIgnoreCase("вс") &&
                !formattedDay.equalsIgnoreCase("жс")) {
            if (todayIndex != -1 ) {
                calendarAdapter.setSelectedPosition(todayIndex);

                // Scroll to the center position
                calendarRecyclerView.scrollToPosition(todayIndex);
                calendarRecyclerView.post(() -> {
                    int offset = (calendarRecyclerView.getWidth() / 2) - (layoutManager.findViewByPosition(todayIndex).getWidth() / 2);
                    layoutManager.scrollToPositionWithOffset(todayIndex+4, offset);
                });
            }
        }
        else {
            calendarAdapter.setSelectedPosition(todayIndex);
        }



        calendarAdapter.setOnDaySelectedListener(new CalendarAdapter.OnDaySelectedListener() {
            @Override
            public void onDaySelected(String selectedDayOfWeek) {
                loadSchedule(calendarAdapter);
            }
        });

        calendarRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                int centerPosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2;
                calendarAdapter.setSelectedPosition(centerPosition);

                if (centerPosition != RecyclerView.NO_POSITION && centerPosition < calendarDays.size()) {
                    loadSchedule(calendarAdapter);
                }
            }
        });

        calendarAdapter.setOnCalendarScrollListener(new CalendarAdapter.OnCalendarScrollListener() {
            @Override
            public void onCalendarScrolled(int position) {
                if (position != RecyclerView.NO_POSITION && position < calendarDays.size()) {
                    loadSchedule(calendarAdapter);
                }
            }
        });

        apiService = RetrofitClient.getRetrofitInstance(getContext()).create(ApiService.class);

        return rootView;
    }



    private List<TimeTable> filterScheduleByUserId(List<TimeTable> scheduleList, int userId) {
        List<TimeTable> filteredList = new ArrayList<>();
        for (TimeTable timeTable : scheduleList) {
            if (timeTable.getTeacher() != null && timeTable.getTeacher().getId() == userId) {
                filteredList.add(timeTable);
            }
        }
        return filteredList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setCurrentDate(TextView currentDateTextView) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d', 'yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        currentDate = currentDate.substring(0, 1).toUpperCase() + currentDate.substring(1);

        currentDateTextView.setText(currentDate);
    }
    private void setupCalendarItems() {
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        calendar.add(Calendar.DAY_OF_MONTH, -14);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        for (int i = 0; i < 31; i++) {
            String formattedDate = dateFormat.format(calendar.getTime());
            String formattedDay = dayFormat.format(calendar.getTime());
            if (!formattedDay.equalsIgnoreCase("Sun") &&
                    !formattedDay.equalsIgnoreCase("вс") &&
                    !formattedDay.equalsIgnoreCase("жс")) {
                calendarDays.add(formattedDay);
                calendarItems.add(formattedDate);
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }




    private String convertWeekDays(String weekDay){
        if (weekDay.equalsIgnoreCase("Пн") || weekDay.equalsIgnoreCase("Дс")) {
            weekDay = "Mon";
        } else if (weekDay.equalsIgnoreCase("Вт") || weekDay.equalsIgnoreCase("Сс")) {
            weekDay = "Tue";
        } else if (weekDay.equalsIgnoreCase("Ср") || weekDay.equalsIgnoreCase("ср")) {
            weekDay = "Wed";
        } else if (weekDay.equalsIgnoreCase("Чт") || weekDay.equalsIgnoreCase("Бс")) {
            weekDay = "Thu";
        } else if (weekDay.equalsIgnoreCase("Пт") || weekDay.equalsIgnoreCase("Жм")) {
            weekDay = "Fri";
        }


        return weekDay;
    }

    String currentDate = null;

    private Handler handler = new Handler();
    private Runnable runnable;


    private void loadSchedule(CalendarAdapter calendarAdapter) {
        String selectedDate = calendarAdapter.getSelectedDate();
        setCurrentDateTextView(selectedDate);
        String date = calendarAdapter.getSelectedDate();
        if (date.equals(currentDate)) {
            return;
        }
        currentDate = date;

        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = preferences.getInt("userId", -1);

                Call<UserDTO> userCall = apiService.getUserById((long) userId);
                userCall.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.isSuccessful()) {
                            UserDTO user = response.body();
                            if (user != null) {
                                groupName = user.getGroup();
                                Call<List<TimeTable>> callSchedule = apiService.getSchedule();
                                callSchedule.enqueue(new Callback<List<TimeTable>>() {
                                    @Override
                                    public void onResponse(Call<List<TimeTable>> call, Response<List<TimeTable>> response) {
                                        if (response.isSuccessful()) {
                                            List<TimeTable> scheduleData = response.body();
                                            if (scheduleData != null) {
                                                timeTableList.clear();
                                                for (TimeTable timeTable : scheduleData) {
                                                    if (timeTable.getTeacher().getId() == userId) {
                                                        if (date.equals(timeTable.getDate())) {
                                                            timeTableList.add(timeTable);
                                                        }
                                                    }
                                                }

                                                // Sort the list by start time
                                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                                Collections.sort(timeTableList, new Comparator<TimeTable>() {
                                                    @Override
                                                    public int compare(TimeTable t1, TimeTable t2) {
                                                        try {
                                                            Date time1 = timeFormat.parse(t1.getStartTime());
                                                            Date time2 = timeFormat.parse(t2.getStartTime());
                                                            return time1.compareTo(time2);
                                                        } catch (ParseException e) {
                                                            throw new IllegalArgumentException(e);
                                                        }
                                                    }
                                                });

                                                adapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Failed to load schedule from server", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<TimeTable>> call, Throwable t) {
                                        Toast.makeText(getContext(), "Error loading schedule: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to load user from server", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        Toast.makeText(getContext(), "Error loading user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        handler.postDelayed(runnable, 300);
    }



    @Override
    public void onCheckAttendanceClicked(int position) {
        updateTimetable();
    }

    public void updateTimetable(){
        Call<List<TimeTable>> callGetSchedule = apiService.getSchedule();
        callGetSchedule.enqueue(new Callback<List<TimeTable>>() {
            @Override
            public void onResponse(Call<List<TimeTable>> call, Response<List<TimeTable>> response) {
                if (response.isSuccessful()) {
                    List<TimeTable> newSchedule = response.body();
                    if (newSchedule != null) {
                        timeTableList.clear();
                        timeTableList.addAll(newSchedule);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "New schedule loaded successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load new schedule", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TimeTable>> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading new schedule: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToolbarText() {
        if (requireActivity() != null && requireActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.menu_schedule);
            }
        }
    }

    private void setCurrentDateTextView(String selectedDate) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

        try {
            java.util.Date date = inputDateFormat.parse(selectedDate);
            currentDateTextView.setText(outputDateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }




}
