<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mytags" uri="/WEB-INF/mytaglibs/MyTagsLib.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="row section">
    <div class="col s12">
        <h4>Usuarios registrados</h4>
        <c:if test="${not empty buscando}">
            <span>Nombre buscado: <strong><em>${buscando}</em></strong></span>
        </c:if>
    </div>
</div>
<!-- Navegación cabecera de resultados (buscar, ordenar) -->
<div class="row no-margin">
    <div class="col s8 offset-s4 right-align">
        <form id="admin-search" method="get" action="${path}/admin/usuarios">
            <div class="input-field">
                <input id="search" type="search" required name="buscar" placeholder="Búsqueda por nombre">
                <label class="label-icon" for="search"><i class="material-icons">search</i></label>
                <i class="material-icons">close</i>
            </div>
        </form>
        <a class="dropdown-filter btn waves-effect grey darken-4" href="#" data-activates="dropdown-filter">Ordenar<i class="material-icons right">arrow_drop_down</i></a>
    </div>
</div> <!-- /Navegación cabecera de resultados -->

<!-- Dropdown "ordenar" -->
<div class="row" id="filter-content">
    <div class="col s10 offset-s1 tabs-vertical z-depth-5 grey darken-3">
        <div class="menu-tabs col s3">
            <ul class="tabs">
                <li class="tab">
                    <a class="waves-effect waves-cyan" href="#lastDir"><i class="zmdi zmdi-apps"></i>Últimos</a>
                </li>
                <li class="tab">
                    <a class="waves-effect waves-cyan" href="#titleDir"><i class="zmdi zmdi-email"></i>Nombre</a>
                </li>
                <li class="tab">
                    <a class="waves-effect waves-cyan" href="#resultsDir"><i class="zmdi zmdi-code"></i>Resultados por página</a>
                </li>
            </ul>
        </div>
        <div class="col s9">
            <div id="lastDir" class="tab-content">
                <h6>Por fecha de registro:</h6>
                <ul>
                    <li><a href="${path}/admin/usuarios${url_params}pagina=${page.number}&ver=${page.size}&sort=id,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                    <li><a href="${path}/admin/usuarios${url_params}pagina=${page.number}&ver=${page.size}&sort=id,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
                </ul>
            </div>
            <div id="titleDir" class="tab-content">
                <h6>Por nombre de usuario:</h6>
                <ul>
                    <li><a href="${path}/admin/usuarios${url_params}pagina=${page.number}&ver=${page.size}&sort=userName,ASC"><i class="material-icons">arrow_drop_up</i>Ascendente</a></li>
                    <li><a href="${path}/admin/usuarios${url_params}pagina=${page.number}&ver=${page.size}&sort=userName,DESC"><i class="material-icons">arrow_drop_down</i>Descendente</a></li>
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

<c:if test="${not empty accounts}">
<!-- Resultados -->
<div class="row">
    <div class="col s12">
        <ul class="collection">
            <c:forEach items="${accounts}" var="account">
                <li class="row collection-item grey darken-3">
                    <div class="col s6">
                        <h6 class="username">${account.userName}</h6>
                        <p>${account.email}</p>
                    </div>
                    <div class="col s6 actions flex-middle-right">
                        <!-- Switch -->
                        <div class="switch">
                            <span>¿Administrador?</span>
                            <label>
                                No
                                <input type="checkbox" class="chk-admin" data-id="${account.id}"
                                    ${account.isAdmin() ? "checked" : ""} ${loggedAccount.id == account.id ? "disabled" : ""}>
                                <span class="lever"></span>
                                Sí
                            </label>
                        </div>
                        <!-- Switch -->
                        <div class="switch">
                            <span>¿Permitir acceso?</span>
                            <label>
                                No
                                <input type="checkbox" class="chk-active" data-id="${account.id}"
                                    ${account.isActive() ? "checked" : ""} ${loggedAccount.id == account.id ? "disabled" : ""}>
                                <span class="lever"></span>
                                Sí
                            </label>
                        </div>
                        <a href="#confirm-delete" class="secondary-content account-delete flex-middle-right" data-id="${account.id}" data-name="${account.userName}" title="Borrar"><i class="material-icons white-text">delete</i></a>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
