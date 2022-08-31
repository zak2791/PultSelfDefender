package ru.startandroid.h2htablo;

//import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import static ru.startandroid.h2htablo.ServerUDP.setPort;
import static android.app.PendingIntent.getActivity;
import static ru.startandroid.h2htablo.verificationCode.gen_code;

public class MainActivity extends AppCompatActivity {
    static public Handler h;
    static public Handler hConn;
    static public Handler hPort;
    static public Thread t;
    static public Thread tReg;

    static private boolean permission;

    static private String androidID;

    private TextView tMat;
    private TextView tHalf;
    private TextView tOne;
    private TextView tTwo;
    private TextView tSum;
    private TextView tRateVal;
    private TextView tRate;
    private Button bHalf_plus;
    private Button bHalf_minus;
    private Button bOne_plus;
    private Button bOne_minus;
    private Button bTwo_plus;
    private Button bTwo_minus;
    private Button bZero;
    private Button bEnter;

    private Button bMenu;

    private int task;
    private int height;
    private float textHeight;

    private LinearLayout lRate;

    private int iHalf;  //количество штрафных оценок "0.5"
    private int iOne;  //количество штрафных оценок "1"
    private int iTwo;  //количество штрафных оценок "2"
    private int iSum;  //итоговая оценка * 10
    static public float fSum; //строка, представляющая итоговую оценку
    static public boolean enter; //флаг нажатия клавиши "Enter"

    static public boolean bFlagKey;           //флаг наличия правильного ключа регистрации программы


