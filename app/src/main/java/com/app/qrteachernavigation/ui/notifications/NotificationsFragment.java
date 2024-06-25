package com.app.qrteachernavigation.ui.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.qrteachernavigation.API.ApiService;
import com.app.qrteachernavigation.API.RetrofitClient;
import com.app.qrteachernavigation.R;
import com.app.qrteachernavigation.models.UserDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GuestAdapter guestAdapter;
    private List<UserDTO> guestList;
    int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        SharedPreferences idPrefs = getActivity().getApplicationContext().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        userId = idPrefs.getInt("userId", -1);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//      updateToolbarText();
        fetchGuests();

        return view;
    }

    private void fetchGuests() {
        ApiService apiService = RetrofitClient.getRetrofitInstance(getActivity()).create(ApiService.class);
        Call<List<UserDTO>> call = apiService.getGuests();

        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDTO>> call, @NonNull Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guestList = response.body();
                    Log.d("NotificationsFragment", "Number of guests received: " + guestList.size());

                    List<UserDTO> filteredGuestList = filterGuests(guestList);

                    guestAdapter = new GuestAdapter(filteredGuestList);
                    recyclerView.setAdapter(guestAdapter);
                } else {
                    Log.e("NotificationsFragment", "Failed to fetch guests");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDTO>> call, @NonNull Throwable t) {
                Log.e("NotificationsFragment", "Error: " + t.getMessage());
            }
        });
    }

    private List<UserDTO> filterGuests(List<UserDTO> guestList) {
        List<UserDTO> filteredList = new ArrayList<>();
        for (UserDTO guest : guestList) {
            if (guest.getUser() != null && guest.getUser().getId() == userId && !guest.getVerified()) {
                filteredList.add(guest);
            }
        }
        return filteredList;
    }




}
