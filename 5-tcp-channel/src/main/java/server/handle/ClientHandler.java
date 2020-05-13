package server.handle;

import clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 客户端消息处理
 */
public class ClientHandler extends Thread {
    private final Socket socket;
    private final ClientReadHandler readHandler;
    private final ClientWriteHandler writeHandler;
    private final CloseNotify closeNotify;

    public ClientHandler(Socket socket, CloseNotify closeNotify) throws IOException {
        this.socket = socket;
        this.closeNotify = closeNotify;
        this.readHandler = new ClientReadHandler(socket.getInputStream());
        this.writeHandler = new ClientWriteHandler(socket.getOutputStream());
        System.out.println("新客户端连接：" + socket.getInetAddress() +
                " P:" + socket.getPort());
    }


    public void exit(){
        readHandler.exit();
        writeHandler.exit();
        CloseUtils.close(socket);
        System.out.println("客户端已退出：" + socket.getInetAddress() +
                " P:" + socket.getPort());
    }

    public void send(String str) {
        writeHandler.send(str);
    }

    public void readToPrint() {
        readHandler.start();
    }

    class ClientReadHandler extends Thread{

        private boolean done = false;
        private final InputStream inputStream;

        public ClientReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            super.run();



            try {
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));



                do {
                    // 客户端拿到一条数据f
                    String str = socketInput.readLine();

                    if(str == null){
                        System.out.println("客户端已无法读取数据");
                        ClientHandler.this.exitBySelf();
                        break;
                    }

                    System.out.println(str);

                } while (!done);


            } catch (Exception e) {
                if(!done){
                    System.out.println("连接异常断开");
                    ClientHandler.this.exitBySelf();
                }
            } finally {
                // 连接关闭
                CloseUtils.close(inputStream);
            }
        }

        void exit(){
            done = true;
            CloseUtils.close(inputStream);
        }
    }

    class ClientWriteHandler {
        private boolean done = false;
        private final PrintStream printStream;
        private final ExecutorService executorService;

        public ClientWriteHandler(OutputStream outputStream) {
            this.printStream = new PrintStream(outputStream);
            this.executorService = Executors.newSingleThreadExecutor();
        }

        public void send(String str) {
            executorService.execute(()->{
                if(ClientWriteHandler.this.done){
                    return;
                }
                try {
                    ClientWriteHandler.this.printStream.println(str);
                }catch (Exception e){
                    e.printStackTrace();
                }

            });
        }


        void exit(){
            done = true;
            CloseUtils.close(printStream);
            executorService.shutdownNow();
        }
    }



    public interface CloseNotify{
        void onSelfClosed(ClientHandler handler);
    }

    private  void exitBySelf() {
        exit();
        closeNotify.onSelfClosed(this);
    }
}
