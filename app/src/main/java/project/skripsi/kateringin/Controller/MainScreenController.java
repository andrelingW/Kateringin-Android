package project.skripsi.kateringin.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import project.skripsi.kateringin.Fragment.CartFragment;
import project.skripsi.kateringin.Fragment.ExploreFragment;
import project.skripsi.kateringin.Fragment.OrderFragment;
import project.skripsi.kateringin.Fragment.UserFragment;
import project.skripsi.kateringin.R;

public class MainScreenController extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_view);

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        frameLayout = findViewById(R.id.frame_layout);

        int fragmentId = getIntent().getIntExtra("fragmentId", -1);
        int menuItemId = getIntent().getIntExtra("menuItemId", -1);
        if (fragmentId != -1 && menuItemId != -1) {
            // Navigate to the specified fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, OrderFragment.newInstance())
                    .commit();

            // Set the selected menu item in the bottom navigation bar
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
            bottomNavigationView.setSelectedItemId(menuItemId);
        } else{
            loadFragement(new ExploreFragment(), true);
        }



        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if(itemId == R.id.menu_explore){
                    loadFragement(new ExploreFragment(), true);
                }else if(itemId == R.id.menu_order){
                    loadFragement(new OrderFragment(), true);
                }else if(itemId == R.id.menu_cart){
                    loadFragement(new CartFragment(), true);
                }else if(itemId == R.id.menu_profile){
                    loadFragement(new UserFragment(), true);
                }
                return true;
            }
        });


    }

    private void loadFragement(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isAppInitialized){
            fragmentTransaction.add(R.id.frame_layout, fragment);
        }else{
            fragmentTransaction.replace(R.id.frame_layout, fragment);
        }
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}