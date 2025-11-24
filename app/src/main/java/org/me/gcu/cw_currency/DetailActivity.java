




package org.me.gcu.cw_currency;

import android.os.Bundle;

import android.widget.Button;

import android.widget.EditText;

import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;




public class DetailActivity extends AppCompatActivity {

    private TextView titleView, descriptionView, pubDateView, linkView, categoryView;

    private EditText gbpInput, currInput;

    private TextView gbpResult, currResult;

    private Button convertGbpBtn, convertCurrBtn;

    private double rateValue = -1.0;   // parsed exchange rate



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        titleView = findViewById(R.id.titleView);

        descriptionView = findViewById(R.id.descriptionView);

        pubDateView = findViewById(R.id.pubDateView);

        linkView = findViewById(R.id.linkView);

        categoryView = findViewById(R.id.categoryView);

        gbpInput = findViewById(R.id.gbpInput);

        convertGbpBtn = findViewById(R.id.convertGbpBtn);

        gbpResult = findViewById(R.id.gbpResult);

        currInput = findViewById(R.id.currInput);

        convertCurrBtn = findViewById(R.id.convertCurrBtn);

        currResult = findViewById(R.id.currResult);




        //get data

        String title = getIntent().getStringExtra("title");

        String description = getIntent().getStringExtra("description");

        String pubDate = getIntent().getStringExtra("pubDate");

        String link = getIntent().getStringExtra("link");

        String category = getIntent().getStringExtra("category");




        //details display

        titleView.setText(title);

        descriptionView.setText(description);

        pubDateView.setText(pubDate);

        linkView.setText(link);

        categoryView.setText(category);




        //calc

        rateValue = parseRateFromDescription(description);

        //gbp to currency

        convertGbpBtn.setOnClickListener(v -> {

            if (!isRateValid()) {
                return;
            }

            String amt = gbpInput.getText().toString().trim();

            if (amt.isEmpty()) {
                Toast.makeText(this, "Enter an amount in GBP", Toast.LENGTH_SHORT).show();
                return;
            }


            try {
                double gbp = Double.parseDouble(amt);

                double converted = gbp * rateValue;

                gbpResult.setText(String.format(Locale.UK, "= %.2f", converted));
            }

            catch (NumberFormatException e) {

                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }


        });



        //currency to gbp


        convertCurrBtn.setOnClickListener(v -> {

            if (!isRateValid()) {
                return;
            }

            String amt = currInput.getText().toString().trim();

            if (amt.isEmpty()) {
                Toast.makeText(this, "Enter an amount in the foreign currency", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double foreign = Double.parseDouble(amt);
                double converted = foreign / rateValue;
                currResult.setText(String.format(Locale.UK, "= %.2f", converted));
            }

            catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }

        });
    }

  //gets rate from rss

    private double parseRateFromDescription(String desc) {

        if (desc == null) {
            return -1.0;
        }

        try {
            // Split around '='
            String[] parts = desc.split("=");

            if (parts.length < 2) {
                return -1.0;
            }


            String right = parts[1].trim();

            //first token is the number

            String firstToken = right.split("\\s+")[0];

            return Double.parseDouble(firstToken);

        } catch (Exception e) {

            return -1.0;
        }
    }

    //check the rate is correct

    private boolean isRateValid() {

        if (rateValue <= 0) {
            Toast.makeText(this, "Exchange rate not available", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}





