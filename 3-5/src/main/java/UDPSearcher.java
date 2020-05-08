import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class UDPSearcher {
    private static final int LISTEN_PORT = 9003;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher Started.");

        Listener listener = listen();
        sendBroadcast();

        System.in.read();

        List<Device> devices = listener.getDevicesAndClose();


        for (Device device :
                devices) {
            System.out.println("Device:"+device.toString());
        }

    }


    private static Listener listen() throws InterruptedException {
        System.out.println("UDPSearcher Started  listen");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();
        countDownLatch.await();

        return listener;
    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast Started.");
        DatagramSocket datagramSocket = new DatagramSocket();

        // 构建一份会送数据
        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);

        byte[] requestDataBytes = requestData.getBytes();

        DatagramPacket requestPack = new DatagramPacket(
                requestDataBytes,
                requestDataBytes.length
        );

        requestPack.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPack.setPort(9002);

        datagramSocket.send(requestPack);

        datagramSocket.close();

        System.out.println("UDPSearcher Finished");
    }

    private static class Device {
        final int port;
        final String ip;
        final String sn;

        public Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }

    private static class Listener extends Thread {

        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds = null;

        public Listener(int listenPort, CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();

            countDownLatch.countDown();
            try {

                ds = new DatagramSocket(listenPort);

                while (!done) {

                    // 构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

                    // 接收
                    ds.receive(datagramPacket);

                    // 发送者的ip地址
                    String hostAddress = datagramPacket.getAddress().getHostAddress();
                    int port = datagramPacket.getPort();
                    int length = datagramPacket.getLength();
                    String data = new String(datagramPacket.getData(), 0, length);

                    System.out.println("UDPSearcher receive from ip:" + hostAddress + "\tport:" + port + "\tdata:" + data);


                    String sn = MessageCreator.parseSn(data);
                    if (sn != null) {
                        Device device = new Device(port, hostAddress, sn);
                        devices.add(device);
                    }

                }


            } catch (Exception e) {

            } finally {
                System.out.println("UDPSearcher finished");
            }
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }
}
