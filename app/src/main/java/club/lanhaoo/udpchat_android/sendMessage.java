package club.lanhaoo.udpchat_android;

import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class sendMessage {
    public  static void SendMessage(final String string, final String server_ip){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                DatagramSocket datagramSocket =null;
                try {

                    datagramSocket = new DatagramSocket(2113);


                        String raw_Data = string;

                        byte[] bytes = raw_Data.getBytes("UTF-8");
                        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(server_ip), 2112);

                        datagramSocket.send(datagramPacket);

                    } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (SocketException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }finally {
                    if (datagramSocket!=null){
                        datagramSocket.close();
                    }
                }
            }

    });
        thread.start();
    }
    }