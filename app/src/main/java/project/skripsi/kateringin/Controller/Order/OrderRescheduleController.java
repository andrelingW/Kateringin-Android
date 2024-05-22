package project.skripsi.kateringin.Controller.Order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.UtilClass.CustomRescheduleDateValidator;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class OrderRescheduleController extends AppCompatActivity {

    //XML
    Toolbar toolbar;
    EditText rescheduleDate;
    TextView rescheduleItemName, rescheduleItemPrice, rescheduleItemQuantity, rescheduleItemNote, rescheduleItemTime, rescheduleItemDate;
    RadioGroup radioGroup;
    RadioButton rescheduleTime;
    AppCompatButton cancelButton, submitButton;
    ImageView rescheduleMenuImage;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    OrderItem order;
    String timeRange, date;
    Boolean checkRadio;
    String mainOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_reschedule_view);
        binding();
        setField();
        buttonAndLogic();
    }



    private void binding() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.reschedule_toolbar);
        order = (OrderItem) getIntent().getSerializableExtra("ORDER_DETAIL_ITEM");
        mainOrderId = (String) getIntent().getSerializableExtra("ORDER_ID");
        rescheduleMenuImage = findViewById(R.id.reschedule_item_menu_image);
        rescheduleItemName = findViewById(R.id.reschedule_item_menu_name);
        rescheduleItemPrice = findViewById(R.id.reschedule_item_menu_price);
        rescheduleItemQuantity = findViewById(R.id.reschedule_item_quantity);
        rescheduleItemNote = findViewById(R.id.reschedule_item_note);
        rescheduleItemTime = findViewById(R.id.reschedule_item_time);
        rescheduleItemDate = findViewById(R.id.reschedule_item_date);
        rescheduleDate = findViewById(R.id.reschedule_item_date_change);
        radioGroup = findViewById(R.id.reschedule_time_group);
        cancelButton = findViewById(R.id.reschedule_cancel_button);
        submitButton = findViewById(R.id.reschedule_button);
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DocumentReference menuDocRef = database.collection("menu").document(order.getMenuId());
        menuDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    rescheduleItemName.setText(document.getString("menuName"));
                    long menuPriceTemp = document.getLong("menuPrice");
                    rescheduleItemPrice.setText(IdrFormat.format(menuPriceTemp));

                    Glide.with(this)
                            .load(document.getString("menuPhotoUrl"))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .into(rescheduleMenuImage);
                } else {
                    System.out.println("No such document");
                }
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });

        rescheduleItemQuantity.setText("x" + order.getQuantity());
        rescheduleItemTime.setText(order.getTimeRange());
        rescheduleItemDate.setText(order.getDate());
        if (order.getNote() == null){
            rescheduleItemNote.setText("-");
        } else{
            rescheduleItemNote.setText(order.getNote());
        }
        rescheduleDate.setText(order.getDate());
        switch (order.getTimeRange()){
            case "08:00 - 12:00":
                rescheduleTime = findViewById(R.id.reschedule_morning_time);
                rescheduleTime.setChecked(true);
                break;
            case "12:00 - 16:00":
                rescheduleTime = findViewById(R.id.reschedule_afternoon_time);
                rescheduleTime.setChecked(true);
                break;
            case "16:00 - 20:00":
                rescheduleTime = findViewById(R.id.reschedule_night_time);
                rescheduleTime.setChecked(true);
                break;
        }
    }

    private void buttonAndLogic() {
        radioGroup.setOnCheckedChangeListener((group, checkId) -> {
            rescheduleTime = findViewById(checkId);
            if (rescheduleTime != null) {
                if (checkId == R.id.reschedule_morning_time) {
                    timeRange = "08:00 - 12:00";
                    checkRadio = true;
                } else if (checkId == R.id.reschedule_afternoon_time) {
                    timeRange = "12:00 - 16:00";
                    checkRadio = true;
                } else if (checkId == R.id.reschedule_night_time) {
                    timeRange = "16:00 - 20:00";
                    checkRadio = true;
                }
            }
        });

        rescheduleDate.setOnClickListener(v ->{
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(new CustomRescheduleDateValidator()).build())
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
                rescheduleDate.setText(date);
            });
            materialDatePicker.show(getSupportFragmentManager(), "tag");
        });

        cancelButton.setOnClickListener(v -> finish());

        submitButton.setOnClickListener(v ->{
            String orderItemId = order.getOrderItemId();
            DocumentReference docRef = database.collection("order").document(mainOrderId);
            docRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        String orderId = document.getId();
                        String storeId = document.getString("storeId");
                        String userId = document.getString("userId");
                        String receiverName = document.getString("receiverName");
                        String receiverPhone = document.getString("receiverPhone");
                        String receiverAddress = document.getString("receiverAddress");
                        String orderStatus = document.getString("orderStatus");

                        ArrayList<Map<String, Object>> orderItemsList = (ArrayList<Map<String, Object>>) document.get("orderItems");
                        ArrayList<OrderItem> listOfOrderItem = new ArrayList<>();

                        for (Map<String, Object> item : orderItemsList) {
                            OrderItem order = new OrderItem();
                            String checker = (String) item.get("orderItemId");
                            Log.d("TAG", "buttonAndLogic: " +checker + "-" + orderItemId);
                            if(checker.equalsIgnoreCase(orderItemId)) {
                                order.setDate(date);
                                order.setTimeRange(timeRange);
                                order.setReschedule(true);
                            } else {
                                order.setDate((String) item.get("date"));
                                order.setTimeRange((String) item.get("timeRange"));
                                order.setReschedule((Boolean) item.get("reschedule"));
                            }
                            order.setOrderItemStatus((String) item.get("orderItemStatus"));
                            order.setOrderItemId((String) item.get("orderItemId"));
                            order.setNote((String) item.get("note"));
                            order.setMenuId((String) item.get("menuId"));
                            order.setPrice(((Long) item.get("price")).intValue());
                            order.setQuantity(((Long) item.get("quantity")).intValue());

                            listOfOrderItem.add(order);

                            Order newOrder = new Order(
                                    orderId,
                                    storeId,
                                    userId,
                                    receiverAddress,
                                    receiverName,
                                    receiverPhone,
                                    orderStatus,
                                    listOfOrderItem
                            );

                            docRef.set(newOrder);
                            finish();
                        }
                    }else{
                        Log.i("TAG", "No such document");
                    }
                } else {
                    Log.i("TAG", "get failed with ", task.getException());
                }
            });



        });
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("ORDER_ID", mainOrderId);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}