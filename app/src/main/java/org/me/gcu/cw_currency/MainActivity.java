






package org.me.gcu.cw_currency;


// Name                 Hesam Kordi
// Student ID           S2436734
// Programme of Study   Software Development






import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;

import android.os.Looper;

import android.text.Editable;

import android.text.TextWatcher;

import android.util.Log;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ListView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import org.xmlpull.v1.XmlPullParserException;

import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.StringReader;

import java.net.URL;

import java.net.URLConnection;

import java.util.ArrayList;




public class MainActivity extends AppCompatActivity implements OnClickListener {


    private ArrayAdapter<CurrencyRate> adapter;

    private ListView currencyListView;

    private Button startButton;

    private String result = "";

    private String url1 = "";

    private String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";

    private ArrayList<CurrencyRate> currencyList = new ArrayList<>();

    private Button btnMainUsd, btnMainEur, btnMainJpy;

    private CurrencyRate usdRate, eurRate, jpyRate;

    private Handler uiHandler;

    private Runnable autoRefreshTask;

    private static final long REFRESH_INTERVAL_MS = 5 * 60 * 1000; // 5 minutes




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //handler for refresh

        uiHandler = new Handler(Looper.getMainLooper());

        startButton = findViewById(R.id.startButton);

        btnMainUsd = findViewById(R.id.btnMainUsd);

        btnMainEur = findViewById(R.id.btnMainEur);

        btnMainJpy = findViewById(R.id.btnMainJpy);

        currencyListView = findViewById(R.id.currencyListView);

        startButton.setOnClickListener(this);



        //auto load when start and auto refrsh

        startProgress();

        autoRefreshTask = new Runnable() {

            @Override

            public void run() {

                startProgress();

                uiHandler.postDelayed(this, REFRESH_INTERVAL_MS);
            }
        };


        //first

