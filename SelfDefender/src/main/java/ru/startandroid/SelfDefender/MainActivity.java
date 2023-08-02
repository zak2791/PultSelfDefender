package ru.startandroid.SelfDefender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static ru.startandroid.SelfDefender.ServerUDP.setPort;
import static ru.startandroid.SelfDefender.verificationCode.gen_code;

public class MainActivity extends AppCompatActivity {
    static public Handler h;
    static public Handler hConn;
    static public Handler hPort;
    static public Thread t;

    private int height;
    private int width;

    private Button btnErr1;
    private Button btnErr2;
    private Button btnErr3;
    private Button btnErr4;
    private Button btnErr5;
    private Button btnErr6;
    private Button btnErr7;
    private Button btnErr8;
    private Button btnErr9;
    private Button btnErr10;
    private Button btnErr11;
    private Button btnErr12;
    private Button btnErr13;
    private Button btnErr14;
    private Button btnErr15;
    private Button btnOne;
    private Button btnZero;
    private Button btnEnter;

    private ColorStateList cButton;

    static public int btnMask;

    private int task;

    private Button bMenu;   //кнопка меню настроек

    private TextView tvRate;
    private TextView tvMat;
    private TextView tvTask;

    static private String androidID;

    static public boolean bFlagKey;           //флаг наличия правильного ключа регистрации программы

    static String PORT_NUMBER = "port_number";
    SharedPreferences sharedPreferences;// = getSharedPreferences("data", MODE_PRIVATE);
    static String sPort;// = sharedPreferences.getString(sPort, "4000");
    static String sKey;
    SharedPreferences.Editor ed;
    //SharedPreferences.Editor ed;// = sharedPreferences.edit();

    static public boolean enter; //флаг нажатия клавиши "Enter"
    static public float fSum; //итоговая оценка

    void checkKey(){            //функция проверки ключа регистрации программы
        String s;
        s = sharedPreferences.getString(sKey, "");
        if(s.equals(gen_code(androidID))){
            bFlagKey = true;
        }else{
            bFlagKey = false;
        }
    }

    static public int getPref() {
        return new Integer(sPort);          //permission;
    }

    static public String getID() {
        return androidID;
    }

    private String mask_to_string(int key_mask){
        int mask = 1;
        float rate = 10.0f;
        for(int i = 0; i < 17; i++){
            if(i < 5){
                if((key_mask & mask) > 0){
                    rate = rate - 0.5f;
                }
            }else if(i < 10){
                if((key_mask & mask) > 0){
                    rate = rate - 1.0f;
                }
            }else if(i < 15){
                if((key_mask & mask) > 0){
                    rate = rate - 2.0f;
                }
            }else if(i == 15){
                if((key_mask & mask) > 0){
                    rate = rate + 1.0f;
                }
            }else if(i == 16){
                if((key_mask & mask) > 0){
                    rate = 0.0f;
                }
            }
            mask = mask << 1;
        }
        if(rate < 0){
            rate = 0.0f;
        }
        return String.format ("%.1f", rate);
    }

