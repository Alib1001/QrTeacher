package com.app.qrteachernavigation.ui.schedule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.app.qrteachernavigation.API.ApiService;
import com.app.qrteachernavigation.API.RetrofitClient;
import com.app.qrteachernavigation.CheckAttendance.CheckAttendanceActivity;
import com.app.qrteachernavigation.R;
import com.app.qrteachernavigation.models.TimeTable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {

    private List<TimeTable> timeTableList;
    private  LayoutInflater inflater;
    private  Context context;



    public ScheduleListAdapter(List<TimeTable> timeTableList) {
        this.timeTableList = timeTableList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeTable timeTable = timeTableList.get(position);
        holder.bind(timeTable);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return timeTableList.size();
    }


    public void setScheduleList(List<TimeTable> scheduleList) {
        this.timeTableList = scheduleList;
        this.timeTableList = scheduleList;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    private void showOptionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Options");
        builder.setItems(new CharSequence[]{"Show QR code", "Check Attendance"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    showQRCodeDialog(position);
                    updateScanable(position, true);
                    break;
                case 1:
                    openCheckAttendanceActivity(position);
                    updateScanable(position, false);
                    break;
            }
        });
        builder.show();
    }

    private void showQRCodeDialog(int position) {
        int qrCodeSize = 500;
        int backgroundColor = getBackgroundColor(); // Получаем цвет фона в зависимости от темы
        int foregroundColor = Color.BLACK;
        TimeTable schedule = timeTableList.get(position);

        String qrCodeContent = "timetableID:" + schedule.getId();

        Bitmap qrCodeBitmap = generateQRCode(qrCodeContent, qrCodeSize, qrCodeSize, backgroundColor, foregroundColor);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_qr_code, null);
        ImageView imageViewQRCode = dialogView.findViewById(R.id.imageViewQRCode);
        imageViewQRCode.setImageBitmap(qrCodeBitmap);
        builder.setView(dialogView);
        builder.setTitle("QR Code");
        builder.setPositiveButton("Close", (dialog, which) -> {
            updateScanable(position, false); // Устанавливаем scanable в false при закрытии QR-кода
            notifyDataSetChanged();
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateScanable(position, false);
                notifyDataSetChanged();
            }
        });

        builder.show();
    }
    private int getBackgroundColor() {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            return Color.parseColor("#424242"); // Темная тема
        } else {
            return Color.parseColor("#ffffff"); // Светлая тема
        }
    }

    private void updateScanable(int position, Boolean value) {
        TimeTable schedule = timeTableList.get(position);
        schedule.setScanable(value);
        notifyDataSetChanged(); // Обновляем RecyclerView после изменения scanable
        ApiService apiService = RetrofitClient.getRetrofitInstance(context).create(ApiService.class);
        Call<Void> callUpdateScanable = apiService.updateScanable(schedule.getId(), value);
        callUpdateScanable.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("ScheduleListAdapter", "Failed to update scanable on server " + response.raw());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ScheduleListAdapter", "Error updating scanable on server: " + t.toString());
            }
        });
    }

    private Bitmap generateQRCode(String content, int width, int height, int backgroundColor, int foregroundColor) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? foregroundColor : backgroundColor);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            Log.e("QRCodeGenerator", "Error generating QR code", e);
            return null;
        }
    }


    private void openCheckAttendanceActivity(int position) {
        TimeTable schedule = timeTableList.get(position);
        long timetableId = schedule.getId();
        Intent intent = new Intent(context, CheckAttendanceActivity.class);
        intent.putExtra("timetableId", timetableId);
        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStartTime;
        TextView textViewEndTime;
        TextView textViewSubjectName;
        TextView textViewRoom;
        TextView textViewGroup;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewEndTime = itemView.findViewById(R.id.textViewEndTime);
            textViewSubjectName = itemView.findViewById(R.id.textViewSubjectName);
            textViewRoom = itemView.findViewById(R.id.textViewRoom);
            textViewGroup = itemView.findViewById(R.id.textViewGroup);
        }

        public void bind(TimeTable timeTable) {
            textViewStartTime.setText(timeTable.getStartTime());
            textViewEndTime.setText(timeTable.getEndTime());
            textViewSubjectName.setText(timeTable.getSubjectName());
            textViewGroup.setText(timeTable.getGroupName());
            textViewRoom.setText("Room: " + timeTable.getClassroom().getRoomName()+", "
                    + timeTable.getClassroom().getBuilding());

        }
    }


    private ScheduleItemClickListener scheduleItemClickListener;

    public void setScheduleItemClickListener(ScheduleItemClickListener listener) {
        this.scheduleItemClickListener = listener;
    }

    public interface ScheduleItemClickListener {
        void onCheckAttendanceClicked(int position);
    }
}
