<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>
<!-- Carrusel -->
<div class="row section">
    <div class="col s12">
        <div class="carousel carousel-slider center-align" data-indicators="true">
            <div class="carousel-arrow">
                <a id="prev" href="#" class="left carousel-control">
                    <i class="material-icons medium">chevron_left</i>
                </a>
                <a id="next" href="#" class="right carousel-control">
                    <i class="material-icons medium">chevron_right</i>
                </a>
            </div>
        <c:forEach var="film" items="${homePageFilms['ultimas']}" varStatus="loop">
            <div class="carousel-item" href="${idsCarousel[loop.index]}">
                <div class="col s12 carousel-content valign-wrapper">
                    <div class="col s5 offset-s2">
                        <a href="${path}/pelicula/${film.id}/${film.title}">
                            <h4 class="valign center">${film.title}</h4>
                        </a>
                        <span>${film.year} - ${film.filmGenres[0].name} - ${film.duration} min</span>
                    </div>
                    <div class="col s5">
                        <img src="${path}/img/posters/${film.poster}" height="200px" class="left z-depth-3" />
                    </div>
                </div>
                <div class="carousel-background" style="background-image: url('${path}/img/posters/${film.poster}');">
                    <div class="carousel-overlay"></div>
                </div>
            </div>
        </c:forEach>
        </div>
    </div>
</div><!--/Carrusel -->

<c:if test="${not empty watchlistFilms}">
<!-- Películas en lista de reproducción -->
<div class="row">
    <div class="col s12">
        <h6>En mi lista de reproducción<span class="right"><a href="${path}/micuenta/milista">Editar</a></span></h6>
    </div>
    <div class="col s12 films-slider">
        <c:forEach var="film" items="${watchlistFilms}">
            <div class="slick-films">
                <a href="${path}/pelicula/${film.id}/${film.title}" class="tooltipped" data-tooltip="${film.title}">
                    <img class="z-depth-2" src="${path}/img/posters/${film.poster}" height="210"/>
                </a>
            </div>
        </c:forEach>
    </div>
</div><!-- /Películas en lista de reproducción -->
</c:if>
<!-- Películas más vistas -->
<div class="row section">
    <div class="col s12">
        <h6>Más vistas</h6>
    </div>
    <div class="col s12 films-slider">
        <c:forEach var="film" items="${homePageFilms['vistas']}">
            <div class="slick-films">
                <a href="${path}/pelicula/${film.id}/${film.title}" class="tooltipped" data-tooltip="${film.title}">
                    <img class="z-depth-2" src="${path}/img/posters/${film.poster}" height="210"/>
                </a>
            </div>
        </c:forEach>
    </div>
</div><!-- /Películas más vistas -->
<!-- Películas mejor valoradas -->
<div class="row section">
    <div class="col s12">
        <h6>Mejor valoradas</h6>
    </div>
    <div class="col s12 films-slider">
        <c:forEach var="film" items="${homePageFilms['valoradas']}">
            <div class="slick-films">
                <a href="${path}/pelicula/${film.id}/${film.title}" class="tooltipped" data-tooltip="${film.title}">
                    <img class="z-depth-2" src="${path}/img/posters/${film.poster}" height="210"/>
                </a>
            </div>
        </c:forEach>
    </div>
</div><!-- /Películas mejor valoradas -->
<!-- Películas por géneros -->
<c:forEach var="genre" items="${randomGenres}">
<div class="row section">
    <div class="col s12">
        <h6>En ${genre}<span><a href="${path}/buscar?ref=genero&buscar=${genre}">Ver todas</a></span></h6>
    </div>
    <div class="col s12 films-slider">
        <c:forEach var="film" items="${homePageFilms[genre]}">
            <div class="slick-films">
                <a href="${path}/pelicula/${film.id}/${film.title}" class="tooltipped" data-tooltip="${film.title}">
                    <img class="z-depth-2" src="${path}/img/posters/${film.poster}" height="210"/>
                </a>
            </div>
        </c:forEach>
    </div>
</div>
</c:forEach><!-- /Películas por géneros -->


<%@ include file="_js.jsp"%>
<script type="text/javascript" src="${path}/js/slick.min.js"></script>
<script>
    $(document).ready(function () {
        $('.carousel.carousel-slider').carousel({
            fullWidth: true
        });
        myInterval();

        $('.films-slider').slick({
            infinite: false,
            slidesToShow: 7,
            variableWidth: true
        });
    });

    /* Intervalo para auto-reproducir el carrusel */
    var autoPlay;
    function myInterval() {
        autoPlay = setInterval(function ()
            {$('.carousel.carousel-slider').carousel('next');},
            7000);
    };

    /* Botones anterior/siguiente carrusel */
    $('#prev').on('click', function () {
        $('.carousel.carousel-slider').carousel('prev');
        clearInterval(autoPlay);
        myInterval();
    });
    $('#next').on('click', function () {
        $('.carousel.carousel-slider').carousel('next');
        clearInterval(autoPlay);
        myInterval();
    });
</script>
<%@ include file="_footer.jsp"%>