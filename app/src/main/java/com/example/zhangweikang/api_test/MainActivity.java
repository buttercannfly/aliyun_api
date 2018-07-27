package com.example.zhangweikang.api_test;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;

public class MainActivity extends AppCompatActivity {

    public static String real_answer="";
    private final String lock="lock";
    private EditText ed;
    private Button bt;
    private TextView tx;
    public String finalanswer;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt=findViewById(R.id.bt1);
        ed=findViewById(R.id.ed);
        tx=findViewById(R.id.tx1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Demo_智能问答.智能回复接口HttpsTest(ed.getText().toString());
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            finalanswer = Constant.mAnswer;
                            if (finalanswer != null) {
                                Log.e("TAG:::::::::::", finalanswer);
                            }
                            lock.notify();
                        }
                    }
                };
                Timer timer =new Timer();
                timer.schedule(task,500);
                synchronized (lock) {
                    try {
                        lock.wait();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

//                parseJson(Constant.mAnswer);

                mHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        super.handleMessage(msg);
                        tx.setText(real_answer);
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        parseJson(Constant.mAnswer);
                        Message message = new Message();
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });

    }

    //json转化，提取content
    private void parseJson(String json){
        try{

            JSONObject jsonObject = (JSONObject) new JSONObject(json);
            JSONObject jsonObject1=(JSONObject)jsonObject.getJSONObject("result");
            real_answer=jsonObject1.getString("content");
        }
        catch(Exception e){
            Log.e("NNNNNN","OOOOOOOO");
            e.printStackTrace();
        }
    }
}
