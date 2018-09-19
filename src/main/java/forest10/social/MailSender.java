package forest10.social;

import lombok.Builder;
import lombok.Data;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发送邮件(JDK实现)
 *
 * @author Forest10
 * 2017/12/6
 */
@Data
@Builder
public class MailSender {

	/***发送者***/
	private String sender;
	/***发送者账号***/
	private String userName;
	/***发送者密码***/
	private String password;
	/***发送者主机地址:ip***/
	private String host;
	/***发送者端口***/
	private String port;
	/***接收者***/
	private String receiver;
	/***内容***/
	private String text;
	/****主题***/
	private String subject;

	/**
	 * 发送邮件
	 *
	 * @throws MessagingException
	 */
	public void doSend() throws MessagingException {

		Properties props = new Properties();
		props.put("mail.smtp.user", sender);
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", port);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getInstance(props, getAuth());
		session.setDebug(true);

		MimeMessage msg = new MimeMessage(session);
		msg.setSubject(subject);
		msg.setText(text);
		msg.setFrom(new InternetAddress(sender));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

		Transport transport = session.getTransport("smtps");
		transport.connect(host, Integer.valueOf(port), userName, password);
		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();

	}

	/**
	 * 构造认证
	 *
	 * @return
	 */
	private Authenticator getAuth() {
		return new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
	}
}
