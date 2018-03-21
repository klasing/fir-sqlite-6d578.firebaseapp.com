'use strict';

// initializes FirebaseObject
// ********************* FirebaseObject **************************************
function FirebaseObject() {
	console.log('<<constructor>> FirebaseObject()');

	this.checkSetup();

	// shortcuts to DOM Elements
	this.messageList = document.getElementById('messages');
	this.messageForm = document.getElementById('message-form');
	this.messageInput = document.getElementById('message');
	this.submitButton = document.getElementById('submit');
	this.submitImageButton = document.getElementById('submitImage');
	this.imageForm = document.getElementById('image-form');
	this.mediaCapture = document.getElementById('mediaCapture');
	this.userPic = document.getElementById('user-pic');
	this.userName = document.getElementById('user-name');
	this.signInButton = document.getElementById('sign-in');
	this.signOutButton = document.getElementById('sign-out');
	this.signInSnackbar = document.getElementById('must-signin-snackbar');

	// saves message on form submit
	this.messageForm.addEventListener('submit', this.saveMessage.bind(this));
	this.signOutButton.addEventListener('click', this.signOut.bind(this));
	this.signInButton.addEventListener('click', this.signIn.bind(this));

	// toggle for the button
	var buttonTogglingHandler = this.toggleButton.bind(this);
	this.messageInput.addEventListener('keyup', buttonTogglingHandler);
	this.messageInput.addEventListener('change', buttonTogglingHandler);

	// events for image upload
	this.submitImageButton.addEventListener('click', function(e) {
		e.preventDefault();
		this.mediaCapture.click();
	}.bind(this));
	this.mediaCapture.addEventListener('change', this.saveImageMessage.bind(this));

	this.initFirebase();
};

// sets up shortcuts to Firebase features and initiate firebase auth
// ********************* initFirebase ****************************************
FirebaseObject.prototype.initFirebase = function() {
	console.log('FirebaseObject.initFirebase()');

	// shortcuts to Firebase SDK features
	this.auth = firebase.auth();
	this.database = firebase.database();
	this.storage = firebase.storage();

	// initiates Firebase auth and listen to auth state changes
	this.auth.onAuthStateChanged(this.onAuthStateChanged.bind(this));
};

// loads chat messages history and listens for upcoming ones
// ********************* loadMessages ****************************************
FirebaseObject.prototype.loadMessages = function() {
	console.log('FirebaseObject.loadMessages()');

	// reference to the /messages/ database path
	this.messagesRef = this.database.ref('messages');
	// make sure we remove all previous listeners
	this.messagesRef.off();

	// loads the last 12 messages and listen for new ones
	var setMessage = function(data) {
		var val = data.val();
		this.displayMessage(data.key, val.name, val.text, val.photoUrl, val.imageUrl);
	}.bind(this);
	this.messagesRef.limitToLast(12).on('child_added', setMessage);
	this.messagesRef.limitToLast(12).on('child_changed', setMessage);
};

// saves a new message on the Firebase Db
// ********************* saveMessage *****************************************
FirebaseObject.prototype.saveMessage = function(e) {
	e.preventDefault();
	console.log('FirebaseObject.saveMessage()');

	// check that the user entered a message and is signed in
	if (this.messageInput.value && this.checkSignedInWithMessage()) {
		var currentUser = this.auth.currentUser;
		// add a new message entry to the Firebase Database
		this.messagesRef.push({
			name: currentUser.displayName,
			text: this.messageInput.value,
			photoUrl: currentUser.photoURL || '/images/profile_placeholder.png'
		}).then(function() {
			// clear message text field and SEND button state
			FirebaseObject.resetMaterialTextfield(this.messageInput);
			this.toggleButton();
		}.bind(this)).catch(function(error) {
			console.error('Error writing new message to Firebase Database', error);
		});
	}
};

// sets the URL of the given img element with the URL of the image stored in
// Cloud Storage
// ********************* setImageUrl *****************************************
FirebaseObject.prototype.setImageUrl = function(imageUri, imgElement) {
	console.log('FirebaseObject.setImageUrl()');

	// if the image is a Cloud Storage URI we fetch the URL
	if (imageUri.startsWith('gs://')) {
		// display a loading image first
		imgElement.src = FirebaseObject.LOADING_IMAGE_URL;
		this.storage.refFromURL(imageUri).getMetadata().then(function(metadata) {
			imgElement.src = metadata.downloadURLs[0];
		});
	} else {
		imgElement.src = imageUri;
	}
};

