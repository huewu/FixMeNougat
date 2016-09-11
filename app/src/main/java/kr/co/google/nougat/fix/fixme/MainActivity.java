package kr.co.google.nougat.fix.fixme;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import dalvik.system.DexFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new MyListAdapter(this));
        Button sendMessage = (Button) findViewById(R.id.connServer);
        Button loadLib = (Button) findViewById(R.id.loadExtLib);

        sendMessage.setOnClickListener(this);
        loadLib.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.connServer:
                MyNetworkService.startService(this);
                break;
            case R.id.loadExtLib:
                loadExpansionLibrary();
                break;
        }
    }

    private void loadExpansionLibrary() {

        try {
            moveExpansionFile();
            File myDex = new File(getDir("dex", Context.MODE_PRIVATE), "expansion.dex");
            Class clz = Class.forName("dalvik.system.DexPathList");
            Method method = clz.getDeclaredMethod("loadDexFile", File.class, File.class);
            method.setAccessible(true);
            DexFile file = (DexFile) method.invoke(null, myDex, null);
            Log.d(TAG, "Load custom dex:"  + file.getName());
        } catch (Exception e) {
            throw new RuntimeException("Fail to load an expansion library");
        }


    }

    private void moveExpansionFile() {
        File dexPath = new File(getDir("dex", Context.MODE_PRIVATE),
                "expansion.dex");

        BufferedInputStream bis = null;
        OutputStream dexWriter = null;

        final int BUF_SIZE = 8 * 1024;
        try {
            bis = new BufferedInputStream(getAssets().open("expansion.dex"));

            dexWriter = new BufferedOutputStream(
                    new FileOutputStream(dexPath));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyListAdapter extends SimpleAdapter {

        private static final int EDIT_TEXT = 0;
        private static final int SIMPLE_TEXT = 1;

        MyListAdapter(final Context context) {
            super(context, null, 0, null, null);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return EDIT_TEXT;
            } else {
                return SIMPLE_TEXT;
            }
        }

        @Override
        public int getCount() {
            return 15;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            if (convertView == null) {
               view = generateView(getItemViewType(position), parent);
            } else {
                view = convertView;
            }

            return bindView(view, position);
        }

        private View bindView(View view, int position) {

            switch (getItemViewType(position)) {
                case EDIT_TEXT:
                    break;
                case SIMPLE_TEXT:
                    TextView tv = ((TextView)view.findViewById(R.id.list_text));
                    tv.setText("Value:" + position);
                    break;
            }
            return view;
        }

        private View generateView(int itemViewType, ViewGroup parent) {

            switch (itemViewType) {
                case EDIT_TEXT:
                    return LayoutInflater.from(
                            parent.getContext()).inflate(
                            R.layout.simple_search_list_item, parent, false);
                case SIMPLE_TEXT:
                    return LayoutInflater.from(
                            parent.getContext()).inflate(
                            R.layout.simple_text_list_item, parent, false);
            }

            return new View(parent.getContext());
        }
    }
}
