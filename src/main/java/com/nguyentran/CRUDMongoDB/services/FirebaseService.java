package com.nguyentran.CRUDMongoDB.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nguyentran.CRUDMongoDB.entity.Note;

@Service
public class FirebaseService {
	@Autowired
	private final FirebaseMessaging firebaseMessaging;

	public FirebaseService(FirebaseMessaging firebaseMessaging) {
		this.firebaseMessaging = firebaseMessaging;
	}

	public String sendNotification(Note note) throws FirebaseMessagingException {

		Notification notification = Notification.builder().setTitle(note.getSubject()).setBody(note.getContent())
				.build();

		Message message = Message.builder().setToken(note.getToken()).setNotification(notification).putAllData(note.getData())
				.build();

		return firebaseMessaging.send(message);
	}
}
