/* local bond js */

function bondFunction() {
    document.getElementById("demo").innerHTML="My First JavaScript Function";
}

function loadXMLDoc()
{
	var url = "/bond/public/api/v1/heartbeat/self"
	$.ajax({
	    url : url,                          
	    type: "get",                   
	    dataType:"json",                   
	    success : function(data) {  
	        console.log(data);
	    }
	});
}