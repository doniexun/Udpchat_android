package club.lanhaoo.udpchat_android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class server {

    public static void StartServer(){
        Thread thread = new Thread(new Runnable() {
            ArrayList Iplist = new ArrayList();
            ArrayList BanIp = new ArrayList();

            @Override
            public void run() {

                DatagramSocket datagramSocket=null;
                try {

                    InetAddress localHost = InetAddress.getLocalHost();

                    //自动生成广播地址
                    String[] temp_arr;

                    temp_arr = getIpAddress().split("\\.");
                    temp_arr[3] = "255";
                    String broadcast_ip = temp_arr[0] + "." + temp_arr[1] + "." + temp_arr[2] + "." + temp_arr[3];

                    System.out.println("广播地址: " + broadcast_ip);

                    byte[] bytes = new byte[1024];
                    datagramSocket = new DatagramSocket(2112);
                    DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                    System.out.println("在 " + getIpAddress() + " : 2112" + "上运行服务端 ");
                    boolean ServerOn = true;
                    String username;


                    while (ServerOn) {

                        datagramSocket.receive(datagramPacket);
                        //收到消息

                        String received_message_merge = new String(datagramPacket.getData(), 0, datagramPacket.getLength()) + " 来自 " + datagramPacket.getAddress().getHostAddress();
                        String pure_message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                        String fromIP = datagramPacket.getAddress().getHostAddress();


                        Iplist.add(fromIP);
                        //todo 超级命令登陆ip

                        if (BanIp.contains(fromIP)) {
                            //如果来自被封禁IP，则不进行操作
                            String bannedtips = "你已被管理员封禁，无法发送群消息&[系统消息]";
                            byte[] bytes1;
                            bytes1 = bannedtips.getBytes();
                            datagramSocket.send(new DatagramPacket(bytes1, bytes1.length, InetAddress.getByName(fromIP), 12251));
                            System.out.println("来自封禁Ip:" + fromIP + "内容:" + pure_message);
                            continue;
                        } else {

                            //config
                            String SuperendendCommand = "SYSTEM_COMMAND.ENDSERVER";
                            String SuperbanipCommand = "BANIP";
                            String SuperunbanipCommand = "UNBAN";
                            String ServerOfftips = localHost + "服务器下线&[系统消息]";

                            System.out.println(received_message_merge);
                            //System.out.println(pure_message);

                            String UserCommand_Nonamesend = "NoNameSend";
                            //config


                            // 广播前检测
                            if (pure_message.contains("UserCommand")) {
                                if (pure_message.contains(UserCommand_Nonamesend)) {
                                    String string = "[匿名消息]" + pure_message.split("#")[1] + "&匿名用户";
                                    datagramSocket.send(new DatagramPacket(string.getBytes(), string.getBytes().length, InetAddress.getByName(broadcast_ip), 12251));
                                }
                                continue;
                            }


                            if (pure_message.contains("SYSTEM_COMMAND")) {
                                try {
                                    if (pure_message.split("#")[1].equals("mima111")) {

                                        if (pure_message.contains(SuperbanipCommand)) {
                                            //SYSTEM_COMMAND.BANIP#mima111#127.0.0.1

                                            BanIp.add(pure_message.split("#")[2]);
                                            System.out.println("Banned ip:" + pure_message.split("#")[2]);

                                            String temp = "[" + fromIP + "已被管理员封禁]&[系统消息]";
                                            datagramSocket.send(new DatagramPacket(temp.getBytes(), temp.getBytes().length, InetAddress.getByName(broadcast_ip), 12251));
                                            continue;

                                        }
                                        if (pure_message.contains(SuperunbanipCommand)) {
                                            //SYSTEM_COMMAND.BANIP#mima111#127.0.0.1

                                            BanIp.remove(pure_message.split("#")[2]);
                                            System.out.println("unban ip:" + pure_message.split("#")[2]);

                                            String temp = "[" + fromIP + "解除封禁]&[系统消息]";
                                            datagramSocket.send(new DatagramPacket(temp.getBytes(), temp.getBytes().length, InetAddress.getByName(broadcast_ip), 12251));
                                            continue;

                                        }
                                        if (pure_message.contains(SuperendendCommand)) {
                                            //todo pure_message 判断来源用户

                                            System.out.println(ServerOfftips);

                                            byte[] temp_byte = ServerOfftips.getBytes();

                                            datagramSocket.send(new DatagramPacket(temp_byte, temp_byte.length, InetAddress.getByName(broadcast_ip), 12251));
                                            break;
                                        }

                                    }

                                } catch (Exception e) {
                                    continue;
                                }
                            }
                            //广播消息
                            System.out.println("广播来自 " + datagramPacket.getAddress().getHostAddress() + " 的消息 " + pure_message);
                            byte[] bytes1;
                            pure_message = pure_message + "&" + fromIP;
                            bytes1 = pure_message.getBytes("UTF-8");
                            datagramSocket.send(new DatagramPacket(bytes1, bytes1.length, InetAddress.getByName(broadcast_ip), 12251));


                        }

                    }
                }catch (Exception e){
                    Log.d("Server",e.toString());
                }finally {
                    if(datagramSocket!=null){
                        datagramSocket.close();
                    }
                }


            }
        });
        thread.start();
    }

    //这段代码来自https://stackoverflow.com/questions/17252018/getting-my-lan-ip-address-192-168-xxxx-ipv4，强转了两句的变量类型，适用于这里
    public static String getIpAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address) {
                        String ipAddress=inetAddress.getHostAddress().toString();
                        Log.e("IP address",""+ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.print("get wrong LAN ip");
        }
        return null;
    }
}
