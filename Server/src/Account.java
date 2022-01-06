import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {

    private String username;
    private String password;
    private List<Email> mailbox;

    public Account(String username, String password){
        this.username=username;
        this.password=password;
        this.mailbox=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addMailToMailbox(Email mail){
        mailbox.add(mail);
    }

    public void deleteMailFromMailbox(int id){
        mailbox.remove(id);
    }

    public List<Email> getMailbox() {
        return mailbox;
    }

    public void showMailbox(List<Email> box){
        System.out.println("Id        From                  Subject");
        int j=1;
        for(int i=box.size()-1;i>=0;i--){
            if(box.get(i).getisNew()){
                System.out.println(j+". [New]  "+box.get(i).getSender()+"                 "+box.get(i).getSubject());
            }else{
                System.out.println(j+".        "+box.get(i).getSender()+"                 "+box.get(i).getSubject());
            }
            j++;
        }
    }
}