// saves a new message containing an image URI in Firebase
// this first saves the image in Firebase storage
// ********************* saveImageMessage ************************************
FirebaseObject.prototype.saveImageMessage = function(event) {
	event.preventDefault();
	console.log('FirebaseObject.saveImageMessage()');

	var file = event.target.files[0];

	// clear the selection in the file picker input
	this.imageForm.reset();

	// check if the file is an image
	if (!file.type.match('image.*')) {
		var data = {
			message: 'You can only share images',
			timeout: 2000
		};
		this.signInSnackbar.MaterialSnackbar.showSnackbar(data);
		return;
	}

	// check if the user is signed-in
	if (this.checkSignedInWithMessage()) {

		// we add a message with a loading icon that will get updated with the
		// shared image
		var currentUser = this.auth.currentUser;
        //console.log('currentUser.uid: ' + currentUser.uid);
		this.messagesRef.push({
			name: currentUser.displayName,
			imageUrl: FirebaseObject.LOADING_IMAGE_URL,
			photoUrl: currentUser.photoURL || '/images/profile_placeholder.png'
		}).then(function(data) {

			// upload the image to Cloud Storage
			//var filePath = '/images/' + currentUser.uid + '/' + data.key + '/' + file.name;
			// update for Android
			var filePath = '/images/' + file.name;
			return this.storage.ref(filePath).put(file).then(function(snapshot) {

				// get the file's Storage URI and update the chat message placeholder
				var fullPath = snapshot.metadata.fullPath;
				return data.update({imageUrl: this.storage.ref(fullPath).toString()});
			}.bind(this));
		}.bind(this)).catch(function(error) {
			console.error('There was an error uploading a file to Cloud Storage: ', error);
		});
	}
};

// signs-in to FirebaseObject
// ********************* signIn **********************************************
FirebaseObject.prototype.signIn = function() {
	console.log('FirebaseObject.signIn()');

	// sign in Firebase using popup auth and Google as the identity provider
	var provider = new firebase.auth.GoogleAuthProvider();
	this.auth.signInWithPopup(provider);
};

// signs-out of FirebaseObject
// ********************* signOut *********************************************
FirebaseObject.prototype.signOut = function() {
	console.log('FirebaseObject.signOut()');

	// sign out of Firebase
	this.auth.signOut();
};

// triggers when the auth state change for instance when the user signs-in
// or signs-out
// ********************* onAuthStateChanged **********************************
FirebaseObject.prototype.onAuthStateChanged = function(user) {
	console.log('FirebaseObject.onAuthStateChanged()');

	if (user) { // user is signed in
		// get profile pic and user's name from the Firebase user object
		var profilePicUrl = user.photoURL;
		var userName = user.displayName;

		// set the user profile pic and name
		this.userPic.style.backgroundImage = 'url(' + (profilePicUrl || '/images/profile_placeholder.png') + ')';
		this.userName.textContent = userName;

		// show user's profile and sign-out button
		this.userName.removeAttribute('hidden');
		this.userPic.removeAttribute('hidden');
		this.signOutButton.removeAttribute('hidden');

		// hide sign-in button
		this.signInButton.setAttribute('hidden', true);

		// we load currently existing chant messages
		// loadMessages should be called when the user signs in, to get a ref
		this.loadMessages();

		// we load the Firebase Messaging Device token and enable notifications
		this.saveMessagingDeviceToken();
	} else { // user is signed out
		// hide user's profile and sign-out button
		this.userName.setAttribute('hidden', true);
		this.userPic.setAttribute('hidden', true);
		this.signOutButton.setAttribute('hidden', true);

		// show sign-in button
		this.signInButton.removeAttribute('hidden');
	}
};

// returns true if user is signed-in, otherwise false and displays a message
// ********************* checkSignedInWithMessage ****************************
FirebaseObject.prototype.checkSignedInWithMessage = function() {
	console.log('FirebaseObject.checkSignedInWithMessage()');

	// return true if the user is signed in Firebase
	if (this.auth.currentUser) {
		return true;
	}

	// display a message to the user using a Toast
	var data = {
		message: 'You must sign-in first',
		timeout: 2000
	};
	this.signInSnackbar.MaterialSnackbar.showSnackbar(data);

	return false;
};

