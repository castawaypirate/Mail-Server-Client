import java.io.Serializable;

public class Email implements Serializable {

    private boolean isNew;
    private String sender;
    private String receiver;
    private String subject;
    private String mainbody;

    public Email(boolean isNew, String sender, String receiver, String subject, String mainbody){
        this.isNew=isNew;
        this.sender=sender;
        this.receiver=receiver;
        this.subject=subject;
        this.mainbody=mainbody;
    }

    public boolean getisNew() {
        return isNew;
    }

    public void read(){
        isNew=false;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getMainbody() {
        return mainbody;
    }

    public void show(){
        System.out.println("Sender:");
        System.out.println(sender);
        System.out.println("Subject:");
        System.out.println(subject);
        System.out.println("Main Body:");
        System.out.println(mainbody);
    }
}
