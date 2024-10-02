package com.example.gasbill;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.navigation.NavigationView;

public class CustomerDetailsActivity extends AppCompatActivity {

    private TextView tvCustomerId, tvCustomerName, tvCustomerYYYYMM, tvCustomerAddress, tvCustomerUsedNumGas, tvCustomerGasLevelTypeID;
    private Button btnFirst, btnPrevious, btnNext, btnLast;
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private int currentPosition = 0;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

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
                    Intent intent = new Intent(CustomerDetailsActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_bill) {
                    // Xử lý khi chọn "Bill" và chuyển đến CustomerDetailsActivity
                    Intent intent = new Intent(CustomerDetailsActivity.this, CustomerDetailsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    // Xử lý khi chọn "Settings"
                    Toast.makeText(CustomerDetailsActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Tắt tiêu đề mặc định
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Tìm kiếm SearchView
        SearchView searchView = findViewById(R.id.search_view);

        // Lắng nghe sự kiện mở SearchView
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nhấn vào, mở rộng SearchView thành match_parent
                searchView.setLayoutParams(new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.MATCH_PARENT, // set width to match_parent
                        Toolbar.LayoutParams.WRAP_CONTENT
                ));
                searchView.setQueryHint("Search by name or address");
            }
        });

        // Khi SearchView bị đóng, trở về kích thước ban đầu và đúng vị trí
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Thiết lập lại vị trí layout_gravity về phía bên phải
                Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT
                );
                layoutParams.gravity = Gravity.END; // Đặt gravity cho nút ở bên phải
                searchView.setLayoutParams(layoutParams);
                return false;
            }
        });
        // Đặt màu chữ cho SearchView
        int searchTextColor = Color.WHITE; // Màu trắng
        int hintColor = Color.WHITE; // Màu trắng cho gợi ý văn bản
        int cursorColor = Color.WHITE; // Màu trắng cho con trỏ

        // Áp dụng màu chữ cho SearchView
        searchView.setQueryHint("Search by name or address");
        searchView.setIconifiedByDefault(true);

        // Lấy TextView bên trong SearchView
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(searchTextColor); // Đặt màu chữ
        searchEditText.setHintTextColor(hintColor); // Đặt màu cho gợi ý văn bản

        searchEditText.setTextCursorDrawable(new ColorDrawable(cursorColor));

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
        setCardViewClickListener();
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
        btnFirst.setOnClickListener(v -> {
            if (cursor.moveToFirst()) {
                currentPosition = 0;
                displayCustomerDetails();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (cursor.moveToPrevious()) {
                currentPosition--;
                displayCustomerDetails();
            } else {
                Toast.makeText(CustomerDetailsActivity.this, "No previous customer", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (cursor.moveToNext()) {
                currentPosition++;
                displayCustomerDetails();
            } else {
                Toast.makeText(CustomerDetailsActivity.this, "No next customer", Toast.LENGTH_SHORT).show();
            }
        });

        btnLast.setOnClickListener(v -> {
            if (cursor.moveToLast()) {
                currentPosition = cursor.getCount() - 1;
                displayCustomerDetails();
            }
        });

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCustomer(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCustomer(newText);
                return false;
            }
        });

    }

    private void setCardViewClickListener() {
        findViewById(R.id.card_view).setOnClickListener(v -> showUpdateDialog());
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Customer Details");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_customer, null);
        EditText editTextName = dialogView.findViewById(R.id.edit_customer_name);
        EditText editTextAddress = dialogView.findViewById(R.id.edit_customer_address);
        EditText editTextUsedNumGas = dialogView.findViewById(R.id.edit_customer_used_num_gas);
        EditText editTextGasLevelTypeId = dialogView.findViewById(R.id.edit_customer_gas_level_type_id);

        // Lấy thông tin hiện tại để hiển thị
        editTextName.setText(tvCustomerName.getText().toString().replace("Name: ", ""));
        editTextAddress.setText(tvCustomerAddress.getText().toString().replace("Address: ", ""));
        editTextUsedNumGas.setText(tvCustomerUsedNumGas.getText().toString().replace("Used Num Gas: ", ""));
        editTextGasLevelTypeId.setText(tvCustomerGasLevelTypeID.getText().toString().replace("Gas Level Type ID: ", ""));

        builder.setView(dialogView);

        builder.setPositiveButton("Update", (dialog, which) -> updateCustomerDetails(
                editTextName.getText().toString(),
                editTextAddress.getText().toString(),
                Double.parseDouble(editTextUsedNumGas.getText().toString()),
                Integer.parseInt(editTextGasLevelTypeId.getText().toString())
        ));

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateCustomerDetails(String name, String address, double usedNumGas, int gasLevelTypeId) {
        int customerId = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
        String yyyymm = tvCustomerYYYYMM.getText().toString().replace("YYYYMM: ", "");

        dbHelper.updateCustomer(customerId, name, yyyymm, address, usedNumGas, gasLevelTypeId);

        Toast.makeText(this, "Customer details updated", Toast.LENGTH_SHORT).show();

        // Lấy lại cursor mới
        cursor = dbHelper.getAllCustomers();

        // Cập nhật lại thông tin hiển thị
        cursor.moveToPosition(currentPosition); // Đặt cursor về vị trí hiện tại
        displayCustomerDetails();
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
                tvCustomerYYYYMM.setText("Date: " + cursor.getString(yyyymmIndex));
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


    private void searchCustomer(String query) {
        cursor = dbHelper.searchCustomerByNameOrAddress(query);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            displayCustomerDetails();
        } else {
            Toast.makeText(this, "No customer found", Toast.LENGTH_SHORT).show();
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