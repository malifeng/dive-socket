import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        // 超时时间
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),9001),3000);


        System.out.println("已发起服务连接，并进入后续流程：");
        System.out.println("客户端信息："+socket.getLocalAddress()+"P:"+socket.getLocalPort());
        System.out.println("服务端信息："+socket.getInetAddress()+"P:"+socket.getPort());

        try {
            todo(socket);
        }catch (IOException e){
            System.out.println("异常关闭");
        }


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
