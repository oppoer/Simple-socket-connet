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
    Button socket_btn;
    private static final String Host ="10.10.10.254";
    private static final int Port =6602;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket_edit =(EditText)findViewById(R.id.socket_edit);
        socket_btn =(Button)findViewById(R.id.socket_btn);
        socket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SocketThread socketThread = new SocketThread(Host,Port);
                new Thread(socketThread).start();

            }
        });

    }

    public class SocketThread implements Runnable{

        Socket socket =null;
        private String host;
        private int port;
        private String str ="FFFE0104011200140001";

        public SocketThread(String host,int port){
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {

            Message msg =MainActivity.this.myhandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.clear();

            try{
                socket = new Socket();
                socket.connect(new InetSocketAddress(host,port),5000);


            }catch (SocketTimeoutException aa){

                msg.what=2;
                bundle.putString("msg","put check network open");
                msg.setData(bundle);
                msg.sendToTarget();

            } catch (IOException localIOException){

                localIOException.printStackTrace();

            }


                if (!socket.isClosed()) {
                    if (socket.isConnected()) {
                        msg.what = 1;
                        bundle.putString("msg","conncet is ok");
                        msg.setData(bundle);
                        msg.sendToTarget();
                        try{
                            OutputStream out = socket.getOutputStream();
//                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//                                    socket.getOutputStream())), true);
                            BufferedReader bff = new BufferedReader(new InputStreamReader(
                                    socket.getInputStream()));
                            out.write(str.getBytes());
                            out.flush();

                        }catch (IOException localIOEception){

                            localIOEception.printStackTrace();

                        }

                    }
                }



        }
    }
}
