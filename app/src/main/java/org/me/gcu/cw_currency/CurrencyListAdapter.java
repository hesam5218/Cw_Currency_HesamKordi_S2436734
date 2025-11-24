package org.me.gcu.cw_currency;


import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ArrayAdapter;

import android.widget.ImageView;

import android.widget.TextView;

import java.util.ArrayList;

import java.util.List;






public class CurrencyListAdapter extends ArrayAdapter<CurrencyRate> {


    //adaptor filter
    public CurrencyListAdapter(Context context, List<CurrencyRate> items) {

        super(context, 0, new ArrayList<>(items));
    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {

            convertView = LayoutInflater.from(getContext())

                    .inflate(R.layout.row_currency, parent, false);
        }



        CurrencyRate item = getItem(position);

        if (item == null) {

            return convertView;
        }



        TextView title = convertView.findViewById(R.id.rowTitle);


        TextView description = convertView.findViewById(R.id.rowDescription);


        TextView date = convertView.findViewById(R.id.rowDate);


        ImageView flag = convertView.findViewById(R.id.flagIcon);


        title.setText(item.getTitle());


        description.setText(item.getDescription());


        date.setText(item.getPubDate());





        //color

        double rate = extractRate(item.getDescription());


        if (rate < 1) {

            convertView.setBackgroundColor(0xFFCCFFCC); // light green

        } else if (rate < 5) {

            convertView.setBackgroundColor(0xFFFFFFCC); // light yellow

        } else if (rate < 10) {

            convertView.setBackgroundColor(0xFFFFE0B2); // light orange

        } else {

            convertView.setBackgroundColor(0xFFFFCCCB); // light red
        }





        //for flag

        String code = extractForeignCodeFromTitle(item.getTitle());

        int flagResId = getFlagResIdForCode(code);

        flag.setImageResource(flagResId);

        return convertView;
    }




    //extract rate for description

    private double extractRate(String description) {

        try {

            String[] parts = description.split("=");

            String right = parts[1].trim();

            return Double.parseDouble(right.split("\\s+")[0]);

        } catch (Exception e) {

            return 0.0;
        }

    }






    //get code from ttile

    private String extractForeignCodeFromTitle(String title) {

        if (title == null) return null;

        int lastOpen = title.lastIndexOf('(');

        int lastClose = title.lastIndexOf(')');

        if (lastOpen == -1 || lastClose == -1 || lastClose <= lastOpen) {

            return null;
        }

        return title.substring(lastOpen + 1, lastClose).trim().toUpperCase();
    }




   //icon image

    private int getFlagResIdForCode(String code) {

        if (code == null) {

            return R.drawable.placeholder_flag;

        }

        switch (code) {

            case "USD":

                return R.drawable.flag_usd;

            case "EUR":

                return R.drawable.flag_eur;

            case "AUD":

                return R.drawable.flag_aud;

            case "JPY":

                return R.drawable.flag_jpy;

            case "CAD":

                return R.drawable.flag_cad;

            case "CNY":

                return R.drawable.flag_cny;

            case "AED":

                return R.drawable.flag_aed;

            case "NZD":

                return R.drawable.flag_nzd;

            case "ARS":

                return R.drawable.flag_ars;

            case "ANG":

                return R.drawable.flag_ang;

            default:

                return R.drawable.placeholder_flag;

        }
    }
}
