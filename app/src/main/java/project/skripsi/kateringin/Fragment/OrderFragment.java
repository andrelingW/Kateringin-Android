package project.skripsi.kateringin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import project.skripsi.kateringin.Controller.Order.OrderHistoryController;
import project.skripsi.kateringin.Model.Order;
import project.skripsi.kateringin.Model.OrderItem;
import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Recycler.OrderDetailRecycleviewAdapter;
import project.skripsi.kateringin.Recycler.OrderRecycleviewAdapter;

public class OrderFragment extends Fragment {

    //XML
    RecyclerView recyclerView;
    AppCompatButton history;
    ConstraintLayout orderWarning;

    //FIELD
    FirebaseFirestore database;
    FirebaseAuth mAuth;
    OrderRecycleviewAdapter orderRecycleviewAdapter;
    ArrayList<Order> orders = new ArrayList<>();


    public static OrderFragment newInstance() {
        Bundle args = new Bundle();
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if (currentFragment instanceof OrderFragment) {
                ((OrderFragment) currentFragment).refreshData(); // Assuming you have a method named refreshData() in OrderFragment
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        binding(rootView);
        setField();
        readOrderData(this::orderAdapter);
        return rootView;
    }

    public void refreshData() {
        orderRecycleviewAdapter.clear();
        readOrderData(this::orderAdapter);
    }

    private void setField() {
        history.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), OrderHistoryController.class);
            startActivity(intent);
        });
    }

    private void binding(View rootView){
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        history = rootView.findViewById(R.id.order_history_button);
        recyclerView = rootView.findViewById(R.id.order_recycler_view);
        orderWarning = rootView.findViewById(R.id.order_warning);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
    }

    public void orderAdapter(ArrayList<Order> cartItems){
        if(cartItems.isEmpty()){
            orderWarning.setVisibility(View.VISIBLE);
        }else{
            orderWarning.setVisibility(View.GONE);
        }
        orderRecycleviewAdapter = new OrderRecycleviewAdapter(orders,this, getContext());
        recyclerView.setAdapter(orderRecycleviewAdapter);
    }

    private void readOrderData(FirestoreCallback firestoreCallback){
        CollectionReference collectionRef = database.collection("order");
        Query query = collectionRef
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .where(Filter.or(
                        Filter.equalTo("orderStatus","waiting"),
                        Filter.equalTo("orderStatus","ongoing"),
                        Filter.equalTo("orderStatus","shipping")
                ));

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String orderId = document.getId();
                    String storeId = document.getString("storeId");
                    String userId = document.getString("userId");
                    String receiverName = document.getString("receiverName");
                    String receiverEmail = document.getString("receiverEmail");
                    String receiverPhone = document.getString("receiverPhone");
                    String receiverAddress = document.getString("receiverAddress");
                    String orderStatus = document.getString("orderStatus");

                    ArrayList<Map<String, Object>> orderItemsList = (ArrayList<Map<String, Object>>) document.get("orderItems");
                    ArrayList<OrderItem> listOfOrderItem = new ArrayList<>();

                    for (Map<String, Object> item : orderItemsList) {
                        OrderItem order = new OrderItem();

                        order.setOrderItemId((String) item.get("orderItemId"));
                        order.setDate((String) item.get("date"));
                        order.setNote((String) item.get("note"));
                        order.setMenuId((String) item.get("menuId"));
                        order.setPrice(((Long) item.get("price")).intValue());
                        order.setQuantity(((Long) item.get("quantity")).intValue());
                        order.setTimeRange((String) item.get("timeRange"));
                        order.setReschedule((Boolean) item.get("reschedule"));
                        order.setOrderItemStatus((String) item.get("orderItemStatus"));
                        order.setOrderItemLinkTracker((String) item.get("orderItemLinkTracker"));

                        listOfOrderItem.add(order);
                    }

                    orders.add(new Order(
                            orderId,
                            storeId,
                            userId,
                            receiverAddress,
                            receiverName,
                            receiverPhone,
                            receiverEmail,
                            orderStatus,
                            listOfOrderItem
                    ));
                }
                firestoreCallback.onCallback(orders);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    private interface FirestoreCallback{
        void onCallback(ArrayList<Order> list);
    }

    public ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 200) {
                    refreshData();
                }
            });

}