package com.example.gasbill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ChangeUnitActivity extends AppCompatActivity {

    ListView listViewGasLevels;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_unit);

        listViewGasLevels = findViewById(R.id.listViewGasLevels);
        dbHelper = new DatabaseHelper(this);

        loadGasLevelTypes();

        // Thiết lập sự kiện click vào từng item để chỉnh sửa giá gas
        listViewGasLevels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy ID, tên loại gas và giá hiện tại của item được click
                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] itemParts = selectedItem.split(", ");
                int gasLevelId = Integer.parseInt(itemParts[0].split(": ")[1]);
                String gasLevelName = itemParts[1].split(": ")[1];
                double currentPrice = Double.parseDouble(itemParts[2].split(": ")[1]);

                // Hiển thị hộp thoại để chỉnh sửa giá
                showEditPriceDialog(gasLevelId, gasLevelName, currentPrice);
            }
        });
    }

    private void loadGasLevelTypes() {
        // Lấy dữ liệu từ bảng gas_level_type
        Cursor cursor = dbHelper.getGasLevelTypes();

        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<String> gasLevelList = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double unitPrice = cursor.getDouble(2);

                gasLevelList.add("ID: " + id + ", Type: " + name + ", Price: " + unitPrice);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gasLevelList);
            listViewGasLevels.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void showEditPriceDialog(int gasLevelId, String gasLevelName, double currentPrice) {
        // Tạo một hộp thoại để chỉnh sửa giá
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Price for " + gasLevelName);

        // Tạo EditText để nhập giá mới
        final EditText input = new EditText(this);
        input.setText(String.valueOf(currentPrice));
        builder.setView(input);

        // Thiết lập nút "Save" để cập nhật giá mới
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPriceStr = input.getText().toString();
                if (!newPriceStr.isEmpty()) {
                    double newPrice = Double.parseDouble(newPriceStr);

                    // Cập nhật giá mới vào cơ sở dữ liệu
                    dbHelper.updateGasLevelPrice(gasLevelId, newPrice);

                    // Cập nhật lại danh sách sau khi chỉnh sửa
                    loadGasLevelTypes();
                    Toast.makeText(ChangeUnitActivity.this, "Price updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChangeUnitActivity.this, "Price cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
