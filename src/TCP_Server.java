import java.io.*;
import java.net.*;
public class TCP_Server {

    private static User user1 = new User("FERN","123456",new DebitCard("1234567890123456","121"));

    public static void main(String[] args) throws Exception {


        ServerSocket serverSocket=new ServerSocket(9999);

        System.out.println("server online..");
        Socket client = serverSocket.accept();
        String addr = client.getLocalAddress()+" is connected";
        System.out.println(addr);


        //connection with client
        DataInputStream dIn=new DataInputStream(client.getInputStream());
        DataOutputStream dout=new DataOutputStream(client.getOutputStream());
        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));

        String sendM;
        int statusLogin = 0;
        String phoneNum = null;
        String sim = null;
        String money = null;

        while(true) {

            String massage = dIn.readUTF();
            System.out.println("client: "+massage);

            if(statusLogin == 0) { //non login.

                if (massage.equals("HI")) {
                    sendM = "HELLO Please login to Top-Up easy.";
                    dout.writeUTF(sendM);

                } else if (massage.matches("LOG:USER=(.*)")) {
                    String str = massage;
                    str = str.replaceFirst("LOG:USER=", "");

                    if (str.equals(user1.getUsername()))
                        sendM = "Please enter your password";
                    else
                        sendM = "User name is not correct";

                    dout.writeUTF(sendM);

                } else if (massage.matches("LOG:PWD=(.*)")) {
                    String str = massage.replaceFirst("LOG:PWD=", "");
                    System.out.println(str);

                    if (str.equals(user1.getPassword()))
                        sendM = "Login success! Welcome " + user1.getUsername();
                    else
                        sendM = "Password is not correct";

                    dout.writeUTF(sendM);
                    statusLogin = 1;

                }
                else{
                    dout.writeUTF("");
                }
            }
            else if(statusLogin == 1){ //login success

                if ( massage.matches("TUP=(.*)")) {
                    String str = massage.replaceFirst("TUP=", "");

                    if (str.equals("AIS")||str.equals("DTAC")||str.equals("TRUE")){
                        sendM = "You choose "+ str + ". Are you sure? [Y/N]";
                        dout.writeUTF(sendM);

                        String msg = dIn.readUTF();
                        System.out.println("client: "+msg);

                        if(msg.equals("Y")){
                            sendM = "PLease enter your phone number";
                            dout.writeUTF(sendM);
                            sim = str;
                        }else {
                            sim = null;
                            dout.writeUTF("");
                        }
                    }
                    else {
                        sendM = "Sim is not correct";
                        dout.writeUTF(sendM);
                    }
                }
                else if ((!sim.isEmpty()) && massage.matches("NUM=(.*)")){

                    String str = massage.replaceFirst("NUM=", "");

                    if(str.matches("([0-9]*)") && (str.length() == 10) ){
                        sendM = "Phone number is "+str+". Are you sure? [Y/N]";
                        dout.writeUTF(sendM);

                        String msg = dIn.readUTF();
                        System.out.println("client: "+msg);

                        if(msg.equals("Y")){
                            phoneNum = str ;
                            sendM = "Please enter money";
                            dout.writeUTF(sendM);

                        }else if (msg.equals("N")) {
                            phoneNum = null;
                            dout.writeUTF("");
                        }
                    }
                    else {
                        sendM = "Phone number is not correct.";
                        dout.writeUTF(sendM);
                    }
                }

                else if(!phoneNum.isEmpty() && massage.matches("MONEY=(.*)")){
                    String str = massage.replaceFirst("MONEY=", "");

                    if(str.equals("100") || str.equals("200") || str.equals("500") || str.equals("1000")){
                        sendM = "PLease enter your debit card number (ending with _ ) and then followed by CVV number";
                        dout.writeUTF(sendM);
                        money = str;
                    }else {
                        sendM = "Money out of rank.";
                        dout.writeUTF(sendM);
                    }

                }else if(!money.isEmpty() && massage.matches("BNK=(.*)")){
                    String str = massage.replaceFirst("BNK=", "");

                    if(str.equals(user1.getDebitCard().getNumberDebit()+"_"+user1.getDebitCard().getNumberCVV())){
                        sendM = "Are you confirm TOP-UP? [Y/N]";
                        dout.writeUTF(sendM);

                        String msg = dIn.readUTF();
                        System.out.println("client: "+msg);

                        if(msg.equals("Y")){
                            sendM = "TOP-UP is success GOODBYE.";
                            dout.writeUTF(sendM);
                            sim = null;
                            phoneNum = null;
                            money = null;
                            statusLogin = 0;
                        }
                    }
                    else{
                        sendM = "Your debitcard number or CVV number is not correct";
                        dout.writeUTF(sendM);
                    }
                }
                else{
                    dout.writeUTF("");
                }

            //when client say goodbye
            }else if (massage.equals("GOODBYE")) {
                dout.writeUTF("GOODBYE");
                System.out.println("Server closed.");
                break;
            }
            else{
                dout.writeUTF("");
            }
        }
        client.close();
    }
}