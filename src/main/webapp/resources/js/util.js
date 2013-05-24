function safeLog(dbg) {
	if(console && console.log) {
		console.log(dbg);
	}
	var errorLog = $("errorLog");
	if(errorLog) {
		var text = errorLog.value + dbg + "\n";
		errorLog.value = text;
	}
}

// timestamp must be in correct format, strict so server can deal with it.
function getTimestamp(dayOffset) {
	//(yyyyMMdd'T'HHmmssZ).
	//get the js date, turn it into UTC, split it up, reformat it like so YYYYMMDDT120000Z
	var months_name = new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
	var months_leadingzero = new Array("01","02","03","04","05","06","07","08","09","10","11","12");
	var d = new Date();
	d.setDate(d.getDate()+dayOffset);
	
	var utc = (d.toUTCString());
	var utc_array = utc.split(" ");
	var date = utc_array[1];
	var utc_month = utc_array[2];
	var year = utc_array[3];
	var tt = utc_array[4].split(":")
	var time = tt[0]+tt[1]+tt[2];

	var month = "00";

	for (var i=0;i<=11;i++) {
		if (utc_month == months_name[i]){
			month = months_leadingzero[i];
		}
	}

	var ts = year+month+date+"T"+time+"Z";
	return ts;
}

// construct the signature from timestamp and secret, then hash it.
function createSig(ts,apiSecret) {
	var plain = ts+"."+apiSecret;
	var as = hex_sha256(plain);
	return as;
}


// use info object to decide how to make call
function executeApiCall(info, onComplete) {
	if(!info) {
		alert("no call info provided");
		return
	}
	if(!info.method||!info.requestType) {
		alert("error creating request! no method found.");
		return;
	}

	var urlToSubmit = info.server + info.method;
	
	//then do the ajax stuff
	var parameters = "";
	var postBody;
	if(info.requestType=="GET") {
		parameters = encodeURIComponent(info.requestParams);
	} else if(info.requestType=="POST")
		postBody = info.requestParams

	var requestHeaders={};
	// nosig option is to try calls without any signature, good for open public calls that should be cacheable.
	if(!info.nosig) {
		// for testing, we allow the signature to be off by a day to see the error.
		var ts = getTimestamp(info.stale?-1:0); 
		requestHeaders["X-API-Key"]=info.ak;
		requestHeaders["X-API-Timestamp"]=ts;
		requestHeaders["X-API-Signature"]=createSig(ts,info.as);
	}
	
	// Authentication is a pair inqcloud / auth token
	if(info.auth)
		requestHeaders["Authentication"] = info.auth;
	
	// make the ajax request with proper content type, and body / parameters based on GET / POST
	return new Ajax.Request(urlToSubmit, {
		method:info.requestType.toLowerCase(),
		parameters:parameters,
		postBody:postBody,
		requestHeaders:requestHeaders,
		contentType:"application/json",
		onComplete:onComplete,
		onException:function ajaxException( obj, e ) {
			alert( "Exception during AJAX request: " + e );
		}
	});
}


function callInfo() {
	var info={};
	info.as=$F($("as"));
	info.ak=$F($("ak"));
	info.stale=formIsChecked("stale");
	info.nosig=formIsChecked("nosig");
	// protocol should match the page's protocol, for localhost
	info.server=$F($("server"));
	if (!info.server.endsWith('/')) 
		info.server=info.server+"/";

	if (!(info.server.indexOf("http://") == 0 || info.server.indexOf("https://") == 0)) 
		info.server = "http://" + info.server;
	
	// Always match the protocol to the page itself. 
	var protocol = window.location.href.split("//")[0];
	var serverSplit = info.server.split("//");
	serverSplit[0] = protocol;
	info.server = serverSplit.join("//");

	var methodData=$F($("method")).split(":"); // abc/def:POST
	info.method=methodData[0];
	info.requestType=methodData[1];
	info.auth=$F($("auth"));
	info.requestParams=$F($("requestParams"));

	return info;
}


function getTheResponse() {
	if(!$F($("method"))) setMethod();
	var info = callInfo();
	
	var jsonResultNode =$('jsonResults');
	var durationNode = $("callDuration");
	jsonResultNode.innerHTML = "started";
	durationNode.innerHTML = "...";
	
	var startDate = new Date();
	function callDone() {
		var endDate = new Date();
		var duration = endDate.getTime()-startDate.getTime();
		if (durationNode)
			durationNode.innerHTML = duration;
	}
	
	executeApiCall(info, 
		function requestDone(response) {
			callDone();
			if (response.status==200) {
				var bareResponse = response.responseText;
				if( !bareResponse || bareResponse=="")
					bareResponse = 'error:"server sent no response or error response!"';
				jsonResultNode.innerHTML = bareResponse;
			} else {
				jsonResultNode.innerHTML = "Failure to execute api call. http status " + response.status + " text " + response.statusText + "<br/>" + parseErrorPage(response.responseText);
			}
		}
	);
}

