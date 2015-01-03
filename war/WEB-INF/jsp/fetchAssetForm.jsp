<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<title><fmt:message key="title"/></title>
	<style>
		.error { 
			color: red; 
		}
		SELECT, INPUT[type="text"] {
			width: 160px;
			box-sizing: border-box;
		}
		SECTION {
			padding: 8px;
			background-color: #f0f0f0;
			overflow: auto;
		}
		SECTION > DIV {
			float: left;
			padding: 4px;
		}
		SECTION > DIV + DIV {
			width: 40px;
			text-align: center;
		}
	</style> 
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script type="text/javascript">
	
		function assetSelect() {
		    var selectedItem = $("#components option:selected").val();
		    var alreadySelectedSym = $("#symbol").val();
		    alreadySelectedSym = alreadySelectedSym + "," + selectedItem;
		    $('#symbol').val(alreadySelectedSym);
		}
	
		function fetchComponents() {
			var val = $('#assetIndex').val();
			$.ajax({
				url : 'fetchIndex.htm',
				method : 'post',
				ContentType : 'json',
				data : {
					index : val
				},
				success : function(response) {
					var options = '';
					if (response != null) {
						var data = response;
						data = data.replace('{', '');
						data = data.replace('}', '');
						var arr = data.split(',');
						var create = "";
						for(var i = 0; i < arr.length;i++){
							var element = arr[i];
							element = element.replace(' ', '');
							var elementSplit = element.split('=')
							create += '<option value="'+elementSplit[0]+'">'+elementSplit[1]+'</option>';
						}
						create += '</select>';
						jQuery('#components').html(create);
					}
				}
			});
		}
		
		function validateSubmit(){
			var val = $('#symbol').val();
			val = val.trim();
			var splitResult = val.split(",");
			var count=0;
			for(i = 0; i < splitResult.length; i++){
				if(splitResult[i].trim() != ""){
					count=count+1;
				}
			}
			if(count == 0 || count == 1){
				alert("Please Select More Than One Assets");
				return false;
			}
			else
				return true;
		}
 </script> 
</head>
<body>
<h1><fmt:message key="priceincrease.heading"/></h1>

<%-- <form:form method="POST" modelAttribute="indexes">
<select id="assetIndex" onchange="fetchComponents();">
     <c:forEach items="${indexes.marketIndexes}" var="item">
       <option>${indexes.marketIndexes}</option>
     </c:forEach>
   </select> --%>
<table width="95%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
    <tr>
      <td align="right" width="20%">Please Select an Index</td>
      <td>
      <%-- <form:select path="index" onchange="this.form.submit()">
		<form:option value="NONE" label="--- Select ---"/>
		<form:options items="${indexes.indexes}" />
	  </form:select> --%>
	  
	  
	  <form:form method="POST" modelAttribute="indexes">
		<form:select id="assetIndex" onchange="fetchComponents();" path="index">
			<form:option value="NONE" label="--- Select ---"/>
			<form:options items="${indexes.marketIndexes}" />
		</form:select>
		</form:form>
      </td>
      </tr>
      </table>
      <section class="container">
    <div>
        <select id="components" size="5" ></select>
    </div>
    <div>
        <input type="button" id="btR" value="&gt;&gt;" onclick="assetSelect();"/>
    </div>
    <div>Asset List:
    <form:form method="POST" modelAttribute="assets" ><!-- onsubmit="return validateSubmit();" -->
    <form:textarea id="symbol" path="symbol"/>
    <input type="submit" align="center" value="Execute">
</form:form>
        <!-- <select id="rightValues" size="4" multiple>
            <option>1</option>
            <option>2</option>
            <option>3</option>
        </select> -->
    </div>
</section>
      
      

<%--   <table width="95%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
      <tr>
      <td align="right" width="20%">Asset List:</td>
        <td width="20%">
          <form:input path="symbol"/>
        </td>
        <td width="60%">
          <form:errors path="symbol" cssClass="error"/>
        </td>
    </tr>
  </table>
  <br>
  <input type="submit" align="center" value="Execute"> --%>
</body>
</html>