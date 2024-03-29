<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mytags" uri="/WEB-INF/mytaglibs/MyTagsLib.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="../_header.jsp"%>

<c:if test="${not empty headTitle}">
<div class="row section">
    <div class="col s12">
        <h5>${headTitle}</h5>
    </div>
</div>
</c:if>

<c:if test="${not empty films}">
    <!-- Opciones de resultados -->
    <div class="section"></div>
    <div class="row section no-margin filter-catalog">
        <div class="col s4">
            <a href="${path}/catalogo/refinar_busqueda" class="waves-effect waves-light btn"><i class="material-icons right">send</i>Refinar búsqueda</a>
        </div>
        <div class="input-field input-filter col s8">
            <a class="dropdown-filter btn waves-effect grey darken-4" href="#" data-activates="dropdown-filter">Ordenar<i class="material-icons right">arrow_drop_down</i></a>
            <select>
                <option value="5" ${page.size == 5 ? 'selected' : ''}>5</option>
                <option value="10" ${page.size == 10 ? 'selected' : ''}>10</option>
                <option value="20" ${page.size == 20 ? 'selected' : ''}>20</option>
                <option value="50" ${page.size == 50 ? 'selected' : ''}>50</option>
            </select>
            <span class="page-results">Resultados por página: </span>
        </div>
    </div> <!-- /Opciones de resultados -->

    <!-- Dropdown "ordenar" -->
    <div class="row" id="filter-content">
        <div class="col s10 offset-s1 tabs-vertical z-depth-5 grey darken-3">
            <div class="menu-tabs col s3">
                <ul class="tabs">
                    <li class="tab">
                        <a class="waves-effect waves-cyan" href="#lastDir"><i class="zmdi zmdi-apps"></i>Últimas</a>
                    </li>
                    <li class="tab">
                        <a class="waves-effect waves-cyan" href="#titleDir"><i class="zmdi zmdi-email"></i>Título</a>
                    </li>
                    <li class="tab">
                        <a class="waves-effect waves-cyan" href="#yearDir"><i class="zmdi zmdi-code"></i>Año estreno</a>
                    </li>
                    <li class="tab">
                        <a class="waves-effect waves-cyan" href="#scoreDir"><i class="zmdi zmdi-code"></i>Puntuacion</a>
                    </li>
                </ul>
            </div>
            <div class="col s9">
                <div id="lastDir" class="tab-content">
                    <h6>Por fecha de entrada:</h6>
                    <ul>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=id,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=id,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                    </ul>
                </div>
                <div id="titleDir" class="tab-content">
                    <h6>Por título de la película:</h6>
                    <ul>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=title,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=title,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                    </ul>
                </div>
                <div id="yearDir" class="tab-content">
                    <h6>Por año de estreno:</h6>
                    <ul>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=year,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=year,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                    </ul>
                </div>
                <div id="scoreDir" class="tab-content">
                    <h6>Por puntuación de usuarios:</h6>
                    <ul>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=score,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                        <li><a href="${path}${url_params}pagina=${page.number}&ver=${page.size}&sort=score,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div> <!-- /Dropdown "ordenar" -->

    <!-- Resultados -->
    <c:forEach var="film" items="${films}" varStatus="loop">
    <div class="row catalog">
        <div class="col s12">
            <div class="card horizontal grey darken-3">
                <div class="card-image">
                    <img src="${path}/img/posters/${film.poster}" />
                </div>
                <div class="card-stacked">
                    <div class="card-content">
                        <a href="${path}/pelicula/${film.id}/${film.title}"><h5>${film.title}</h5></a>
                        <div class="film-badges">
                            <mytags:scoreBadge score="${film.score}"/>
                            <span class="badge">${film.year}</span>
                            <span class="badge">${film.duration} min</span>
                            <span class="badge">${film.rating}</span>
                        </div>
                        <p>
                            ${fn:substring(film.description, 0, 350)}
                            ${film.description.length() > 350 ? '<em>[...]</em>': ''}
                        </p>
                    </div>
                    <div class="card-action">
                        <sec:authorize access="isAuthenticated()">
                            <a href="${path}/pelicula/ver/${film.id}/${film.title}" class="btn-floating btn waves-effect waves-light"><i class="material-icons">play_arrow</i></a>
                            <span>Reproducir</span>
                        </sec:authorize>
                        <sec:authorize access="isAnonymous()">
                            <a class="disabled btn-floating btn waves-effect waves-light"><i class="material-icons grey-text">play_arrow</i></a>
                            <span>Inicia sesión para reproducir</span>
                        </sec:authorize>
                        <sec:authorize access="isAuthenticated()">
                            <a data-film="${film.id}" data-action="${userWatchlist.contains(film.id) ? 'delete' : 'add'}" class="waves-effect btn grey darken-1 btn-watchlist">
                                <i class="material-icons left">view_list</i>
                                ${userWatchlist.contains(film.id) ? 'Eliminar de mi lista' : 'Ver más tarde'}
                            </a>
                        </sec:authorize>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </c:forEach><!-- /Resultados -->

    <!-- Paginación -->
    <div class="row">
        <mytags:paginate url="${path}${url_params}" page="${page}"
                         next="<i class=\"material-icons\">chevron_right</i>"
                         prev="<i class=\"material-icons\">chevron_left</i>"
                         liCSS="waves-effect z-depth-2"/>

    </div><!-- /Paginación -->
    <div id="materialbox-overlay" class="filter-overlay"></div>
