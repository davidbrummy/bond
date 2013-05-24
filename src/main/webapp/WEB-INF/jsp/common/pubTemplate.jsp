<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- exposes tiles attributes as '${}' accessible beans --%>
<tiles:importAttribute />

<html>
<head>
<title>${title}</title>

<link href="<%=request.getContextPath()%>/resources/icons/favicon.ico" rel="shortcut icon" type="image/x-icon" >
<link href="<%=request.getContextPath()%>/resources/icons/favicon.ico" rel="icon" type="image/x-icon" >
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
<style type="text/css">

body {
/*     background: none repeat scroll 0 0 #000044; */
    background: none #482eda;
    color: white;
}

.textLarge {
    font-family: Roboto,verdana,arial,sans-serif;
    font-size: 40pt;
}

.textPlain {
    font-family: Roboto,verdana,arial,sans-serif;
    font-size: 24pt;
}

.textSmall {
    font-family: Roboto,verdana,arial,sans-serif;
    font-size: 18pt;
}

a:link {color: #bbaaff;}
a:visited {color:#aa99ee;}
a:hover {color: #bbaaff;}
</style>

${htmlHeader}

</head>

<body>
	${body}

    <tiles:insertAttribute name="pubFooter" />
</body>
</html>
