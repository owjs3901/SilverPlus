package com.mbs.silverplus;

import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class GameNode {
//    ArrayList<String> list=new ArrayList<>();
    public HashMap<Integer,String> map=new HashMap<>();
    public HashMap<Integer,Boolean> dlist=new HashMap<>();
    public String id;
    public int max;
    public GameNode(String name){
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(MainActivity.assetManager.open(name + ".txt", AssetManager.ACCESS_BUFFER)));
            id=r.readLine();
            int ic=0;
            while (true) {
                String st=r.readLine();
                if (st == null || st.length() == 0) break;
                String[] ac=st.split(" ");
                ic+=Integer.parseInt(ac[1]);
                map.put(ic,ac[0]);
                dlist.put(ic,false);
            }
            max=ic;
            r.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean getCom(int time){
        int ok=0;
        if(time/1000<=0){
            return true;
        }
        while(true){
            if(dlist.containsKey(time/1000-ok)){
                return dlist.get(time/1000-ok);
            }
            else ok-=1;
        }
    }
    public void putCom(int time){
        int ok=0;
        if(time/1000<=0){
            return;
        }
        while(true){
            if(dlist.containsKey(time/1000-ok)){
                dlist.put(time/1000-ok,true);
                return;
            }
            else ok-=1;
        }
    }

    public String getNode(int time){
        int ok=0;
        if(time/1000<=0){
            return "m";
        }
        while(true){
            if(map.containsKey(time/1000-ok)){
                return map.get(time/1000-ok);
            }
            else ok-=1;
        }
    }

}
