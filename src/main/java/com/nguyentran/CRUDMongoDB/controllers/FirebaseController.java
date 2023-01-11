//package com.nguyentran.CRUDMongoDB.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.nguyentran.CRUDMongoDB.entity.Note;
//import com.nguyentran.CRUDMongoDB.services.FirebaseService;
//
//@RestController
//@RequestMapping("/admin")
//public class FirebaseController {
//	
//	@Autowired
//	private FirebaseService firebaseService;
//
//	
//	@PostMapping("/send-notification")
//	public String sendNotification(@RequestBody Note note)
//			throws FirebaseMessagingException {
//		return firebaseService.sendNotification(note);
//	}
//
//}
