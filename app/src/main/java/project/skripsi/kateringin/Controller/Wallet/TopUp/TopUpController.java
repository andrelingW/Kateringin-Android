package project.skripsi.kateringin.Controller.Wallet.TopUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import project.skripsi.kateringin.R;
import project.skripsi.kateringin.Util.BottomSheetDialog.BottomSheetDialogTopUpConfirmation;
import project.skripsi.kateringin.Util.UtilClass.IdrFormat;

public class TopUpController extends AppCompatActivity {

    Toolbar toolbar;

    RadioGroup topUpAmount;
    RadioButton radioButton;
    EditText otherAmount;
    AppCompatButton submit;
    TextView otherAmountText, amount;

    int topUpValue, currentBalance;

    Boolean otherAmountChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_view);
        binding();
        setField();
        button();
    }

    private void button() {
        submit.setOnClickListener(v ->{
            if(otherAmountChecker){
                topUpValue = Integer.parseInt(otherAmount.getText().toString());
            }

            Bundle bundle = new Bundle();
            bundle.putInt("topUpAmount", topUpValue);
            bundle.putInt("currentBalance", currentBalance);

            BottomSheetDialogTopUpConfirmation bottomSheetDialogFragment = new BottomSheetDialogTopUpConfirmation();
            bottomSheetDialogFragment.setArguments(bundle);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        });
    }

    private void setField() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        otherAmountText.setVisibility(View.GONE);
        otherAmount.setVisibility(View.GONE);
        topUpAmount.setOnCheckedChangeListener((group, checkedId) -> {
            radioButton = findViewById(checkedId);
            if (radioButton != null) {
                if (checkedId == R.id.top_up_payment_page_50) {
                    topUpValue = 50000;
                    hideOtherAmount();
//                    otherAmountText.setVisibility(View.GONE);
//                    otherAmount.setVisibility(View.GONE);
//                    amount.setText(IdrFormat.format(topUpValue));
//                    otherAmountChecker = false;
                } else if (checkedId == R.id.top_up_payment_page_100) {
                    topUpValue = 100000;
                    hideOtherAmount();
//                    otherAmountText.setVisibility(View.GONE);
//                    otherAmount.setVisibility(View.GONE);
//                    amount.setText(IdrFormat.format(topUpValue));
//
//                    otherAmountChecker = false;
                } else if (checkedId == R.id.top_up_payment_page_150) {
                    topUpValue = 150000;
                    hideOtherAmount();
//                    otherAmountText.setVisibility(View.GONE);
//                    otherAmount.setVisibility(View.GONE);
//                    otherAmountChecker = false;
                } else if (checkedId == R.id.top_up_payment_page_200) {
                    topUpValue = 200000;
                    hideOtherAmount();
//                    otherAmountText.setVisibility(View.GONE);
//                    otherAmount.setVisibility(View.GONE);
//                    otherAmountChecker = false;
                } else if (checkedId == R.id.top_up_payment_page_other_amount) {
                    unHideOtherAmount();
                    otherAmount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(!s.toString().isEmpty()){
                                int value = Integer.parseInt(s.toString());
                                amount.setText(IdrFormat.format(value));
                            } else{
                                amount.setText(IdrFormat.format(0));
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
        });

    }

    public void hideOtherAmount(){
        otherAmountText.setVisibility(View.GONE);
        otherAmount.setVisibility(View.GONE);
        amount.setText(IdrFormat.format(topUpValue));
        otherAmountChecker = false;
    }

    public void unHideOtherAmount(){
        topUpValue = 0;
        otherAmount.setText("");
        amount.setText(IdrFormat.format(0));
        otherAmountText.setVisibility(View.VISIBLE);
        otherAmount.setVisibility(View.VISIBLE);
        otherAmountChecker = true;
    }

    private void binding() {
        topUpAmount = findViewById(R.id.top_up_payment_page_radio_group);
        otherAmount = findViewById(R.id.top_up_payment_page_other_amount_edit_text);
        otherAmountText = findViewById(R.id.top_up_payment_page_other_amount_text);
        currentBalance = (int) getIntent().getSerializableExtra("CURRENT_BALANCE");
        amount = findViewById(R.id.top_up_payment_page_total_top_up_text);
        submit = findViewById(R.id.top_up_payment_page_submit_button);
        toolbar = findViewById(R.id.top_up_payment_page_toolbar);
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