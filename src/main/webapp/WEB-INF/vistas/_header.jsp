<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <%-- Etiquetas con información sobre el token csrf para utilizarlo en ajax POSTs --%>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <link type="text/css" rel="stylesheet" href="${path}/css/custom.css"/>
    <link type="text/css" rel="stylesheet" href="${path}/css/slick.css"/>
    <link type="text/css" rel="stylesheet" href="${path}/css/materialize.min.css"  media="screen,projection"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${title}</title>
</head>

<body class="grey darken-4">
<nav class="grey darken-3" role="navigation">
    <div class="nav-wrapper container">
        <a href="${path}/" class="left brand-logo"><img src="${path}/img/logo_pelis_uned.png"/></a>
        <!-- Dropdown Géneros -->
        <ul id="dropdown-gen" class="dropdown-content">
            <c:forEach items="${genresList}" var="genre" varStatus="loopStatus">
                <li><a href="${path}/buscar?ref=genero&buscar=${genre.name}">${genre.name}</a></li>
                ${!loopStatus.last ? '<li class="divider"></li>' : ''}
            </c:forEach>
        </ul><!-- /Dropdown Géneros -->
        <ul id="nav-mobile" class="main-menu hide-on-med-and-down">
            <li><a href="${path}/catalogo">Catálogo</a></li>
            <li><a class="dropdown-generos" href="#!" data-activates="dropdown-gen">Por Géneros<i class="material-icons right">arrow_drop_down</i></a></li>
            <li><a href="collapsible.html">Informe PED</a></li>
        </ul>
        <!-- Dropdown Cuenta -->
        <sec:authorize access="isFullyAuthenticated()">
        <ul id="dropdown-acc" class="dropdown-content">
            <li><a href="${path}/micuenta">Mi cuenta</a></li>
            <li class="divider"></li>
            <li><a href="${path}/micuenta/milista">Mi Lista de reproducción</a></li>
            <li class="divider"></li>
            <sec:authorize access="hasRole('ADMIN')">
                <li><a href="${path}/admin">Panel de administración</a></li>
                <li class="divider"></li>
            </sec:authorize>
            <li>
                <a href="#" onclick="document.getElementById('logout-form').submit()"">Cerrar sesión</a>
                <form action="${path}/logout" method="POST" id="logout-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </form>
            </li>
        </ul><!-- /Dropdown cuenta -->
        </sec:authorize>
        <ul class="right hide-on-med-and-down">
            <sec:authorize access="isAnonymous()">
                <li><a href="${path}/login" class="tooltipped" data-position="bottom" data-delay="50" data-tooltip="Administrador: admin/admin - Registrado: usuario/usuario"><i class="material-icons teal-text right">help_outline</i>Login</a></li>
                <li><a href="${path}/registro">Crear cuenta</a></li>
            </sec:authorize>
            <sec:authorize access="isFullyAuthenticated()">
                <li><a class="dropdown-account" href="#!" data-activates="dropdown-acc"><i class="material-icons account-icon">account_circle</i><i class="material-icons right arrow-down">arrow_drop_down</i></a></li>
            </sec:authorize>
        </ul>
        <form class="right" method="get" action="${path}/catalogo">
            <div class="input-field">
                <input id="search" type="search" required name="buscar" placeholder="buscar...">
                <label class="label-icon" for="search"><i class="material-icons">search</i></label>
                <i class="material-icons">close</i>
            </div>
        </form>
    </div>
</nav>
<div class="container">
    <c:if test="${infoMsg != null}">
        <div class="section"></div>
        <div class="row">
            <div class="col s10 offset-s1">
                <div class="card-panel light-blue lighten-4">
                    <span class="blue-text text-darken-4">${infoMsg}</span>
                </div>
            </div>
        </div>
    </c:if>