<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta content="IE=100" http-equiv="X-UA-Compatible">
<link rel="shortcut icon" href="../images/favicon.ico" />
<link type="text/css" href="../css/ss.css" rel="stylesheet">
</head>
<body>
    <div id="commanContainer">
		<img src="../images/Accounter_logo_title.png" class="accounterLogo" />
		<c:if test="${message != null}">
		   <div class="common-box create-company-message">${message}</div>
		</c:if>
		<form id = "maintananceForm" method="post" action="/maintanance">
			<div class="text_box_margin">
				<label>Please enter your admin password:
				<input type = "Password"  id = "adminPassword"  name ="password" >
			</div>
			<input type="checkbox" name="option1" ><label>Server under maintainace</label> <br>
			<div class="OkButton">
			    <input type = "Submit" class="allviews-common-button" value = "Submit " id = "submitButton"  >
			</div>
		</form>
	</div>
</body>
</html>