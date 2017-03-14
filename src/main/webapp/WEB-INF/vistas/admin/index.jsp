<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="row section">
    <div class="col s12">
        <div class="row card-panel light-blue lighten-4 blue-text text-darken-4">
            <h5>Estadísticas</h5>
            <ul>
                <li><strong>Películas: </strong>${totalFilms}</li>
                <ul>
                    <li>Más vista: ${filmStats['masVista'].title}, ${filmStats['masVista'].views} reproducciones</li>
                    <li>Menos vista: ${filmStats['menosVista'].title}, ${filmStats['menosVista'].views} reproducciones</li>
                    <li>Mejor valorada: ${filmStats['mejorValorada'].title}, ${filmStats['mejorValorada'].score} estrellas</li>
                    <li>Peor valorada: ${filmStats['peorValorada'].title}, ${filmStats['peorValorada'].score} estrellas</li>
                </ul>
            </ul>
        </div>
    </div>
</div>
<div class="row">
    <div class="col s6">
        <h5>Últimas Películas</h5>
        <ul class="collection">
            <c:forEach items="${films}" var="film">
                <li class="collection-item avatar grey darken-3">
                    <img src="${path}/img/posters/${film.poster}" alt="" class="circle z-depth-3">
                    <span class="title">${film.title}</span>
                    <p>${film.year} - ${film.duration} min</p>
                    <a href="${path}/admin/pelicula/editar/${film.id}" class="secondary-content" title="Editar"><i class="material-icons white-text">mode_edit</i></a>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="col s6">
        <h5>Últimos usuarios</h5>
        <ul class="collection">
            <c:forEach items="${accounts}" var="account">
                <li class="collection-item avatar grey darken-3">
                    <span class="title">${account.userName}</span>
                    <p>${account.provincia} - ${account.email}</p>
                    <a href="${path}/admin/usuario/editar/${account.id}" class="secondary-content" title="Editar"><i class="material-icons white-text">mode_edit</i></a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>

<%@ include file="../_js.jsp"%>
<%@ include file="../_footer.jsp"%>