// if it's an error page, we can try to discet the tomcat page, find useful bits.
function parseErrorPage(result) {
	// try to get the error by parsing the tomcat error page.
	var prefix = "<b>description</b> <u>";
	var pos = result.indexOf(prefix);
	var output="";
	if (pos >= 0) 
		output = result.substring( pos + prefix.length, result.indexOf("</u>", pos));
	return output;
}

// call the users/token function which will return an inqcloud access token for facebook or foursquare tokens, if they validate.
function convertToInqcloudAuth() {
	var info=callInfo();
	info.method="users/token";
	info.requestType="GET";
	info.requestParams="";
	info.stale=false;
	info.nosig=false;

	executeApiCall(info, 
		function updateAuth(response ) {
			if (response.status == 200) {
				var bareResponse = response.responseText;
				try {
					var result = response.responseJSON;
					// check result code, etc
					if(result && result.meta && result.meta.code == 200) {
						if(result.response && result.response.token) {
							var newToken = "oauth2 inqcloud " + result.response.token;
							safeLog("new computed token" + newToken);
							formItem("auth").value = newToken;
						} else {
							safeLog("Expected resonse and token, found " + bareResponse);
						}
					} else {
						var result = result && result.meta ? ( "code " + result.meta.code + " message " + result.meta.errorMessage )
								: "malformed reponse";
						safeLog("expected result with meta of 200: got " + result + " \ "+ bareResponse);
					}
				} catch ( e ) {
					safeLog("exception parsing response to login: " + e );
				}
			} else {
				safeLog("Login call failed with status code: " + response.status + " text " + response.statusText + "\n " + parseErrorPage(response.responseText));
			}
		}
	);
}

/* page bookkeeping stuff - don't confuse it with the acutal call logic */
function qs() {
	var qsParam = new Array();
	var query = window.location.search.substring(1);
	var parms = query.split('&');
	for (var i = 0; i < parms.length; i++) {
		var pos = parms[i].indexOf('=');
		if (pos > 0) {
			var key = parms[i].substring(0, pos);
			var val = parms[i].substring(pos + 1);
			qsParam[key] = val;
		}
	}
	return qsParam;
}

function formItem(key) {
	var item = $("myform")[key];
	if(!item)
		safeLog("form input not found: " + key);
	return item;
}

function init() {
	//TODO: all this should be done via the .jsp at some point.  Not here!
	var qsParam = qs();
	function setFormValue(key) {
		var value = qsParam[key];
		var item = formItem(key);
		if(item && value)
			item.value = decodeURIComponent(value);
	}
	//set the values in the page according to the parms
	
	setFormValue("ak");
	setFormValue("as");
	setFormValue("requestParams");
	if (qsParam['server']) { formItem("server").value = decodeURIComponent(qsParam["server"]) }
	else {
		var windowLoc = window.location;
		var pathBase = "";
		if (windowLoc.pathname.indexOf("/inqcloud")==0) pathBase = "/inqcloud";
		formItem("server").value = windowLoc.protocol + '//' + windowLoc.host + pathBase;
	}

	if (qsParam['method'])
		formItem("method").value = qsParam["method"] + ":" + qsParam["requestType"];

	setChecked("stale", (qsParam['stale'] == "true"));
	setChecked("nosig", (qsParam['nosig'] == "true"));
}

function setMethod() {
	formItem("method").value = $F($("methodName"));
}

function formIsChecked(key) {
	var item = formItem(key);
	if(item)
		return item.checked;
	return null;
}

function setChecked(key, newValue) {
	var checkedButton = formItem(key);
	if(checkedButton) checkedButton.checked = (newValue== true || newValue=="true");
}


function buildUrl() {
	var info = callInfo();
	var where = window.location.href.split('?');
	var urlStart = where[0];
	var components=[];
	if(info.method) components.push("method=" + info.method);
	if(info.requestType)
		components.push("requestType=" + info.requestType);
	if(info.ak)
		components.push("ak=" + info.ak);
	if(info.as)
		components.push("as=" + info.as);
	if(info.requestParams)
		components.push("requestParams=" + encodeURIComponent(info.requestParams));
	if(info.server)
		components.push("server=" + encodeURIComponent(info.server));
	if(info.nosig)
		components.push("nosig=" + info.nosig);
	if(info.stale)
		components.push("stale=" + info.stale);
	
	var testpageURL = urlStart;
	if(components.length>0) {
		testpageURL = urlStart + "?" +
		components.join("&");
	}
	$('testpageURL').innerHTML = "<a href='" + testpageURL + "'>" + testpageURL + "</a>";
}