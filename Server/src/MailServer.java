import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailServer {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private int port;
    private List<Account> users = new ArrayList<>();
    private HashMap<Account,Boolean> userStatus = new HashMap<>();

    public MailServer(int port){
        this.port=port;
        users.add(new Account("test1", "test1"));
        users.get(0).addMailToMailbox(new Email(false, "test2", "test1", "testsubject1", "test1"));
        users.get(0).addMailToMailbox(new Email(true, "test2", "test1", "testsubject2", "test2"));
        users.get(0).addMailToMailbox(new Email(true, "test2", "test1", "testsubject3", "test3"));
        userStatus.put(users.get(0),false);
        users.add(new Account("test2", "test2"));
        users.get(1).addMailToMailbox(new Email(true, "test1", "test2", "subjectuser1", "user1"));
        users.get(1).addMailToMailbox(new Email(true, "test1", "test2", "subjectuser2", "user2"));
        users.get(1).addMailToMailbox(new Email(true, "test1", "test2", "subjectuser3", "user3"));
        userStatus.put(users.get(1),false);
    }

    public void startMailServer(){

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            System.out.println("Server running...");
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Client accepted: "
                        + socket.getInetAddress()
                        .getHostAddress());
                MailClientHandler clientSock = new MailClientHandler(socket, users, userStatus);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MailClientHandler implements Runnable{
        private final Socket clientSocket;
        private List<Account> users;
        private HashMap<Account, Boolean> userStatus;
        private Account user = null;

        public MailClientHandler(Socket clientSocket, List<Account> users, HashMap<Account, Boolean> userStatus) {
            this.clientSocket = clientSocket;
            this.users = users;
            this.userStatus = userStatus;
        }

        @Override
        public void run() {
            DataInputStream input = null;
            DataOutputStream output = null;
            ObjectOutputStream  object = null;
            try {
                output = new DataOutputStream(clientSocket.getOutputStream());
                input = new DataInputStream(
                        new BufferedInputStream(clientSocket.getInputStream()));
                object = new ObjectOutputStream(clientSocket.getOutputStream());
                String line = "";
                while (!line.equals("Exit"))
                {
                    try {
                        line = input.readUTF();
                        if(line.equals("Register")){
                            register(input,output,object);
                        }else if(line.equals("LogIn")){
                            login(input,output,object);
                        }else if(line.equals("NewEmail")){
                            newEmail(input,output);
                        }else if(line.equals("ShowEmails")){
                            showEmails(object);
                        }else if(line.equals("ReadEmail")){
                            readEmail(input,object);
                        }else if(line.equals("DeleteEmail")) {
                            deleteEmail(input,output);
                        }else if(line.equals("LogOut")) {
                            logout();
                        }else{
                            continue;
                        }

                    }
                    catch(IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                System.out.println("Closing connection");
                userStatus.put(user,false);
                input.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void register(DataInputStream input, DataOutputStream output, ObjectOutputStream object){
            System.out.println("Register");
            String line = null;
            boolean taken = false;
            try {
                line = input.readUTF();
                taken=false;
                for(Account account : users){
                    if(account.getUsername().equals(line)){
                        output.writeUTF("Taken");
                        taken=true;
                        break;
                    }
                }
                if(!taken){
                    String username = line;
                    output.writeUTF("Valid");
                    System.out.println(line);
                    line = input.readUTF();
                    String password = line;
                    System.out.println(line);
                    user = new Account(username, password);
                    users.add(user);
                    userStatus.put(user,true);
                    System.out.println("Successful register");
                    object.writeObject(user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void login(DataInputStream input, DataOutputStream output, ObjectOutputStream object) {
            System.out.println("Login");
            String line = null;
            boolean exists = false;
            boolean valid =false;
            try {
                line = input.readUTF();
                exists=false;
                for(Account account : users){
                    if(account.getUsername().equals(line)){
                        user = account;
                        exists = true;
                        break;
                    }
                }
                System.out.println(line);
                line = input.readUTF();
                if(!exists){
                    output.writeUTF("Wrong username");
                }else{
                    if(line.equals(user.getPassword())){
                        valid = true;
                    }else{
                        output.writeUTF("Wrong password");
                    }
                }
                if(valid){
                    if(userStatus.get(user)){
                        output.writeUTF("Already logged in");
                    }else{
                        System.out.println(line);
                        output.writeUTF("Success");
                        userStatus.put(user,true);
                        System.out.println("User logged in");
                        object.writeObject(user);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void newEmail(DataInputStream input, DataOutputStream output) {
            System.out.println("New Email");
            String line = null;
            Account reciever = null;
            try {
                line = input.readUTF();
                for(Account account : users){
                    if(account.getUsername().equals(line)){
                        reciever = account;
                        break;
                    }
                }
                if(reciever!=null){
                    String subject = input.readUTF();
                    String mainbody = input.readUTF();
                    String sender = input.readUTF();
                    reciever.addMailToMailbox(new Email(true, sender, reciever.getUsername(), subject, mainbody));
                    output.writeUTF("Mail sent successfully!");
                }else {
                    output.writeUTF("User doesn't exist");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void showEmails(ObjectOutputStream object) {
            System.out.println("Show Emails");
            try {
                for(Email mail : user.getMailbox()){
                    object.writeObject(mail);
                }
                object.writeObject(null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void readEmail(DataInputStream input, ObjectOutputStream object) {
            System.out.println("Read Email");
            String line = null;
            Email mail = null;
            try {
                line = input.readUTF();
                int id = user.getMailbox().size()-Integer.parseInt(line);
                int size = user.getMailbox().size();
                if(id>size || id<0){
                    object.writeObject(mail);
                }else {
                    mail=user.getMailbox().get(id);
                    mail.read();
                    object.writeObject(mail);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void deleteEmail(DataInputStream input, DataOutputStream output) {
            System.out.println("Read Email");
            String line = null;
            try {
                line = input.readUTF();
                int id = user.getMailbox().size()-Integer.parseInt(line);
                int size = user.getMailbox().size();
                if(id>size || id<0){
                    output.writeUTF("Wrong Id");
                }else {
                    user.deleteMailFromMailbox(id);
                    output.writeUTF("Deletion complete");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void logout() {
            System.out.println("Log out");
            userStatus.put(user,false);

        }
    }
}
