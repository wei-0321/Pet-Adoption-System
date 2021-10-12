package com.example.pet_adoption;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
{
    //(類別的屬性)
    //介面元件
    Button BTN_search, BTN_map,BTN_reload;
    ImageButton img_reload;
    ListView LV_animal;
    //資料
    public ArrayList<Map<String, String>> animal_list = new ArrayList<Map<String, String>> ();  //全部動物的重要資訊

    public boolean search = false;   //判斷使用者是否從搜尋頁面過來的
    public ArrayList<Map<String, String>> search_animal_list;  //符合搜尋條件的動物資訊

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BTN_search = findViewById(R.id.BTN_search);
        BTN_map = findViewById(R.id.BTN_map);
        BTN_reload = findViewById(R.id.BTN_reload);
        img_reload = findViewById(R.id.img_reload);
        LV_animal = findViewById(R.id.LV_animal);

        BTN_search.setOnClickListener(BTN_search_listener);
        BTN_map.setOnClickListener(BTN_map_listener);
        BTN_reload.setOnClickListener((reload_listener));
        img_reload.setOnClickListener((reload_listener));

        LV_animal.setOnItemClickListener(LV_animal_listener);


        ///接收搜尋頁面傳來的篩選條件
        Map<String, String> search_condotion = new HashMap<String, String> ();   //用字典型式來存放使用者的搜尋條件，方便之後的篩選

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
        {
            search = true;
            search_condotion.put("animal_kind", bundle.getString("animal_kind"));
            search_condotion.put("animal_sex", bundle.getString("animal_sex"));
            search_condotion.put("animal_age", bundle.getString("animal_age"));
            search_condotion.put("animal_colour", bundle.getString("animal_colour"));
            search_condotion.put("shelter_name", bundle.getString("animal_shelter"));

        }

        //接收已經存進sharedpreference的物件，若發現sharedpreference已經有儲存的物件，就拿出來使用  /  否則將預設為null
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String arrayListString = preferences.getString("animal" , null);   //預設為null
        ArrayList<Map<String, String>> animal_list = gson.fromJson(arrayListString, ArrayList.class);

        if (animal_list != null)   //sharedpreference已經有儲存的物件，直接拿出來使用
        {
            if (search)   //如果使用者是從搜尋頁面按搜尋的話，要先篩選出動物，再顯示出來
            {
                search_animal_list = new ArrayList<Map<String, String>> ();  //搜尋後符合條件的動物
                for (int i = 0; i < animal_list.size(); i++)
                {
                    boolean match = true;
                    Map<String, String> each_animal = animal_list.get(i);
                    for (String s : search_condotion.keySet())
                    {
                        if (search_condotion.get(s).equals("所有"))
                            continue;
                        else
                        {
                            if (s.equals("shelter_name"))    //因為我們條件是用縣市，所以要先擷取收容所的前三個字(即為縣市)
                            {
                                String city = each_animal.get(s).substring(0, 3);
                                if (!city.equals(search_condotion.get(s)))
                                {
                                    match = false;
                                    break;
                                }
                            }
                            else
                            {
                                if (!each_animal.get(s).equals(search_condotion.get(s)))
                                {
                                    match = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (match)
                        search_animal_list.add((each_animal));
                }
                if (search_animal_list.size() == 0)    //若找不到符合條件的動物  就使用對話方塊提醒使用者
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("查無資料");
                    builder.setMessage("找不到符合條件的動物，請放寬搜尋的條件!!");
                    builder.setNegativeButton("確定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    //將資料與UI連結
                    SimpleAdapter adapter = set_adapter(search_animal_list);   //使用自創建的函數設定adapter
                    LV_animal.setAdapter(adapter);  //與ListView連接
                }
            }
            else
            {
                //直接將資料與UI連結
                SimpleAdapter adapter = set_adapter(animal_list);   //使用自創建的函數設定adapter
                LV_animal.setAdapter(adapter);  //與ListView連接
            }
        }
        else   //sharedpreference尚未有儲存的物件，所以需要靠網路抓取我們要的資料
        {
            try
            {
                //在程式碼中呼叫AsyncTask類別的建構子產生物件，再呼叫execute()方法
                // 即可產生另一個執行緒，並在背景執行AsyncTask doInBackground方法內的程式碼
                Animal animal = new Animal();
                animal.execute("https://data.coa.gov.tw/Service/OpenData/TransService.aspx?UnitId=QcbUEzN6E6DL");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    //點選重新整理
    private Button.OnClickListener reload_listener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().commit();

            Intent intent = new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        }
    };

    //前往選單
    private Button.OnClickListener BTN_search_listener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(MainActivity.this, Spinner_Main.class);
            startActivity(intent);
        }


    };

    //點選前往map搜尋
    private Button.OnClickListener BTN_map_listener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(MainActivity.this,MapMain.class);
            startActivity(intent);
        }
    };

    //點選電話
    private ListView.OnItemClickListener LV_animal_listener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (search)    //搜尋
            {
                intent.setData(Uri.parse("tel:" + search_animal_list.get(position).get("shelter_tel"))); //取得收容所電話號碼
            }
            else          //未搜尋
            {
                //接收已經存進sharedpreference的物件，若發現sharedpreference已經有儲存的物件，就拿出來使用  /  否則將預設為null
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                Gson gson = new Gson();
                String arrayListString = preferences.getString("animal" , null);   //預設為null
                ArrayList<Map<String, String>> animal_list = gson.fromJson(arrayListString, ArrayList.class);

                intent.setData(Uri.parse("tel:" + animal_list.get(position).get("shelter_tel"))); //取得收容所電話號碼
            }

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                //無權限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
            else
            {
                //有權限
                startActivity(intent);
            }
        }
    };


    //內部類別
    class Animal extends AsyncTask<String,Void,String>   //目標是抓取網頁上所有的(JSON)字串
    {   //AsyncTask<傳入值型態, 更新進度型態, 結果型態>
        //主執行緒不可以執行網路相關的動作，所以要建立AsyncTask類別用來處理背景任務與UI
        //AsyncTask是抽象類別，因此繼承它的子類別都必須實作其抽象方法，也就是唯一的doInBackground方法
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,"載入中","",true);
            progressDialog.setContentView(R.layout.load_gif);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected String doInBackground(String... address)       //執行中 在背景做事情 => 目的是抓到所有資料
        {
            try
            {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();  // 連線

                StringBuffer json = new StringBuffer();

                //開始抓取資料
                if (connection.getResponseCode() == 200)     //若網頁正常
                {
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        json.append(line);
                    }
                    reader.close();
                }
                return json.toString();
            }
            catch (ConnectException e)
            {
                e.printStackTrace();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result)   //已完成背景任務(利用網路抓取所有字串)
        //處理在doInBackground得到的字串，將其剖析
        //除了背景任務(doInBackground)以外的任務都會回到主執行緒(UI執行緒)  所以可以在結果的部分對UI做修改
        {
            super.onPostExecute(result);

            try
            {
                if (progressDialog!= null && progressDialog.isShowing())
                {
                    if (result.length() <= 2000)   //並非抓取到正確的資訊 代表API網頁有問題
                    {
                        //使用對話方塊通知使用者
                        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("錯誤訊息");
                        builder.setMessage("目前API網頁異常，請稍後再試!");
                        builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        progressDialog.dismiss();     //移除進度條
                        return;
                    }

                    //此opendata格式: [{ }, { }, { }, ......, { }]
                    //最外層是陣列，要用JSONArray    (沒有json物件所以不用用到getJSONObject的方法)
                    JSONArray result_array = new JSONArray(result);
                    for (int i = 0; i < result_array.length(); i++) {
                        //if (i >= 30)    //目前只顯示30筆
                        //    break;
                        JSONObject object = result_array.getJSONObject(i);

                        //動物的類型
                        String animal_kind = object.getString("animal_kind");
                        //動物性別
                        String animal_sex = object.getString("animal_sex");
                        if (animal_sex.equals("M"))
                            animal_sex = "公";
                        else if (animal_sex.equals("F"))
                            animal_sex = "母";
                        else
                            animal_sex = "未輸入";
                        //動物毛色
                        String animal_colour = object.getString("animal_colour");
                        //動物年紀
                        String animal_age = object.getString("animal_age");
                        if (animal_age.equals("CHILD"))
                            animal_age = "幼年";
                        else
                            animal_age = "成年";
                        //動物所屬收容所名稱
                        String shelter_name = object.getString("shelter_name");
                        //動物所屬收容所的連絡電話
                        String shelter_tel = object.getString("shelter_tel");
                        //開放認養時間(起)
                        String animal_opendate = object.getString("animal_opendate");
                        if (animal_opendate.equals(""))
                            animal_opendate = "未輸入，請打電話至收容所詢問";
                        //資料備註
                        String animal_remark = object.getString("animal_remark");
                        if (animal_remark.equals(""))
                            animal_remark = "(無)";
                        //動物圖片的網址
                        String album_file = object.getString("album_file");


                        Map<String, String> animal = new HashMap<String, String>();   //字典
                        animal.put("animal_kind", animal_kind);
                        animal.put("animal_sex", animal_sex);
                        animal.put("animal_colour", animal_colour);
                        animal.put("animal_age", animal_age);
                        animal.put("shelter_name", shelter_name);
                        animal.put("shelter_tel", shelter_tel);
                        animal.put("animal_opendate", animal_opendate);
                        animal.put("animal_remark", animal_remark);
                        animal.put("album_file", album_file);
                        animal_list.add(animal);
                    }

                    //到此資料已真正處理完畢(完成JSON格式的剖析)

                    //將重要的動物資訊存放起來  這樣之後才不用重複要求API抓資料
                    //使用SharedPreference，它是Android中的儲存資料機制，使用者離開APP後資料都還會留著
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String animal_list_pass = gson.toJson(animal_list);

                    editor.putString("animal", animal_list_pass);
                    editor.commit();


                    // 接下來要將資料與UI連結
                    SimpleAdapter adapter = set_adapter(animal_list);   //使用自創建的函數設定adapter
                    //與ListView連接
                    LV_animal.setAdapter(adapter);
                }
                progressDialog.dismiss();   //動作完成之後，去除進度條
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    class Get_image extends AsyncTask<String, Void, Bitmap>   //目的是抓取網路上的圖片
    {
        @Override
        protected Bitmap doInBackground(String... address)    //執行中 在背景做事情
        {
            try
            {
                URL image_url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) image_url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                if (connection.getResponseCode() == 200)  //若網頁正常
                {
                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    return bitmap;
                }
            }
            catch (ConnectException e)
            {
                e.printStackTrace();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }


    public SimpleAdapter set_adapter(List<Map<String, String>> data_list)     //進行adapter的相關設定，將資料與UI元件連結
    {
        Context context = MainActivity.this;        //介面元件是屬於MainActivity的
        SimpleAdapter adapter = new SimpleAdapter   //建立adapter 準備要與ListView連接
                (
                        context,               //MainActivity.this
                        data_list,           //動物的資料
                        R.layout.my_layout,    //自定義的layout檔案
                        // 欄位的鍵(key)
                        new String[]{"animal_kind", "animal_sex", "animal_colour", "animal_age", "shelter_name", "shelter_tel", "animal_opendate", "animal_remark", "album_file"},
                        //欄位的值(value) => 要放進layout裡的元件
                        new int[]{R.id.TV_animal_kind, R.id.TV_animal_sex, R.id.TV_animal_colour, R.id.TV_animal_age, R.id.TV_animal_shelter_name, R.id.TV_animal_shelter_tel, R.id.TV_animal_opendate, R.id.TV_animal_remark, R.id.animal_image}
                );

        //改寫方法 : SimpleAdapter本身是不支持網路圖片的，所以要加以改寫
        adapter.setViewBinder(new SimpleAdapter.ViewBinder()
        {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation)  //改寫
            {
                //而主執行緒不可以執行網路相關的動作，所以要再次建立AsyncTask類別(Get_image) => 目的是抓取網路上的圖片
                if (view instanceof ImageView & data instanceof String)
                {
                    ImageView iv = (ImageView) view;
                    String url = (String)data;
                    //透過圖片的url從網路抓取圖片  並設置至imageview
                    try
                    {
                        Get_image get_image = new Get_image();
                        Bitmap image = get_image.execute(url).get();   //執行背景任務
                        if (image != null)    //如果成功抓到圖片
                            iv.setImageBitmap(image);
                        else   //如果抓取圖片失敗  就使用預設的圖片
                            iv.setImageResource(R.drawable.default_picture);  //預設圖片
                    }
                    catch (ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        return adapter;
    }




}
