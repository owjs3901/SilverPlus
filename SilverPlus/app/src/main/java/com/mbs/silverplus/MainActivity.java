package com.mbs.silverplus;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태

    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터

    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋

    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스

    public static BluetoothSocket bluetoothSocket = null; // 블루투스 소켓

    private static OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림

    private static InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
//    private TextView textViewReceive;
    private TextView msg;

    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드

    private static byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼

    private static int readBufferPosition; // 버퍼 내 문자 저장 위치

    private EditText editTextSend; // 송신 할 데이터를 작성하기 위한 에딧 텍스트


    public static WiiNun wii=new WiiNun();




    public void receiveData() {

        readBufferPosition = 0;

        readBuffer = new byte[1024];



        // 데이터를 수신하기 위한 쓰레드 생성
        System.out.println("나는 너다 슈ㅋ레레레");
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        int byteAvailable = inputStream.available();
                        if(byteAvailable > 0) {
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            for(int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                if(tempByte == '\n') {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
//                                    System.out.println("감사링"+text);
                                    String[] st=text.split(" ");
                                    if(st.length!=7)return;
                                    wii.joyX=Integer.parseInt(st[0]);
                                    wii.joyY=Integer.parseInt(st[1]);
                                    wii.acX=Integer.parseInt(st[2]);
                                    wii.acY=Integer.parseInt(st[3]);
                                    wii.acZ=Integer.parseInt(st[4]);
                                    wii.btnC=st[5].equals("1");
                                    wii.btnZ=st[6].equals("1");
                                    String ac="";
                                    if(wii.acX<-150) {
                                        ac="팔 왼쪽으로";
                                        wii.now="l";
                                    }
                                    else if(wii.acX>150){
                                        wii.now = "r";
                                        ac = "팔 오른쪽으로";
                                    }
                                    else if(wii.acY>150){
                                        ac="팔 아래로";
                                        wii.now="d";
                                    }
                                    else if(wii.acY<-150){
                                        ac="팔 위로";
                                        wii.now="u";
                                    }
                                    else{
                                        ac="중앙";
                                        wii.now="m";
                                    }
                                } // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();

    }
    public void connectDevice(String deviceName) {

        // 페어링 된 디바이스들을 모두 탐색

        for(BluetoothDevice tempDevice : devices) {

            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료

            if(deviceName.equals(tempDevice.getName())) {

                bluetoothDevice = tempDevice;

                break;

            }

        }
        // UUID 생성

        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {

            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);

            System.out.println(bluetoothSocket.isConnected()+"나나나나");

            bluetoothSocket.connect();

            System.out.println(bluetoothSocket.isConnected()+"연결됐니니니ㅣ");

            if(bluetoothSocket.isConnected())
                msg.setText("블루투스 연결됨");

            // 데이터 송,수신 스트림을 얻어옵니다.

            outputStream = bluetoothSocket.getOutputStream();

            inputStream = bluetoothSocket.getInputStream();

            // 데이터 수신 함수 호출
            receiveData();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT :
                if(requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                }
                else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                }
                break;

        }
    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        int pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if(pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for(BluetoothDevice bluetoothDevice : devices)
                list.add(bluetoothDevice.getName());
            list.add("취소");
            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);
            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!charSequences[which].toString().equals("취소"))
                        connectDevice(charSequences[which].toString());
                }
            });
            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    private void connectBL(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            System.out.println("블루투스를 지원하지 않습니다!");
        }
        else if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
    }
    private BluetoothAdapter mBluetoothAdapter;
    public static AssetManager assetManager;
    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        font = getResources().getFont(R.font.bhm);
        assetManager = getResources().getAssets();

        System.out.println("OnCreate");

        connectBL();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        selectBluetoothDevice();

        setContentView(R.layout.activity_main);



