import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher Started.");
        DatagramSocket datagramSocket = new DatagramSocket();

        // 构建一份会送数据
        String requestData = "Helloword";

        byte[] requestDataBytes = requestData.getBytes();

        DatagramPacket requestPack = new DatagramPacket(
                requestDataBytes,
                requestDataBytes.length
        );

        requestPack.setAddress(InetAddress.getLocalHost());
        requestPack.setPort(9002);

        datagramSocket.send(requestPack);

        final byte[] buf = new byte[512];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

        // 接收
        datagramSocket.receive(datagramPacket);

        // 发送者的ip地址
        String hostAddress = datagramPacket.getAddress().getHostAddress();
        int port = datagramPacket.getPort();
        int length = datagramPacket.getLength();
        String data = new String(datagramPacket.getData(), 0, length);

        System.out.println("UDPSearcher receive from ip:"+hostAddress+"\tport:"+port+"\tdata:"+data);




        System.out.println("UDPSearcher Finished");

        datagramSocket.close();
    }
}
