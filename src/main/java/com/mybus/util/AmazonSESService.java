package com.mybus.util;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

@Service
public class AmazonSESService {

  private AmazonSimpleEmailService sesClient = null;
  private Session session;

  @PostConstruct
  public void init() {
    sesClient =
            AmazonSimpleEmailServiceClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1).build();
    session = Session.getDefaultInstance(new Properties());
  }

  public void sendEmailWithAttachment(String to, String content, String subject, String fileName,
                                      InputStream attachment)  throws  Exception{
    try {
     // Create a new MimeMessage object.
      MimeMessage message = new MimeMessage(session);

      // Add subject, from and to lines.
      message.setSubject(subject, "UTF-8");
      message.setFrom(new InternetAddress("support@srikrishnatravels.in"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      //message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("srikritravels@gmail.com"));

      // Create a multipart/alternative child container.
      MimeMultipart msg_body = new MimeMultipart("alternative");

      // Create a wrapper for the HTML and text parts.
      MimeBodyPart wrap = new MimeBodyPart();

      // Define the HTML part.
      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(content,"text/html; charset=UTF-8");

      msg_body.addBodyPart(htmlPart);

      // Add the child container to the wrapper object.
      wrap.setContent(msg_body);

      // Create a multipart/mixed parent container.
      MimeMultipart msg = new MimeMultipart("mixed");

      // Add the parent container to the message.
      message.setContent(msg);

      // Add the multipart/alternative part to the message.
      msg.addBodyPart(wrap);

      // Define the attachment
      MimeBodyPart att = new MimeBodyPart();
      DataSource fds = new ByteArrayDataSource(attachment, "application/pdf");
      att.setDataHandler(new DataHandler(fds));
      att.setFileName(fileName);
      // Add the attachment to the message.
      msg.addBodyPart(att);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      message.writeTo(outputStream);
      RawMessage rawMessage =
              new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
      SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
      sesClient.sendRawEmail(rawEmailRequest);
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public void sendEmail(String to, String content, String subject)  throws  Exception{
    try {
      // Create a new MimeMessage object.
      MimeMessage message = new MimeMessage(session);

      // Add subject, from and to lines.
      message.setSubject(subject, "UTF-8");
      message.setFrom(new InternetAddress("support@srikrishnatravels.in"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      //message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("srikritravels@gmail.com"));

      // Create a multipart/alternative child container.
      MimeMultipart msg_body = new MimeMultipart("alternative");

      // Create a wrapper for the HTML and text parts.
      MimeBodyPart wrap = new MimeBodyPart();

      // Define the HTML part.
      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(content,"text/html; charset=UTF-8");

      msg_body.addBodyPart(htmlPart);

      // Add the child container to the wrapper object.
      wrap.setContent(msg_body);

      // Create a multipart/mixed parent container.
      MimeMultipart msg = new MimeMultipart("mixed");

      // Add the parent container to the message.
      message.setContent(msg);

      // Add the multipart/alternative part to the message.
      msg.addBodyPart(wrap);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      message.writeTo(outputStream);
      RawMessage rawMessage =
              new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
      SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
      sesClient.sendRawEmail(rawEmailRequest);
    }catch (Exception e){
      e.printStackTrace();
    }
  }




}