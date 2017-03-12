<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="row section">
    <div class="col s12">
        <h3>Catálogo de películas</h3>
    </div>
</div>
<div class="row">
    <div class="col s12">
        <ul class="collection">
            <c:forEach items="${films}" var="film">
                <li class="collection-item avatar grey darken-3">
                    <img src="${path}/img/${film.poster}" alt="" class="circle">
                    <span class="title">${film.title}</span>
                    <p>${film.year}<br>
                        ${film.duration} min
                    </p>
                    <a href="#!" class="secondary-content"><i class="material-icons">grade</i></a>
                    <a href="#!" class="secondary-content"><i class="material-icons">grade</i></a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>

<%@ include file="../_js.jsp"%>
<%@ include file="../_footer.jsp"%>