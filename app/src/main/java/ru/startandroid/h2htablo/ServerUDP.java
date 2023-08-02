package ru.startandroid.h2htablo;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static ru.startandroid.h2htablo.MainActivity.fSum;
import static ru.startandroid.h2htablo.MainActivity.getID;
//import static ru.startandroid.h2htablo.MainActivity.getP;
import static ru.startandroid.h2htablo.myContext.getIP;

public class ServerUDP implements Runnable {

    byte[] buf_send = new byte[50];// = null;
    static int port = 4001;

    static public int count_conn = 0;     //счетчик количества ошибок приема данных
    String androidID = getID();
    byte lenID = (byte) androidID.length();

    public static void setPort(int p){
        port = p;
    }
    /*
    public static int getPort(){
        return port;
    }
    */

    @Override
    public void run() {
        while(true) {
            if (port > 0) {
                DatagramSocket udpSocket = null;
                try {
                    udpSocket = new DatagramSocket(port);
                    int ipAddress = getIP();
                    byte[] ip = {(byte) (ipAddress & 0xff),
                            (byte) (ipAddress >> 8 & 0xff),
                            (byte) (ipAddress >> 16 & 0xff),
                            (byte) (ipAddress >> 24 & 0xff)};

                    InetAddress serverAddr = InetAddress.getByAddress(ip);
                    DatagramPacket dp;                                                      //////////////////////////////////////
                    if (MainActivity.enter) {                                               //формат передаваемых данных:       //
                        buf_send = ((char)(int)(fSum * 10) + androidID).getBytes();         //1 байт - 101, если не была нажата //
                    } else {                                                                //"ENTER", или значение оценки * 10,//
                        buf_send = ((char)101 + androidID).getBytes();                      //в другом случае, затем идут       //
                    }                                                                       //байты androidID                   //
                    dp = new DatagramPacket(buf_send, lenID + 1, serverAddr, port);  //////////////////////////////////////
                    udpSocket.send(dp);
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                    udpSocket = new DatagramSocket(port);
                    udpSocket.setSoTimeout(500);
                    byte[] buf_recieve = new byte[20];
                    buf_recieve[0] = 6;
                    dp = new DatagramPacket(buf_recieve, buf_recieve.length);
                    udpSocket.receive(dp);
                    if (buf_recieve[0] < 6 && dp.getLength() == 1) {                //
                        MainActivity.h.sendEmptyMessage(buf_recieve[0]);            //
                    }                                                               //
                    if(dp.getLength() == 4){                                        //
                        String s;// = new String()
                        s = new String(buf_recieve, 0, 4);
                        int i = new Integer(s);
                        port = i;
                        MainActivity.hPort.sendEmptyMessage(i);
                    }
                    udpSocket.close();
                    if (count_conn > 0) {
                        MainActivity.hConn.sendEmptyMessage(1);     //посылаем сигнал успешного подключения
                        count_conn = 0;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (SocketException e) {
                    Log.e("Udp:", "Socket Error:", e);
                } catch (IOException e) {
                    Log.e("Udp Send:", "IO Error:", e);
                    if (count_conn == 19) {
                        MainActivity.hConn.sendEmptyMessage(0);     //посылаем сигнал отсутствия подключения
                        port = 0;
                    } else {
                        if (count_conn < 20) {
                            count_conn++;
                        }
                    }
                } finally {
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                }
            }
        }
    }
}