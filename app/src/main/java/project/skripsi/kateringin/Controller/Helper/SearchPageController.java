package project.skripsi.kateringin.Controller.Helper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import project.skripsi.kateringin.R;

public class SearchPageController extends AppCompatActivity {

    SearchView searchView;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page_view);

        searchView = findViewById(R.id.search_page_view);
        backButton = findViewById(R.id.search_page_back_button);
        searchView.setIconified(false);
        searchView.setQueryHint("Mau cari menu apa?");

        backButton.setOnClickListener(v ->{
            finish();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), FoodResultController.class);
                intent.putExtra("SEARCH", query);
                startActivity(intent);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}