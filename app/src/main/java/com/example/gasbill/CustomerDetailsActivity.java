package com.example.gasbill;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerDetailsActivity extends AppCompatActivity {

    private TextView tvCustomerId, tvCustomerName, tvCustomerYYYYMM, tvCustomerAddress, tvCustomerUsedNumGas, tvCustomerGasLevelTypeID;
    private Button btnFirst, btnPrevious, btnNext, btnLast;
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        dbHelper = new DatabaseHelper(this);
        initializeViews();

        cursor = dbHelper.getAllCustomers();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            displayCustomerDetails();
        } else {
            Toast.makeText(this, "No customers found", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có dữ liệu
        }

        setButtonListeners();
    }

    private void initializeViews() {
        tvCustomerId = findViewById(R.id.tv_customer_id);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerYYYYMM = findViewById(R.id.tv_customer_yyyymm);
        tvCustomerAddress = findViewById(R.id.tv_customer_address);
        tvCustomerUsedNumGas = findViewById(R.id.tv_customer_used_num_gas);
        tvCustomerGasLevelTypeID = findViewById(R.id.tv_customer_gas_level_type_id);
        btnFirst = findViewById(R.id.btn_first);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnLast = findViewById(R.id.btn_last);
    }

    private void setButtonListeners() {
        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToFirst()) {
                    currentPosition = 0;
                    displayCustomerDetails();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToPrevious()) {
                    currentPosition--;
                    displayCustomerDetails();
                } else {
                    Toast.makeText(CustomerDetailsActivity.this, "No previous customer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToNext()) {
                    currentPosition++;
                    displayCustomerDetails();
                } else {
                    Toast.makeText(CustomerDetailsActivity.this, "No next customer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.moveToLast()) {
                    currentPosition = cursor.getCount() - 1;
                    displayCustomerDetails();
                }
            }
        });
    }

    private void displayCustomerDetails() {
        if (cursor != null) {
            // Lấy và hiển thị thông tin
            int idIndex = cursor.getColumnIndex("ID");
            int nameIndex = cursor.getColumnIndex("NAME");
            int yyyymmIndex = cursor.getColumnIndex("YYYYMM");
            int addressIndex = cursor.getColumnIndex("ADDRESS");
            int usedNumGasIndex = cursor.getColumnIndex("USED_NUM_GAS");
            int gasLevelTypeIdIndex = cursor.getColumnIndex("GAS_LEVEL_TYPE_ID");

            if (idIndex != -1) {
                tvCustomerId.setText("ID: " + cursor.getInt(idIndex));
            }

            if (nameIndex != -1) {
                tvCustomerName.setText("Name: " + cursor.getString(nameIndex));
            }

            if (yyyymmIndex != -1) {
                tvCustomerYYYYMM.setText("YYYYMM: " + cursor.getString(yyyymmIndex));
            }

            if (addressIndex != -1) {
                tvCustomerAddress.setText("Address: " + cursor.getString(addressIndex));
            }

            if (usedNumGasIndex != -1) {
                tvCustomerUsedNumGas.setText("Used Num Gas: " + cursor.getDouble(usedNumGasIndex));
            }

            if (gasLevelTypeIdIndex != -1) {
                tvCustomerGasLevelTypeID.setText("Gas Level Type ID: " + cursor.getInt(gasLevelTypeIdIndex));
            }
        } else {
            Log.e("TAG", "No data found");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }
}
