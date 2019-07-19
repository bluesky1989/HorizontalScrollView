package com.dyq.horizontalscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private ListView lv_book;
    private List<String> books;
    private ArrayAdapter<String> adapter;
    private String data[];
    private int array[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_book=findViewById(R.id.lv_book);


        data=new String[]{"A","B","C","D","E","F","G","H","I","J","K"};

        array=new int[]{1,2,3,4};
        Log.e(TAG,"整形数组：array="+array.toString());
        for (int i=0;i<array.length;i++){
            Log.e(TAG,"第array["+i+"]="+array[i]);
        }

        books=new ArrayList<>();
        books.add("第一行代码第二版");
        books.add("android开发艺术探索");
        books.add("A");
        books.add("B");
        books.add("C");
        books.add("D");
        books.add("E");
        books.add("F");
        books.add("G");
        books.add("H");
        books.add("I");
        books.add("J");
        books.add("K");

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);

        lv_book.setAdapter(adapter);

        lv_book.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String result=parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this,"result="+result,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
