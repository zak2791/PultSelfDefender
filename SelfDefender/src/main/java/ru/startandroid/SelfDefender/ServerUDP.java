package ru.startandroid.SelfDefender;

//import android.util.log;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

//import static ru.startandroid.h2htablo15questions.MainActivity.fSum;
import static ru.startandroid.SelfDefender.MainActivity.btnMask;
import static ru.startandroid.SelfDefender.MainActivity.getID;
import static ru.startandroid.SelfDefender.myContext.getIP;

//import static ru.startandroid.h2htablo.MainActivity.getP;

public class ServerUDP implements Runnable {

    byte[] buf_send = new byte[32];// = null;
    byte[] buf_id = new byte[32];
    static int port = 4001;

    static public int count_conn = 0;     //счетчик количества ошибок приема данных
    String androidID = getID();
    byte lenID = (byte) androidID.length();

    public static void setPort(int p){
        port = p;
    }
    public static int getPort(){
        return port;
    }

    private boolean flag_conn = false;              //флаг установленного соединения

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

                    DatagramPacket dp;                                                                      //////////////////////////////////////
                    buf_id = androidID.getBytes();
                    if (MainActivity.enter) {                                                               //формат передаваемых данных:       //
                        //buf_send = ((char)(int)(MainActivity.btnMask * 10) + androidID).getBytes();       //1 байт - 101, если не была нажата //
                        buf_send[0] = (byte) btnMask;
                        buf_send[1] = (byte)(btnMask >> 8);
                        buf_send[2] = (byte)(btnMask >> 16);
                        for(int count = 0; count < lenID; count++){
                            buf_send[count + 3] = buf_id[count];
                        }
                    } else {                                                                                //"ENTER", или значение оценки * 10,//
                        //buf_send = ((char)101 + androidID).getBytes();                                    //в другом случае, затем идут       //
                        buf_send[0] = (byte) 0xff;
                        buf_send[1] = (byte) 0;
                        buf_send[2] = (byte) 0;
                        for(int count = 0; count < lenID; count++){
                            buf_send[count + 3] = buf_id[count];
                        }
                    }                                                                                       //байты androidID                   //
                    dp = new DatagramPacket(buf_send, lenID + 3, serverAddr, port);  //////////////////////////////////////
                    udpSocket.send(dp);
                    if (udpSocket != null) {
                        udpSocket.close();
                    }
                    udpSocket = new DatagramSocket(port);
                    udpSocket.setSoTimeout(1000);
                    byte[] buf_recieve = new byte[20];
                    buf_recieve[0] = 6;
                    dp = new DatagramPacket(buf_recieve, buf_recieve.length);
                    udpSocket.receive(dp);
                    if (buf_recieve[0] < 6 && dp.getLength() == 1) {                //
                        MainActivity.h.sendEmptyMessage(buf_recieve[0]);            //отсылаем сигнал с полученной задачей
                    }                                                               //
//                    if(dp.getLength() == 4){                                        //
//                        String s;// = new String()
//                        s = new String(buf_recieve, 0, 4);
//                        int i = new Integer(s);
//                        port = i;
//                        MainActivity.hPort.sendEmptyMessage(i);
//                    }
                    udpSocket.close();
                    if (flag_conn == false) {
                        MainActivity.hConn.sendEmptyMessage(1);     //посылаем сигнал успешного подключения
                        flag_conn = true;
                        Log.e("conn:", "conn start");
                    }
                    count_conn = 0;
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (SocketException e) {
                    Log.e("Udp:", "Socket Error:", e);
                } catch (IOException e) {
                    Log.e("Udp Send:", "IO Error:", e);
                    if (count_conn == 4) {
                        MainActivity.hConn.sendEmptyMessage(0);     //посылаем сигнал отсутствия подключения
                        Log.e("conn:", "conn stop");
                        port = 0;
                        flag_conn = false;
                    } else {
                        if (count_conn < 5) {
                            count_conn++;
                            Log.e("conn:", "conn count");
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
