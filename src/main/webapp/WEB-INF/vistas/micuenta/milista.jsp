<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="../_header.jsp"%>

<div class="row section">
    <div class="col s12">
        <h5>Mi lista de reproducción</h5>
    </div>
</div>
<div class="row">
    <div class="col s12 wl-container">
        <c:forEach var="film" items="${films}">
            <div class="wl-film card-panel grey darken-3">
                <a class="wl-link" href="${path}/pelicula/${film.id}/${film.title}">
                    <img class="wl-img z-depth-3" src="${path}/img/posters/${film.poster}" height="210"/>
                </a>
                <div class="wl-action-link">
                    <a class="wl-action" data-action="delete" data-film="${film.id}">Eliminar</a>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@ include file="../_js.jsp"%>

<script>
    $(document).ready(function () {
        $('.wl-container').on('click', 'a.wl-action', function () {
            updateWatchlist($(this));
        });
    });

    /*
        Petición ajax para añadir/eliminar película de la lista de reproducción
     */
    function updateWatchlist(link_btn) {
        var action = link_btn.data("action");
        var film_id = link_btn.data("film");
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/rest/milista/"+action,
            data: "film-id="+film_id,
            dataType: 'json',
            timeout: 10000,
            success: function() {
                if (action === 'delete') {
                    Materialize.toast('Película eliminada de tu lista de reproducción', 3000, 'teal rounded');
                    deleteFilm(link_btn, film_id);
                }
                else if (action === 'add') {
                    Materialize.toast('Película añadida a tu lista de reproducción', 3000, 'teal rounded');
                    undoDelete(link_btn);
                }
            },
            error: function() {
                Materialize.toast('Error en la operación. Inténtalo más adelante.', 5000, 'red darken-3 rounded');
            }
        })
    };

    /*
        Ocultar botón Eliminar y añadir capa con información sobre Deshacer
     */
    function deleteFilm(link_btn, film_id) {
        link_btn.toggle();
        var container = link_btn.closest('.wl-film');
        container.append('<div class="wl-delete"></div>');
        container.find('.wl-delete').append('<div class="wl-delete-msg">Película eliminada de tu lista</div>');
        container.find('.wl-delete').append('<a class="wl-action" data-action="add" data-film="' + film_id + '">Deshacer</a>');
    };

    /*
        Eliminar capa con información sobre Deshacer y mostrar botón Eliminar
     */
    function undoDelete(link_btn) {
        link_btn.closest('.wl-film').find('.wl-action').toggle();
        link_btn.closest('.wl-delete').remove();
    }

</script>

<%@ include file="../_footer.jsp"%>
