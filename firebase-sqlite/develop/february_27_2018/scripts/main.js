'use strict';

// initialize Firebase
var config = {
	apiKey: "AIzaSyA8OEcbD6fAGShgIWNrRR7Yf0snkmM8kj4",
    authDomain: "fir-sqlite-6d578.firebaseapp.com",
    databaseURL: "https://fir-sqlite-6d578.firebaseio.com",
    projectId: "fir-sqlite-6d578",
    storageBucket: "fir-sqlite-6d578.appspot.com",
    messagingSenderId: "803841220305"
};
firebase.initializeApp(config);

// shortcutes to DOM elements
var signInButton = document.getElementById('sign-in-button');
var signOutButton = document.getElementById('sign-out-button');

// bindings on load
window.addEventListener('load', function() {
	// bind sign in button
	signInButton.addEventListener('click', function() {
		console.log('signInButton.addEventListener: click');
		//var provider = new firebase.auth.GoogleAuthProvider();
		//firebase.auth().signInWithPopup(provider);
		var provider = new firebase.auth.EmailAuthProvider();
	});

	// bind sign out button
	signOutButton.addEventListener('click', function() {
		console.log('signOutButton.addEventListener: click');
		firebase.auth().signOut();
	});
}, false);