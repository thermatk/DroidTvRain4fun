package com.thermatk.android.tvrain;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.LibsChecker;

/**
 * List
 */
public class StartActivity extends ListActivity {

	List<Map<String, Object>> loadedData;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		handler = new Handler();
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		sendRequest();
	}

	protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("title", name);
		temp.put("intent", intent);
		data.add(temp);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
		Intent intent = (Intent) map.get("intent");
		startActivity(intent);
	}

	private void sendRequest() {
		AsyncHttpClient client = new AsyncHttpClient();

		client.addHeader("Accept", "application/tvrain.api.2.8+json");
		client.addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
		client.addHeader("Accept-Encoding", "gzip, deflate");
		client.addHeader("X-User-Agent", "TV Client (Browser); API_CONSUMER_KEY=SECRETSECRETSECRET");
		client.addHeader("Content-Type", "application/x-www-form-urlencoded");
		client.addHeader("X-Result-Define-Thumb-Width", "200");
		client.addHeader("X-Result-Define-Thumb-height", "110");
		client.addHeader("Referer", "http://smarttv.tvrain.ru/");
		client.addHeader("Origin", "http://smarttv.tvrain.ru");
		client.addHeader("Connection", "keep-alive");

		RequestParams params = new RequestParams();
		client.get("https://api.tvrain.ru/api_v2/live/", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Ответ от серверов Дождя получен",
								Toast.LENGTH_SHORT).show();
					}
				});
				JSONArray qualities = null;
				try {
					qualities = response.getJSONArray("RTMP");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				loadedData = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < qualities.length(); i++) {
					JSONObject json_data = null;
					try {
						json_data = qualities.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					String labelq = null;
					try {
						labelq = json_data.getString("label");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					String urlq = null;
					try {
						urlq = json_data.getString("url");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					addItem(loadedData, labelq, new Intent(getApplicationContext(), LiveStream.class).putExtra("url", urlq));
				}
				setListAdapter(new SimpleAdapter(getApplicationContext(), loadedData, android.R.layout.simple_list_item_1, new String[]{"title"}, new int[]{android.R.id.text1}));
			}
		});
	}

}