    static String sPort = "0";
    SharedPreferences sharedPreferences;// = getSharedPreferences("data", MODE_PRIVATE);
    static String savedText;// = sharedPreferences.getString(sPort, "4000");
    static String sKey;
    Editor ed;// = sharedPreferences.edit();


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
        return new Integer(savedText);          //permission;
    }

    public void setPref(String port){
        ed.putString(sPort, port);
        ed.commit();
        //permission = perm;
    }

    static public String getID() {
        return androidID;
    }


    //private boolean enter_else;  //флаг повторного нажатия клавиши "Enter"

    //static public Context ctx;//
    //private String addressAsString;
    //private Thread udpConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);


        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setContentView(R.layout.activity_main);

        ActionBar actionBar =  getSupportActionBar();
        //actionBar.setDisplayShowHomeEnabled(false); //не показываем иконку приложения
        //actionBar.setDisplayShowTitleEnabled(true); // и заголовок тоже прячем
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.act_bar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        savedText = sharedPreferences.getString(sPort, "4120");

        ed = sharedPreferences.edit();

        checkKey();
        iHalf = 0;
        iOne = 0;
        iTwo = 0;
        iSum = 100;

        task = 0;
        enter = false;




        tHalf = (TextView) findViewById(R.id.tHalf);
        tOne = (TextView) findViewById(R.id.tOne);
        tTwo = (TextView) findViewById(R.id.tTwo);
        tSum = (TextView) findViewById(R.id.tSum);
        tMat = (TextView) findViewById(R.id.tMat);
        tRateVal = (TextView) findViewById(R.id.tRateVal);
        tRate = (TextView) findViewById(R.id.tRate);

        bHalf_plus = (Button) findViewById(R.id.bHalf_plus);
        bHalf_minus = (Button) findViewById(R.id.bHalf_minus);
        bOne_plus = (Button) findViewById(R.id.bOne_plus);
        bOne_minus = (Button) findViewById(R.id.bOne_minus);
        bTwo_plus = (Button) findViewById(R.id.bTwo_plus);
        bTwo_minus = (Button) findViewById(R.id.bTwo_minus);
        bZero = (Button) findViewById(R.id.bZero);
        bEnter = (Button) findViewById(R.id.bEnter);


        lRate = (LinearLayout) findViewById(R.id.lRate);

        //bHalf_plus.setEnabled(false);
        bHalf_plus.setOnClickListener(onClickListener);
        bHalf_minus.setOnClickListener(onClickListener);
        bOne_plus.setOnClickListener(onClickListener);
        bOne_minus.setOnClickListener(onClickListener);
        bTwo_plus.setOnClickListener(onClickListener);
        bTwo_minus.setOnClickListener(onClickListener);
        bZero.setOnClickListener(onClickListener);
        bEnter.setOnClickListener(onClickListener);

        bHalf_plus.setEnabled(false);
        bHalf_minus.setEnabled(false);
        bOne_plus.setEnabled(false);
        bOne_minus.setEnabled(false);
        bTwo_plus.setEnabled(false);
        bTwo_minus.setEnabled(false);
        bZero.setEnabled(false);
        bEnter.setEnabled(false);

        tRateVal.setText("");
        tSum.setText("");
        /////////////////////////////////////////////////////////

        bMenu = (Button) findViewById(R.id.button2);

        View.OnClickListener oclBMenu = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bMenu.getContext() , RegistrationActivity.class);
                startActivity(intent);
            }
        };
        bMenu.setOnClickListener(oclBMenu);

        /////////////////////////////////////////////
        // установка размера круглой кнопки в меню //
        /////////////////////////////////////////////
        bMenu.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                if(left == 0 && top == 0 && right == 0 && bottom == 0){
                    return;
                }
                //Do what ...
                int height = bMenu.getHeight();
                //bMenu.setMinWidth(height-1);
                bMenu.setWidth(height);
                //bMenu.setBackgroundColor(Color.RED);
                bMenu.setBackgroundResource(R.drawable.circle_button_red);
                //height = bMenu.getWidth();
            }
        });

        /////////////////////////////////////////////////////////
        // установка шрифтов попорционально размерам элементов //
        /////////////////////////////////////////////////////////
        tMat.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                if(left == 0 && top == 0 && right == 0 && bottom == 0){
                    return;
                }
                //Do what ...
                height = tMat.getHeight();
                tMat.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.7f);
                tRate.setTextSize(TypedValue.COMPLEX_UNIT_PX,tMat.getTextSize() * 0.43f);
                tRateVal.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.7f);
                tSum.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.9f);
                tHalf.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                tOne.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                tTwo.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bHalf_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bHalf_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bOne_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bOne_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bTwo_minus.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bTwo_plus.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bZero.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
                bEnter.setTextSize(TypedValue.COMPLEX_UNIT_PX, height * 0.24f);
            }
        });

        String mat;
        int p = Integer.valueOf(savedText);
        if(((p - 4000) / 100) < 2){
            mat = "A" + Integer.toString((p - 4100) / 10);
        }
        else{
            mat = "B" + Integer.toString((p - 4200) / 10);
        }
        tMat.setText(mat);
        tRate.setText("RATE\nTHE\nTASK");
        tHalf.setText("0");
        tOne.setText("0");
        tTwo.setText("0");
        bHalf_minus.setText("-0.5");
        bHalf_plus.setText("+0.5");
        bOne_minus.setText("-1");
        bOne_plus.setText("+1");
        bTwo_minus.setText("-2");
        bTwo_plus.setText("+2");
        bZero.setText("0");
        bEnter.setText("ENTER");

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
                ed.putString(sPort, Integer.toString(p));
                ed.commit();
                String mat;
                if(((p - 4000) / 100) < 2){
                    mat = "A" + Integer.toString((p - 4100) / 10);
                }
                else{
                    mat = "B" + Integer.toString((p - 4200) / 10);
                }
                tMat.setText(mat);
                setPort(p);
            }
        };
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                if(task != msg.what) {
                    task = msg.what;
                    lRate.setBackgroundResource(R.drawable.frame_green);
                    tSum.setTextColor(Color.WHITE);
                    enter = false;

                    iHalf = 0;
                    iOne = 0;
                    iTwo = 0;
                    iSum = 0;
                    tHalf.setText("0");
                    tOne.setText("0");
                    tTwo.setText("0");
                    iSum = 100;
                    Log.d("task:", "new task: " + Integer.toString(msg.what));
                    if (msg.what != 0) {
                        if(bFlagKey) {
                            bHalf_plus.setEnabled(true);
                            bHalf_minus.setEnabled(true);
                            bOne_plus.setEnabled(true);
                            bOne_minus.setEnabled(true);
                            bTwo_plus.setEnabled(true);
                            bTwo_minus.setEnabled(true);
                        }
                        bZero.setEnabled(true);
                        bEnter.setEnabled(true);
                        tRateVal.setText(Integer.toString(msg.what));
                        tSum.setText("10");
                    } else {
                        tRateVal.setText("");
                        tSum.setText("");
                    }
                }
            };
        };
        permission = true;
        t = new Thread(new ServerUDP());
        setPort(Integer.valueOf(savedText));
        t.start();
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        //menu.add("Registration");
        getMenuInflater().inflate(R.menu.main, menu);
        return true; //super.onCreateOptionsMenu(menu);
    }
    */



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

     private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bHalf_plus:
                    if (iHalf < 9 && iSum >= 5) {
                        tHalf.setText(Integer.toString(++iHalf));
                        iSum -= 5;
                        tSum.setText(Float.toString((float) iSum / 10));
                    }
                    break;
                case R.id.bHalf_minus:
                    if (iHalf > 0) {
                        tHalf.setText(Integer.toString(--iHalf));
                        iSum += 5;
                        if (iSum == 100) {
                            tSum.setText("10");
                        } else {
                            tSum.setText(Float.toString((float) iSum / 10));
                        }
                    }
                    break;
                case R.id.bOne_plus:
                    if (iOne < 9 && iSum >= 10) {
                        tOne.setText(Integer.toString(++iOne));
                        iSum -= 10;
                        tSum.setText(Float.toString((float) iSum / 10));
                    }
                    break;
                case R.id.bOne_minus:
                    if (iOne > 0) {
                        tOne.setText(Integer.toString(--iOne));
                        iSum += 10;
                        if (iSum == 100) {
                            tSum.setText("10");
                        } else {
                            tSum.setText(Float.toString((float) iSum / 10));
                        }
                    }
                    break;
                case R.id.bTwo_plus:
                    if (iTwo < 6 && iSum >= 20) {
                        tTwo.setText(Integer.toString(++iTwo));
                        iSum -= 20;
                        tSum.setText(Float.toString((float) iSum / 10));
                    }
                    break;
                case R.id.bTwo_minus:
                    if (iTwo > 0) {
                        tTwo.setText(Integer.toString(--iTwo));
                        iSum += 20;
                        if (iSum == 100) {
                            tSum.setText("10");
                        } else {
                            tSum.setText(Float.toString((float) iSum / 10));
                        }
                    }
                    break;
                case R.id.bZero:
                    if (bHalf_plus.isEnabled() || !bFlagKey) {
                        iHalf = 0;
                        iOne = 0;
                        iTwo = 0;
                        iSum = 0;
                        tHalf.setText("0");
                        tOne.setText("0");
                        tTwo.setText("0");
                        tSum.setText("0.0");
                        bHalf_plus.setEnabled(false);
                        bHalf_minus.setEnabled(false);
                        bOne_plus.setEnabled(false);
                        bOne_minus.setEnabled(false);
                        bTwo_plus.setEnabled(false);
                        bTwo_minus.setEnabled(false);
                    } else {
                        iSum = 100;
                        tSum.setText("10");
                        if(bFlagKey) {
                            bHalf_plus.setEnabled(true);
                            bHalf_minus.setEnabled(true);
                            bOne_plus.setEnabled(true);
                            bOne_minus.setEnabled(true);
                            bTwo_plus.setEnabled(true);
                            bTwo_minus.setEnabled(true);
                        }
                    }
                    break;
                case R.id.bEnter:
                    bHalf_plus.setEnabled(false);
                    bHalf_minus.setEnabled(false);
                    bOne_plus.setEnabled(false);
                    bOne_minus.setEnabled(false);
                    bTwo_plus.setEnabled(false);
                    bTwo_minus.setEnabled(false);
                    bZero.setEnabled(false);
                    bEnter.setEnabled(false);
                    lRate.setBackgroundResource(R.drawable.frame_green_white);
                    tSum.setTextColor(Color.BLACK);
                    fSum = (float)iSum / 10;
                    if(iSum == 100){
                    tSum.setText("10");
                    }else{
                        tSum.setText(String.valueOf(fSum));
                    }
                    enter = true;
                    break;
            }
        }
     };
}

