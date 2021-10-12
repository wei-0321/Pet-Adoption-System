package com.example.pet_adoption;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapMain extends AppCompatActivity {

    ImageButton east_btn,west_btn,south_btn,north_btn;
    Button back_btn;

    int count=0; //計算動物數量

    ArrayList<Map<String, String>> init = new ArrayList<>(); //要記錄animal_list

    Set<String> shelter_name = new HashSet<>(); //收容所名稱
    //各縣市分區
    Set<String> shelter_north = new HashSet<>();
    Set<String> shelter_center = new HashSet<>();
    Set<String> shelter_east = new HashSet<>();
    Set<String> shelter_south = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        east_btn=findViewById(R.id.east_btn);
        west_btn=findViewById(R.id.west_btn);
        south_btn=findViewById(R.id.south_btn);
        north_btn=findViewById(R.id.north_btn);
        back_btn=findViewById(R.id.back_btn);


        east_btn.setOnClickListener(listener);
        west_btn.setOnClickListener(listener);
        south_btn.setOnClickListener(listener);
        north_btn.setOnClickListener(listener);
        back_btn.setOnClickListener(backlistener);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MapMain.this);
        Gson gson = new Gson();
        String arrayListString = preferences.getString("animal" , null);   //預設為null
        ArrayList<Map<String, String>> animal_list = gson.fromJson(arrayListString, ArrayList.class);
        init = animal_list;

        //北區
        shelter_north.add("基隆市");
        shelter_north.add("臺北市");
        shelter_north.add("新北市");
        shelter_north.add("桃園市");
        shelter_north.add("新竹市");
        shelter_north.add("臺北市");
        shelter_north.add("新竹縣");
        shelter_north.add("苗栗縣");
        shelter_north.add("連江縣");
        //中
        shelter_center.add("臺中市");
        shelter_center.add("彰化縣");
        shelter_center.add("南投縣");
        shelter_center.add("雲林縣");
        shelter_center.add("金門縣");
        //東
        shelter_east.add("宜蘭縣");
        shelter_east.add("花蓮縣");
        shelter_east.add("臺東縣");
        //南
        shelter_south.add("嘉義市");
        shelter_south.add("嘉義縣");
        shelter_south.add("臺南市");
        shelter_south.add("高雄市");
        shelter_south.add("屏東縣");
        shelter_south.add("澎湖縣");

    }

    public String count_animal_and_shelter_name (ArrayList<Map<String, String>> animal_list , Set<String> shelter_side)
    {
        if(animal_list!=null)
        {
            for(int i=0;i<animal_list.size();i++)
            {
                Map<String, String> each_animal = animal_list.get(i);

                if (each_animal.containsKey("shelter_name")==true)
                {
                    String str = each_animal.get("shelter_name").substring(0,3);
                    if(shelter_side.contains(str))
                    {
                        count+=1;
                        shelter_name.add(each_animal.get("shelter_name"));
                    }
                }
            }
        }
        return null;
    };


    public ImageView.OnClickListener listener = new ImageButton.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.east_btn:
                {
                    //當按下按鈕會去 統計該區域的 收容所 以及 流浪動物數量
                    count_animal_and_shelter_name(init,shelter_east);

                    AlertDialog.Builder builder=new AlertDialog.Builder(MapMain.this);
                    builder.setTitle("東部流浪動物資訊");
                    builder.setMessage("收容所數量 : "+ shelter_name.size() +"間"+ "\n"+
                            "流浪動物數量 : " + count + "隻");
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    builder.setNegativeButton("確定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });

                    //顯示完後初始化
                    count=0;
                    shelter_name.clear();


                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                }


                case R.id.west_btn:
                {
                    //當按下按鈕會去 統計該區域的 收容所 以及 流浪動物數量
                    count_animal_and_shelter_name(init,shelter_center);

                    AlertDialog.Builder builder=new AlertDialog.Builder(MapMain.this);
                    builder.setTitle("中部流浪動物資訊");
                    builder.setMessage("收容所數量 : " + shelter_name.size() +"間"+"\n"+
                            "流浪動物數量 : " + count + "隻");
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    //顯示完後初始化
                    count=0;
                    shelter_name.clear();


                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;


                }


                case R.id.south_btn:
                {
                    //當按下按鈕會去 統計該區域的 收容所 以及 流浪動物數量
                    count_animal_and_shelter_name(init,shelter_south);

                    AlertDialog.Builder builder=new AlertDialog.Builder(MapMain.this);
                    builder.setTitle("南部流浪動物資訊");
                    builder.setMessage("收容所數量 : "  +shelter_name.size() + "間" +"\n"+
                            "流浪動物數量 : " + count + "隻");
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    builder.setNegativeButton("確定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });

                    //顯示完後初始化
                    count=0;
                    shelter_name.clear();


                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                }


                case R.id.north_btn:
                {
                    //當按下按鈕會去 統計該區域的 收容所 以及 流浪動物數量
                    count_animal_and_shelter_name(init,shelter_north);

                    AlertDialog.Builder builder=new AlertDialog.Builder(MapMain.this);
                    builder.setTitle("北部流浪動物資訊");
                    builder.setMessage("收容所數量 : "  +shelter_name.size() + "間" +  "\n"+
                            "流浪動物數量 : " + count +"隻");
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    builder.setNegativeButton("確定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });

                    //顯示完後初始化
                    count=0;
                    shelter_name.clear();


                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                }
            }
        }
    };
    private Button.OnClickListener backlistener=new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent=new Intent(MapMain.this,MainActivity.class);
            startActivity(intent);
        }
    };

}
