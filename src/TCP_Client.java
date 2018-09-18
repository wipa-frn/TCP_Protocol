import java.io.*;
import java.net.*;
public class TCP_Client {
    public static void main(String[] args) throws Exception
    {

        Socket connectS = new Socket("localhost",9999);
        System.out.println("Send something to server.");
        DataOutputStream dout=new DataOutputStream(connectS.getOutputStream());
        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
        DataInputStream dIn=new DataInputStream(connectS.getInputStream());

        while(true) {
            String sendMassage = br.readLine();
            dout.writeUTF(sendMassage);
            if(sendMassage.equals("GOODBYE")) {
                String massage = dIn.readUTF();
                System.out.println(massage);
                break;

            }
            //read from server
            String massage = dIn.readUTF();
            if (!massage.equals(""))
                System.out.println(massage);

        }
        connectS.close();}

}
