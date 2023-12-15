/*
Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
"Using the Fetch API" Mozilla Documentation (referenced), 24 Nov. 2023, https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
*/
document.getElementById('Login').addEventListener('submit',function(event){
	event.preventDefault();
	const loginInfo = {
		username: document.getElementById('username').value,
		password: document.getElementById('password').value
	};
	fetch('LoginServlet', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		 body: JSON.stringify(loginInfo)
		 
	})
	.then(response => {
		if (response.ok){
			return response.json();
			
		} else {
			throw new Error('Request failed');
		}
	})
	.then(data => {
		const status = data.status;
		console.log(status);
		console.log(data.username);
		if (status === 'invalid'){
			var displayError = document.getElementById("loginerror");
			displayError.innerHTML = "Invalid username/password";
		} else {
			console.log("sending to home page");
			window.location.href = 'home.html';
		}
	})
	.catch(error => {
  	console.error('Error:', error);
	})
});

/*
Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
"Using the Fetch API" Mozilla Documentation (referenced), 24 Nov. 2023, https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
*/
document.getElementById('SignUp').addEventListener('submit',function(event){
	event.preventDefault();
	let p1 = document.getElementById('password1').value;
	let p2 = document.getElementById('password2').value;
	var passError = document.getElementById("signuperrorP");
	console.log("p1: "+p1);
	console.log("p2: "+p2);
	if (p1 !== p2){
		passError.innerHTML = "Passwords do not match";
		return;
	} else {
		passError.innerHTML = "";
	}
	const SignUpInfo = {
		username: document.getElementById('usernameS').value,
		password: p1,
		email: document.getElementById('email').value
	};
	fetch('SignUpServlet', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		 body: JSON.stringify(SignUpInfo)
		 
	})
	.then(response => {
		if (response.ok){
			return response.json();
			
		} else {
			throw new Error('Request failed');
		}
	})
	.then(data => {
		const uStatus = data.uStatus;
		const eStatus = data.eStatus;
		console.log(eStatus);
		console.log(uStatus);
		console.log(data.username);
		if (uStatus === 'invalid' || eStatus === 'invalid'){
			var usernameError = document.getElementById("signuperrorU");
			var emailError = document.getElementById("signuperrorE");
			if (uStatus === 'invalid'){
				usernameError.innerHTML = "There is already an account registered with that username";
			} else {
				usernameError.innerHTML = "";
			}
			if (eStatus === 'invalid'){
				emailError.innerHTML = "There is already an account registered with that email";
			} else {
				emailError.innerHTML = "";
			}
		} else {
			console.log("sending to home page");
			window.location.href = 'home.html';
		}
	})
	.catch(error => {
  	console.error('Error:', error);
	})
});