</c:if><%-- /if (not empty ${films} --%>

<%@ include file="../_js.jsp"%>

<c:set var="order" value="${fn:replace(page.sort, ': ', ',' )}"/>
<script>
    $(document).ready(function() {
        $("select").material_select();

        /* Redireccionar al hacer click en uno de los selects de "mostrar N elementos por página". */
        $("div.input-filter > div.select-wrapper > ul.select-dropdown > li > span ").click(function () {
            var url = '${path}${url_params}pagina=0&ver=' + $(this).text() + '&sort=${order}';
            if (url != '')
                window.location = url;
        });
    });

    /* Mostrar/ocultar el dropbox de Ordenar los resultado */
    $(".dropdown-filter").click(function (e) {
        $("#filter-content").slideToggle({
            duration: 150
        });
        $("#materialbox-overlay").fadeToggle({
            duration: 150
        })
        e.stopPropagation();
    });
    $("#filter-content").click(function (e) {
        e.stopPropagation();
    });
    $("#materialbox-overlay").click(function(){
        $("#filter-content").slideUp({
            duration: 100
        })
        $("#materialbox-overlay").fadeToggle({
            duration: 100
        })
    });
</script>

<sec:authorize access="isAuthenticated()">
<script>
    $(document).ready(function () {
        $('.btn-watchlist').on('click', function () {
            addToWatchlist($(this));
        });
    });

    /*
        Petición ajax para añadir/eliminar película de la lista de reproducción
     */
    function addToWatchlist(btn) {
        var action = btn.data("action");
        var film_id = btn.data("film");
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/rest/milista/"+action,
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
            flyToElement(btn.closest('.card').find('img'), $('.account-icon'));
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
        flyerClone.height(280);
        $(flyerClone).css({position: 'absolute', top: $(flyer).offset().top + "px", left: $(flyer).offset().left + "px", opacity: 1, 'z-index': 1000});
        $('body').append($(flyerClone));
        var gotoX = $(flyingTo).offset().left + ($(flyingTo).width() / 2) - ($(flyer).width()/divider)/2;
        var gotoY = $(flyingTo).offset().top + ($(flyingTo).height() / 2) - ($(flyer).height()/divider)/2;
        console.log(gotoY);
        gotoY = Math.max(gotoY, pageYOffset-30);

        $(flyerClone).animate({
                opacity: 0.4,
                left: gotoX,
                top: gotoY,
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

<%@ include file="../_footer.jsp"%>