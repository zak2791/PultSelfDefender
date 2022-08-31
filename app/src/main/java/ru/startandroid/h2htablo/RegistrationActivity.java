package ru.startandroid.h2htablo;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import static ru.startandroid.h2htablo.MainActivity.bFlagKey;
import static ru.startandroid.h2htablo.MainActivity.getID;
import static ru.startandroid.h2htablo.MainActivity.getPref;
import static ru.startandroid.h2htablo.MainActivity.sKey;
import static ru.startandroid.h2htablo.ServerUDP.setPort;
import static ru.startandroid.h2htablo.myContext.getIP;
import static ru.startandroid.h2htablo.verificationCode.gen_code;

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
    /*
    int getPort(){
        return Integer.getInteger(sharedPreferences.getString(MainActivity.sPort, ""));
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        String androidID = getID();
        //Toast.makeText(this, androidID, Toast.LENGTH_LONG).show();
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
        //if(getPort() != 0) {
        //    btnConn.setEnabled(false);
        //}
        /*
        if(getRunning()){
            btnReg.setEnabled(false);
            etReg.setEnabled(false);
        }
*/
        //Toast toast = Toast.makeText(getApplicationContext(), MainActivity.sPort, Toast.LENGTH_SHORT);
        //toast.show();

        hReg = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(msg.what == 0){
                    tvConn.setText("registration failed");
                    btnA.setEnabled(true);
                    btnB.setEnabled(true);
                    etReg.setEnabled(true);
                }
                if(msg.what == 1){
                    tvConn.setText("registration sucsefully");
                    btnA.setEnabled(true);
                    btnB.setEnabled(true);
                    etReg.setEnabled(true);
                }
                if(msg.what == 2){
                    message += " .";
                    tvConn.setText(message);
                }
            }
        };

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
                new MyTask().execute("matA");
                btnA.setEnabled(false);
                btnB.setEnabled(false);
                message = "registration";
                break;
            case R.id.bConnB:
                // кнопка Cancel
                new MyTask().execute("matB");
                btnA.setEnabled(false);
                btnB.setEnabled(false);
                message = "registration";
                break;
            case R.id.bRefresh:
                int i = getPref();
                //Toast toast = Toast.makeText(getApplicationContext(), "port " + String.valueOf(i), Toast.LENGTH_SHORT);
                //toast.show();
                setPort(i);
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
                tvConn.setText(message);
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

    class MyTask extends AsyncTask<String, Void, Integer> {
        //DatagramSocket udpSocket = null;
        byte[] buf_send = new byte[4];
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
            port = 4000;
            code = etReg.getText().toString();
            connecting = true;
            count_conn = 0;

        }
        @Override
        protected Integer doInBackground(String... param) {
            int newPort = 0;
            while(connecting) {
                DatagramSocket udpSocket = null;
                try {
                    buf_send = param[0].getBytes();         //первый (и единственный) входной параметр -- "matA" or "matB"
                    udpSocket = new DatagramSocket(port);
                    int ipAddress = getIP();
                    byte[] ip = {(byte) (ipAddress & 0xff),
                            (byte) (ipAddress >> 8 & 0xff),
                            (byte) (ipAddress >> 16 & 0xff),
                            (byte) (ipAddress >> 24 & 0xff)};
                    InetAddress serverAddr = InetAddress.getByAddress(ip);
                    DatagramPacket dp;                                                      //////////////////////////////////////
                    dp = new DatagramPacket(buf_send, 4, serverAddr, port);          //////////////////////////////////////
                    udpSocket.send(dp);
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                    udpSocket = new DatagramSocket(port);
                    udpSocket.setSoTimeout(500);
                    byte[] buf_recieve = new byte[4];
                    //buf_recieve[0] = 6;
                    dp = new DatagramPacket(buf_recieve, buf_recieve.length);
                    udpSocket.receive(dp);
                    if (dp.getLength() == 4) {                                    //если получено 4 байта (номер порта)
                        String s = new String(buf_recieve, 0, 4);   //;// = new String()
                        if(s.equals(param[0])) {
                            connecting = false;
                            address = dp.getAddress().toString().substring(1);
                        }
                    }
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                } catch (SocketException e) {
                    //Log.e("Udp:", "Socket Error:", e);
                } catch (IOException e) {                               //timeout
                    Log.e("Udp Send:", "IO Error:", e);
                    if ((count_conn++) == 10) {
                        //MainActivity.hConn.sendEmptyMessage(0);       //посылаем сигнал отсутствия подключения
                        connecting = false;
                        //break;
                    }else{
                        RegistrationActivity.hReg.sendEmptyMessage(2);
                    }
                } finally {
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                }
            }   //конец цикла определения ip адреса

            if(address != null){
                Socket tcpSocket = null;

                int len_in;
                byte[] buf_in = new byte[4];
                try {
                    tcpSocket = new Socket();
                    tcpSocket.setSoTimeout(10000);
                    tcpSocket.connect(new InetSocketAddress(InetAddress.getByName(address.toString()), 9000), 5000);
                    tcpSocket.getOutputStream().write((code + androidID).getBytes());
                    tcpSocket.getOutputStream().flush();

                    len_in= tcpSocket.getInputStream().read(buf_in);
                    //tcpSocket.getOutputStream().close();
                    //tcpSocket.getInputStream().close();
                    tcpSocket.close();
                    String s;
                    if(len_in == 4){
                        //s = new String(buf_in);
                        newPort = Integer.valueOf(new String(buf_in));
                        RegistrationActivity.hReg.sendEmptyMessage(1);
                    }else{
                        newPort = 0;
                    }
                    connResult = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    newPort = 0;
                }
                /*
                if(newPort == 0) {
                    try {
                        if (tcpSocket.isConnected()) {
                            tcpSocket.getOutputStream().write(("0").getBytes());
                        }
                        tcpSocket.close();
                    } catch (IOException e) {

                    }
                }
                */
            }
            return newPort;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //tvInfo.setText("End");
            if(address != null) {
                if(connResult) {
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "connection failed", Toast.LENGTH_LONG).show();
                }
            }
            btnA.setEnabled(true);
            btnB.setEnabled(true);
            if(result != 0) {
                MainActivity.hPort.sendEmptyMessage(result);
            }else{
                RegistrationActivity.hReg.sendEmptyMessage(0);
            }
        }
    }
}
