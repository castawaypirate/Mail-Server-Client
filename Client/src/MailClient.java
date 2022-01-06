import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MailClient {

    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private DataInputStream data = null;
    private ObjectInputStream object = null;
    private Account user = null;
    private boolean loggedin = false;

    public MailClient(String address, int port){
        try {
            socket = new Socket(address,port);
            input = new DataInputStream(System.in);
            output = new DataOutputStream(socket.getOutputStream());
            object = new ObjectInputStream(socket.getInputStream());
            data = new DataInputStream(new BufferedInputStream(socket.getInputStream()));


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = "";
        while (!line.equals("Exit")){
            try {
                if(loggedin){
                    System.out.println("----------");
                    System.out.println("Choose one of the options.");
                    System.out.println("==========");
                    System.out.println(">NewEmail");
                    System.out.println(">ShowEmails");
                    System.out.println(">ReadEmail");
                    System.out.println(">DeleteEmail");
                    System.out.println(">LogOut");
                    System.out.println(">Exit");
                    System.out.println("==========");
                    line = input.readLine();
                    output.writeUTF(line);
                    if(line.equals("NewEmail")){
                        newEmail();
                    }else if(line.equals("ShowEmails")){
                        showEmails();
                    }else if(line.equals("ReadEmail")){
                        readEmail();
                    }else if(line.equals("DeleteEmail")){
                        deleteEmail();
                    }else if(line.equals("LogOut")){
                        logout(line);
                    }else if(line.equals("Exit")){
                        continue;
                    }else {
                        System.out.println("Wrong input. Try again.");
                    }
                }else{
                    System.out.println("----------");
                    System.out.println("MailServer:");
                    System.out.println("----------");
                    System.out.println("Hello, you connected as a quest.");
                    System.out.println("==========");
                    System.out.println(">Register");
                    System.out.println(">LogIn");
                    System.out.println(">Exit");
                    System.out.println("==========");
                    line = input.readLine();
                    output.writeUTF(line);
                    if(line.equals("Register")){
                        register();
                    }
                    else if(line.equals("LogIn")) {
                        login();
                    }else if(line.equals("Exit")){
                        continue;
                    }else{
                        System.out.println("Wrong input. Try again.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(){
        String line=null;
        System.out.println("Type your username");
        try {
            line = this.input.readLine();
            this.output.writeUTF(line);
            line = data.readUTF();
            if(line.equals("Taken")){
                System.out.println("Username Taken");
            }
            if(!line.equals("Taken")) {
                System.out.println("Type your Password");
                line = this.input.readLine();
                this.output.writeUTF(line);
                System.out.println("You have created an account successfully and you have logged in");
                user = (Account) this.object.readObject();
                loggedin = true;
                System.out.println("----------");
                System.out.println("Welcome "+user.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        String line = null;
        try {
            System.out.println("Type your username");
            line = this.input.readLine();
            this.output.writeUTF(line);
            System.out.println("Type your password");
            line = this.input.readLine();
            this.output.writeUTF(line);
            line = data.readUTF();
            System.out.println(line);
            if (line.equals("Success")) {
                System.out.println("You have logged in");
                user = (Account) this.object.readObject();
                loggedin = true;
                System.out.println("----------");
                System.out.println("Welcome "+user.getUsername());
            }
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void newEmail() {
        String line = null;
        try {
            System.out.println("Receiver:");
            line = this.input.readLine();
            this.output.writeUTF(line);
            System.out.println("Subject:");
            line = this.input.readLine();
            this.output.writeUTF(line);
            System.out.println("Main Body:");
            line = this.input.readLine();
            this.output.writeUTF(line);
            this.output.writeUTF(user.getUsername());
            line = data.readUTF();
            System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEmails() {
        Email mail = null;
        List<Email> box = new ArrayList<>();
        try {
            do{
                mail = (Email) this.object.readObject();
                if(mail!=null){
                    System.out.println(mail.getisNew());
                    box.add(mail);
                }
            }while (!(mail ==null));
            user.showMailbox(box);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readEmail() {
        String line = null;
        Email mail = null;
        try {
            System.out.println("Choose Id:");
            line = this.input.readLine();
            this.output.writeUTF(line);
            mail = (Email) this.object.readObject();
            if(mail==null){
                System.out.println("Wrong Id");
            }else {
                mail.show();
                mail.read();
            }
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteEmail() {
        String line = null;
        try {
            System.out.println("Choose Id:");
            line = this.input.readLine();
            this.output.writeUTF(line);
            line = data.readUTF();
            System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout(String line) {
        loggedin=false;
    }
}
