
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>Webservice HomePage</title>
<style>
#footer div a {
	color: white;
}

#footer div a.focus {
	color: white;
}

#footer div {
	background-color: #337ab7;
}

/* #b1 div { */
/* 	background-color: #aaaaaa; */
/* } */


#footer .row {
	margin-right: 0px;
}
</style>
<!-- Bootstrap Core CSS -->
<link
	href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.min.css"
	rel="stylesheet">

<!-- MetisMenu CSS -->
<link
	href="//cdnjs.cloudflare.com/ajax/libs/metisMenu/2.7.0/metisMenu.css"
	rel="stylesheet">

<!-- Custom CSS -->
<!-- <link href="resources/dist/css/sb-admin-2.css" rel="stylesheet"> -->
<link
	href="//cdnjs.cloudflare.com/ajax/libs/startbootstrap-sb-admin-2/3.3.7/css/sb-admin-2.min.css"
	rel="stylesheet">



<link
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
	rel="stylesheet">
</head>

<body>
	<c:url value="/resources/images/iocl.png" var="iocllogo1" />
	<c:url value="/resources/images/ioclandscape.png" var="iocllogo2" />
	<div id="wrapper">
		<div id="header" style="background-color: white;">

			<nav class="navbar navbar-static-top ep-main-header"
				style="border: 0px; background-color: #fefefe; min-height: 60px"
				role="navigation">
				<div style="border: 0px solid red" class="container-fluid">
					<div  class="navbar-header pull-left">

						<!-- <a class="navbar-brand goi-logo" href="#"><img
              src="images/emblem4.png" /> </a> -->
						<!-- <img src="images/logo82.png" />
              <img id="imageLogo" src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"/> -->
						<img src="${iocllogo1}" alt="IOCL Logo" style="height: 60px;">

						<!--<a class="navbar-brand" href="#" id="ePramaantext">KRDH
              <small style="color:#a7411b;">A National e-Authentication Service</small>
            </a> -->

					</div>
					<div class="navbar-header pull-right">
					<img src="${iocllogo2}" alt="IOCL Logo" style="height: 60px;">
					</div>


				</div>
				<!-- /.container -->
			</nav>


			<div class="container-fluid"
				style="padding-left: 0px; padding-right: 0px;">
				<hr
					style="display: block; border: 0; border-top: 5px solid #3e76b9; padding: 0; margin-top: 5px; margin-bottom: 5px;">
			</div>
		</div>
<script
	src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>


		<!--     
Header end

-->

		<!--     	body begin -->

<div class="container">

		<div class="row" id="b1" style="border: 0px solid red;height: 250px" >
			<div style="border: 0px solid red" class="col-md-4">
<!-- 			first -->
			<c:url value="/resources/images/1.png" var="iocllogo" />
			
<%-- 			<img src="${iocllogo}" alt="IOCL Logo" style="padding: 1px 1px 1px 1px; height: 250px "> --%>
			
			</div>
			<div style="border: 0px solid red" class="col-md-4">
			
<!-- 			second -->
			<c:url value="/resources/images/iocl_uid.png" var="iocllogo3" />
			  <img src="${iocllogo3}" alt="IOCL Logo" style="padding: 1px 1px 1px 1px;height: 250px"> 
			
			</div>
			<div style="border: 0px solid red" class="col-md-4">
<!-- 			third -->
  <label style="">Server Time :</label>
<p id="st" ></p> 

			<c:url value="/resources/images/2.png" var="iocllogo" />
<%-- 			<img src="${iocllogo}" alt="IOCL Logo" style="padding: 1px 1px 1px 1px; height: 250px"> --%>
			</div>
			
		</div>
		</div>
		
	
<!-- <div class="container"> -->
<div class="row">
  <div class="jumbotron">
    <h1 class="text-center">IOCL ASA Webservice</h1>      
<!--     <p>Bootstrap is the most popular HTML, CSS, and JS framework for developing responsive, mobile-first projects on the web.</p> -->
  </div>
     
</div>
	</div>	
<!-- </div> -->


		<!-- body end -->


		<!-- footer begin -->
<script >
$(document).ready(function(){
var jsVar= "${serverTime}";
//alert(jsVar);
var d = new Date(parseInt(jsVar));
//alert(d);
document.getElementById("st").innerHTML = d;
//alert(d);
//alert(new Date(parseInt(jsVar)+10000));

var updateTime=parseInt(jsVar);
function refreshDate()
{
	//updateTime=jsVar
	var d = new Date(parseInt(updateTime)+1000);
	//alert(d);
	updateTime=updateTime+1000;

	document.getElementById("st").innerHTML = d;
	
	}


setInterval(refreshDate, 1000);
});


</script>

	<div id="footer">
		<div class="row">

			

			<div class="col-md-3 text-center"></div>

		</div>



		<div class="row" style="padding-top: 5px">
			<div class="col-md-2  text-center">
			</div>
			<div class="col-md-8 text-left text-center" style="height: 50px">
				<p style="color: white;">Copyright © 2020 C-DAC Mumbai. All
					Rights Reserved.</p>

			</div>
			<div class="col-md-2  text-center ">
				<!-- 				<a>Last updated on : 23/03/2017</a> -->
			</div>
		</div>
	</div>


</body>
</html>
<!-- footer end -->






