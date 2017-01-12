var i_loc=[
"Eastern Market",
"Court House",
"White Flint",
"College Park-U of MD",
"Prince George's Plaza",
"Van Dorn Street",
"Pentagon City",
"Smithsonian",
"L'Enfant Plaza",
"Medical Center",
"Woodley Park-Zoo"
];
var fso,f;
function openFile(){
	fso = new ActiveXObject("Scripting.FileSystemObject");      
	f = fso.OpenTextFile("C:\\Users\\y\\Desktop\\start.txt",2,true);
}
function writeFile(i0,i1,i2){   
    f.WriteLine(i0+","+i1+","+i2);     
}   
function closeFile(){
	 f.Close();   
	 alert('write ok');  
}
var i_st=[];
function i_init(){
	d3.csv("js/test.csv",function(error,data){
	  		if(error){alert(error);}
	  		else{
		  		data.forEach(function(d){
		  			i_st.push({EntLat : d.EntLat , EntLng : d.EntLng , ExtLat : d.ExtLat , ExtLng:d.ExtLng});
		  		});
	  		}
	  	})
}
var clat = 38.9071923000,clng = -77.0368707000;
var mlat = 38.9906657000,mlng = -77.0260880000;
var md = (clat-mlat)*(clat-mlat)+(clng-mlng)*(clng-mlng);
function isCovered(d){
	if(d.EntLat*d.ExtLat <0 || d.EntLng*d.ExtLng <0){
		return false;
	}
	var std = (d.EntLat-d.ExtLat)*(d.EntLat-d.ExtLat)+(d.EntLng-d.ExtLng)*(d.EntLng-d.ExtLng);
	if(std>md){return false;}
	return true;
}


/*
//一段废弃的平滑处理算法,极坐标系
var rad = 1;
function getPath(d){
	if(isCoinside(d)){
		return [];
	}
	var midPoint = getMid(d);//{lat,lng,r}
	var k1 = getAngle(d.llat*1-midPoint.lat,d.llng*1-midPoint.lng),
		k2 = getAngle(d.rlat*1-midPoint.lat,d.rlng*1-midPoint.lng);
	var points=[];
	var k3 = k2;
	if(k1>k2){
		k2 = k1;
		k1=k3;
	}
	k3 = k2-k1;
	if(k3 < 180){
		for(k1 = k1+1;k1<k2;k1 += 1){
			points.push(getPoint(k1,midPoint));
		}	
	}else{
		for(k3 = k2;k3<=360;k3++){
			points.push(getPoint(k3,midPoint));
		}
		for(k3 = 0;k3<k1;k3++){
			points.push(getPoint(k3,midPoint));
		}
	}
	
	return points;
}

function isCoinside(d){
	return (d.llat*1 == d.rlat*1 && d.llng*1 == d.rlng*1);
}

function getPoint(k,midPoint){
	return {lat:midPoint.r*Math.cos(k)+midPoint.lat,lng:midPoint.r*Math.sin(k)+midPoint.lng};
}

function getMid(d){
	var l = Math.pow(d.llat*1-d.rlat*1,2)+Math.pow(d.llng*1-d.rlng*1,2);
	l = Math.pow(l,0.5);
	var lk = l/2*rad;
	var k = (d.llat*1-d.rlat*1)/(d.llng*1-d.rlng*1),k1 = k;
	k = Math.pow(k,2)+1;
	k = Math.pow(lk,2)/k;
	var x = Math.pow(k,0.5)+(d.llat*1+d.rlat*1)/2;
	var y = k1*(x-(d.llat*1+d.rlat*1)/2)+(d.llng*1+d.rlng*1)/2;
	var r  = Math.pow(Math.pow(l/2*l,2)+Math.pow(lk,2),0.5);
	
	return {lat:x,lng:y,r:r};
}

function getAngle(lat,lng){
	var angle;
	if(lat>0&&lng>0){
		angle = Math.atan2(lng,lat);
	}else if(lat >0 && lng < 0){
		angle = Math.atan2(-lng,lat);
		angle = 2*Math.PI-2*angle;
	}else if(lat<0&&lng>0){
		angle  = Math.atan2(lng,-lat);
		angle = Math.PI-angle;
	}else if(lat < 0 && lng <0){
		angle = Math.atan2(-lng,-lat);
		angle = Math.PI+angle;
	}
	
	return angle*180/Math.PI;
}
*/