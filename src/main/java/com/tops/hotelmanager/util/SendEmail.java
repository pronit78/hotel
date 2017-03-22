package com.tops.hotelmanager.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//@Service
public class SendEmail {

	private JavaMailSender javaMailSender;

	@Autowired
	public SendEmail(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendNotification(String[] toMails, String[] ccMails,
			String[] bccMails, String subject, String text) {
		// https://www.google.com/settings/security/lesssecureapps
		SimpleMailMessage mail = new SimpleMailMessage();
		if (toMails != null) {
			mail.setTo(toMails);
		}
		if (ccMails != null) {
			mail.setCc(ccMails);
		}
		if (bccMails != null) {
			mail.setBcc(bccMails);
		}

		mail.setSubject(subject);
		mail.setText(text);
		javaMailSender.send(mail);
		System.out.println("Send Mail");
	}

	public void sendNotifacertion(String toMail, String ccMail, String bccMail,
			String subject, String text) {
		String[] toMails = { toMail };
		String[] ccMails = { ccMail };
		String[] bccMails = { bccMail };
		sendNotification(toMails, ccMails, bccMails, subject, text);
	}
}
