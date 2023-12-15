/*
Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
*/
window.addEventListener('load', function() {
	hide();
	// document.getElementById("sorttype").style.display = "none";
	forGuest();
    fetchRes('none');
    hide();
    console.log('Page and all resources have finished loading');
});

/*
Google Maps API Documentation (27 lines, referenced), 26 Nov. 2023, https://developers.google.com/maps/documentation/geocoding/start
*/
let pin;

function displayMap(){
	console.log("initmap");
	document.getElementById("displaymap").addEventListener("click", function(){
		console.log("button clicked");
		document.getElementById("map").style.display = "block";
		const center = { lat: 34.0224, lng: -118.2851 };
		const map = new google.maps.Map(document.getElementById("map"), {
    		zoom: 5,
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



function hide(){
	document.getElementById("delfav").style.display = "none";
	document.getElementById("addfav").style.display = "none";
	document.getElementById("addres").style.display = "none";
	document.getElementById("revealres").style.display = "none";
}

/*
"how to get the value of a cookie in HTML" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
*/
function forGuest(){
	const cookies = document.cookie.split(';').map(cookie => cookie.trim());
    const usernameCookie = cookies.find(cookie => cookie.startsWith('username='));
    console.log("usernameCookie: " + usernameCookie);
    let user = document.getElementById("user");
    user.innerHTML = `
	<p class="usertxt"></p>
	  `;
	let usertxt = document.querySelector('.usertxt');
	const usertxtdisplay = usernameCookie.split("=")[1];
	usertxt.innerHTML = usertxtdisplay+"'s Reservations:";
}

function logout() {
    console.log("in logout()");
    document.cookie = "userID=-1;";
    document.cookie = "username=bad;";
}

/*
"Using the Fetch API" Mozilla Documentation (referenced), 24 Nov. 2023, https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
*/
function fetchRes(action) {
    fetch("ReservationsServlet?action="+action, {
		method: 'GET',
		headers: {
            "Content-Type": "application/json",
        },
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
		let listres = document.getElementById("listres");
		if (listres != "") {
			listres.innerHTML = "";
		}
		let jsondata = getRestaurantData(data);
		console.log("data:");
        console.log(data);
        console.log("arr:");
        console.log(jsondata);
        displayRes(jsondata);
        /*
		"how to add click functionality to an image in js" prompt (5 lines) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
		*/
        document.getElementById("listres").addEventListener("click", function(event){
			if (event.target.classList.contains("image")) {
			        let restID = event.target.id;
			        let r = jsondata[restID];
			        console.log("restID: "+restID);
			        console.log("r: "+r);
			        clickImage(r);
			}
		})
    })
    .catch(err => console.error(err));
}


function getRestaurantData(data) {
	let restaurants = data["restaurants"];
	let r = {};
	for (let i=0; i < restaurants.length; i++) {
		r[i] =  restaurants[i];
	}
	return r;
}

function displayRes(restJson) {
	for (key in restJson) {
		let restaurant = restJson[key];
		let listres = document.getElementById("listres");
		/*
		"how to insert into an HTML document in js" prompt (15 lines, referenced) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
		Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
		*/
		let restHTML = `
		<div class="restaurant">
			<div class="imgs">
		  		<img class="image" src="">
	  		</div>
		  	<div class="restaurant-info">
		  		<p class="restname"></p>
				<p class="addr"></p>
				<p class="d"></p>
				<p class="t"></p>
		  	</div>
		</div>
	  	`;
		
		listres.innerHTML += restHTML;
		
		let resarr = listres.getElementsByClassName("restaurant");
		let r = resarr[resarr.length - 1];

		r.querySelector(".image").id = key;
		r.querySelector(".image").src = restaurant["image"];
		r.querySelector(".restname").innerHTML = restaurant["rName"];
		r.querySelector(".addr").innerHTML = restaurant["addr"];
		r.querySelector(".d").innerHTML = "Date: "+restaurant["date"];
		r.querySelector(".t").innerHTML = "Time: "+restaurant["time"];
	}
}

function clickImage(restaurant) {
	console.log("here");
	let reservations = document.getElementById("listres");
	let rd = document.getElementById("resdetails");
	
	if (reservations.innerHTML != "") {
		reservations.innerHTML = "";
	}
	/*
	"how to insert into an HTML document in js" prompt (15 lines, referenced) ChatGPT 3 Aug. version, OpenAI, 26 Nov. 2023, chat.openai.com/chat.
	Javascript Tutorial (referenced), 26 Nov. 2023, https://www.w3schools.com/js/
	*/
	let moredetails = `
		<p class="restname"></p>
		<div class="restaurant">
			<div class="imgs">
		  		<a><img class="image" src=""></a>
	  		</div>
		  	<div class="restaurant-info">
		  		<p class="addr"></p>
				<p class="phone"></p>
				<p class="cuisine"></p>
				<p class="price"></p>
				<p class="rating"></p>
		  	</div>
		</div>
	  	`;
	rd.innerHTML += moredetails;
	
	let resarr = rd.getElementsByClassName("restaurant");
	let clickedresult = resarr[0];
	
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
	// if (checkLogin()){
		console.log("user logged in");
		document.getElementById("revealres").style.display = "block";
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
			restaurant["res_date"] = document.getElementById("res_date").value;
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
	// }
}

function revealRes(){
	document.getElementById("addres").style.display = "block";
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