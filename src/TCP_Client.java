import java.io.*;
import java.net.*;
public class TCP_Client {
    public static void main(String[] args) throws Exception
    {
        Socket connectS = new Socket("localhost",9999);
        System.out.println("Send something to server.");
        DataOutputStream dOut = new DataOutputStream(connectS.getOutputStream());
        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
        DataInputStream dIn=new DataInputStream(connectS.getInputStream());
        try {
            while (true) {
                String sendMassage = br.readLine();
                dOut.writeUTF(sendMassage);

                //read from server
                String message = dIn.readUTF();
                if (!message.equals("")) System.out.println("Server: " + message);
                else if (message.matches(connectS.getLocalAddress().toString())) {
                    dOut.writeUTF("EXIT");
                    dOut.flush();
                    break;
                }
            }
        }
        catch (EOFException e) {
            e.printStackTrace();
        }
        finally {
            connectS.close();
        }

        /*while(true) {
            *//*String sendMassage = br.readLine();
            dOut.writeUTF(sendMassage);

            //read from server
            String massage = dIn.readUTF();
            if (!massage.equals("")) System.out.println("Server: " + massage);
            else if (massage.matches( connectS.getLocalAddress().toString())) {
                dOut.writeUTF("EXIT");
                dOut.flush();
                break;
            }*//*

            *//*if(sendMassage.equals("GOODBYE")) {
                massage = dIn.readUTF();
                System.out.println(massage);

            }
            else if (sendMassage.equals("EXIT")){
                dOut.flush();
                break;
            }*//*


        }*/

    }

}