//        nav.setNavigationItemSelectedListener(this);
//        View v=nav.getHeaderView(0);
//        msg=v.findViewById(R.id.nav_msg);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleMargin(50,0,50,0);
        toolbar.setTitle("                 Silver+");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.imageView1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println(event);
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    Intent in=new Intent(getApplicationContext(),ImageActivity.class);
                    in.putExtra("img",0);
                    startActivity(in);
                }
                return true;
            }
        });
        findViewById(R.id.imageView2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println(event);
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    Intent in=new Intent(getApplicationContext(),ImageActivity.class);
                    in.putExtra("img",1);
                    startActivity(in);
                }
                return true;
            }
        });
        findViewById(R.id.imageView3).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println(event);
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    Intent in=new Intent(getApplicationContext(),ImageActivity.class);
                    in.putExtra("img",2);
                    startActivity(in);
                }
                return true;
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        msg=navigationView.getHeaderView(0).findViewById(R.id.nav_msg);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        navigationView.setCheckedItem(R.id.side1);
//        textViewReceive=(TextView)findViewById(R.id.nun);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.imageView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImageAc
//                ImageView.setImageResource(R.drawable.image_sample)
            }
        });


        try{
            BufferedReader r=new BufferedReader(new InputStreamReader(assetManager.open("list.txt",AssetManager.ACCESS_BUFFER)));
            LinearLayout li=((LinearLayout)findViewById(R.id.flist));
            li.removeAllViews();
            while(true){
                String st=r.readLine();
                Button btn=new Button(this);
                btn.setText(st);
                btn.setGravity(Gravity.LEFT);
                btn.setTextSize(30);
                btn.setTypeface(font);

                btn.setBackgroundColor(getResources().getColor(R.color.btnOn));

                btn.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(((ColorDrawable)v.getBackground()).getColor()==getResources().getColor(R.color.btnIn))
                            v.setBackgroundColor(getResources().getColor(R.color.btnOn));
                        else
                            v.setBackgroundColor(getResources().getColor(R.color.btnIn));
                    }
                });
                li.addView(btn);
                if(st==null||st.length()==0)break;
            }
            r.close();

            r=new BufferedReader(new InputStreamReader(assetManager.open("motionList.txt",AssetManager.ACCESS_BUFFER)));
            li=((LinearLayout)findViewById(R.id.motionList));
            li.removeAllViews();
            while(true){
                String st=r.readLine();
                if(st==null||st.length()==0)break;
                Button btn=new Button(this);
                btn.setText(st);
                btn.setGravity(Gravity.LEFT);
                btn.setTextSize(30);
                btn.setTypeface(font);

                btn.setBackgroundColor(getResources().getColor(R.color.btnOn));

                btn.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        Intent in=new Intent(getApplicationContext(),GameActivity.class);
                        System.out.println("ㄲㄲㄲㄲㄲㄲㄲㄲㄲㄲㄲ"+((Button)v).getText());
                        in.putExtra("img",((Button)v).getText());
                        startActivity(in);
                    }
                });
                li.addView(btn);
            }
            r.close();
        }
        catch(Exception ec){
            ec.printStackTrace();
        }

    }
    private Typeface font;
    private NavigationView navigationView;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    private void display(int index){
        findViewById(R.id.main_include0).setVisibility(View.GONE);
        findViewById(R.id.main_include1).setVisibility(View.GONE);
        findViewById(R.id.main_include2).setVisibility(View.GONE);
        switch(index){
            case 0:
                findViewById(R.id.main_include0).setVisibility(View.VISIBLE);
                break;
            case 1:
                findViewById(R.id.main_include1).setVisibility(View.VISIBLE);
                break;
            case 2:
                findViewById(R.id.main_include2).setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.side1:
                display(0);

                break;
            case R.id.side2:
                break;
            case R.id.side3:
                break;
            case R.id.side4:
                break;
            case R.id.side5:
                display(1);
                break;
            case R.id.side6:
                display(2);
                break;
            case R.id.side7:
                if(bluetoothSocket==null||!bluetoothSocket.isConnected())
                    selectBluetoothDevice();
                else{
                    Toast.makeText(getApplicationContext(),"이미 블루투스에 연결되었습니다",Toast.LENGTH_LONG).show();
                }
                return false;
            case R.id.nav_share: case R.id.nav_send:
                break;
            default:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = getResources().getFont(R.font.bhm);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //mNewTitle.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, mNewTitle.length(), 0); Use this if you want to center the items
        mi.setTitle(mNewTitle);
    }
}
