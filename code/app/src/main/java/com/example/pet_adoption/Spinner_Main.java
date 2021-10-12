package com.example.pet_adoption;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class Spinner_Main extends AppCompatActivity {

    Spinner Sp_kind,Sp_sex,Sp_age,Sp_colour,Sp_shelter;
    Button BTN_back,BTN_type_search;

    String[] kind ={"所有","貓","狗","其他"};
    String[] sex ={"所有","公","母"};
    String[] age ={"所有","幼年","成年"};
    String[] colour ={"所有","白色","黃色","黑色","棕色","灰色","咖啡色","花色","米色","花白色","三花色","黃虎斑色","棕黑色","黑棕色","棕灰色","虎斑白色","虎斑色","黃白色","白黃色","白黑色","黑黃色"};
    String[] shelter ={"所有","基隆市","臺北市","新北市",
            "桃園市","新竹市","新竹縣","苗栗縣","臺中市","彰化縣",
            "南投縣","雲林縣","嘉義市","嘉義縣","臺南市","高雄市",
            "屏東縣","宜蘭縣","花蓮縣","臺東縣","連江縣",
            "金門縣","澎湖縣"};

    String selected_kind="";
    String selected_sex="";
    String selected_age="";
    String selected_colour="";
    String selected_shelter="";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);


        BTN_back = findViewById(R.id.BTN_back);
        BTN_type_search = findViewById(R.id.BTN_type_search);

        //Spinner
        Sp_kind = findViewById(R.id.Sp_kind);
        Sp_age = findViewById(R.id.Sp_age);
        Sp_colour = findViewById(R.id.Sp_colour);
        Sp_sex = findViewById(R.id.Sp_sex);
        Sp_shelter = findViewById(R.id.Sp_shelter);

        BTN_type_search.setOnClickListener(type_search_listener);
        BTN_back.setOnClickListener(back_listener);

        ArrayAdapter<String> adapter_kind = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kind);
        adapter_kind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_kind.setAdapter(adapter_kind);
        Sp_kind.setOnItemSelectedListener(kind_listener);

        ArrayAdapter<String> adapter_sex = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sex);
        adapter_sex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_sex.setAdapter(adapter_sex);
        Sp_sex.setOnItemSelectedListener(sex_listener);

        ArrayAdapter<String> adapter_age = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, age);
        adapter_age.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_age.setAdapter(adapter_age);
        Sp_age.setOnItemSelectedListener(age_listener);

        ArrayAdapter<String> adapter_colour = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colour);
        adapter_colour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_colour.setAdapter(adapter_colour);
        Sp_colour.setOnItemSelectedListener(colour_listener);

        ArrayAdapter<String> adapter_shelter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shelter);
        adapter_shelter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sp_shelter.setAdapter(adapter_shelter);
        Sp_shelter.setOnItemSelectedListener(shelter_listener);
    }
    private Spinner.OnItemSelectedListener kind_listener = new Spinner.OnItemSelectedListener()
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {

            selected_kind = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Sp_kind.setSelection(0);
        }
    };
    private Spinner.OnItemSelectedListener sex_listener = new Spinner.OnItemSelectedListener()
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selected_sex = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Sp_sex.setSelection(0);
        }
    };
    private Spinner.OnItemSelectedListener age_listener = new Spinner.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selected_age = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Sp_age.setSelection(0);
        }
    };
    private Spinner.OnItemSelectedListener colour_listener = new Spinner.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selected_colour = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Sp_colour.setSelection(0);
        }
    };
    private Spinner.OnItemSelectedListener shelter_listener = new Spinner.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selected_shelter = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Sp_shelter.setSelection(0);
        }
    };

    private Button.OnClickListener back_listener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(Spinner_Main.this,MainActivity.class);
            startActivity(intent);
        }
    };
    private  Button.OnClickListener type_search_listener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent=new Intent(Spinner_Main.this, MainActivity.class);
            //利用bundle傳送Spinner的值
            Bundle bundle = new Bundle();
            bundle.putString("animal_kind", selected_kind);
            bundle.putString("animal_sex", selected_sex);
            bundle.putString("animal_age", selected_age);
            bundle.putString("animal_colour", selected_colour);
            bundle.putString("animal_shelter", selected_shelter);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
}
