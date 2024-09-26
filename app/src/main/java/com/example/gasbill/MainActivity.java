package com.example.gasbill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText etName, etYYYYMM, etAddress, etUsedNumGas;
    Spinner spinnerGasLevelTypeID; // Thay đổi thành Spinner
    Button btnSave, btnViewCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.et_name);
        etYYYYMM = findViewById(R.id.et_yyyymm);
        etAddress = findViewById(R.id.et_address);
        etUsedNumGas = findViewById(R.id.et_used_num_gas);
        spinnerGasLevelTypeID = findViewById(R.id.spinner_gas_level_type_id); // Liên kết với Spinner
        btnSave = findViewById(R.id.btn_save);
        btnViewCustomer = findViewById(R.id.btn_view_customer);

        // Thiết lập DatePicker cho etYYYYMM
        etYYYYMM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault());
                    String formattedDate = dateFormat.format(calendar.getTime());

                    etYYYYMM.setText(formattedDate);
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        // Thiết lập Spinner với các giá trị 1 và 2
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"1", "2"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGasLevelTypeID.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String yyyymm = etYYYYMM.getText().toString();
                String address = etAddress.getText().toString();
                String usedNumGasStr = etUsedNumGas.getText().toString();
                String gasLevelTypeIDStr = spinnerGasLevelTypeID.getSelectedItem().toString(); // Lấy giá trị từ Spinner

                if (name.isEmpty() || yyyymm.isEmpty() || address.isEmpty() || usedNumGasStr.isEmpty() || gasLevelTypeIDStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double usedNumGas = Double.parseDouble(usedNumGasStr);
                int gasLevelTypeID = Integer.parseInt(gasLevelTypeIDStr);

                boolean isInserted = dbHelper.insertCustomerData(name, yyyymm, address, usedNumGas, gasLevelTypeID);

                if (isInserted) {
                    Toast.makeText(MainActivity.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error Inserting Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnViewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}