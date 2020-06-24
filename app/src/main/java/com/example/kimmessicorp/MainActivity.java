package com.example.kimmessicorp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class MainActivity extends AppCompatActivity {

    private long time= 0;
    private TextView tv_id;
    private DrawerLayout drawerLayout;
    private View drawerView;
    ArrayAdapter<String> adapter;

    //받아올 data(사용자가 쓴 글)
    List<String> data = new ArrayList<>();

    //test for listview
//    static final String[] LIST_POST = {
//            "post1 - Paul - 2020/6/6",
//            "post2 - Mike - 2020/6/7",
//            "post3 - Sarah - 2020/6/8"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.ii);
        ab.setDisplayUseLogoEnabled(true) ;
        ab.setDisplayShowHomeEnabled(true) ;

        tv_id = findViewById(R.id.tv_id);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        final String userPass = intent.getStringExtra("userPass");
        final String userName = intent.getStringExtra("userName");
        final String userAge = intent.getStringExtra("userAge");

        tv_id.setText(userID);

            Button btn_infor = (Button)findViewById(R.id.btn_infor);

        btn_infor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,InformationActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("userPass",userPass);
                intent.putExtra("userName",userName);
                intent.putExtra("userAge",userAge);
                startActivity(intent);
            }
        });

        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ImageButton close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() { //메뉴 닫기 버튼 누를 때 닫기
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });



        //xml에서 담아온 listview 정의
        ListView listView = (ListView) findViewById(R.id.listview_posts);
        //adapter 선언: 리스트 방식, LIST_POST의 정보를 adapter에
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        //listview와 adapter를 연결
        listView.setAdapter(adapter);

        //listview 테스트
        data.add("post1 - Paul - 2020/6/6");
        data.add("post2 - Mike - 2020/6/7");
        data.add("post3 - Sarah - 2020/6/8");
        //상태 저장
        //adapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //list를 눌렀을 때 화면 전환 -> 게시글 보여주기
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);

                String str = adapter.getItem(position);
                String BBS_NO = "";
                int i = 0;
                while(str.charAt(i) != '-') {
                    BBS_NO += str.charAt(i);
                    i++;
                }
                intent.putExtra("BBS_NO",BBS_NO);

                startActivity(intent);
                Toast.makeText(getApplicationContext(), "게시글", Toast.LENGTH_SHORT).show();
            }
        });


        //버튼 선언, xml에서 가져오기
        ImageButton btn_write = (ImageButton) findViewById(R.id.btn_write);

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //버튼을 눌렀을 때 화면 전환 -> 작성페이지로
                //fill blank -> Activity name.class
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"새 게시글 작성",Toast.LENGTH_SHORT).show();
            }
        });

        Getlistdata getlistdata = new Getlistdata();
        getlistdata.execute();

    }

        public class Getlistdata extends AsyncTask<String, Void, String[]> {

            private final String LOG_TAG = Getlistdata.class.getSimpleName();

            private  String[] getListDataFromJson(String dataJsonStr)
                throws JSONException{

                final String STR_NUM = "BBS_NO";
                final String STR_ID = "userID";
                final String STR_TITLE = "TITLE";
                final String STR_CON = "CONTENT";
                final String STR_DATE = "REG_DATE";

                JSONObject dataJson = new JSONObject(dataJsonStr);
                JSONArray dataArray = dataJson.getJSONArray("BBSList");


                String[] resultStrs = new String[dataArray.length()];
                for(int i=0;i < dataArray.length(); i++){
                    int num;
                    String ID;
                    String description;
                    String title;
                    String date;

                    JSONObject data = dataArray.getJSONObject(i);

                    num = data.getInt(STR_NUM);
                    ID = data.getString(STR_ID);
                    description = data.getString(STR_CON);
                    title = data.getString(STR_TITLE);
                    date = data.getString(STR_DATE);

                    resultStrs[i] = num + "-" + ID + "-" + title + "-" + date;
                 }

                for (String s: resultStrs) {
                    Log.v(LOG_TAG, "data entry " + s);
                }
                return resultStrs;
            }


            @Override
            protected String[] doInBackground(String... params) {
                HttpURLConnection urlConnection =  null;
                BufferedReader reader = null;

                String dataJsonStr = null;

                try {
                    final String URL = "http://kimmessi.dothome.co.kr/BBSList.php";

                    Uri builtUri = Uri.parse(URL).buildUpon().build();

                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG,"Built URI" + builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if(inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;

                    while((line = reader.readLine()) != null) {

                        buffer.append(line + "\n");

                    }

                    if(buffer.length() == 0) {
                        return null;
                    }

                    dataJsonStr = buffer.toString();

                    Log.v(LOG_TAG,"data String " + dataJsonStr);
                }catch (IOException e) {
                    Log.e(LOG_TAG, "Error",e);

                    return null;
                }finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if(reader != null) {
                        try {
                            reader.close();
                        }catch (final IOException e){
                            Log.e(LOG_TAG,"Error closing stream", e);
                        }
                    }
                }
                try {
                    return getListDataFromJson(dataJsonStr);
                }catch (JSONException e){
                    Log.e(LOG_TAG,e.getMessage(),e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String[] result) {
                if(result != null){
                    adapter.clear();
                    for(String Getdata : result){
                        adapter.add(Getdata);
                    }
                }
            }
        }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_btn1:
                drawerLayout.openDrawer(drawerView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //뒤로가기 버튼을 두번누르면 종료
    @Override
    public void onBackPressed(){
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }
}
