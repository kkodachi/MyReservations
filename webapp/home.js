/*
Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
*/
window.onload = function() {
	hideFav();
	forGuest();
	let rest = getParameterByName('rName');
	let lat = getParameterByName('lat');
	let lon = getParameterByName('lon');
	let filter = getParameterByName('filter');
	console.log("rest: "+rest);
	console.log("lat: "+lat);
	console.log("lon: "+lon);
	console.log("filter: "+filter);
	let latdoc = document.getElementById("lat");
	let londoc = document.getElementById("lon");
	let namedoc = document.getElementById("rName");
	latdoc.value = lat;
	londoc.value = lon;
	namedoc.value = rest;
	if (rest && lat && lon && filter){
		callYelp(rest,lat,lon,filter);
	}
}

/*
Google Maps API Documentation (27 lines, referenced), 26 Nov. 2023, https://developers.google.com/maps/documentation/geocoding/start
*/
let pin;

function displayMap(){
	document.getElementById("displaymap").addEventListener("click", function(){
		console.log("button clicked");
		document.getElementById("map").style.display = "block";
		const center = { lat: 34.0224, lng: -118.2851 };
		const map = new google.maps.Map(document.getElementById("map"), {
    		zoom: 4,
    		center: center,
  		});
		google.maps.event.addListener(map, "click", (event) => {
		    if (!pin) {
				pin = new google.maps.Marker({
	    			position: event.latLng,
	    			map: map,
		  		});
			} else {
	  			pin.setPosition(event.latLng);
			}
			let lat = document.getElementById("lat");
			let lon = document.getElementById("lon");
			lat.value = pin.getPosition().lat();
			lon.value = pin.getPosition().lng();
			document.getElementById("map").style.display = "none";
		});
	})
}


function hideFav(){
	document.getElementById("delfav").style.display = "none";
	document.getElementById("addfav").style.display = "none";
	document.getElementById("addres").style.display = "none";
	document.getElementById("revealres").style.display = "none";
}

