import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9001);

        System.out.println("服务器准备就绪：");
        System.out.println("服务端信息："+serverSocket.getInetAddress()+"P:"+serverSocket.getLocalPort());




        for (;;){
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client);
            clientHandler.start();
        }



    }

    private static class ClientHandler extends Thread{
        private Socket socket;
        private boolean flag = true;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端信息："+socket.getInetAddress()+"P:"+socket.getPort());
            try {
                // 得到打印流，用于数据输出；服务器会送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                // 输入流
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                do{
                    String str = socketInput.readLine();
                    if("bye".equalsIgnoreCase(str)){
                        flag = false;
                        socketOutput.println("bye");
                    }else{
                        System.out.println(str);
                        socketOutput.println("回送" + str.length());
                    }

                }while (flag);

                socketInput.close();
                socketOutput.close();

            }catch (Exception e){
                System.out.println("链接异常断开");
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("客户端已关闭"+socket.getInetAddress()+"P:"+socket.getPort());
        }
    }
}
