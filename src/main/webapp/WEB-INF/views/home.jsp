<%@ include file="../inc/declarations.inc"%>
<tiles:insertDefinition name="adminLayout" flush="true">
    <tiles:putAttribute name="title" value="Smurf admin page" />
    
    <tiles:putAttribute name="htmlHeader" type="string">
        <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-1.9.1.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/bond.js"></script>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/bond.css"></link>
    </tiles:putAttribute>
    
    <tiles:putAttribute name="body" type="string">

		<p id="demo">A Paragraph</p>
        <button type="button" onclick="bondFunction()">Try it</button>
        
        
        <div id="myDiv"><h2>Let AJAX change this text</h2></div>
		<button type="button" onclick="loadXMLDoc()">Change Content</button>

    </tiles:putAttribute>
</tiles:insertDefinition>