/*
"how to url parameter in js" prompt (6 lines) ChatGPT 3 Aug. version, OpenAI, 24 Nov. 2023, chat.openai.com/chat.
*/
function getParameterByName(name, url) {
  if (!url) url = window.location.href;
  name = name.replace(/[\[\]]/g, '\\$&');
  var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
      results = regex.exec(url);
  if (!results) return null;
  if (!results[2]) return '';
  return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

function forGuest(){
	let toppage = document.getElementById("toppage");
	let topdisplay = "";
	if (!checkLogin()){
		console.log("user not logged in");
		topdisplay = `
			<a class="JoesTable" href="home.html">JoesTable!</a>
            <a class="topbar" href="home.html">Home</a>
            <a class="topbar" id="login" href="login.html">Login / Signup</a>
	  	`;
	} else {
		console.log("user logged in");
		topdisplay = `
			<a class="JoesTable" href="home.html">JoesTable!</a>
            <a class="topbar" href="home.html">Home</a>
		 	<a class="topbar" id="favorites" href="favorites.html">Favorites</a>
            <a class="topbar" id="reservations" href="reservations.html">Reservations</a>
            <a class="topbar" onclick="logout()" id="login" href="login.html">Logout</a>
	  	`;
	}
	toppage.innerHTML = topdisplay;
}


function logout() {
    console.log("in logout()");
    document.cookie = "userID=-1;";
    document.cookie = "username=bad;";
}

/*
"how to get the value of a cookie in HTML" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
*/
function checkLogin() {
	const cookies = document.cookie.split(';').map(cookie => cookie.trim());
    const userIDCookie = cookies.find(cookie => cookie.startsWith('userID='));
    const userIDval = userIDCookie.split("=")[1];
    console.log("userIDCookie: " + userIDval);
    if (userIDval === "-1"){
		return false;
	}
	return true;
}

/*
"replace syntax in js" prompt (1 line, referenced) ChatGPT 3 Aug. version, OpenAI, 1 Dec. 2023, chat.openai.com/chat.
*/
function replaceSpaces(rest) {
    return rest.replace(/ /g, '+');
}

function callYelp(rest,lat,lon,filter){
	let newrest = replaceSpaces(rest);
	console.log(newrest);
	const urlInfo = {
		url: "https://api.yelp.com/v3/businesses/search?latitude=" +lat+ "&longitude=" +lon+ "&term=" +newrest+ "&sort_by=" +filter+ "&limit=10",
		key: "insert-key-here"
	};
	
	console.log(JSON.stringify(urlInfo));
	
	fetch("RestaurantSearchServlet", {
		method: 'POST',
		headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(urlInfo)
	})
	.then(response => {
		if (response.ok) {
			return response.json();
		}
		else {
			throw new Error("Request failed");
		}
	})
	.then(data => {
		console.log("data:");
		console.log(data);
		let arr = getRestaurantData(data);
		console.log("arr:");
		console.log(arr);
		displayR(arr);
		/*
		"how to add click functionality to an image in js" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
		*/
		document.getElementById("results").addEventListener("click", function(event){
			if (event.target.classList.contains("image")) {
			        let restID = event.target.id;
			        let r = arr[restID];
			        console.log(restID, r);
			        clickImage(r);
			}
		})
	})
	.catch(err => console.error(err))
}


document.getElementById("searchbar").addEventListener("submit", function(event) {
	event.preventDefault();
    
    let rest = document.getElementById("rName").value;
    let lat = document.getElementById("lat").value;
    let lon = document.getElementById("lon").value;
    let filter = document.querySelector('input[name="filter"]:checked').id;
    let resultsfor = document.getElementById("resultsfor");
    resultsfor.innerHTML = `
		<p class="resultsfortxt"></p>
	  	`;
	let resultsfortxt = document.querySelector('.resultsfortxt');
	resultsfortxt.innerHTML = "Showing results for \""+rest+"\"";
	
    callYelp(rest,lat,lon,filter);
});

function getRestaurantData(data) {
	let restaurants = data["restaurants"];
	let r = {};
	/*
	"convert this json format to a better format to suppport image clicks" prompt (7 lines) ChatGPT 3 Aug. version, OpenAI, 24 Nov. 2023, chat.openai.com/chat.
	*/
	for (let i=0; i < restaurants.length; i++) {
		let rest = restaurants[i];
		let temp = {};
		temp["image"] = rest["image_url"];
		temp["rName"] = rest["name"];
		temp["addr"] = rest["location"]["display_address"].join(" ");
		temp["link"] = rest["url"].split('?')[0];
		temp["phone"] = rest["display_phone"];
		temp["cuisine"] = rest["categories"][0].title;
		temp["price"] = rest["price"];
		temp["rating"] = rest["rating"];
		r[i] = temp;
	}
	return r;
}

function displayR(restJson) {
	let homeimg = document.getElementById("homeimg");
	if (homeimg != null) {
		homeimg.remove();
	}
	
	let restaurantResults = document.getElementById("results"); // here
	if (restaurantResults != "") {
		restaurantResults.innerHTML = "";
	}
	/*
	"how to insert into an HTML document in js" prompt (15 lines, referenced) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
	Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
	*/
	for (let key in restJson) {
		let restaurant = restJson[key];

		let listrestaurants = document.getElementById("results");
		let restHtml = `
		<div class="restaurant">
			<div class="imgs">
		  		<a><img class="image" src=""></a>
	  		</div>
		  	<div class="restdetails">
		  		<p class="restname"></p>
				<p class="addr"></p>
				<a class="link"></a>
		  	</div>
		</div>
	  	`;
		listrestaurants.innerHTML += restHtml;
		
		let restarr = listrestaurants.getElementsByClassName("restaurant");

		let r = restarr[restarr.length - 1];

		r.querySelector(".image").id = key;
		r.querySelector(".image").src = restaurant["image"];
		r.querySelector(".restname").innerHTML = restaurant["rName"];
		r.querySelector(".addr").innerHTML = restaurant["addr"];
		r.querySelector(".link").href = restaurant["link"];
		r.querySelector(".link").innerHTML = restaurant["link"];
	}
}

function clickImage(restaurant) {
	let resultsfor = document.getElementById("resultsfor");
	if (resultsfor.innerHTML != "") {
		resultsfor.innerHTML = "";
	}
	
	let restaurantResults = document.getElementById("results");
	if (restaurantResults.innerHTML != "") {
		restaurantResults.innerHTML = "";
	}
	/*
	"how to insert into an HTML document in js" prompt (15 lines, referenced) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
	Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
	*/
	let rd = document.getElementById("resultdetails");
	let moredetails = `
		<p class="restname"></p>
		<div class="restaurant">
			<div class="imgs">
		  		<a><img class="image" src=""></a>
	  		</div>
		  	<div class="restdetails">
		  		<p class="addr"></p>
				<p class="phone"></p>
				<p class="cuisine"></p>
				<p class="price"></p>
				<p class="rating"></p>
		  	</div>
		</div>
	  	`;
	rd.innerHTML += moredetails;
	
	let resultarr = rd.getElementsByClassName("restaurant");
	let clickedresult = resultarr[0];
	
	clickedresult.querySelector(".image").src = restaurant["image"];
	clickedresult.querySelector(".image").parentElement.href = restaurant["link"]
	rd.getElementsByClassName("restname")[0].innerHTML = restaurant["rName"];
	clickedresult.querySelector(".addr").innerHTML = "Address: " + restaurant["addr"];
	clickedresult.querySelector(".phone").innerHTML = "Phone: " + restaurant["phone"];
	clickedresult.querySelector(".cuisine").innerHTML = "Cuisine: " + restaurant["cuisine"];
	clickedresult.querySelector(".price").innerHTML = "Price: " + restaurant["price"];
	//change to stars
	// clickedresult.querySelector(".rating").innerHTML = "Rating: " + restaurant["rating"];
	console.log("Rating: "+restaurant["rating"]+" : "+convertToStarRating(restaurant["rating"]));
	clickedresult.querySelector(".rating").innerHTML = "Rating: "+ convertToStarRating(restaurant["rating"]);
	
	/*
	Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
	"Using the Fetch API" Mozilla Documentation (referenced), 24 Nov. 2023, https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
	*/
	if (checkLogin()){
		console.log("user logged in");
		document.getElementById("revealres").style.display = "inline-block";
		checkFav(restaurant);
		
		document.getElementById("addfav").addEventListener("submit", function(event) {
			event.preventDefault();
			console.log("in addfav event");
			restaurant["action"] = "add";
			fetch("FavoritesServlet", {
				method: 'POST',
				headers: {
		            "Content-Type": "application/json",
		        },
		        body: JSON.stringify(restaurant)
			})
			.then(response => {
				if (response.ok) {
					return response.json();
				}
				else {
					throw new Error("Request failed");
				}
			})
			.then(data => {
				console.log(data);
				document.getElementById("delfav").style.display = "block";
				document.getElementById("addfav").style.display = "none";
			})
			.catch(err => console.error(err))
		});

		document.getElementById("delfav").addEventListener("submit", function(event) {
				event.preventDefault();
				console.log("in delfav event");
				restaurant["action"] = "del";
					fetch("FavoritesServlet", {
						method: 'POST',
						headers: {
							"Content-Type": "application/json",
					    },
					    body: JSON.stringify(restaurant)
					})
					.then(response => {
						if (response.ok) {
							return response.json();
						}
						else {
							throw new Error("Request failed");
						}
					})
					.then(data => {
						console.log(data);
						document.getElementById("delfav").style.display = "none";
						document.getElementById("addfav").style.display = "block";
					})
					.catch(err => console.error(err))
		});
		
		document.getElementById("addres").addEventListener("submit", function(event) {
			event.preventDefault();
			console.log("in addres event");
			restaurant["res_date"] = document.getElementById("res_date").value; // set from form filled
			restaurant["res_time"] = document.getElementById("res_time").value;
			fetch("ReservationsServlet", {
						method: 'POST',
						headers: {
							"Content-Type": "application/json",
					    },
					    body: JSON.stringify(restaurant)
					})
					.then(response => {
						if (response.ok) {
							return response.json();
						}
						else {
							throw new Error("Request failed");
						}
					})
					.then(data => {
						console.log(data);
						document.getElementById("addres").style.display = "none";
					})
					.catch(err => console.error(err))
		})
		
	
		
	}
}

function revealRes(){
	document.getElementById("addres").style.display = "inline-block";
}

/*
Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
"Using the Fetch API" Mozilla Documentation (referenced), 24 Nov. 2023, https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
*/
function checkFav(restaurant){
	restaurant["action"] = "check";
	fetch("FavoritesServlet", {
		method: 'POST',
		headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(restaurant)
	})
	.then(response => {
		if (response.ok) {
			return response.json();
		}
		else {
			throw new Error("Request failed");
		}
	})
	.then(data => {
		const fav = data.fav;
		console.log("fav: " + fav);
		if (fav === 'exists'){
			console.log("in first");
			document.getElementById("delfav").style.display = "block";
			document.getElementById("addfav").style.display = "none";
		} else {
			console.log("in second");
			document.getElementById("delfav").style.display = "none";
			document.getElementById("addfav").style.display = "block";
		}
	})
	.catch(err => {
		console.error(err)
		console.log("returning false in error");
		return false;
	})
}

/*
"convert double to a star rating" prompt (14 lines) ChatGPT 3 Aug. version, OpenAI, 1 Dec. 2023, chat.openai.com/chat.
*/
function convertToStarRating(rating) {
    const totalStars = 5;
    const fullStar = '★';
    const halfStar = '½';
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    let starRating = fullStar.repeat(fullStars);

    if (hasHalfStar) {
        starRating += halfStar;
    }
    const emptyStars = totalStars - starRating.length;
    if (emptyStars > 0) {
        starRating += '☆'.repeat(emptyStars);
    }
    return starRating;
}