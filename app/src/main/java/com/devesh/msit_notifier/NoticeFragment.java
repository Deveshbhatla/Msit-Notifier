package com.devesh.msit_notifier;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class NoticeFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> arrayList;
    private Button close;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ProgressDialog pDialog;

    private WebView web_View;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            final  View view = inflater.inflate(R.layout.fragment_news, container, false);

            listView = view.findViewById(R.id.list_news);
            arrayList=new ArrayList<>();

            //Log.d("jsoup", "size: ");
            new Thread(new Runnable() {
                @Override

                public void run() {
                    dialogBuilder=new AlertDialog.Builder(getContext());

                    final StringBuilder builder = new StringBuilder();

                    try {
                        final Document doc = Jsoup.connect("http://msit.in/notices").get();

                        final Elements li = doc.select("div.tab-content").select("li");

                     Elements links;

                        for (int i = 0; i < li.size(); i++) {

                            arrayList.add(li.get(i).select("li").text());
                            //Log.d("text",""+li.get(i).select("li").text());
                            //li.get(i).select("li").select("strong").remove();
                           // Log.d("jsoup", " " + li.get(i));

                            li.get(i).select("li").select("strong").remove();
                            //Log.d("text",""+li.get(i).select("li").select("strong").select("a").attr("href"));
                            links = doc.select("div.tab-content").select("li").select("a");

                            final Elements finalLinks = links;
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                            {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String url = "http://msit.in"+ finalLinks.get(position).attr("href");
                                   // Log.d("pdf", " " + url);
                                   View v = getLayoutInflater().inflate(R.layout.popup, null);
                                    web_View=v.findViewById(R.id.web_view);
                                    close=v.findViewById(R.id.dismissPopTop);
                                    web_View.getSettings().setJavaScriptEnabled(true);
                                    pDialog = new ProgressDialog(getContext());
                                    pDialog.setTitle("PDF");
                                    pDialog.setMessage("Loading...");
                                    pDialog.setIndeterminate(true);
                                    pDialog.setCancelable(true);
                                    WebSettings settings = web_View.getSettings();
                                    settings.setDomStorageEnabled(true);

                                    if (url.contains(".doc") || url.contains(".pdf"))
                                    {
                                        web_View.getSettings().setBuiltInZoomControls(true);
                                        web_View.getSettings().setDisplayZoomControls(false);
                                        web_View.loadUrl("https://docs.google.com/viewer?embedded=true&url="+url);



                                    }
                                    else
                                        {
                                            web_View.getSettings().setDisplayZoomControls(false);
                                            web_View.getSettings().setBuiltInZoomControls(true);
                                            web_View.setInitialScale(100);
                                            web_View.loadUrl(url);
                                        }

                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    listener();
                                    dialogBuilder.setView(v);
                                    dialog = dialogBuilder.create();
                                    dialog.show();


                                }
                            });
                        }
                        //Log.d("jsoup", "size: " + li.size());

                    } catch (IOException e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }
                    if(isAdded())
                    {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                //
                                arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1,
                                        android.R.id.text1, arrayList);
                                listView.setAdapter(arrayAdapter);

                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }



                }

            }).start();

            return view;

        }
    private void listener() {
        web_View.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pDialog.show(); web_View.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");

                web_View.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('ndfHFb-c4YZDc-nJjxad-nK2kYb-i5oIFb')[0].style.display='none'; })()");


            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pDialog.dismiss();
                web_View.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");

                web_View.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('ndfHFb-c4YZDc-nJjxad-nK2kYb-i5oIFb')[0].style.display='none'; })()");
            }
        });
    }

    }
