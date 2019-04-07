package com.mbs.silverplus;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagea);
        switch((int)getIntent().getSerializableExtra("img")){
            case 0:
                ((ImageView)findViewById(R.id.cardImg)).setImageResource(R.drawable._card_news1);
                break;
            case 1:
                ((ImageView)findViewById(R.id.cardImg)).setImageResource(R.drawable._card_news2);
                break;
            case 2:
                ((ImageView)findViewById(R.id.cardImg)).setImageResource(R.drawable._card_news1);
                break;
        }
    }
}
