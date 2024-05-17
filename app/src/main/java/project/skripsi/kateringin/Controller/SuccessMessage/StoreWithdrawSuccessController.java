package project.skripsi.kateringin.Controller.SuccessMessage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import project.skripsi.kateringin.Controller.StoreEarning.StoreEarningController;
import project.skripsi.kateringin.Controller.Wallet.WalletController;
import project.skripsi.kateringin.R;

public class StoreWithdrawSuccessController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_success_view);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue, null));
        getWindow().getDecorView().setSystemUiVisibility(0);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, StoreEarningController.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();


        }, 3000);
    }
}