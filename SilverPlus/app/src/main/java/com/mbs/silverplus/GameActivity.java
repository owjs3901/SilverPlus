package com.mbs.silverplus;

import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Predicate;

public class GameActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        System.out.println("안돼");
    }
    YouTubePlayer youTube;
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(node.id);
        youTube=youTubePlayer;
    }

    ArrayList<String> list=new ArrayList<>();
    int index=0;
    YouTubePlayerView youP;
    GameNode node;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=true;
        setContentView(R.layout.activity_game);
        youP=((YouTubePlayerView)findViewById(R.id.youtube_view));
        youP.initialize(getResources().getString(R.string.you_key),this);

        String st1=(String)getIntent().getSerializableExtra("img");


        node=new GameNode(st1);
        final Handler h=new Handler();

        new Thread(){
            @Override
            public void run() {
                super.run();
                final TextView t =findViewById(R.id.now_m);
                final TextView t1 =findViewById(R.id.next_m);
                final TextView t2 =findViewById(R.id.index_m);
                final TextView t3 =findViewById(R.id.com);
                try{
                while(b&&MainActivity.bluetoothSocket!=null) {
                    sleep(20);
                    h.post(new Runnable() {
                               @Override
                               public void run() {

                                   if(youTube==null)return;

                                   int cm=youTube.getCurrentTimeMillis();

                                   t2.setText(String.valueOf((int)((cm/(float)youTube.getDurationMillis())*100))+"%");
                                   if(youTube!=null&&cm/1000<=node.max){
                                        if(node.getNode(cm).equals(MainActivity.wii.now)||node.getCom(cm)){
                                            t3.setText("잘했다");
                                            node.putCom(cm);
                                            index++;
                                        }
                                        else t3.setText("틀림");
                                   }
                                   else{
                                       if(youTube!=null){

                                           int ii=0;
                                           for (int key1:node.dlist.keySet()){
                                               if (node.dlist.get(key1).equals(true))
                                                   ii++;
                                               System.out.println(key1+"/"+node.dlist.get(key1));
                                           }

                                           t2.setText("종료! : "+ii+"/"+node.dlist.size());


                                           b=false;
                                       }

                                       return;
                                   }
                                   switch (node.getNode(youTube.getCurrentTimeMillis())){
                                       case "m":
                                           t1.setText("중간");
                                           break;
                                       case "u":
                                           t1.setText("위");
                                           break;
                                       case "d":
                                           t1.setText("아래");
                                           break;
                                       case "r":
                                           t1.setText("오른쪽");
                                           break;
                                       case "l":
                                           t1.setText("왼쪽");
                                           break;
                                   }
                                   switch (MainActivity.wii.now){
                                       case "m":
                                           t.setText("중간");
                                           break;
                                       case "u":
                                           t.setText("위");
                                           break;
                                       case "d":
                                           t.setText("아래");
                                           break;
                                       case "r":
                                           t.setText("오른쪽");
                                           break;
                                       case "l":
                                           t.setText("왼쪽");
                                           break;
                                   }
                               }
                           }
                    );
                }

                }catch(Exception e){

                }
            }
        }.start();
    }
    static boolean b=true;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        b=false;
        youTube=null;
    }
}
