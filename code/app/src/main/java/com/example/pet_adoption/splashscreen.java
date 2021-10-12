package com.example.pet_adoption;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

public class splashscreen extends AppCompatActivity {

    private ProgressBar pb1,pb2;
    private TextView tv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        pb1=findViewById(R.id.pb1);
        pb2=findViewById(R.id.pb2);
        tv1=findViewById(R.id.tv1);

        Toast.makeText(splashscreen.this,"請以領養代替購買", Toast.LENGTH_SHORT).show();   //標語

        Sprite wave = new Wave();
        pb2.setIndeterminateDrawable(wave);


        pb1.setMax(99);
        pb1.setScaleY(3f);
        progressAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(splashscreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },5000);
    }
    public void progressAnimation(){
        progressBar_animation anim=new progressBar_animation(this,pb1,tv1,0f,100f);
        anim.setDuration(5000);
        pb1.setAnimation(anim);
    }

    private class progressBar_animation extends Animation{
        private Context context;
        private ProgressBar progressBar;
        private TextView textView;
        private float from;
        private float to;

        public progressBar_animation(Context context,ProgressBar progressBar,TextView textView,float from,float to){
            this.context=context;
            this.progressBar=progressBar;
            this.textView=textView;
            this.from=from;
            this.to=to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value=from+(to-from)*interpolatedTime;
            progressBar.setProgress((int)value);
            textView.setText((int)value+" %");

            if(value==to){
                context.startActivity(new Intent(context,MainActivity.class));
            }
        }
    }
}