    public void onClickRate(View v) {
        switch (v.getId()) {
            case R.id.btnErr1:
                if ((btnMask & 0b00000000000000001) > 0) {
                    btnMask = btnMask & 0b11111111111111110;
                    btnErr1.setTextColor(cButton);//Color.BLACK);
                } else {
                    btnMask = btnMask | 0b00000000000000001;
                    btnErr1.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr2:
                if ((btnMask & 0b00000000000000010) > 0) {
                    btnMask = btnMask & 0b11111111111111101;
                    btnErr2.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000000000000010;
                    btnErr2.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr3:
                if ((btnMask & 0b00000000000000100) > 0) {
                    btnMask = btnMask & 0b11111111111111011;
                    btnErr3.setTextColor(cButton);
                    btnEnter.getTextColors();
                } else {
                    btnMask = btnMask | 0b00000000000000100;
                    btnErr3.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr4:
                if ((btnMask & 0b00000000000001000) > 0) {
                    btnMask = btnMask & 0b11111111111110111;
                    btnErr4.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000000000001000;
                    btnErr4.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr5:
                if ((btnMask & 0b00000000000010000) > 0) {
                    btnMask = btnMask & 0b11111111111101111;
                    btnErr5.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000000000010000;
                    btnErr5.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr6:
                if ((btnMask & 0b00000000000100000) > 0) {
                    btnMask = btnMask & 0b11111111111011111;
                    btnErr6.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000000000100000;
                    btnErr6.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr7:
                if ((btnMask & 0b00000000001000000) > 0) {
                    btnMask = btnMask & 0b11111111110111111;
                    btnErr7.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000000001000000;
                    btnErr7.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr8:
                if ((btnMask & 0b00000000010000000) > 0) {
                    btnMask = btnMask & 0b11111111101111111;
                    btnErr8.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000000010000000;
                    btnErr8.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr9:
                if ((btnMask & 0b00000000100000000) > 0) {
                    btnMask = btnMask & 0b11111111011111111;
                    btnErr9.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000000100000000;
                    btnErr9.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr10:
                if ((btnMask & 0b00000001000000000) > 0) {
                    btnMask = btnMask & 0b11111110111111111;
                    btnErr10.setTextColor(cButton);;
                } else {
                    btnMask = btnMask | 0b00000001000000000;
                    btnErr10.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr11:
                if ((btnMask & 0b00000010000000000) > 0) {
                    btnMask = btnMask & 0b11111101111111111;
                    btnErr11.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000010000000000;
                    btnErr11.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr12:
                if ((btnMask & 0b00000100000000000) > 0) {
                    btnMask = btnMask & 0b11111011111111111;
                    btnErr12.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00000100000000000;
                    btnErr12.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr13:
                if ((btnMask & 0b00001000000000000) > 0) {
                    btnMask = btnMask & 0b11110111111111111;
                    btnErr13.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00001000000000000;
                    btnErr13.setTextColor(Color.RED);;
                }
                break;
            case R.id.btnErr14:
                if ((btnMask & 0b00010000000000000) > 0) {
                    btnMask = btnMask & 0b11101111111111111;
                    btnErr14.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00010000000000000;
                    btnErr14.setTextColor(Color.RED);
                }
                break;
            case R.id.btnErr15:
                if ((btnMask & 0b00100000000000000) > 0) {
                    btnMask = btnMask & 0b11011111111111111;
                    btnErr15.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b00100000000000000;
                    btnErr15.setTextColor(Color.RED);
                }
                break;
            case R.id.btnOne:
                if ((btnMask & 0b01000000000000000) > 0) {
                    btnMask = btnMask & 0b10111111111111111;
                    btnOne.setTextColor(cButton);
                } else {
                    btnMask = btnMask | 0b01000000000000000;
                    btnOne.setTextColor(Color.RED);
                }
                break;
            case R.id.btnZero:
                if ((btnMask & 0b10000000000000000) > 0) {
                    btnMask = btnMask & 0b01111111111111111;
                    btnZero.setTextColor(cButton);;
                } else {
                    btnMask = btnMask | 0b10000000000000000;
                    btnZero.setTextColor(Color.RED);
                }
                break;
            case R.id.btnEnter:
                //btnErr1.setTextColor(cButton);
                btnErr1.setEnabled(false);
                //btnErr2.setTextColor(cButton);
                btnErr2.setEnabled(false);
                //btnErr3.setTextColor(cButton);
                btnErr3.setEnabled(false);
                //btnErr4.setTextColor(cButton);
                btnErr4.setEnabled(false);
                //btnErr5.setTextColor(cButton);
                btnErr5.setEnabled(false);
                //btnErr6.setTextColor(cButton);
                btnErr6.setEnabled(false);
                //btnErr7.setTextColor(cButton);
                btnErr7.setEnabled(false);
                //btnErr8.setTextColor(cButton);
                btnErr8.setEnabled(false);
                //btnErr9.setTextColor(cButton);
                btnErr9.setEnabled(false);
                //btnErr10.setTextColor(cButton);
                btnErr10.setEnabled(false);
                //btnErr11.setTextColor(cButton);
                btnErr11.setEnabled(false);
                //btnErr12.setTextColor(cButton);
                btnErr12.setEnabled(false);
                //btnErr13.setTextColor(cButton);
                btnErr13.setEnabled(false);
                //btnErr14.setTextColor(cButton);
                btnErr14.setEnabled(false);
                //btnErr15.setTextColor(cButton);
                btnErr15.setEnabled(false);
                //btnOne.setTextColor(cButton);
                btnOne.setEnabled(false);
                //btnZero.setTextColor(cButton);
                btnZero.setEnabled(false);
                //btnEnter.setTextColor(cButton);
                btnEnter.setEnabled(false);

                enter = true;
                break;
        }
        tvRate.setText(mask_to_string(btnMask));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        setContentView(R.layout.activity_main);

        ActionBar actionBar =  getSupportActionBar();
        //actionBar.setDisplayShowHomeEnabled(false); //не показываем иконку приложения
        //actionBar.setDisplayShowTitleEnabled(true); // и заголовок тоже прячем
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.act_bar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        sPort = sharedPreferences.getString(PORT_NUMBER, "4120");   //чтение назначенного номера порта из файла настроек

        checkKey();

        task = 0;
        //fSum = 10.0f;
        //Toast toast = Toast.makeText(getApplicationContext(), sPort, Toast.LENGTH_SHORT);
        //toast.show();

        ed = sharedPreferences.edit();

        btnErr1 = (Button) findViewById(R.id.btnErr1);
        btnErr2 = (Button) findViewById(R.id.btnErr2);
        btnErr3 = (Button) findViewById(R.id.btnErr3);
        btnErr4 = (Button) findViewById(R.id.btnErr4);
        btnErr5 = (Button) findViewById(R.id.btnErr5);
        btnErr6 = (Button) findViewById(R.id.btnErr6);
        btnErr7 = (Button) findViewById(R.id.btnErr7);
        btnErr8 = (Button) findViewById(R.id.btnErr8);
        btnErr9 = (Button) findViewById(R.id.btnErr9);
        btnErr10 = (Button) findViewById(R.id.btnErr10);
        btnErr11 = (Button) findViewById(R.id.btnErr11);
        btnErr12 = (Button) findViewById(R.id.btnErr12);
        btnErr13 = (Button) findViewById(R.id.btnErr13);
        btnErr14 = (Button) findViewById(R.id.btnErr14);
        btnErr15 = (Button) findViewById(R.id.btnErr15);
        btnOne = (Button) findViewById(R.id.btnOne);
        btnZero = (Button) findViewById(R.id.btnZero);
        btnEnter = (Button) findViewById(R.id.btnEnter);

        tvRate = (TextView) findViewById(R.id.tvRate);
        tvMat = (TextView) findViewById(R.id.twMat);
        tvTask = (TextView) findViewById(R.id.tvTask);

        cButton = btnErr1.getTextColors();

        bMenu = (Button) findViewById(R.id.button2);

        View.OnClickListener oclBMenu = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bMenu.getContext() , RegistrationActivity.class);
                startActivity(intent);
            }
        };

        bMenu.setOnClickListener(oclBMenu);

        /////////////////////////////////////////////////////////
        // установка шрифтов попорционально размерам элементов //
        /////////////////////////////////////////////////////////
        btnErr14.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                if(left == 0 && top == 0 && right == 0 && bottom == 0){
                    return;
                }
                //Do what ...
                //height = btnErr14.getHeight();
                width = btnErr1.getWidth();
                btnErr1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 111);
                Paint textPaint = btnErr1.getPaint();
                float widthText = textPaint.measureText("НЕЭФФЕКТИВНЫЙ");
                int textSize = (int)((width * 111) / widthText);
                btnErr1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                //widthText = textPaint.measureText("НЕЭФФЕКТИВНЫЙ");
                btnErr1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr5.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr6.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr7.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr8.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr9.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr10.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr11.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr12.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr13.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr14.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                btnErr15.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                //btnOne.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                //btnZero.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        });
        //tvRate.
        String mat;
        int p = Integer.valueOf(sPort);
        if(((p - 4000) / 100) < 2){
            mat = "A" + Integer.toString((p - 4100) / 10);
        }
        else{
            mat = "B" + Integer.toString((p - 4200) / 10);
        }
        tvMat.setText(mat);

        hConn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(msg.what == 1){
                    bMenu.setBackgroundResource(R.drawable.circle_button_green);
                }
                else{
                    bMenu.setBackgroundResource(R.drawable.circle_button_red);
                }
                // обновляем TextView

            };
        };

        hPort = new Handler() {
            public void handleMessage(android.os.Message msg) {
                int p = msg.what;
                String port = Integer.toString(p);
                ed.putString(PORT_NUMBER, port);
                ed.commit();
                String mat;
                if(((p - 4000) / 100) < 2){
                    mat = "A" + Integer.toString((p - 4100) / 10);
                }
                else{
                    mat = "B" + Integer.toString((p - 4200) / 10);
                }
                tvMat.setText(mat);
                setPort(p);
                Toast toast = Toast.makeText(getApplicationContext(), port, Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                if(task != msg.what) {
                    task = msg.what;
                    //lRate.setBackgroundResource(R.drawable.frame_green);
                    //tSum.setTextColor(Color.WHITE);
                    enter = false;
                    btnMask = 0;
                    //iHalf = 0;
                    //iOne = 0;
                    //iTwo = 0;
                    //iSum = 0;
                    //tHalf.setText("0");
                    //tOne.setText("0");
                    //tTwo.setText("0");
                    //iSum = 100;
                    Log.d("task:", "new task: " + Integer.toString(msg.what));
                    if (msg.what != 0) {
                        if(bFlagKey) {
                            btnErr1.setEnabled(true);
                            btnErr2.setEnabled(true);
                            btnErr3.setEnabled(true);
                            btnErr4.setEnabled(true);
                            btnErr5.setEnabled(true);
                            btnErr6.setEnabled(true);
                            btnErr7.setEnabled(true);
                            btnErr8.setEnabled(true);
                            btnErr9.setEnabled(true);
                            btnErr10.setEnabled(true);
                            btnErr11.setEnabled(true);
                            btnErr12.setEnabled(true);
                            btnErr13.setEnabled(true);
                            btnErr14.setEnabled(true);
                            btnErr15.setEnabled(true);
                            btnErr1.setTextColor(cButton);
                            btnErr2.setTextColor(cButton);
                            btnErr3.setTextColor(cButton);
                            btnErr4.setTextColor(cButton);
                            btnErr5.setTextColor(cButton);
                            btnErr6.setTextColor(cButton);
                            btnErr7.setTextColor(cButton);
                            btnErr8.setTextColor(cButton);
                            btnErr9.setTextColor(cButton);
                            btnErr10.setTextColor(cButton);
                            btnErr11.setTextColor(cButton);
                            btnErr12.setTextColor(cButton);
                            btnErr13.setTextColor(cButton);
                            btnErr14.setTextColor(cButton);
                            btnErr15.setTextColor(cButton);
                        }
                        btnOne.setEnabled(true);
                        btnZero.setEnabled(true);
                        btnEnter.setEnabled(true);
                        btnOne.setTextColor(cButton);
                        btnZero.setTextColor(cButton);
                        btnEnter.setTextColor(cButton);
                        //tRateVal.setText(Integer.toString(msg.what));
                        tvTask.setText(Integer.toString(msg.what));
                        tvRate.setText("10.0");
                        //tSum.setText("10");
                    } else {
                        //tRateVal.setText("");
                        tvTask.setText("");
                        //tSum.setText("");
                    }
                }
            };
        };
        t = new Thread(new ServerUDP());
        setPort(Integer.valueOf(sPort));
        t.start();
    }

}
