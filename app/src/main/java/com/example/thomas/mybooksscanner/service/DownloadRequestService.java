package com.example.thomas.mybooksscanner.service;

/**
 * Created by Thomas on 17.07.2018.
 */


import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DownloadRequestService extends IntentService {

    private static final String TAG = DownloadRequestService.class.getSimpleName();
    public static final String PENDING_RESULT_EXTRA = "pending_result";
    public static final String URL_EXTRA = "url";
    public static final String QUERY_RESULT_EXTRA = "url";

    public static final int RESULT_CODE = 0;
    public static final int INVALID_URL_CODE = 1;
    public static final int ERROR_CODE = 2;

    public DownloadRequestService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);
        try {
            try {
                URL url = new URL(intent.getStringExtra(URL_EXTRA));
                String answer = executeRequest(url);
                Intent result = new Intent();
                result.putExtra(QUERY_RESULT_EXTRA,answer);
                reply.send(this,RESULT_CODE,result);
            } catch (MalformedURLException e) {
                reply.send(INVALID_URL_CODE);
            } catch (Exception e) {
                reply.send(ERROR_CODE);
            }

        } catch (PendingIntent.CanceledException e) {
            Log.i(TAG,"reply cancelled",e);
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    private String readStream(InputStream in) {

/*
        <classify xmlns="http://classify.oclc.org">
           <response code="2"/>
           <work title="Der Tote im Strandkorb" owi="4649227675" itemtype="itemtype-book-digital" holdings="1" format="eBook" eholdings="1" editions="2" author="Johannsen, Anna">1013472193</work>
        </classify>
*/

        if (in == null) return "";

        String result = "";

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            doc = dBuilder.parse(in);
        } catch (SAXException e) {
            Log.e(TAG,"Parse error");
            return "";
        } catch (IOException e) {
            Log.e(TAG, "IO error");
            return "";
        }

        NodeList nList = doc.getElementsByTagName("work");

        for (int i=0; i<nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element2 = (Element) node;
/*
                result = result + "\nTitle: "+ element2.getAttributeNode("title").getValue()+"\n";
                result = result + "Author: "+ element2.getAttributeNode("author").getValue()+"\n";
                result = result + "----------------------";
*/
                result = element2.getAttributeNode("title").getValue();
            }
        }
        return result;
    }

    private String executeRequest(URL url) {
        // Request format: http://classify.oclc.org/classify2/Classify?isbn=9781542047906

        String result = "";
        HttpURLConnection urlConnection;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "MSIE");
        } catch (IOException e) {
            Log.e(TAG, "Request failed");
            return "";
        }

        try {
            InputStream in = null;
            try {
                in = urlConnection.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "IO error");
                return "";
            } catch (Exception e) {
                Log.e(TAG,e.toString());
                return "";
            }
            result = readStream(in);
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }
}
