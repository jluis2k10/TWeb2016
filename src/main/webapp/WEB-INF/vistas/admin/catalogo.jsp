<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mytags" uri="/WEB-INF/mytaglibs/MyTagsLib.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="row section">
    <div class="col s12">
        <h4>Catálogo de películas</h4>
        <c:if test="${not empty buscando}">
            <span>Término de búsqueda: <strong><em>${buscando}</em></strong></span>
        </c:if>
    </div>
</div>
<!-- Opciones de resultados -->
<div class="row no-margin">
    <div class="col s4">
        <a href="${path}/admin/catalogo/nueva" class="waves-effect waves-light btn"><i class="material-icons right">send</i>Nueva película</a>
    </div>
    <div class="col s8 right-align">
        <form id="admin-search" method="get" action="${path}/admin/catalogo">
            <div class="input-field">
                <input id="search" type="search" required name="buscar" placeholder="Búsqueda por título">
                <label class="label-icon" for="search"><i class="material-icons">search</i></label>
                <i class="material-icons">close</i>
            </div>
        </form>
        <a class="dropdown-filter btn waves-effect grey darken-4" href="#" data-activates="dropdown-filter">Ordenar<i class="material-icons right">arrow_drop_down</i></a>
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
                    <a class="waves-effect waves-cyan" href="#resultsDir"><i class="zmdi zmdi-code"></i>Resultados por página</a>
                </li>
            </ul>
        </div>
        <div class="col s9">
            <div id="lastDir" class="tab-content">
                <h6>Por fecha de entrada:</h6>
                <ul>
                    <li><a href="${path}/admin/catalogo${url_params}pagina=${page.number}&ver=${page.size}&sort=id,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                    <li><a href="${path}/admin/catalogo${url_params}pagina=${page.number}&ver=${page.size}&sort=id,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                </ul>
            </div>
            <div id="titleDir" class="tab-content">
                <h6>Por título de la película:</h6>
                <ul>
                    <li><a href="${path}/admin/catalogo${url_params}pagina=${page.number}&ver=${page.size}&sort=title,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                    <li><a href="${path}/admin/catalogo${url_params}pagina=${page.number}&ver=${page.size}&sort=title,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                </ul>
            </div>
            <div id="yearDir" class="tab-content">
                <h6>Por año de estreno:</h6>
                <ul>
                    <li><a href="${path}/admin/catalogo${url_params}pagina=${page.number}&ver=${page.size}&sort=year,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                    <li><a href="${path}/admin/catalogo${url_params}pagina=${page.number}&ver=${page.size}&sort=year,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                </ul>
            </div>
            <div id="resultsDir" class="tab-content">
                <c:set var="order" value="${fn:replace(page.sort, ': ', ',' )}"/>
                <div class="input-field input-filter">
                    <select>
                        <option value="5" ${page.size == 5 ? 'selected' : ''}>5</option>
                        <option value="10" ${page.size == 10 ? 'selected' : ''}>10</option>
                        <option value="20" ${page.size == 20 ? 'selected' : ''}>20</option>
                        <option value="50" ${page.size == 50 ? 'selected' : ''}>50</option>
                    </select>
                    <label>Número de resultados por página:</label>
                </div>
            </div>
        </div>
    </div>
</div> <!-- /Dropdown "ordenar" -->
<c:if test="${not empty films}">
<!-- Resultados -->
<div class="row">
    <div class="col s12">
        <ul class="collection">
            <c:forEach items="${films}" var="film">
                <li class="collection-item avatar grey darken-3">
                    <img src="${path}/img/posters/${film.poster}" alt="" class="circle z-depth-3">
                    <span class="title">${film.title}</span>
                    <p>${film.year} - ${film.duration} min
                    </p>
                    <a href="${path}/admin/pelicula/recalcular/${film.id}" class="secondary-content redo-votes" title="Recalcular votos"><i class="material-icons white-text">refresh</i></a>
                    <a href="${path}/admin/pelicula/editar/${film.id}" class="secondary-content edit-film" title="Editar"><i class="material-icons white-text">mode_edit</i></a>
                    <a href="#confirm-delete" class="secondary-content borrar-film" data-id="${film.id}" data-title="${film.title}" title="Borrar"><i class="material-icons white-text">delete</i></a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div> <!-- /Resultados -->
<div class="row">
    <mytags:paginate url="${path}/admin/catalogo${url_params}" page="${page}"
                         next="<i class=\"material-icons\">chevron_right</i>"
                         prev="<i class=\"material-icons\">chevron_left</i>"
                         listItemsCssClass="waves-effect z-depth-2"/>

</div>
</c:if>
<!-- Modal confirmación de borrado -->
<div id="confirm-delete" class="modal grey darken-2">
    <div class="modal-content">
        <div class="row grey darken-2">
            <div class="col s12">
                <h5>Confirmación</h5>
                <p>¿Realmente deseas borrar la película <span class="film-title yellow-text"></span>?</p>
                <p class="yellow-text center-align">¡No se puede deshacer!</p>
            </div>
        </div>
    </div>
    <div class="modal-footer grey darken-2">
        <a href="#" class="modal-action modal-close waves-effect btn-flat yellow-text">Cancelar</a>
        <a href="#" class="confirm-delete waves-effect btn-flat white-text">Borrar</a>
    </div>
</div><!-- /Modal confirmación de borrado -->

<div id="materialbox-overlay" class="filter-overlay"></div>

<%@ include file="../_js.jsp"%>

<script>
    $( document ).ready(function() {
        $("select").material_select();

        /* Redireccionar al hacer click en uno de los selects de "mostrar N elementos por página". */
        $("div.input-filter > div.select-wrapper > ul.select-dropdown > li > span ").click(function () {
            var url = '${path}/admin/catalogo${url_params}pagina=0&ver=' + $(this).text() + '&sort=${order}';
            if (url != '')
                window.location = url;
        });

        /* Modal de confirmación de borrado de película */
        $('.modal').modal({
            inDuration: 150,
            outDuration: 150,
            startingTop: '4%',
            endingTop: '30%'
        });
        // Al hacer click en el botón de borrar actualizamos la ventana modal con la
        // información de la película
        $('.borrar-film').on('click', function () {
            $('span.film-title').text($(this).data('title'));
            $('a.confirm-delete').attr("href", '${path}/admin/pelicula/borrar/' + $(this).data('id'));
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

<%@ include file="../_footer.jsp"%>