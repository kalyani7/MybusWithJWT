package com.mybus.service;

import org.springframework.stereotype.Service;

/**
 * @author yks-Srinivas
 */

@Service
public class CommunicationManager {


	/*
	public void sendMail(final EmailData emailData,final ByteArrayOutputStream attachmentAsByteArrResource) {  
		if(null != emailData){
			MimeMessagePreparator mailMsg = mimeMessagePreparator(emailData,attachmentAsByteArrResource);
			JavaMailSenderImpl.send(mailMsg);
		}
	}

	public MimeMessagePreparator mimeMessagePreparator(final EmailData emailData,final ByteArrayOutputStream attachmentAsByteArrResource){

		MimeMessagePreparator mailMsg = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage, true);
				msgHelper.setSubject(emailData.getSubject());
				msgHelper.setTo(emailData.getTo().toArray(new String[emailData.getTo().size()]));
				msgHelper.setCc(emailData.getCc().toArray(new String[emailData.getCc().size()]));
				if(!CollectionUtils.isEmpty(emailData.getBcc())){
					msgHelper.setBcc(emailData.getBcc().toArray(new String[emailData.getBcc().size()]));
				}
				msgHelper.setText(emailData.getBody(), true);
				if (attachmentAsByteArrResource != null){
					saveHtmlToPdf(emailData.getBody(), attachmentAsByteArrResource);
					if(attachmentAsByteArrResource.size() > 0){
						msgHelper.addAttachment("Ticket.pdf", new ByteArrayResource(attachmentAsByteArrResource.toByteArray()));
					}
				}
				msgHelper.setFrom(emailData.getFrom());
			}
		};
		return mailMsg;
	}

	public static void saveHtmlToPdf(String htmlString, OutputStream os)throws IOException, DocumentException {
		try{
			ITextRenderer render = new ITextRenderer();
			if(htmlString != null){
				htmlString = htmlString.replaceAll("&nbsp;", "");
				htmlString = htmlString.replaceAll("&", "&amp;");
				render.setDocumentFromString(htmlString);
				render.layout();
				render.createPDF(os);
			}
		}catch (Exception e) {
		}finally{
			if(os !=null){
				try {
					os.close();
				} catch (Exception e) {
				}
			}
		}
	}
	public String sendSMS(final String to,final String  body){
		return null;
	}
	*/
}