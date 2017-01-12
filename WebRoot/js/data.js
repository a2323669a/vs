window.onload = load;
function load(){
	document.getElementById("iframe").src = "https://index.1688.com/alizs/purchaser.htm?userType=supplier&cat=123614002";
	document.getElementById("iframe").contentWindow.onload = test;
}

function test(){
	alert();
}