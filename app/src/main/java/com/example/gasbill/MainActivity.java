package com.example.gasbill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText etName, etYYYYMM, etAddress, etUsedNumGas, etGasLevelTypeID;
    Button btnSave, btnViewCustomer; // Thêm nút xem khách hàng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Link UI elements
        etName = findViewById(R.id.et_name);
        etYYYYMM = findViewById(R.id.et_yyyymm);
        etAddress = findViewById(R.id.et_address);
        etUsedNumGas = findViewById(R.id.et_used_num_gas);
        etGasLevelTypeID = findViewById(R.id.et_gas_level_type_id);
        btnSave = findViewById(R.id.btn_save);
        btnViewCustomer = findViewById(R.id.btn_view_customer); // Khởi tạo nút

        // Set up onClick listener for Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String name = etName.getText().toString();
                String yyyymm = etYYYYMM.getText().toString();
                String address = etAddress.getText().toString();
                String usedNumGasStr = etUsedNumGas.getText().toString();
                String gasLevelTypeIDStr = etGasLevelTypeID.getText().toString();

                // Validate the input (you can add more validations)
                if (name.isEmpty() || yyyymm.isEmpty() || address.isEmpty() ||
                        usedNumGasStr.isEmpty() || gasLevelTypeIDStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double usedNumGas = Double.parseDouble(usedNumGasStr);
                int gasLevelTypeID = Integer.parseInt(gasLevelTypeIDStr);

                // Insert data into database
                boolean isInserted = dbHelper.insertCustomerData(name, yyyymm, address, usedNumGas, gasLevelTypeID);

                if (isInserted) {
                    Toast.makeText(MainActivity.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error Inserting Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up onClick listener for View Customer button
        btnViewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến CustomerDetailsActivity
                Intent intent = new Intent(MainActivity.this, CustomerDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}
