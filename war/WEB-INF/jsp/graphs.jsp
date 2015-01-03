<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@taglib uri="http://cewolf.sourceforge.net/taglib/cewolf.tld" prefix="cewolf" %>
<html>
  <head><title><fmt:message key="title"/></title></head>
  <body>
  <%-- <c:out value="${assets.Portfolios}"/> --%>
  <%-- <c:forEach items="${assets.Portfolios}" var="portfolio">
      <c:out value="${portfolio.portfolioOneYrReturn}"/> 
   </c:forEach> --%>
<cewolf:chart id="scatterPlot" title="Markowitz Efficient Frontier" type="scatter" xaxislabel="% Risk(Standard Deviation)" yaxislabel="% Returns">
    <cewolf:data>
        <cewolf:producer id="graph" />
    </cewolf:data>
</cewolf:chart>
<cewolf:img chartid="scatterPlot" renderer="/cewolf" width="800" height="600" />
<br><br>
The above frontier is calculated based on data obtained from <fmt:formatDate value="${assets.Assets[0].fromDate.time}" type="date" dateStyle="long" />
to <fmt:formatDate value="${assets.Assets[0].toDate.time}" type="date" dateStyle="long" />. <br>
The 1 Year, 3 Year and 5 Year Returns are calculated assuming the investment is performed on <fmt:formatDate value="${assets.Assets[0].toDate.time}" type="date" dateStyle="long" />.
<br>
All the Assets are converted to a common EUR Currency.
<br>
<br> 
<div>
<table  border="1">
	<tr>
		<td>Portfolio Number</td>
		<td>Portfolio Distribution</td>
		<td>Portfolio Expected Return</td>
		<td>Portfolio Standard Deviation</td>
		<td>One Yr Return</td>
		<td>Three Yr Return</td>
		<td>Five Yr Return</td>
	</tr>
<c:forEach items="${assets.Portfolios}" var="portfolio" varStatus="theCount">
	<tr>
		<td>${theCount.index + 1}</td>
		<td>${portfolio.weights}</td>
		<td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${portfolio.portfolioExpectedReturn}"/></td>
		<td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${portfolio.portfolioStandardDev}"/></td>
		<td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${portfolio.portfolioOneYrReturn}"/></td>
		<td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${portfolio.portfolioThreeYrReturn}"/></td>
		<td><fmt:formatNumber type="percent" maxFractionDigits="2" value="${portfolio.portfolioFiveYrReturn}"/></td>
	</tr>
</c:forEach>
</table>
</div>
</body>
</html>