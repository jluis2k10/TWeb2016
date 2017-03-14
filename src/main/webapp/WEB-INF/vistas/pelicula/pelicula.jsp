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
                <span class="badge">
                    <select id="score">
                        <option value=""></option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                        <option value="5">5</option>
                    </select>
                    <sec:authorize access="isFullyAuthenticated()">
                        <span style="${myScore == 0 ? 'display: none;' : ''}">
                            <a href="#" id="mi-score" data-score="${myScore}">Mia</a> /
                            <a href="#" id="global-score" data-score="${globalScore}">Global</a>
                        </span>
                    </sec:authorize>
                </span>
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

<link rel="stylesheet" href="${path}/js/themes/css-stars.css"/>
<%@ include file="../_js.jsp"%>
<script type="text/javascript" src="${path}/js/jquery.barrating.min.js"></script>
<script>
    $( document ).ready(function() {
        /*
            Modal para Youtube
         */
        $(".modal").modal({
            ready: function () {
                $('#trailer-youtube')[0].contentWindow.postMessage('{"event":"command","func":"' + 'playVideo' + '","args":""}', '*');
            },
            complete: function () {
                $('#trailer-youtube')[0].contentWindow.postMessage('{"event":"command","func":"' + 'pauseVideo' + '","args":""}', '*');
            }
        });

        /* Recuperar token csrf para e incluirlo como cabecera en cada envío ajax */
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        /*
            Inicializar barrating (estrellitas)
         */
        $(function() {
            $('#score').barrating({
                theme: 'css-stars',
                deselectable: false,
                initialRating: ${globalScore},
                onSelect: function(value, text, event) {
                    if (typeof(event) !== 'undefined') {
                        ajaxVote(value);
                        $('span.badge > span').show();
                    }
                }
            });
        });

        /*
            Enlaces mostrar puntuación del usuario/global
         */
        $('a#mi-score').on('click', function () {
            $('#score').barrating('set', $(this).data('score'));
        });
        $('a#global-score').on('click', function () {
            $('#score').barrating('set', $(this).data('score'));
        });

    });

    /*
        JSON request con el voto emitido
     */
    function ajaxVote(value) {
        var vote = {};
        var id_vote = {};
        id_vote.filmId = ${film.id};
        id_vote.accountId = ${userId != null ? userId : 0}; // Chapucilla, pero de todos modos un invitado no puede votar
        vote.id = id_vote;
        vote.score = value;

        var midata = JSON.stringify(vote);
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "${path}/rest/votar",
            data: midata,
            dataType: 'json',
            timeout: 10000,
            success: function(data) {
                var voteResults = jQuery.parseJSON(data);
                updateScores(voteResults.myScore, voteResults.globalScore);
            },
            error: function(e) {
                console.log("ERROR", e);
            }
        });
    };

    /*
        Función para actualizar datos en enlaces para ver puntuación del usuario / puntuación global
     */
    function updateScores(myScore, globalScore) {
        $('a#mi-score').data('score', myScore);
        $('a#global-score').data("score", globalScore);
    };
</script>
<sec:authorize access="isAnonymous()">
    <script>
        $( document ).ready(function() {
            // Si es anónimo desactivamos la opción de votar
            $(function () {
                $('#score').barrating('readonly', true);
            });
        });
    </script>
</sec:authorize>
<%@ include file="../_footer.jsp"%>