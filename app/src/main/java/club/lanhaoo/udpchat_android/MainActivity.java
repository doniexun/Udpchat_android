package club.lanhaoo.udpchat_android;

import android.app.Activity;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {


    boolean firstrun=true;
    String server_ip;
    String username="游客";

    String end_command="UserCommand.Droplink";//config 结束聊天指令及提示
    String end_tips="下线";
    String set_Username="UserCommand.setMyName";
    //conig 用户设置自己的名字,setMyName#test
    boolean setname=false;
    String  secret_Talk="secretTalk";
    //secretTalk format UserCommand.secretTalk#ip#chatContent;
    String raw_Data;
    boolean inCommunication;
    Activity myActivity;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        //https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView Chat_area=(TextView)findViewById(R.id.textView2);
        Chat_area.setHeight(height-70);
        EditText editText22=(EditText)findViewById(R.id.editText);
        editText22.setWidth(width-50);
        //适应屏幕大小
        Chat_area.setMovementMethod(new ScrollingMovementMethod());
        Chat_area.setText("开始客户端，将使用端口2113\n");
        
        try {

            Chat_area.append("输入服务器地址\n");


        }catch (Exception e){
            Chat_area.append("开启端口失败\n");
        }
        Button serverbutton=(Button)findViewById(R.id.button2);
        serverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server server=new server();
                server.StartServer();
                Chat_area.append("开始服务器\n");
                Chat_area.append("将在"+ club.lanhaoo.udpchat_android.server.getIpAddress()+"上广播\n");
                Chat_area.append("将使用2112作为服务器端口\n");
            }
        });



        Button sendButton=(Button)findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(firstrun){
                    EditText editText_area=(EditText)findViewById(R.id.editText);
                    server_ip=editText_area.getText().toString();
                    editText_area.setText("");
                    Chat_area.append("设置服务器ip："+server_ip+"成功\n");

                    inCommunication=true;
                    Message("");


                }else {
                    EditText editText=(EditText)findViewById(R.id.editText);
                    sendMessage sendMessage=new sendMessage();
                    sendMessage.SendMessage(editText.getText().toString(),server_ip);
                    editText.setText("");
                }
            }




            private void Message(final String Message) {
                final Handler handler = new Handler();
                Thread thread = new Thread(new Runnable() {
                   @Override
                    public void run() {
                       if(firstrun) {
                           firstrun=false;
                           Chat_area.append("开始接收来自服务器数据\n");
                           DatagramSocket datagramSocket = null;
                           try {
                               String username;

                               datagramSocket = new DatagramSocket(12251);
                               byte[] bytes_from_server = new byte[1024];
                               DatagramPacket datagramPacket = new DatagramPacket(bytes_from_server, bytes_from_server.length);

                               datagramSocket.setBroadcast(true);
                               while (true) {
                                   datagramSocket.receive(datagramPacket);


                                   //todo 接受到的消息都是来自服务端的。。。。
                                   String message_pure = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                                   String fromip = message_pure.split("&")[1];
                                   message_pure = message_pure.split("&")[0];

                                   if (message_pure.startsWith("[with_name]")) {
                                       if (message_pure.contains("@")) {
                                           message_pure = message_pure.replace("[with_name]", "");
                                           username = message_pure.split("@")[0];
                                           Chat_area.append("来自 " + username + "\n");
                                           Chat_area.append(message_pure.split("@")[1] + "\n");
                                           continue;
                                       }
                                   }

                                   Chat_area.append("来自 " + fromip + "\n");
                                   Chat_area.append(message_pure + "\n");
                                   final int scrollAmount = Chat_area.getLayout().getLineTop(Chat_area.getLineCount()) - Chat_area.getHeight();
                                   // if there is no need to scroll, scrollAmount will be <=0
                                   if (scrollAmount > 0)
                                       Chat_area.scrollTo(0, scrollAmount);
                                   else
                                       Chat_area.scrollTo(0, 0);
                                   //自动滚到下面https://stackoverflow.com/questions/3506696/auto-scrolling-textview-in-android-to-bring-text-into-view
                               }


                           } catch (Exception e) {
                               Chat_area.append(e.toString());
                               Chat_area.append("接收数据失败\n");
                           } finally {
                               if (datagramSocket != null) {
                                   datagramSocket.close();
                               }
                           }
                       }



                       }

                });
                thread.start();
                //参考https://stackoverflow.com/questions/19540715/send-and-receive-data-on-udp-socket-java-android
            }
        });







    }

}
