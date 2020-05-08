
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

public class UDPProvider {

    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider Started.");
        String sn = UUID.randomUUID().toString();

        Provider provider = new Provider(sn);
        provider.start();

        System.in.read();
        provider.exit();
    }

    private static class Provider extends Thread {
        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            try {
                ds = new DatagramSocket(9002);

                while (!done) {

                    final byte[] buf = new byte[512];
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    // 接收
                    ds.receive(datagramPacket);

                    // 发送者的ip地址
                    String hostAddress = datagramPacket.getAddress().getHostAddress();
                    int port = datagramPacket.getPort();
                    int length = datagramPacket.getLength();
                    String data = new String(datagramPacket.getData(), 0, length);

                    System.out.println("UDPProvider receive from ip:" + hostAddress + "\tport:" + port + "\tdata:" + data);

                    int responsePort = MessageCreator.parsePort(data);

                    if(responsePort!=-1){
                        // 构建一份会送数据
                        String responseData = MessageCreator.buildWithSn(sn);

                        byte[] responseDataBytes = responseData.getBytes();

                        DatagramPacket responsePack = new DatagramPacket(
                                responseDataBytes,
                                responseDataBytes.length,
                                datagramPacket.getAddress(),
                                responsePort
                        );

                        ds.send(responsePack);
                    }



                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }

            System.out.println("UDPProvider Finished");

        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        void exit() {
            done = true;
            close();
        }
    }
}
