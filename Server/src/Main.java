public class Main {

    public static void main(String[] args) {
        //new MailServer(Integer.parseInt(args[0])).startMailServer();
        new MailServer(8080).startMailServer();
    }
}
