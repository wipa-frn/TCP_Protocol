import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCP_Server {

    private static User user1 = new User("FERN","123456", new DebitCard("1234567890123456","121"));

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(9999);

        System.out.println("Top UP Easy Server online..");
        Socket client = serverSocket.accept();
        String address = "Client form " + client.getLocalAddress() + " is connected";
        System.out.println(address);


        //connection with client
        DataInputStream dIn = new DataInputStream(client.getInputStream());
        DataOutputStream dOut = new DataOutputStream(client.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String sendM;
        String msg;
        int statusLogin = 0;
        Boolean statusUser = false;
        Boolean statusPasswd = false;
        String phoneNum = null;
        String networkSim = null;
        String money = null;
        ArrayList<String> statusServer = new ArrayList<>();
        statusServer.add("100 CONNECTED");
        statusServer.add("200 FOUND");
        statusServer.add("201 NOT FOUND");
        statusServer.add("300 SUCCESS");
        statusServer.add("400 ERROR");
        statusServer.add("500 OK");
        statusServer.add("101 DISCONNECTED");

        try {

            while (true) {

                String message = dIn.readUTF();
                System.out.println("C: " + message);

                if(statusLogin == 0 && !message.equals("EXIT")) { //non login.

                    if (message.equals("HI")) {
                        sendM = statusServer.get(0) + " > " + "Hello " + client.getLocalAddress() + " , please login to Top-Up easy.";
                        dOut.writeUTF(sendM);
                    }
                    // Check user
                    else if (message.matches("LOG:USER=(.*)")) {
                        String str = message.replaceFirst("LOG:USER=", "");

                        if (str.equals(user1.getUsername()))
                        {
                            sendM = statusServer.get(1) + " > " + "Please enter your password";
                            statusUser = true;
                            dOut.writeUTF(sendM);
                        }

                        else{
                            sendM = statusServer.get(2) + " > " + "User name is not correct";
                            dOut.writeUTF(sendM);
                        }
                    }
                    // Check password when user is found
                    else if (statusUser && message.matches("LOG:PWD=(.*)")) {
                        String str = message.replaceFirst("LOG:PWD=", "");

                        if (str.equals(user1.getPassword())){
                            statusLogin = 1;
                            sendM = statusServer.get(3) + " > " + "Login success! Welcome " + user1.getUsername();
                            dOut.writeUTF(sendM);
                        }
                        else {
                            sendM = statusServer.get(2) + " > " + "Password is not correct";
                            dOut.writeUTF(sendM);
                        }
                    }
                    else {
                        sendM = statusServer.get(4) + " > " + "Your syntax error ";
                        dOut.writeUTF(sendM);
                    }

                }

                //when login success
                else if(statusLogin == 1 && !message.equals("EXIT")){
                    if (message.matches("TOP UP=(.*)")) {
                        String str = message.replaceFirst("TOP UP=", "");

                        if (str.equals("AIS") || str.equals("DTAC") || str.equals("TRUE")){
                            sendM = statusServer.get(1) + " > " + "You choose "+ str + ". Are you sure? [Y/N]";
                            dOut.writeUTF(sendM);

                            msg = dIn.readUTF();
                            System.out.println("C: "+ msg);

                            if(msg.equals("Y")){
                                sendM = statusServer.get(5) + " > " + "Please enter your phone number";
                                networkSim = str;
                            }
                            else if (msg.equals("N")){
                                networkSim = null;
                                sendM = statusServer.get(5) + " > " + "Please choose new network or exit";
                            }
                            dOut.writeUTF(sendM);
                        }
                        else {
                            sendM = statusServer.get(2) + " > " + "Network sim is not correct";
                            dOut.writeUTF(sendM);
                        }
                    }
                    // Check it have to found network, it will fill phone number
                    else if (networkSim.isEmpty() && message.matches("TO PHONENUM=(.*)")){
                        sendM = statusServer.get(4) + " > " + "Please choose new network or exit";
                        dOut.writeUTF(sendM);
                    }
                    else if ((!networkSim.isEmpty()) && message.matches("TO PHONENUM=(.*)")){

                        String str = message.replaceFirst("TO PHONENUM=", "");

                        if(str.matches("([0-9]*)") && (str.length() == 10) ){
                            sendM = statusServer.get(5) + " > " + "Phone number is " + str + ". Are you sure? [Y/N]";
                            dOut.writeUTF(sendM);

                            msg = dIn.readUTF();
                            System.out.println("C: " + msg);

                            if(msg.equals("Y")){
                                phoneNum = str ;
                                sendM = statusServer.get(5) + " > " + "Please enter money";
                            }
                            else if (msg.equals("N")) {
                                phoneNum = null;
                                sendM = statusServer.get(5) + " > " + "Please add new phone number or exit";
                            }
                            dOut.writeUTF(sendM);
                        }
                        else {
                            sendM = statusServer.get(4) + " > " + "Phone number is incorrect.";
                            dOut.writeUTF(sendM);
                        }
                    }
                    else if(phoneNum.isEmpty() && message.matches("MONEY=(.*)")){
                        sendM = statusServer.get(4) + " > " + "Please add new phone number or exit";
                        dOut.writeUTF(sendM);
                    }
                    else if(!phoneNum.isEmpty() && message.matches("MONEY=(.*)")){
                        String str = message.replaceFirst("MONEY=", "");

                        if(str.equals("100") || str.equals("200") || str.equals("500") || str.equals("1000")){
                            sendM = statusServer.get(1) + " > " +  "Please enter your debit card number (ending with _ ) and then followed by CVV number";
                            money = str;
                        }
                        else {
                            money = null;
                            sendM = statusServer.get(2) + " > " + "Money out of range.";
                        }
                        dOut.writeUTF(sendM);
                    }
                    else if(money.isEmpty() && message.matches("FROM BNK=(.*)")){
                        sendM = statusServer.get(4) + " > " + "PLease add new money";
                        dOut.writeUTF(sendM);
                    }
                    else if(!money.isEmpty() && message.matches("FROM BNK=(.*)")){
                        String str = message.replaceFirst("FROM BNK=", "");

                        if(str.equals(user1.getDebitCard().getNumberDebit() + "_" + user1.getDebitCard().getNumberCVV())){
                            sendM = statusServer.get(1) + " > " + "Are you confirm TOP-UP? [Y/N]";
                            dOut.writeUTF(sendM);

                            msg = dIn.readUTF();
                            System.out.println("C: "+msg);

                            if(msg.equals("Y")){
                                sendM = statusServer.get(3) + " > " + "Top up is success, goodbye.";
                                dOut.writeUTF(sendM);
                                networkSim = null;
                                phoneNum = null;
                                money = null;
                                statusLogin = 0;
                            }
                            else if (msg.equals("N")){
                                sendM = statusServer.get(5) + " > " + "Please enter your debit card number or exit";
                                dOut.writeUTF(sendM);
                            }
                        }
                        else{
                            sendM = statusServer.get(2) + " > " + "Your debit card number or CVV number is not correct";
                            dOut.writeUTF(sendM);
                        }
                    }
                    else {
                        sendM = statusServer.get(4) + " > " + "Your syntax error ";
                        dOut.writeUTF(sendM);
                    }
                }
                else if (message.equals("EXIT") && statusLogin == 0 || message.equals("EXIT") && statusLogin == 1 ) {
                    dOut.writeUTF(statusServer.get(6) + " > " + client.getLocalAddress().toString() + " is logout");
                    break;
                }
            }
        }
        catch (EOFException e) {
            e.printStackTrace();

        }
        finally {
            client.close();
        }
    }
}