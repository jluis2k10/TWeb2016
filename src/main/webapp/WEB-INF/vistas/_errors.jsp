<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<h1 class="center-align">Algo se ha roto</h1>
<c:if test="${errors != null}">
    <div class="row section">
        <div class="col s10 offset-s1">
            <div class="card-panel red lighten-4">
                <span class="red-text text-darken-4">${errors}</span>
            </div>
        </div>
    </div>
    <p>${errors}</p>
</c:if>

<a href="${path}/pelicula/1/Logan" title="Logan">Logan</a>

<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>