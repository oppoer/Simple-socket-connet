package com.example.wxk.socketdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {

    EditText socket_edit;
    EditText ssid_edit;
    Button ssid_btn;
    Button socket_btn;
    OutputStream out =null;
    private static final String Host ="10.10.10.254";
    private static final int Port =6602;
    Socket socket =null;
    Message msg =null;
    Bundle bundle;

    public Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle localbundle = msg.getData();

            switch (msg.what) {
                case 1:
                    socket_edit.setText(localbundle.getString("msg")+"\n");
                    break;
                case 2:
                    socket_edit.setText(localbundle.getString("msg")+"\n");
                default:
                    break;
            }
        }
    };

    public static String getCheckNumber(String paramString){
        int i =paramString.substring(0, 1).getBytes()[0];
        for(int j=1;;j++){
            if(j>=paramString.length())
                return intToHex(Integer.valueOf(i));
            i=(byte)(i^paramString.substring(j, j+1).getBytes()[0]);

        }
    }
    public static String intToHex(Integer paramInteger){
        return Integer.toHexString((paramInteger.intValue()&0x000000FF)|0xFFFFFF00).substring(6);

    }

    public static String CombineCommand(String paramString1,String paramString2,String paramString3,String paramString4,String paramString5,String paramString6){
        String str1 = intToHex(Integer.valueOf((4+(2+(paramString1.length()+paramString2.length()+paramString3.length()+paramString4.length()))+paramString5.length())));
        String str2 = "00"+intToHex(Integer.valueOf(Integer.valueOf(2 + (4 + (2 + (paramString1.length() + paramString2.length() + paramString3.length() + paramString4.length())) + paramString5.length() + paramString6.length()))));
        String str3 = getCheckNumber(paramString1.toUpperCase()+paramString2+paramString3+paramString4+str1.toUpperCase()+str2.toUpperCase()+paramString5+paramString6);
        if(str3.length()<1);
        while(true){
            return paramString1.toUpperCase()+paramString2+paramString3+paramString4+str1.toUpperCase()+str2.toUpperCase()+paramString5+paramString6+str3.toUpperCase();
        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket_edit =(EditText)findViewById(R.id.socket_edit);
        socket_btn =(Button)findViewById(R.id.socket_btn);
        ssid_edit = (EditText)findViewById(R.id.ssid_edit);
        ssid_btn = (Button)findViewById(R.id.socket_btn);
        msg = new Message();
        bundle =new Bundle();
        socket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    SocketThread socketThread = new SocketThread(Host, Port);
                    new Thread(socketThread).start();


            }
        });



    }

    public class SocketThread implements Runnable{


        private String host;
        private int port;

        public SocketThread(String host,int port){
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {

            MainActivity.this.msg =MainActivity.this.myhandler.obtainMessage();
            MainActivity.this.bundle.clear();

            try{
                MainActivity.this.socket = new Socket();
                MainActivity.this.socket.connect(new InetSocketAddress(host, port), 5000);


            }catch (SocketTimeoutException aa){

                MainActivity.this.msg.what=2;
                MainActivity.this.bundle.putString("msg","put check network open");
                MainActivity.this.msg.setData(bundle);
                MainActivity.this.msg.sendToTarget();

            } catch (IOException localIOException){

                localIOException.printStackTrace();

            }


                if (!MainActivity.this.socket.isClosed()) {
                    if (MainActivity.this.socket.isConnected()) {
                        MainActivity.this.msg.what = 1;
                        MainActivity.this.bundle.putString("msg","conncet is ok");
                        MainActivity.this.msg.setData(bundle);
                        MainActivity.this.msg.sendToTarget();
                        try{
                            MainActivity.this.out = socket.getOutputStream();
//                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//                                    socket.getOutputStream())), true);
                            BufferedReader bff = new BufferedReader(new InputStreamReader(
                                    socket.getInputStream()));
                            String str = CombineCommand("FFFE", "01", "04", "11", "00", "{\"SSID\":\""+Integer.toString(20)+"\"}");
                            MainActivity.this.out.write(str.getBytes());
                            MainActivity.this.out.flush();


                        }catch (IOException localIOEception){

                            localIOEception.printStackTrace();

                        }

                    }
                }



        }
    }
}
