<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- exposes tiles attributes as '${}' accessible beans --%>
<tiles:importAttribute />

<!DOCTYPE html>
<html>
  <head>
    <title>${title}</title>

    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">

    <meta name="viewport" content="width=device-width, initial-scale=1">

	<script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery.mobile-1.3.1.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/jquery.mobile-1.3.1.css"></link>

  </head>

  <body>
    <div data-role="page">

	  <div data-role="header">
	    <h1>${title}</h1>
	  </div><!-- /header -->

	  <div data-role="content">
        <p>${content}</p>
	  </div><!-- /content -->

	  <div data-role="footer">
        <h4>${footer}</h4>
	  </div><!-- /footer -->
    </div><!-- /page -->
  </body>
</html>