        uiHandler.postDelayed(autoRefreshTask, REFRESH_INTERVAL_MS);
    }

    @Override
    public void onClick(View aview) {

        startProgress();
    }



    //network access thread
    public void startProgress() {

        new Thread(new Task(urlSource)).start();
    }

    private class Task implements Runnable {

        private String url;

        public Task(String aurl) {

            url = aurl;
        }



        @Override
        public void run() {

            URL aurl;

            URLConnection yc;

            BufferedReader in = null;

            String inputLine;

            String tempResult = "";

            ArrayList<CurrencyRate> tempList = new ArrayList<>();

            Log.d("MyTask", "in run");



            //download
            try {

                Log.d("MyTask", "in try (network)");

                aurl = new URL(url);

                yc = aurl.openConnection();

                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                while ((inputLine = in.readLine()) != null) {

                    tempResult = tempResult + inputLine;
                }

                in.close();
            }

            catch (IOException ae) {

                Log.e("MyTask", "ioexception", ae);


                //when offline

                uiHandler.post(() ->

                        Toast.makeText(

                                MainActivity.this,

                                "Unable to load data. Check your internet connection.",

                                Toast.LENGTH_LONG
                        ).show()

                );

                return;
            }



            //xml check

            int i = tempResult.indexOf("<?");

            if (i == -1) {

                Log.e("Parsing", "No XML start tag found");

                uiHandler.post(() ->

                        Toast.makeText(

                                MainActivity.this,

                                "No valid data received from server.",

                                Toast.LENGTH_LONG

                        ).show()
                );

                return;

            }

            tempResult = tempResult.substring(i);

            i = tempResult.indexOf("</rss>"); // final tag

            if (i == -1) {

                Log.e("Parsing", "No </rss> end tag found");

                uiHandler.post(() ->

                        Toast.makeText(

                                MainActivity.this,

                                "Data from server was incomplete.",

                                Toast.LENGTH_LONG

                        ).show()

                );

                return;

            }


            tempResult = tempResult.substring(0, i + 6);

            CurrencyRate currentItem = null;

            String currentTag = "";



            //parsing

            try {

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(true);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(tempResult));

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {

                        currentTag = xpp.getName();

                        if (currentTag.equalsIgnoreCase("item")) {

                            currentItem = new CurrencyRate();
                        }
                    }

                    else if (eventType == XmlPullParser.TEXT) {

                        String text = xpp.getText();

                        if (currentItem != null) {

                            switch (currentTag) {


                                case "title":

                                    currentItem.setTitle(text);

                                    break;


                                case "link":

                                    currentItem.setLink(text);

                                    break;


                                case "pubDate":

                                    currentItem.setPubDate(text);

                                    break;


                                case "description":

                                    currentItem.setDescription(text);

                                    break;


                                case "category":

                                    currentItem.setCategory(text);

                                    break;
                            }
                        }
                    }

                    else if (eventType == XmlPullParser.END_TAG) {

                        if (xpp.getName().equalsIgnoreCase("item")) {

                            if (currentItem != null) {

                                tempList.add(currentItem);
                            }
                        }

                        currentTag = "";

                    }

                    eventType = xpp.next();
                }


            }

            catch (XmlPullParserException | IOException e) {

                Log.e("Parsing", "EXCEPTION " + e);

                uiHandler.post(() ->

                        Toast.makeText(

                                MainActivity.this,

                                "Error parsing data from server.",

                                Toast.LENGTH_LONG

                        ).show()

                );

                return;
            }



            //updates

            result = tempResult;

            currencyList.clear();

            currencyList.addAll(tempList);

            uiHandler.post(() -> {

                adapter = new CurrencyListAdapter(MainActivity.this, currencyList);

                currencyListView.setAdapter(adapter);




                //main currencys access

                usdRate = findCurrencyByCode("USD");

                eurRate = findCurrencyByCode("EUR");

                jpyRate = findCurrencyByCode("JPY");




                setupMainCurrencyButton(btnMainUsd, usdRate, "USD");

                setupMainCurrencyButton(btnMainEur, eurRate, "EUR");

                setupMainCurrencyButton(btnMainJpy, jpyRate, "JPY");


                //search

                EditText searchBox = findViewById(R.id.searchBox);

                searchBox.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}



                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        adapter.getFilter().filter(s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });


                //details open

                currencyListView.setOnItemClickListener((parent, view, position, id) -> {

                    CurrencyRate selected = adapter.getItem(position);

                    if (selected != null) {

                        openDetail(selected);
                    }
                });
            });
        }
    }



    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (uiHandler != null && autoRefreshTask != null) {

            uiHandler.removeCallbacks(autoRefreshTask);
        }
    }


    private CurrencyRate findCurrencyByCode(String code) {

        for (CurrencyRate c : currencyList) {

            String title = c.getTitle();

            if (title != null && title.contains("(" + code + ")")) {
                return c;
            }
        }

        return null;
    }

    private void openDetail(CurrencyRate selected) {

        if (selected == null) return;

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        intent.putExtra("title", selected.getTitle());

        intent.putExtra("description", selected.getDescription());

        intent.putExtra("pubDate", selected.getPubDate());

        intent.putExtra("link", selected.getLink());

        intent.putExtra("category", selected.getCategory());

        startActivity(intent);
    }


    //helper for key currency ratea
    private double extractRate(String description) {

        try {

            if (description == null) return 0.0;

            String[] parts = description.split("=");

            String right = parts[1].trim();

            return Double.parseDouble(right.split("\\s+")[0]);
        }
        catch (Exception e) {

            return 0.0;

        }
    }


    //main currencey buttons
    private void setupMainCurrencyButton(Button button, CurrencyRate rate, String code) {

        if (button == null) return;

        if (rate == null) {

            //with no data

            button.setEnabled(false);

            button.setText(code);

        }
        else {

            double val = extractRate(rate.getDescription());

            String label;

            if (val > 0) {

                label = String.format("%s %.4f", code, val);

            }

            else {

                label = code;
            }


            button.setEnabled(true);

            button.setText(label);

            button.setOnClickListener(v -> openDetail(rate));
        }

    }

}