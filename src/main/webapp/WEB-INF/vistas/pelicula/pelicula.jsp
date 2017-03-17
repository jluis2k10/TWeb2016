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
                            <a href="${path}/buscar?ref=director&buscar=${director.name}">${director.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <dl>
                    <dt>Estrellas</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmStars}" var="actor" varStatus="loopStatus">
                            <a href="${path}/buscar?ref=actor&buscar=${actor.name}">${actor.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <dl>
                    <dt>Géneros</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmGenres}" var="genre" varStatus="loopStatus">
                            <a href="${path}/buscar?ref=genero&buscar=${genre.name}">${genre.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <dl>
                    <dt>País</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmCountries}" var="country" varStatus="loopStatus">
                            <a href="${path}/buscar?ref=pais&buscar=${country.name}">${country.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                <c:if test="${not empty film.filmSupportings}">
                <dl>
                    <dt>Reparto</dt>
                    <dd class="grey-text">
                        <c:forEach items="${film.filmSupportings}" var="actor" varStatus="loopStatus">
                            <a href="${path}/buscar?ref=actor&buscar=${actor.name}">${actor.name}</a>${!loopStatus.last ? ', ' : ''}
                        </c:forEach>
                    </dd>
                </dl>
                </c:if>
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
                <c:if test="${not empty film.trailer}">
                    <a href="#trailer" class="waves-effect btn-large grey darken-3"><i class="material-icons left">theaters</i>Ver tráiler</a>
                </c:if>
                <sec:authorize access="isFullyAuthenticated()">
                    <a data-film="${film.id}" data-action="${userWatchlist.contains(film.id) ? 'delete' : 'add'}" class="waves-effect btn-large grey darken-3 btn-watchlist">
                        <i class="material-icons left">view_list</i>
                            ${userWatchlist.contains(film.id) ? 'Eliminar de mi lista' : 'Ver más tarde'}
                    </a>
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
    });
</script>
<%-- JavaScript para usuarios identificados. --%>
<sec:authorize access="isFullyAuthenticated()">
<script>
    $( document ).ready(function() {
        /* Recuperar token csrf para e incluirlo como cabecera en cada envío ajax */
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
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

        /*
            Acción (json request) al hacer click en el botón de Añadir/Eliminar de lista de reproducción
         */
        $('.btn-watchlist').on('click', function () {
            addToWatchlist($(this))
        });
    });

    /*
        JSON request con el voto emitido
     */
    function ajaxVote(value) {
        var vote = {};
        var id_vote = {};
        id_vote.filmId = ${film.id};
        id_vote.accountId = ${userId != null ? userId : 0};
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
                Materialize.toast('Error en la operación. Inténtalo más adelante.', 5000, 'red darken-3 rounded');
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

    /*
        Petición ajax para añadir/eliminar película de la lista de reproducción
     */
    function addToWatchlist(btn) {
        var action = btn.data("action");
        var film_id = btn.data("film");
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/micuenta/milista/"+action,
            data: "film-id="+film_id,
            dataType: 'json',
            timeout: 10000,
            success: function() {
                updateWatchlistBtn(btn, action);
            },
            error: function() {
                Materialize.toast('Error en la operación. Inténtalo más adelante.', 5000, 'red darken-3 rounded');
            }
        });
    };

    /*
     Cambiar el estado del botón Añadir/Eliminar de lista de reproducción
     */
    function updateWatchlistBtn(btn, action) {
        if (action === 'add') {
            btn.data("action", "delete");
            btn.text('Eliminar de mi lista');
            flyToElement($('img.poster'), $('.account-icon'));
        } else {
            btn.data("action", "add");
            btn.text('Ver más tarde');
        }
        btn.prepend('<i class="material-icons left">view_list</i>');
    };

    /*
     Animación/efecto al añadir una película a la lista de reproducción.
     Pequeña adaptación de: http://www.codexworld.com/fly-to-cart-effect-using-jquery/
     */
    function flyToElement(flyer, flyingTo) {
        var divider = 10;
        var flyerClone = $(flyer).clone();
        flyerClone.height(350);
        $(flyerClone).css({position: 'absolute', top: $(flyer).offset().top + "px", left: $(flyer).offset().left + "px", opacity: 1, 'z-index': 1000});
        $('body').append($(flyerClone));
        var gotoX = $(flyingTo).offset().left + ($(flyingTo).width() / 2) - ($(flyer).width()/divider)/2;
        var gotoY = $(flyingTo).offset().top + ($(flyingTo).height() / 2) - ($(flyer).height()/divider)/2;

        $(flyerClone).animate({
                opacity: 0.4,
                left: gotoX,
                top: gotoY-60,
                width: $(flyer).width()/divider,
                height: $(flyer).height()/divider
            }, 700,
            function () {
                $(flyingTo).fadeOut('fast', function () {
                    $(flyingTo).fadeIn('fast', function () {
                        $(flyerClone).fadeOut('fast', function () {
                            $(flyerClone).remove();
                        });
                    });
                });
            });
    };
</script>
</sec:authorize>

<%-- JavaScript para invitados/no autentificados. Desactivamos la opción de votar. --%>
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