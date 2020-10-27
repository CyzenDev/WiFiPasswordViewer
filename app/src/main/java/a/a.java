/*
 * Copyright (C) 2019-2020 Cyzen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package a;

import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class a extends ListActivity {
    public List<Map<String, String>> mList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, String> map = new HashMap<>();
        try {
            Object[] k = grep("-w SSID");
            Object[] v = grep("reS");
            for (int i = 0; i < k.length; i++) {
                map = new HashMap<>();
                map.put("k", k[i].toString().split(";")[1].split("&q")[0]);
                map.put("v", v[i].toString().contains(";") ? v[i].toString().split(";")[1].split("&q")[0] : "无密码");
                mList.add(map);
            }
        } catch (Exception e) {
            map.put("k", "读取失败");
            map.put("v", e.getMessage());
            mList.add(map);
        }

        setListAdapter(new SimpleAdapter(this, mList, android.R.layout.simple_list_item_2, new String[]{"k", "v"}, new int[]{android.R.id.text1, android.R.id.text2}));
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, String.format("%s\n%s", mList.get(position).get("k"), mList.get(position).get("v"))));
        Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_LONG).show();
    }

    public Object[] grep(String text) throws Exception {
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(String.format("su -c grep %s /data/misc/wifi/WifiConfigStore.xml", text)).getInputStream())).lines().toArray();
    }
}
