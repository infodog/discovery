<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
			
			.highlightStyle { color:red; }
</style>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Search ${keyword}</title>
</head>
<body>
	
<table border="0">
  <p>搜索：<span style="color:red">${keyword}</span>	 的总结果数为：${total}</p>
	
  <c:if test="${suggestions != null && fn:length(suggestions) > 0}" >  
	<p>相关搜索：
	
	<c:forEach items="${suggestions}" var="item">
		${item}
		&nbsp;&nbsp;
	</c:forEach>
	</p>
</c:if> 

	
  <c:if test="${spellcheck != null}" >  
	<p>您是不是搜索：<span style="color:red">${spellcheck.name}</span></p>
</c:if> 
  
  <c:forEach items="${items}" var="item">
  	<tr>
   	 	<td><a href="${item.url}"> ${item.title} </a></td>
	</tr>
	<tr>
		<td>${item.url}</td>
	</tr>
	<tr>
		<td><br></td>
	</tr>
	
  </c:forEach>

<br>

    <c:forEach items="${facets}" var="facet">
        <tr>
            <td> ${facet.name} </td>
        </tr>
        <tr>
            <td>${facet.value}</td>
        </tr>
        <tr>
            <td><br></td>
        </tr>

    </c:forEach>


    <c:forEach items="${merchantFacets}" var="facet">
        <tr>
            <td> ${facet.name} </td>
        </tr>
        <tr>
            <td>${facet.value}</td>
        </tr>
        <tr>
            <td><br></td>
        </tr>

    </c:forEach>


</table>

</body>
</html>