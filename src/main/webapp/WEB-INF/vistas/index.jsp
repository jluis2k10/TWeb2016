<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<h1>Mi portada</h1>
<i class="material-icons">face</i>
<a href="${path}/pelicula/1/Logan" title="Logan">Logan</a>

<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>