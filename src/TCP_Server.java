import java.io.*;
import java.net.*;
public class TCP_Server {

    private static User user1 = new User("FERN","123456", new DebitCard("1234567890123456","121"));

    public static void main(String[] args) throws Exception {


        ServerSocket serverSocket=new ServerSocket(9999);

        System.out.println("Top UP Easy Server online..");
        Socket client = serverSocket.accept();
        String address = "Client form " + client.getLocalAddress() + " is connected";
        System.out.println(address);


        //connection with client
        DataInputStream dIn = new DataInputStream(client.getInputStream());
        DataOutputStream dOut = new DataOutputStream(client.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String sendM;
        int statusLogin = 0;
        String phoneNum = null;
        String networkSim = null;
        String money = null;

        while(true) {
            String massage = dIn.readUTF();
            System.out.println("Client: " + massage);

            if(statusLogin == 0) { //non login.

                if (massage.equals("HI")) {
                    sendM = "HELLO " + client.getLocalAddress() + " , please login to Top-Up easy.";


                } else if (massage.matches("LOG:USER=(.*)")) {
                    String str = massage;
                    str = str.replaceFirst("LOG:USER=", "");

                    if (str.equals(user1.getUsername()))
                        sendM = "Please enter your password";
                    else
                        sendM = "User name is not correct";


                } else if (massage.matches("LOG:PWD=(.*)")) {
                    String str = massage.replaceFirst("LOG:PWD=", "");

                    if (str.equals(user1.getPassword()))
                        sendM = "Login success! Welcome " + user1.getUsername();
                    else
                        sendM = "Password is not correct";

                    statusLogin = 1;

                }
                else{
                    sendM = "Your syntax is error";
                }
                dOut.writeUTF(sendM);
            }
            else if(statusLogin == 1){ //when login success

                if ( massage.matches("TUP=(.*)")) {
                    String str = massage.replaceFirst("TUP=", "");

                    if (str.equals("AIS")||str.equals("DTAC")||str.equals("TRUE")){
                        sendM = "You choose "+ str + ". Are you sure? [Y/N]";
                        dOut.writeUTF(sendM);

                        String msg = dIn.readUTF();
                        System.out.println("client: "+ msg);

                        if(msg.equals("Y")){
                            sendM = "PLease enter your phone number";
                            dOut.writeUTF(sendM);
                            networkSim = str;
                        }else {
                            networkSim = null;
                            dOut.writeUTF("");
                        }
                    }
                    else {
                        sendM = "Sim is not correct";
                        dOut.writeUTF(sendM);
                    }
                }
                else if ((!networkSim.isEmpty()) && massage.matches("NUM=(.*)")){

                    String str = massage.replaceFirst("NUM=", "");

                    if(str.matches("([0-9]*)") && (str.length() == 10) ){
                        sendM = "Phone number is "+str+". Are you sure? [Y/N]";
                        dOut.writeUTF(sendM);

                        String msg = dIn.readUTF();
                        System.out.println("client: "+msg);

                        if(msg.equals("Y")){
                            phoneNum = str ;
                            sendM = "Please enter money";
                            dOut.writeUTF(sendM);

                        }else if (msg.equals("N")) {
                            phoneNum = null;
                            dOut.writeUTF("");//
                        }
                    }
                    else {
                        sendM = "Phone number is not correct.";
                        dOut.writeUTF(sendM);
                    }
                }

                else if(!phoneNum.isEmpty() && massage.matches("MONEY=(.*)")){
                    String str = massage.replaceFirst("MONEY=", "");

                    if(str.equals("100") || str.equals("200") || str.equals("500") || str.equals("1000")){
                        sendM = "PLease enter your debit card number (ending with _ ) and then followed by CVV number";
                        dOut.writeUTF(sendM);
                        money = str;
                    }else {
                        sendM = "Money out of range.";
                        dOut.writeUTF(sendM);
                    }

                }else if(!money.isEmpty() && massage.matches("BNK=(.*)")){
                    String str = massage.replaceFirst("BNK=", "");

                    if(str.equals(user1.getDebitCard().getNumberDebit()+"_"+user1.getDebitCard().getNumberCVV())){
                        sendM = "Are you confirm TOP-UP? [Y/N]";
                        dOut.writeUTF(sendM);

                        String msg = dIn.readUTF();
                        System.out.println("client: "+msg);

                        if(msg.equals("Y")){
                            sendM = "TOP-UP is success GOODBYE.";
                            dOut.writeUTF(sendM);
                            networkSim = null;
                            phoneNum = null;
                            money = null;
                            statusLogin = 0;
                        }
                    }
                    else{
                        sendM = "Your debit card number or CVV number is not correct";
                        dOut.writeUTF(sendM);
                    }
                }
                else{
                    dOut.writeUTF("Server: Your syntax is error");
                }

            //when client say goodbye
            }else if (massage.equals("GOODBYE") && statusLogin == 0) {
                dOut.writeUTF(client.getLocalAddress().toString());
            }
            else if (massage.equals("EXIT")) {
                break;
            }
            else{
                dOut.writeUTF("Server: Your syntax is error");
            }

        }
        client.close();
    }
}