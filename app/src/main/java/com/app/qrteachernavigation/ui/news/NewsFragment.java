package com.app.qrteachernavigation.ui.news;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.qrteachernavigation.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.qrteachernavigation.API.ApiService;
import com.app.qrteachernavigation.API.RetrofitClient;
import com.app.qrteachernavigation.models.News;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

public class NewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateToolbarText();

        ApiService apiService = RetrofitClient.getRetrofitInstance(requireContext()).create(ApiService.class);

        Call<List<News>> call = apiService.getAllNews();
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if (response.isSuccessful()) {
                    List<News> newsList = response.body();
                    adapter = new NewsAdapter(getContext() ,newsList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Failed to retrieve news", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("NewsError", t.getMessage());
            }
        });

        return rootView;
    }

    private void updateToolbarText() {
        if (requireActivity() != null && requireActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.menu_news);
            }
        }
    }
}

