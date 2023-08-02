package ru.startandroid.SelfDefender;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//
import static ru.startandroid.SelfDefender.MainActivity.bFlagKey;
import static ru.startandroid.SelfDefender.MainActivity.getID;
//import static ru.startandroid.h2htablo15questions.MainActivity.getPref;
import static ru.startandroid.SelfDefender.MainActivity.sKey;
import static ru.startandroid.SelfDefender.ServerUDP.setPort;
import static ru.startandroid.SelfDefender.myContext.getIP;
import static ru.startandroid.SelfDefender.MainActivity.getPref;
import static ru.startandroid.SelfDefender.verificationCode.gen_code;

public class RegistrationActivity extends AppCompatActivity  implements OnTouchListener {
    TextView tvConn;
    public static EditText etReg;
    static public Handler hReg;
    Button btnA;
    Button btnB;
    Button bKey;
    EditText etKey;
    SharedPreferences sP;
    String sPort;
    SharedPreferences sharedPreferences;
    String message = "registration";
    boolean timer;
    int matA = 0;
    int matB = 1;
    //boolean startTime = false;
    //boolean time = false;

    int getPort(){
        return Integer.getInteger(sharedPreferences.getString(MainActivity.sPort, ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        String androidID = getID();
        Toast.makeText(this, androidID, Toast.LENGTH_LONG).show();
        btnA = (Button) findViewById(R.id.bConnA);
        btnB = (Button) findViewById(R.id.bConnB);
        tvConn = (TextView) findViewById(R.id.tvConn);
        etReg = (EditText) findViewById(R.id.etReg);
        bKey = (Button) findViewById(R.id.bKey);
        etKey = (EditText) findViewById(R.id.etKey);
        if(bFlagKey){
            bKey.setEnabled(false);
            etKey.setEnabled(false);
        }else{
            etKey.setText(getID());
        }

        etReg.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                // Прописываем то, что надо выполнить после изменения текста
                if(etReg.getText().length() != 4){
                    btnA.setEnabled(false);
                    btnB.setEnabled(false);
                }else{
                    btnA.setEnabled(true);
                    btnB.setEnabled(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }
//////////////////////////////////////////////////////////////////////////////////////
// запрет закрытия эрана подключения к программе табло во вемя процесса подключения //
//////////////////////////////////////////////////////////////////////////////////////
    public void onBackPressed() {
        if(this.btnA.isEnabled() && btnB.isEnabled()) {
            this.finish();
        }
    }
///////////////////////////////////////////////////////////////////////////////////////

    public void onClickConn(View v) {
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.bConnA:
                // кнопка ОК
                new MyTask().execute(matA);
                btnA.setEnabled(false);
                btnB.setEnabled(false);
                //tvConn.setText("registration");
                //message = "registration";
                break;
            case R.id.bConnB:
                // кнопка Cancel
                new MyTask().execute(matB);
                btnA.setEnabled(false);
                btnB.setEnabled(false);
                //tvConn.setText("registration");
                //message = "registration";
                break;
            case R.id.bRefresh:
                int i = getPref();
                //Toast toast = Toast.makeText(getApplicationContext(), "port " + String.valueOf(i), Toast.LENGTH_SHORT);
                //toast.show();
                setPort(i);
                Toast toast = Toast.makeText(getApplicationContext(), i, Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.bKey:
                String key = etKey.getText().toString();
                if(key.equals(gen_code(getID()))) {
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putString(sKey, key);
                    ed.commit();
                    etKey.setEnabled(false);
                    bKey.setEnabled(false);
                    bFlagKey = true;
                }
                break;
        }
        //String savedText = sharedPreferences.getString(MainActivity.sPort, "");
        //Toast toast = Toast.makeText(getApplicationContext(), savedText, Toast.LENGTH_SHORT);
        //toast.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                Log.i("поток", "нажали" );
                message = "registration";
                //setRunning();
                tvConn.setText("registration");
                v.setEnabled(false);
                etReg.setEnabled(false);
                break;
            case MotionEvent.ACTION_MOVE: // движение

                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                Log.i("поток", "отпустили" );
                //setP(true);
                break;
        }
        return true;
    }

    class MyTask extends AsyncTask<Integer, Integer, Integer> {
        //DatagramSocket udpSocket = null;
        byte[] buf_send = new byte[32];
        int port;
        boolean connecting;
        int count_conn;
        //InetAddress
        String address = null;
        boolean connResult;
        String androidID;
        byte lenID;
        String code;
        //DatagramPacket dp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //buf_send = "pult".getBytes();
            androidID = getID();
            lenID = (byte) androidID.length();
            //port = 4000;
            code = etReg.getText().toString();


        }
        @Override
        protected Integer doInBackground(Integer... param) {
            int newPort = 0;
            //while(connecting) {
                DatagramSocket udpSocket = null;
                try {
                    if(param[0] == 0) port = 4000;
                    else port = 4001;
                    buf_send = (code + androidID).getBytes();         //проверочный код
                    udpSocket = new DatagramSocket(port);
                    int ipAddress = getIP();
                    byte[] ip = {(byte) (ipAddress & 0xff),
                            (byte) (ipAddress >> 8 & 0xff),
                            (byte) (ipAddress >> 16 & 0xff),
                            (byte) (ipAddress >> 24 & 0xff)};
                    InetAddress serverAddr = InetAddress.getByAddress(ip);
                    DatagramPacket dp;                                                      //////////////////////////////////////
                    dp = new DatagramPacket(buf_send, 4 + lenID, serverAddr, port);          //////////////////////////////////////
                    udpSocket.send(dp);
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                    udpSocket = new DatagramSocket(port);
                    udpSocket.setSoTimeout(10000);
                    byte[] buf_recieve = new byte[4];
                    //buf_recieve[0] = 6;
                    dp = new DatagramPacket(buf_recieve, buf_recieve.length);
                    udpSocket.receive(dp);
                    if (dp.getLength() == 4) {                                    //если получено 4 байта (номер порта)
                        String s = new String(buf_recieve, 0, 4);   //;// = new String()
                        //Toast.makeText(getApplicationContext(), "connection", Toast.LENGTH_LONG).show();
                        //if(s.equals(code)) {
                            //connecting = false;
                            //d
                        newPort = Integer.valueOf(new String(buf_recieve));
                        address = dp.getAddress().toString().substring(1);
                        //}
                    }
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                } catch (IOException e) {                                //timeout

                } finally {
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                }
           return newPort;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //tvInfo.setText("End");

            btnA.setEnabled(true);
            btnB.setEnabled(true);
            if(result != 0) {
                tvConn.setText("registration succefull");
                MainActivity.hPort.sendEmptyMessage(result);
            }else{
                tvConn.setText("registration failed");
                //RegistrationActivity.hReg.sendEmptyMessage(0);
            }
        }
    }
}