</div> <!-- /Resultados -->
<!-- Paginación -->
<div class="row">
    <mytags:paginate url="${path}/admin/usuarios${url_params}" page="${page}"
                     next="<i class=\"material-icons\">chevron_right</i>"
                     prev="<i class=\"material-icons\">chevron_left</i>"
                     liCSS="waves-effect z-depth-2"/>

</div><!-- /Paginación -->
<!-- Modal confirmación de borrado -->
<div id="confirm-delete" class="modal grey darken-2">
    <div class="modal-content">
        <div class="row grey darken-2">
            <div class="col s12">
                <h5>Confirmación</h5>
                <p>¿Realmente deseas borrar el usuario <span class="account-name yellow-text"></span>?</p>
                <p class="yellow-text center-align">¡No se puede deshacer!</p>
            </div>
        </div>
    </div>
    <div class="modal-footer grey darken-2">
        <a href="#" class="modal-action modal-close waves-effect btn-flat yellow-text">Cancelar</a>
        <a href="#" class="confirm-delete waves-effect btn-flat white-text">Borrar</a>
    </div>
</div><!-- /Modal confirmación de borrado -->
</c:if>
<div id="materialbox-overlay" class="filter-overlay"></div>
<%@ include file="../_js.jsp"%>
<script>
    $( document ).ready(function() {
        $("select").material_select();

        /* Redireccionar al hacer click en uno de los selects de "mostrar N elementos por página". */
        $("div.input-filter > div.select-wrapper > ul.select-dropdown > li > span ").click(function () {
            var url = '${path}/admin/usuarios${url_params}pagina=0&ver=' + $(this).text() + '&sort=${order}';
            if (url != '')
                window.location = url;
        });

        /* Modal de confirmación de borrado de usuario */
        $('.modal').modal({
            inDuration: 150,
            outDuration: 150,
            startingTop: '4%',
            endingTop: '30%'
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

        /* Acción al hacer click en los checkboxes de editar usuario */
        $('.chk-admin').on('click', function () {
            editAccount($(this).data("id"), "admin", $(this).prop("checked"))
        });
        $('.chk-active').on('click', function () {
            editAccount($(this).data("id"), "active", $(this).prop("checked"))
        })

        /* Al hacer click en el botón de borrar actualizamos la ventana modal con la
         información del nombre de usuario */
        var _id;
        var _row;
        $('.account-delete').on('click', function () {
            _id = $(this).data('id');
            _row = $(this).closest('li.row');
            $('span.account-name').text($(this).data('name'));
        });
        /* Acción al hacer click en el botón de confirmar borrado */
        $('a.confirm-delete').on('click', function () {
            deleteAccount(_id, _row);
        });
    });

    /*
        Ajax request para editar un usuario
     */
    function editAccount(account_id, modify, state) {
        var action;
        (state === true ? action = 'add' : action = 'delete');
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/admin/usuarios/edit",
            data: "modify="+modify+"&action="+action+"&accountId="+account_id,
            dataType: 'json',
            timeout: 10000,
            success: function(response) {
                Materialize.toast(response.message, 3000, 'teal rounded');
            },
            error: function(response) {
                if (response.responseJSON != null)
                    Materialize.toast(response.responseJSON.message, 5000, 'red darken-3 rounded');
                else
                    Materialize.toast("Error. Inténtalo de nuevo más tarde", 5000, 'red darken-3 rounded');
            }
        })
    };

    /*
        Ajax request para borrar un usuario
     */
    function deleteAccount(account_id, row_to_delete) {
        $('.modal').modal('close');
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/admin/usuarios/delete",
            data: "accountId="+account_id,
            dataType: 'json',
            timeout: 10000,
            success: function(response) {
                row_to_delete.remove();
                Materialize.toast(response.message, 3000, 'teal rounded');
            },
            error: function(response) {
                if (response.responseJSON != null)
                    Materialize.toast(response.responseJSON.message, 5000, 'red darken-3 rounded');
                else
                    Materialize.toast("Error. Inténtalo de nuevo más tarde", 5000, 'red darken-3 rounded');
            }
        })
    };
</script>
<%@ include file="../_footer.jsp"%>