// saves the messaging device token to the datastore
FirebaseObject.prototype.saveMessagingDeviceToken = function() {
	console.log('FirebaseObject.saveMessagingDeviceToken()');

	firebase.messaging().getToken().then(function(currentToken) {
		if (currentToken) {
			console.log('Got FCM device token: ', currentToken);
			// saving the device token to the datastore
			firebase.database().ref('/fcmTokens').child(currentToken)
				.set(firebase.auth().currentUser.uid);
		} else {
			// need to request permissions to show notifications
			this.requestNotificationsPermissions();
		}
	}.bind(this)).catch(function(error) {
		console.error('Unable to get messaging token: ', error);
	});
};

// requests permissions to show notifications
FirebaseObject.prototype.requestNotificationsPermissions = function() {
	console.log('FirebaseObject.requestNotificationsPermissions()');

	firebase.messaging().requestPermission().then(function() {
		// notification permission granted
		this.saveMessagingDeviceToken();
	}.bind(this)).catch(function(error) {
		console.error('Unable to get permission to notify: ', error);
	});
};

// resets the given MaterialTextfield
// ********************* resetMaterialTextfield ******************************
FirebaseObject.resetMaterialTextfield = function(element) {
	console.log('FirebaseObject.resetMaterialTextfield()');

	element.value = '';
	// wtf, is this???
	element.parentNode.MaterialTextfield.boundUpdateClassesHandler();
};

// template for messages
FirebaseObject.MESSAGE_TEMPLATE =
	'<div class="message-container">' +
		'<div class="spacing"><div class="pic"></div></div>' +
		'<div class="message"></div>' +
		'<div class="name"></div>' +
	'</div>';

// a loading image URL
FirebaseObject.LOADING_IMAGE_URL = 'https://www.google.com/images/spin-32.gif';

// displays a message in the UI
// ********************* displayMessage **************************************
FirebaseObject.prototype.displayMessage = function(key, name, text, picUrl, imageUri) {
	console.log('FirebaseObject.displayMessage()');

	var div = document.getElementById(key);
	// if an element for that message does not exists yet we create it
	if (!div) {
		var container = document.createElement('div');
		container.innerHTML = FirebaseObject.MESSAGE_TEMPLATE;
		div = container.firstChild;
		div.setAttribute('id', key);
		this.messageList.appendChild(div);
	}
	if (picUrl) {
		div.querySelector('.pic').style.backgroundImage = 'url(' + picUrl + ')';
	}
	div.querySelector('.name').textContent = name;
	var messageElement = div.querySelector('.message');
	if (text) { // if the message is text
		messageElement.textContent = text;
		// replace all line breaks by <br>
		messageElement.innerHTML = messageElement.innerHTML.replace(/\n/g, '<br>');
	} else if (imageUri) { // if the message is an image
		var image = document.createElement('img');
		image.addEventListener('load', function() {
			this.messageList.scrollTop = this.messageList.scrollHeight;
		}.bind(this));
		this.setImageUrl(imageUri, image);
		messageElement.innerHTML = '';
		messageElement.appendChild(image);
	}
	// show the card fading-in and scroll to the view the new message
	// wtf, is this???????????????????????????????????????????????????????????
	//setTimeOut(function() {div.classList.add('visible')}, 1);
	div.classList.add('visible');
	this.messageList.scrollTop = this.messageList.scrollHeight;
	this.messageInput.focus();
};

// enables or disables the submit button depending on the values of the input
// fields
// ********************* toggleButton ****************************************
FirebaseObject.prototype.toggleButton = function() {
	console.log('FirebaseObject.toggleButton()');

	if (this.messageInput.value) {
		this.submitButton.removeAttribute('disabled');
	} else {
		this.submitButton.setAttribute('disabled', true);
	}
};

// checks that the Firebase SDK has been correctly setup and configured
// ********************* checkSetup ******************************************
FirebaseObject.prototype.checkSetup = function() {
	console.log('FirebaseObject.checkSetup()');

	if (!window.firebase || !(firebase.app instanceof Function) || !firebase.app().options) {
		window.alert('Firebase SDK is not imported and configured.' +
			'Run this app with firebase serve on http://localhost:5000');
	}
};

// ********************* onload **********************************************
window.onload = function() {
  window.firebaseObject = new FirebaseObject();
};