package project.skripsi.kateringin.Controller.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import project.skripsi.kateringin.R;

public class SearchPageController extends AppCompatActivity {

    SearchView searchView;
    ImageButton backButton;

    TextView quickSearchNasi, quickSearchMie, quickSearchSoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page_view);

        searchView = findViewById(R.id.search_page_view);
        backButton = findViewById(R.id.search_page_back_button);
        quickSearchNasi = findViewById(R.id.quick_search_nasi);
        quickSearchMie = findViewById(R.id.quick_search_mie);
        quickSearchSoto = findViewById(R.id.quick_search_soto);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("What menu are you looking for?");

        backButton.setOnClickListener(v ->{
            finish();
        });

        quickSearchNasi.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), FoodResultController.class);
            intent.putExtra("SEARCH", "nasi");
            startActivity(intent);
            finish();
        });

        quickSearchMie.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), FoodResultController.class);
            intent.putExtra("SEARCH", "mie");
            startActivity(intent);
            finish();
        });

        quickSearchSoto.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), FoodResultController.class);
            intent.putExtra("SEARCH", "soto");
            startActivity(intent);
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