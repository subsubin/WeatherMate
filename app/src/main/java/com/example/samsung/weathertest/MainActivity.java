package com.example.samsung.weathertest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static SQLiteDatabase sqlDB;
    static DBHelper dbHelper;
    //listAct
    static CheckBox check1,check2,check3,check4,check5;
    static List<CheckBox> ch=new ArrayList<CheckBox>();   //체크박스 리스트
    static List<String> chch=new ArrayList<String>(); //DB에 넣을 체크박스 정보 리스트
    static Button but_save,but_alldel;
    static String eventname,result="";
    static ArrayAdapter adapter;
    static ArrayList<String> items = new ArrayList<String>() ;
    static int topcheck=0;

    static Button create;
    static Button remove;
    static long mNow;
    static Date mDate;
    static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    static String today="";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper= new DBHelper(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        adapter  = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items) ;
//        Log.e("온도 : ", tp);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }



        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        //public int images[] = {R.drawable.sunny, R.drawable.thunder, R.drawable.rain};
        //public int imagetest;

        public int clothes[] = {R.drawable.shortt, R.drawable.longt, R.drawable.hoodie, R.drawable.coat, R.drawable.cardigan, R.drawable.padding, R.drawable.jacket};

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) { // 이 안에서 fragment가 바뀌는 내용 지정
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ImageView img = (ImageView) rootView.findViewById(R.id.section_image);
            int i = getArguments().getInt(ARG_SECTION_NUMBER)-1;
            Log.e("i print", i+"");
            if(i == 2){
                rootView = inflater.inflate(R.layout.activity_list, container, false);
                final TextView tv = (TextView) rootView.findViewById(R.id.textView1);
                Spinner s = (Spinner) rootView.findViewById(R.id.spinner1);
                final ListView listView = (ListView) rootView.findViewById(R.id.listview1);
                //상단바버튼
                create = rootView.findViewById(R.id.create);
                remove = rootView.findViewById(R.id.remove);
                //오늘 날짜정보
                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                today=mFormat.format(mDate);

                listView.setAdapter(adapter) ;
                but_save=rootView.findViewById(R.id.but_save);
                but_alldel=rootView.findViewById(R.id.but_alldel);

                s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        eventname=(String)parent.getItemAtPosition(position);
                        tv.setText(parent.getItemAtPosition(position) + "일정 준비물");
                        sqlDB = dbHelper.getReadableDatabase();
                        Cursor cur = sqlDB.rawQuery("select * from event where eventname='" + parent.getItemAtPosition(position) + "'", null);

                        String give = "",ch_give="";
                        int count = 0;
                        while (cur.moveToNext()) {
                            give = cur.getString(1);
                            ch_give = cur.getString(2);
                        }
                        String pp[] = give.split(",");  //받아온 정보를 ,로 나눠서 배열에 넣음
                        String ch_g[] = ch_give.split(",");
                        items.clear();
                        listView.clearChoices();
                        adapter.notifyDataSetChanged();

                        for(int i = 0; i < pp.length; i++){
                            items.add(pp[i]); // listview 갱신 adapter.notifyDataSetChanged();
                        }
                        for(int j=0;j<ch_g.length;j++) {   //나눠 넣은 정보길이만큼 돈다
                            if (ch_g[j].equals("0")) {     //0이면 체크가 안되어 있음
                                listView.setItemChecked(j, false);
                            } else {
                                listView.setItemChecked(j, true);
                            }
                        }
                        for(int j=0; j<ch_g.length;j++){
                            chch.add((String)ch_g[j]);
                        }
                        cur.close();
                        sqlDB.close();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                but_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //정보 불러오기
                        sqlDB = dbHelper.getReadableDatabase();
                        Cursor cursor = sqlDB.rawQuery("select * from event where eventname='"+eventname+"';",null);
                        String all="";
                        while(cursor.moveToNext()){
                            all += cursor.getString(0)+"   ";
                            all += cursor.getString(1)+"\r\n";
                        }
                        sqlDB.close();
                        //check된 상태 확인하기
                        CheckBox checkbox1;
                        topcheck=0;
                        for(int j=0;j<chch.size();j++) {
                            if(listView.isItemChecked(j)) {
                                chch.set(j,"1");   //체크되어 있으면 1
                            }
                            else {
                                chch.set(j,"0");  //체크 안 되어 있으면 0
                            }
                        }

                        for(int j=0;j<chch.size();j++) {
                            if(j ==chch.size()-1) result+= chch.get(j);
                            else result += chch.get(j)+",";
                        }

                        //업데이트하기
                        sqlDB = dbHelper.getReadableDatabase();
                        sqlDB.execSQL("update event set selected=0;");
                        sqlDB.execSQL("update event set checklist='"+result+"', selected=1 where eventname='"+eventname+"';");
                        result="";
                        chch.clear();
                        ch.clear();
                        cursor.close();
                        sqlDB.close();
                        Toast.makeText(getActivity().getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
                //상단바 생성
                create.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View view) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(), "default");

                        Intent landingIntent = new Intent(getActivity().getApplicationContext(), Splash.class);
                        landingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        PendingIntent landingPendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0,landingIntent, PendingIntent.FLAG_ONE_SHOT);


                        sqlDB = dbHelper.getReadableDatabase();
                        Cursor cursor = sqlDB.rawQuery("select eventname from event where selected=1",null);
                        while(cursor.moveToNext()){
                            eventname=cursor.getString(0);
                        }
                        cursor.close();
                        sqlDB.close();


                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setContentTitle(today+"의 일정 ");
                        builder.setContentText("오늘의 일정은 "+eventname+"입니다.");

                        builder.setColor(getResources().getColor(R.color.colorPrimaryDark));
                        builder.setAutoCancel(false);
                        builder.setContentIntent(landingPendingIntent);

                        // 알림 표시
                        NotificationManager notificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
                            builder.setOngoing(true);
                        }

                        // id값은
                        // 정의해야하는 각 알림의 고유한 int값
                        notificationManager.notify(1, builder.build());
                    }
                });
                //상단바 삭제
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NotificationManagerCompat.from(getActivity().getApplicationContext()).cancel(1);
                    }
                });
                //계정 삭제
                but_alldel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sqlDB = dbHelper.getWritableDatabase();
                        dbHelper.onUpgrade(sqlDB,1,2);
                        sqlDB.close();
                        Toast.makeText(getActivity().getApplicationContext(), "계정이 삭제되었습니다.", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                });



            }else {
                String tc, tmax, tmin, code , name, stormYn, username;

                Intent intent =getActivity().getIntent();

                tc = intent.getStringExtra("tc");
                tmax = intent.getStringExtra("tmax");
                tmin = intent.getStringExtra("tmin");
                code = intent.getStringExtra("code");
                name = intent.getStringExtra("name");
                stormYn = intent.getStringExtra("stormYn");
                username = intent.getStringExtra("username");

                Log.e("TTESTMAIN", "오늘 기온 :"+tc+"최고 기온 : "+tmax+"최저 기온"+tmin+""+"하늘코드 : "+code+"하늘 상태 : "+name + "stormYn" + stormYn);

                sqlDB = dbHelper.getReadableDatabase();
                Cursor cursor3 = sqlDB.rawQuery("select name from name",null);
                while(cursor3.moveToNext()){
                    username=cursor3.getString(0);  //테이블이 있다면 이름을 가져온다.
                }
                cursor3.close();
                sqlDB.close();

                double tc_if = 0;
                long result = 0;
                if(!"".equals(tc)){ //만약 받아온 데이터가 공백이 아니면 데이터를 형변환 하여 적재

                    tc_if = Double.parseDouble(tc);
                    result = Math.round(tc_if);
                }
                Log.e("TC_IF", tc_if+"");

                if(i == 0) {
                    TextView nameset = rootView.findViewById(R.id.sayhi);
                    nameset.setText(username + " 님 오늘 날씨는");
                    switch (code){
                        case "SKY_O01":
                            img.setImageResource(R.drawable.sun);
                            break;
                        case "SKY_O02":
                            img.setImageResource(R.drawable.cloud);
                            break;
                        case "SKY_O03":
                            img.setImageResource(R.drawable.ccloud);
                            break;
                        case "SKY_O04":
                            img.setImageResource(R.drawable.crain);
                            break;
                        case "SKY_O05":
                            img.setImageResource(R.drawable.csnow);
                            break;
                        case "SKY_O06":
                            img.setImageResource(R.drawable.crainnsnow);
                            break;
                        case "SKY_O07":
                            img.setImageResource(R.drawable.cloud);
                            break;
                        case "SKY_O08":
                            img.setImageResource(R.drawable.rain);
                            break;
                        case "SKY_O09":
                            img.setImageResource(R.drawable.snow);
                            break;
                        case "SKY_O10":
                            img.setImageResource(R.drawable.rainnsnow);
                            break;
                        case "SKY_O11":
                            img.setImageResource(R.drawable.thunder);
                            break;
                        case "SKY_O12":
                            img.setImageResource(R.drawable.rain);
                            break;
                        case "SKY_O13":
                            img.setImageResource(R.drawable.snow);
                            break;
                        case "SKY_O14":
                            img.setImageResource(R.drawable.thunder);
                            break;
                        default:
                            Log.e("SWITCHIN : ", "들어옴");
                            break;
                    }
                    //img.setImageResource(R.drawable.cloud);

                    if(stormYn.equals("Y")) {
                        TextView text = (TextView) rootView.findViewById(R.id.weather);
                        text.setText("오늘 온도 : "+ tc+"\n" +
                                "최고 기온 : " +tmax+ "\n" +
                                "최저 기온 : "+tmin + "\n" +
                                "오늘 날씨는 " + name + "\n" +
                                "태풍 주의!");
                    //   img.setImageResource(R.drawable.storm);
                    }else{
                        TextView text = (TextView) rootView.findViewById(R.id.weather);
                        text.setText("오늘 온도 : "+ tc+"\n" +
                                "최고 기온 : " +tmax+ "\n" +
                                "최저 기온 : "+tmin + "\n" +
                                "오늘 날씨는 " + name);
                    }

                }else if(i == 1) {

                    TextView textView = (TextView) rootView.findViewById(R.id.weather);

                    String origin_string="";
                    sqlDB = dbHelper.getReadableDatabase();
                    Cursor cursor = sqlDB.rawQuery("select * from weather where temper = " + result + ";",null);

                    while(cursor.moveToNext()) {
                        origin_string += cursor.getString(1);
                        break;
                    }

                    Log.e("STRING", origin_string);
                    cursor.close();
                    sqlDB.close();

                    String lists[] = origin_string.split(",");
                    String final_list = "";

                    TextView nameset = rootView.findViewById(R.id.sayhi);
                    nameset.setText(username + " 님 오늘 추천 옷은");

                    if (result <= 28 && result > 27) {
                        img.setImageResource(clothes[0]);
                        for (int j = 0; j < lists.length; j++) {
                            final_list += lists[j] + "\r\n";
                        }
                        textView.setText(final_list);
                    } else if (result <= 27 && result >= 23) {
                        img.setImageResource(clothes[1]);
                        for (int j = 0; j < lists.length; j++) {
                            final_list += lists[j] + "\r\n";
                        }
                        textView.setText(final_list);
                    } else if (result <= 22 && result >= 17) {
                        img.setImageResource(clothes[2]);
                        for (int j = 0; j < lists.length; j++) {
                            final_list += lists[j] + "\r\n";
                        }
                        textView.setText(final_list);
                    } else if (result <= 16 && result >= 12) {
                        img.setImageResource(clothes[4]);
                        for (int j = 0; j < lists.length; j++) {
                            final_list += lists[j] + "\r\n";
                        }
                        textView.setText(final_list);
                    } else if (result <= 11 && result >= 10) {
                        img.setImageResource(clothes[3]);
                        for (int j = 0; j < lists.length; j++) {
                            final_list += lists[j] + "\r\n";
                        }
                        textView.setText(final_list);
                    } else if (result <= 9 && result >= 6) {
                        img.setImageResource(clothes[6]);
                        for (int j = 0; j < lists.length; j++) {
                            final_list += lists[j] + "\r\n";
                        }
                        textView.setText(final_list);
                    } else if (result <= 5 && result >= 0) {
                        img.setImageResource(clothes[5]);
                        for (int j = 0; j < lists.length; j++) {
                            final_list += lists[j] + "\r\n";
                        }
                        textView.setText(final_list);
                    } else {
                    }
//                textView.setText(set);
                }
            }
            //  API model = new API();


//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;


        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public class DBHelper extends SQLiteOpenHelper {
        //groupDB라는 이름의 DB를 생성
        public DBHelper(Context context) {
            super(context, "weather", null, 5);   //db이름  버전을 올리면 새 DB를 만들 수 있다.

        }

        //새 테이블 생성
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table if not exists weather(temper integer , clothes text)");
            db.execSQL("create table if not exists event(eventname text, supplies text,checklist text,selected integer)");
        }

        //기존 테이블 삭제하고 새 테이블 생성
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists weather");
            db.execSQL("drop table if exists event");
            onCreate(db);
        }
    }// class MyDBHelper
}