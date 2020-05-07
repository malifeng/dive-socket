
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPProvider {

    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider Started.");
        DatagramSocket datagramSocket = new DatagramSocket(9002);

        final byte[] buf = new byte[512];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

        // 接收
        datagramSocket.receive(datagramPacket);

        // 发送者的ip地址
        String hostAddress = datagramPacket.getAddress().getHostAddress();
        int port = datagramPacket.getPort();
        int length = datagramPacket.getLength();
        String data = new String(datagramPacket.getData(), 0, length);

        System.out.println("UDPProvider receive from ip:"+hostAddress+"\tport:"+port+"\tdata:"+data);


        // 构建一份会送数据
        String responseData = "Receive data with len:"+length;

        byte[] responseDataBytes = responseData.getBytes();

        DatagramPacket responsePack = new DatagramPacket(
                responseDataBytes,
                responseDataBytes.length,
                datagramPacket.getAddress(),
                datagramPacket.getPort()
        );

        datagramSocket.send(responsePack);

        System.out.println("UDPProvider Finished");

        datagramSocket.close();





    }
}
