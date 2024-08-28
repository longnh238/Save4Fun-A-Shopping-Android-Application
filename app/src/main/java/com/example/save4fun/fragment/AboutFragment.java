package com.example.save4fun.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.save4fun.R;

public class AboutFragment extends Fragment {

    WebView webViewAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        webViewAbout = view.findViewById(R.id.webViewAbout);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><p align=\"justify\">");
        sb.append("<strong>Save4Fun</strong> - A shopping application to make shopping " +
                "experience easier and more convenient.");
        sb.append("<br><br>");
        sb.append("With <strong>Save4Fun</strong>, we focus on keeping things organized and efficient. From making your profiles to\n" +
                "accessing your account info and making changes, our easy sign-in process ensures a smooth\n" +
                "experience.");
        sb.append("<br><br>");
        sb.append("Whether you\n" +
                "are grabbing groceries or just picking up a few things, <strong>Save4Fun</strong> is here to make your shopping\n" +
                "experience the best it can be &#128512;");
        sb.append("<div style=\"text-align: center\">");
        sb.append("<a href=\"https://www.google.com/\" align=\"center\">Visit our website</a>");
        sb.append("</div>");
        sb.append("</p></body></html>");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webViewAbout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webViewAbout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webViewAbout.getSettings().setJavaScriptEnabled(true);
        // webViewAbout.loadData(sb.toString(), "text/html", "utf-8");
        webViewAbout.loadDataWithBaseURL(null, sb.toString(), "text/html", "UTF-8", null);

        return view;
    }
}