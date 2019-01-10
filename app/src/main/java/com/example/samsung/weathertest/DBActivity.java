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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DBActivity extends AppCompatActivity {

    EditText namee;
    Button butok,butdel,butshow;
    SQLiteDatabase sqlDB;
    MyDBHelper dbHelper;
    String tc, tmax, tmin, code, stormYn, name;
    String get_name="";
    int topcheck;

    private Button create;
    private Button remove;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today="";
    String eventname="";
    String getname="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        namee = findViewById(R.id.insertname);
        butok = findViewById(R.id.but_ok);
        butdel = findViewById(R.id.but_del);
        dbHelper=new MyDBHelper(this);
        butshow=findViewById(R.id.but_show);
        create = findViewById(R.id.create);
        remove = findViewById(R.id.remove);

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        today=mFormat.format(mDate);

        create.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                createNotification();

            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeNotification();
            }
        });
        //값 받아오기
        Intent intent=new Intent(this.getIntent());
        tc = intent.getStringExtra("tc");
        tmax = intent.getStringExtra("tmax");
        tmin = intent.getStringExtra("tmin");
        code = intent.getStringExtra("code");
        stormYn = intent.getStringExtra("stormYn");
        name = intent.getStringExtra("name");
        //이름을 받았는지 테이블 확인
        sqlDB = dbHelper.getReadableDatabase();
        Cursor curname = sqlDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name ='name'",null);
        curname.moveToFirst();

        if(curname.getCount()>0){  //테이블이 있다.
            Cursor cursor3 = sqlDB.rawQuery("select name from name",null);
            while(cursor3.moveToNext()){
                getname=cursor3.getString(0);  //테이블이 있다면 이름을 가져온다.
            }
            cursor3.close();
            sqlDB.close();

            if(!getname.equals("")){  //가져온 이름이 있다면
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);  //다음 화면으로 넘어간다.
                intent1.putExtra("tc", tc);
                intent1.putExtra("tmax", tmax);
                intent1.putExtra("tmin", tmin);
                intent1.putExtra("code", code);
                intent1.putExtra("name", name);
                intent1.putExtra("stormYn", stormYn);
                startActivity(intent1);
                finish();
                // Toast.makeText(this, getname+"  name테이블", Toast.LENGTH_LONG).show();
            }else{
            }
            curname.close();

        }else{ //테이블이 없다
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
        }


        butok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_name = namee.getText().toString();
                sqlDB = dbHelper.getReadableDatabase();
                Cursor cursor1 = sqlDB.rawQuery("select selected from event",null);
                int eventn = 2 ;
                while(cursor1.moveToNext()){  //이미 insert가 되어 있다면 0이나 1반환
                    eventn=cursor1.getInt(0);
                }
                cursor1.close();
                sqlDB.close();

                Log.e("what is name?2 우우우우우우우ㅏ아아아아",get_name+"end");
                if(eventn == 2) {   //테이블에 insert가 되지 않았다는 뜻
                    Toast.makeText(getApplicationContext(), "insert", Toast.LENGTH_LONG).show();
                    sqlDB = dbHelper.getWritableDatabase();
                    sqlDB.execSQL("insert into name values('"+get_name+"');");
                    sqlDB.execSQL("insert into event values('면접','이력서,신분증,필기도구,제출 서류,포토폴리오,복장','0,0,0,0,0,0',0);");
                    sqlDB.execSQL("insert into event values('결혼식','복장,축의금,선물','0,0,0',0);");
                    sqlDB.execSQL("insert into event values('국내여행','여분 옷,세면도구,화장품,보조배터리,충전기,이어폰,신분증,지갑,비닐봉지,상비약,우산,휴지','0,0,0,0,0,0,0,0,0,0,0,0',0);");//12
                    sqlDB.execSQL("insert into event values('해외여행','여권,여권사본,항공권,현지 화폐,국제 신분증,예약증,가이드북,충전기,멀티 어댑터,상비약,여분 옷,외투','0,0,0,0,0,0,0,0,0,0,0,0',0);");//12
                    sqlDB.execSQL("insert into event values('현장학습','도시락,돗자리,간식,물,보조배터리,편한 옷,휴지','0,0,0,0,0,0,0',0);");
                    sqlDB.execSQL("insert into event values('수영장','수영복,물안경,타올,방수팩,여분 옷,세면도구,간식,물','0,0,0,0,0,0,0,0',0);");
                    sqlDB.execSQL("insert into event values('등산','등산복,등산스틱,등산화,모자,가방,간식,도시락,물,돗자리,구급약','0,0,0,0,0,0,0,0,0,0',0);");

                    for (int i = 28; i >= 0; i--) {
                        if (i <= 28 && i > 27) {
                            sqlDB.execSQL("insert into weather values(" + i + ",'민소매,반팔,반바지,치마,원피스');");

                        } else if (i <= 27 && i >= 23) {
                            sqlDB.execSQL("insert into weather values(" + i + ",'반팔,얇은셔츠,긴팔,반바지,얇은 바지');");

                        } else if (i <= 22 && i >= 17) {
                            sqlDB.execSQL("insert into weather values(" + i + ",'얇은 긴 팔,가디건,후드,맨투맨,면바지');");

                        } else if (i <= 16 && i >= 12) {
                            sqlDB.execSQL("insert into weather values(" + i + ",'가디건,야상,살색 스타킹,긴팔,긴바지,자켓,니트');");

                        } else if (i <= 11 && i >= 10) {
                            sqlDB.execSQL("insert into weather values(" + i + ",'트렌치 코트,간절기 야상,얇게 여러 겹,가디건 겹처입기,긴팔,긴바지,자켓');");

                        } else if (i <= 9 && i >= 6) {
                            sqlDB.execSQL("insert into weather values(" + i + ",'코트,가죽자켓,두꺼운 야상,니트,긴팔,긴바지,두 겹 입기');");

                        } else if (i <= 5 && i >= 0) {
                            sqlDB.execSQL("insert into weather values(" + i + ",'롱패딩,기모 긴팔,긴바지,목도리,장갑,귀마개,히트텍');");

                        } else {
                        }
                    }//for

                    sqlDB.close();
                }//if
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tc", tc);
                intent.putExtra("tmax", tmax);
                intent.putExtra("tmin", tmin);
                intent.putExtra("code", code);
                intent.putExtra("name", name);
                intent.putExtra("stormYn", stormYn);
                startActivity(intent);
                finish();
            }
        });

        butdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = dbHelper.getWritableDatabase();
                dbHelper.onUpgrade(sqlDB,1,2);
                sqlDB.close();
            }
        });

        //butshow없어도 됨
        butshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = dbHelper.getReadableDatabase();
                Cursor cursor2 = sqlDB.rawQuery("select name from name;",null);
                String all="";
                while(cursor2.moveToNext()){
                    all += cursor2.getString(0)+"   ";
                }
                sqlDB.close();
            }
        });



    }//onCreate

    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        Intent landingIntent = new Intent(this, MainActivity.class);
        landingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent landingPendingIntent = PendingIntent.getActivity(this, 0,landingIntent, PendingIntent.FLAG_ONE_SHOT);


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
        //builder.addAction(R.mipmap.ic_launcher, "TEST");

        builder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        builder.setAutoCancel(false);
        builder.setContentIntent(landingPendingIntent);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            builder.setOngoing(true);
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

    private void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(this).cancel(1);
    }

    public class MyDBHelper extends SQLiteOpenHelper {
        //groupDB라는 이름의 DB를 생성
        public MyDBHelper(Context context) {
            super(context, "weather", null, 5);   //db이름  버전을 올리면 새 DB를 만들 수 있다.

        }

        //새 테이블 생성
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table IF NOT EXISTS weather(temper integer , clothes text)");
            db.execSQL("create table IF NOT EXISTS event(eventname text, supplies text,checklist text,selected integer)");  //면접이름, 준비물, 체크되었는지, 선택되었는지
            db.execSQL("create table IF NOT EXISTS name(name text)");
        }

        //기존 테이블 삭제하고 새 테이블 생성
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists weather");
            db.execSQL("drop table if exists event");
            db.execSQL("drop table if exists name");
            onCreate(db);
        }
    }// class MyDBHelper
} //all class