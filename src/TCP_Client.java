/*  Thikamporn Simud 5910401033
    Wipawadee Monkhut 5910406451
    SEC 1
 */
import java.io.*;
import java.net.*;

public class TCP_Client {
    public static void main(String[] args) throws Exception
    {
        Socket connectS = new Socket("localhost",9999);
        System.out.println("Send something to server.");
        DataOutputStream dOut = new DataOutputStream(connectS.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        DataInputStream dIn  =new DataInputStream(connectS.getInputStream());
        try {
            while (true) {
                String sendMessage = br.readLine();
                dOut.writeUTF(sendMessage);

                //read from server
                String message = dIn.readUTF();
                if (!message.equals("")) System.out.println("S: " + message);
                if (message.matches("101 DISCONNECTED" + " > " + connectS.getLocalAddress().toString() + " is logout")) {
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
    }

}
