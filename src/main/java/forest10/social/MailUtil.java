package forest10.social;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Forest10
 * 2017/12/6
 */
public class MailUtil {

	private static String d_email = "1234@139.com", d_uname = "13718799673", d_password = "343434",
			d_host = "smtp.139.com", d_port = "465", m_to = "1234@qq.com",
			m_subject = "Indoors Readable File: " + "2132";


	public static void main(String[] args) throws Exception{
		send();
	}

	public static void send() throws Exception{

		Properties props = new Properties();
		props.put("mail.smtp.user", d_email);
		props.put("mail.smtp.host", d_host);
		props.put("mail.smtp.port", d_port);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", d_port);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getInstance(props, getAuth());
		session.setDebug(true);

		MimeMessage msg = new MimeMessage(session);
		msg.setSubject(m_subject);
		msg.setText("from java");
		msg.setFrom(new InternetAddress(d_email));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));

		Transport transport = session.getTransport("smtps");
		transport.connect(d_host, Integer.valueOf(d_port), d_uname, d_password);
		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();


	}

	public static Authenticator getAuth() {
		return new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(d_uname, d_password);
			}
		};
	}
}
