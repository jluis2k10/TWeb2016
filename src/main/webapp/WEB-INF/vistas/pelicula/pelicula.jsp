<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="../_header.jsp"%>

<div class="section row">
    <div class="col s7">
        <div class="row">
            <div class="col s12">
                <h4>${film.title}</h4>
            </div>
            <div class="col s12 film-badges">
                <span class="badge">${film.duration} min</span>
                <span class="badge">${film.year}</span>
                <span class="badge">${film.rating}</span>
            </div>
        </div>
        <div class="row">
            <div class="col s12">
                <p>${film.description}</p>
            </div>
            <div class="col s12 film-info">
                <dl>
                    <dt>Directores</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmDirectors}" var="director" varStatus="loopStatus">
                            <a href="${path}/buscar">${director.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <dl>
                    <dt>Estrellas</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmStars}" var="actor" varStatus="loopStatus">
                            <a href="${path}/buscar">${actor.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <dl>
                    <dt>Géneros</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmGenres}" var="genre" varStatus="loopStatus">
                            <a href="${path}/buscar">${genre.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <dl>
                    <dt>País</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmCountries}" var="country" varStatus="loopStatus">
                            <a href="${path}/buscar">${country.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <dl>
                    <dt>Reparto</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmSupportings}" var="actor" varStatus="loopStatus">
                            <a href="${path}/buscar">${actor.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
            </div>
            <div class="col s12 reproducir">
                <div class="section"></div>
                <sec:authorize access="isFullyAuthenticated()">
                    <a class="btn-floating btn-large waves-effect waves-light"><i class="material-icons">play_arrow</i></a>
                    <span>Reproducir</span>
                </sec:authorize>
                <sec:authorize access="isAnonymous()">
                    <a class="disabled btn-floating btn-large waves-effect waves-light"><i class="material-icons grey-text">play_arrow</i></a>
                    <span>Inicia sesión para reproducir</span>
                </sec:authorize>
                <a href="#trailer" class="waves-effect btn-large grey darken-3"><i class="material-icons left">theaters</i>Ver tráiler</a>
                <sec:authorize access="isFullyAuthenticated()">
                    <a class="waves-effect btn-large grey darken-3"><i class="material-icons left">view_list</i>Ver más tarde</a>
                </sec:authorize>
            </div>
        </div>
    </div>
    <div class="col s5">
        <img class="materialboxed z-depth-3 poster" height="350" src="${path}/img/posters/${film.poster}"></p>
    </div>
</div>

<div class="film-background" style="background-image: url(${path}/img/posters/${film.poster});">
    <div class="film-bg-left"></div>
    <div class="film-bg-bottom"></div>
</div>

<div id="trailer" class="modal">
    <div class="modal-content">
        <div class="video-container">
            <iframe id="trailer-youtube" class="yt_player_iframe" width="853" height="480" src="//www.youtube.com/embed/${film.trailer}?enablejsapi=1&version=3&playerapiid=ytplayer" frameborder="0" allowfullscreen allowscriptaccess="always"></iframe>
        </div>
    </div>
</div>

<%@ include file="../_js.jsp"%>
<script>
    $( document ).ready(function() {
        $(".modal").modal({
            ready: function () {
                $('#trailer-youtube')[0].contentWindow.postMessage('{"event":"command","func":"' + 'playVideo' + '","args":""}', '*');
            },
            complete: function () {
                $('#trailer-youtube')[0].contentWindow.postMessage('{"event":"command","func":"' + 'pauseVideo' + '","args":""}', '*');
            }
        });
    });
</script>
<%@ include file="../_footer.jsp"%>