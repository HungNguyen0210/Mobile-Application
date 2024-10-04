package com.example.gasbill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText etName, etYYYYMM, etAddress, etUsedNumGas;
    Spinner spinnerGasLevelTypeID;
    Button btnSave, btnViewCustomer;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Toolbar và thiết lập như ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo DrawerLayout và ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Xử lý các sự kiện khi chọn item trong NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Xử lý khi chọn "Home"
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_bill) {
                    // Xử lý khi chọn "Bill" và chuyển đến CustomerDetailsActivity
                    Intent intent = new Intent(MainActivity.this, CustomerDetailsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    // Xử lý khi chọn "Settings"
                    Toast.makeText(MainActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_change_unit) {
                    // Xử lý khi chọn "Change unit"
                    Intent intent = new Intent(MainActivity.this, ChangeUnitActivity.class);  // Tạo Activity ChangeUnitActivity
                    startActivity(intent);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        dbHelper = new DatabaseHelper(this);

        // Các phần còn lại của code như thiết lập EditText, Spinner và các sự kiện khác
        etName = findViewById(R.id.et_name);
        etYYYYMM = findViewById(R.id.et_yyyymm);
        etAddress = findViewById(R.id.et_address);
        etUsedNumGas = findViewById(R.id.et_used_num_gas);
        spinnerGasLevelTypeID = findViewById(R.id.spinner_gas_level_type_id);
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
                String gasLevelTypeIDStr = spinnerGasLevelTypeID.getSelectedItem().toString();

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
