import java.io.*;
import java.net.*;

public class Client  {
    private static final int PORT = 9001;
    private static final int LOCAL_PORT = 20000;
    private static final int TIME_OUT = 3000;

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        initSocket(socket);


        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),PORT),TIME_OUT);

        System.out.println("已发起服务连接，并进入后续流程：");
        System.out.println("客户端信息："+socket.getLocalAddress()+"P:"+socket.getLocalPort());
        System.out.println("服务端信息："+socket.getInetAddress()+"P:"+socket.getPort());

        try {
            todo(socket);
        }catch (IOException e){
            System.out.println("异常关闭");
        }


    }

    private static Socket createSocket() throws IOException {
//        Socket socket = new Socket(Proxy.NO_PROXY);
//
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Inet4Address.getByName("www.baidu.com"),8800));
//
//        socket = new Socket(proxy);
//
//        socket = new Socket("localhost",PORT);
//        socket = new Socket(Inet4Address.getLocalHost(),PORT);
//
//        socket = new Socket("localhost",PORT,Inet4Address.getLocalHost(),LOCAL_PORT);
//        socket = new Socket(Inet4Address.getLocalHost(),PORT,Inet4Address.getLocalHost(),LOCAL_PORT);

        Socket socket = new Socket();

        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(),LOCAL_PORT));

        return socket;

    }

    static void initSocket(Socket socket) throws SocketException {
        socket.setSoTimeout(3000);
        socket.setReuseAddress(true);
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        socket.setSoLinger(true,20);

        // 是否让进技术局内敛，默认false；
        socket.setOOBInline(true);
        // 拆分包大小设置
        socket.setReceiveBufferSize(64*1024*1024);
        socket.setSendBufferSize(64*1024*1024);
        // 设置性能参数： 短连接，延迟，带宽的相对重要性
        socket.setPerformancePreferences(1,1,1);
    }

    private static void todo(Socket client) throws IOException {
        InputStream in = System.in;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));


        OutputStream outputStream = client.getOutputStream();

        PrintStream printStream = new PrintStream(outputStream);

        // 得到输入流
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferReader = new BufferedReader(new InputStreamReader(inputStream));


        boolean flag = true;


        do {
            String line = bufferedReader.readLine();

            printStream.println(line);

            String serverLine = socketBufferReader.readLine();

            if("bye".equalsIgnoreCase(serverLine)){
                flag = false;
            }else {
                System.out.println(serverLine);
            }
        }while (flag);

        // 资源释放
        printStream.close();
        socketBufferReader.close();

    }
}
