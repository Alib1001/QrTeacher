package com.app.qrteachernavigation.ui.qrScanner;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.qrteachernavigation.API.ApiService;
import com.app.qrteachernavigation.API.RetrofitClient;
import com.app.qrteachernavigation.R;
import com.app.qrteachernavigation.models.TurnstileHistory;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private ZXingScannerView scannerView;

    private ApiService apiService;
    String codeInUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q_r_scanner, container, false);

        updateToolbarText();

        scannerView = view.findViewById(R.id.scannerView);
        apiService = RetrofitClient.getRetrofitInstance(getContext()).create(ApiService.class);


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startScanner();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

        SharedPreferences themePrefs = getContext().getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkModeEnabled = themePrefs.getBoolean("isDarkModeEnabled", false);

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        getQRCode();


        return view;
    }

    private void startScanner() {
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        String url = result.getText();
        if (url.equals(codeInUrl)){
            scanUserOnBackend();
        }
        else {
            Toast.makeText(getContext(),"QR code not recognized",Toast.LENGTH_SHORT).show();
        }

        scannerView.resumeCameraPreview(this);
    }

    private void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        requireActivity().startActivity(browserIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        startScanner();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                Toast.makeText(requireContext(), "Camera permission denied. Cannot scan QR code.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanUserOnBackend() {
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        Call<TurnstileHistory> call = apiService.scanUser(userId);
        call.enqueue(new Callback<TurnstileHistory>() {
            @Override
            public void onResponse(Call<TurnstileHistory> call, Response<TurnstileHistory> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),"Qr scanned successfully",Toast.LENGTH_SHORT).show();
                    getQRCode();
                } else {
                    Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<TurnstileHistory> call, Throwable t) {
                Log.e("QrError",t.toString());
                t.printStackTrace();
            }
        });
    }

    private void updateToolbarText() {
        if (requireActivity() != null && requireActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.menu_home);
            }
        }
    }

    private void getQRCode() {
        apiService.getDynamicQRCode().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String qrCode = response.body();
                    codeInUrl = qrCode;
                } else {
                    Log.e("QRCodeError", "Failed to fetch QR code");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("QRCodeError", "Error fetching QR code: " + t.getMessage());
            }
        });
    }
}
