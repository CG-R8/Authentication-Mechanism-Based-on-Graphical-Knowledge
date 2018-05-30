package mailPack;


import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

public class GenericMailSender
{
    String userName;
    public String reply;

    public GenericMailSender(String userName) {
        this.userName = userName;
        reply = "";
    }

    public boolean sendMail( Session session, Vector <String> recipients, String subject, String message, String from, Vector<String> files)
    {
        BodyPart messageBodyPart;
        session.setDebug(false);
        // create a message
        Message msg = new MimeMessage(session);

        try {
            // set the from and to address
            InternetAddress addressFrom = new InternetAddress(from + "<" + userName + ">");
            msg.setFrom(addressFrom);
            InternetAddress[] addressTo = new InternetAddress[recipients.size()];
            for(int i=0;i<recipients.size();i++) {
                System.out.println("-" + recipients.get(i) + "-");
                addressTo[i] = new InternetAddress(recipients.get(i));
            }
            msg.setRecipients(Message.RecipientType.TO, addressTo);
            // Setting the Subject and Content Type
            msg.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // add text....
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(message);
            multipart.addBodyPart(messageBodyPart);
            
            for(int i=0;i<files.size();i++) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(files.get(i));
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(files.get(i)).getName());
                multipart.addBodyPart(messageBodyPart);
            }
            msg.setContent(multipart);
            Transport.send(msg);
            reply = "Mail Sent Successfully!";
            return true;
        }catch(Exception e) {
            System.out.println("Mail Send Exception: " + e);
            e.printStackTrace();
            reply = "Error Sending Mail: " + e;
            return false;
        }
    }
}
