// import and configure the Firebase SDK
//importScripts('/__/firebase/4.1.3/firebase-app.js');
//importScripts('/__/firebase/4.1.3/firebase-messaging.js');
//importScripts('/__/firebase/init.js');
//
//firebase.messaging();

// give the service worker access to Firebase Messaging
// note that you can only use Firebase Messaging here, other Firebase libraries
// are not available in the service worker
importScripts('https://www.gstatic.com/firebasejs/4.8.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/4.8.1/firebase-messaging.js');

// initialize the Firebase app in the service worker by passing in the
// messagingSenderId
firebase.initializeApp({
  //'messagingSenderId': 'YOUR-SENDER-ID'
  'messagingSenderId': '103953800507' // gcm_sender_id from the manifest.json
});

// retrieve an instance of Firebase Messaging so that it can handle background
// messages
const messaging = firebase.messaging();
