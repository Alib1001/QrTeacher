package com.app.qrteachernavigation;

import static com.app.qrteachernavigation.API.RetrofitClient.getRetrofitInstance;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.qrteachernavigation.API.ApiService;
import com.app.qrteachernavigation.models.UserDTO;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.app.qrteachernavigation.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    UserDTO user;
    ImageView headerImageView;
    TextView nameTextView;
    TextView textViewId;


    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1001;
    private static final String TAG = "FCMTokenTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_schedule, R.id.nav_settings, R.id.nav_rooms, R.id.nav_news, R.id.nav_notifications)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        IntentFilter filter = new IntentFilter("LANGUAGE_CHANGED");
        LocalBroadcastManager.getInstance(this).registerReceiver(languageChangeReceiver, filter);



        View headerView = navigationView.getHeaderView(0);
        headerImageView = headerView.findViewById(R.id.imageView);
        nameTextView = headerView.findViewById(R.id.textViewName);
        textViewId = headerView.findViewById(R.id.textViewId);

        String defaultLanguage = Locale.getDefault().getLanguage();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        String savedLanguage = preferences.getString("selectedLanguage", defaultLanguage);
        if (!TextUtils.isEmpty(savedLanguage)) {
            updateLanguage(savedLanguage);
            updateNavigationView();

        }

        getUserById();
    }

    public void getUserById() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        ApiService apiService = getRetrofitInstance(getApplicationContext()).create(ApiService.class);
        Call<UserDTO> call = apiService.getUserById(Long.valueOf(userId));
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    UserDTO userDTO = response.body();
                    user = userDTO;
                    nameTextView.setText(user.getFirstName() + " " + user.getLastName());
                    textViewId.setText("ID :" + user.getId());
                    Picasso.get()
                            .load(user.getImageUrl())
                            .fit().centerCrop()
                            .placeholder(R.drawable.ic_image_profile)
                            .error(R.drawable.ic_image_profile)
                            .into(headerImageView);

                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(response.errorBody()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(getApplicationContext(), String.valueOf(t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("LanguagePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selectedLanguage", language);
        editor.apply();

    }

    private BroadcastReceiver languageChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("selectedLanguage")) {
                String selectedLanguage = intent.getStringExtra("selectedLanguage");

                updateNavigationView();
            }
        }
    };

    private void updateNavigationView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem homeItem = menu.findItem(R.id.nav_home);
        MenuItem galleryItem = menu.findItem(R.id.nav_schedule);
        MenuItem slideshowItem = menu.findItem(R.id.nav_settings);
        MenuItem roomItem = menu.findItem(R.id.nav_rooms);
        MenuItem newsItem = menu.findItem(R.id.nav_news);
        homeItem.setTitle(R.string.menu_home);
        galleryItem.setTitle(R.string.menu_schedule);
        slideshowItem.setTitle(R.string.menu_settings);
        roomItem.setTitle(R.string.menu_rooms);
        newsItem.setTitle(R.string.menu_news);
    }

    private boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            return notificationManager.areNotificationsEnabled();
        } else {
            return NotificationManagerCompat.from(this).areNotificationsEnabled();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_PERMISSION);
        }
    }


}
