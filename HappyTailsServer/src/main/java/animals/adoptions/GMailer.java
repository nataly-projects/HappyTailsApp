
package animals.adoptions;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class GMailer {

    private static final String HAPPY_TAILS_EMAIL = System.getenv("SMTP_EMAIL");
    private static final String EMAIL_ADOPT_SUBJECT = "Regarding Adoption";
    private static final String EMAIL_CONTACT_US_SUBJECT = "Happy Tails contact us message";
    private static final String EMAIL_RESET_PASSWORD_SUBJECT = "Reset your Happy Tails password";
    private static final String HOST = "smtp.gmail.com";

    private static final String PORT = System.getenv("SMTP_PORT");

    private static final String PASSWORD = System.getenv("SMTP_PASSWORD");

    public void sendAdoptMail(String message, String toEmail) throws Exception {
        sendEmail(message, toEmail, EMAIL_ADOPT_SUBJECT);
    }

    public void sendContactUsMail(String message) throws Exception {
        sendEmail(message, HAPPY_TAILS_EMAIL, EMAIL_CONTACT_US_SUBJECT);
    }

    public void sendResetPasswordMail(String toEmail, String message) throws Exception {
        sendEmail(message, toEmail, EMAIL_RESET_PASSWORD_SUBJECT);
    }

    private void sendEmail(String message, String toEmail, String subject) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(HAPPY_TAILS_EMAIL, PASSWORD);
            }
        });

        Message email = new MimeMessage(session);
        email.setFrom(new InternetAddress(HAPPY_TAILS_EMAIL));
        email.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject(subject);
        email.setText(message);

        // Send the message
        try {
            Transport.send(email);
            System.out.println("Message sent!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



}
