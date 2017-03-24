<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s10 offset-s1">
        <h4 class="center-align">Algo se ha roto</h4>
        <c:if test="${not empty errCode}">
            <h1 class="center-align">error ${errCode}</h1>
        </c:if>
    </div>
</div>
<c:if test="${exceptionMsg != null}">
<div class="row section">
    <div class="col s10 offset-s1">
        <div class="card-panel red lighten-4">
            <span class="red-text text-darken-4">${exceptionMsg}</span>
        </div>
    </div>
</div>
</c:if>

<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>