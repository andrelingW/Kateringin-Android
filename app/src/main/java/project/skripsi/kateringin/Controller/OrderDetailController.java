package project.skripsi.kateringin.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.User;
import project.skripsi.kateringin.R;

public class OrderDetailController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_view);
        Order order = (Order) getIntent().getSerializableExtra("DETAILS_SCREEN");
        Log.d("TAG", "onCreate: " + order.toString());
    }
}