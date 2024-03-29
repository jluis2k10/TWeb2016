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
<header>
<nav class="grey darken-3" role="navigation">
    <div class="nav-wrapper container">
        <a href="${path}/" class="brand-logo"><img src="${path}/img/logo_pelis_uned.png"/></a>
        <a href="#" data-activates="mobile-nav" class="button-collapse"><i class="material-icons">menu</i></a>
        <ul id="nav-mobile" class="main-menu hide-on-med-and-down">
            <li><a href="${path}/catalogo">Catálogo</a></li>
            <li><a class="dropdown-generos" href="#!" data-activates="dropdown-gen">Por Géneros<i class="material-icons right">arrow_drop_down</i></a></li>
            <li><a href="${path}/informe">Informe PED</a></li>
        </ul>
        <ul class="right hide-on-med-and-down">
            <sec:authorize access="isAnonymous()">
                <li><a href="${path}/login" class="tooltipped" data-position="bottom" data-delay="50" data-tooltip="Administrador: admin/admin - Registrado: usuario/usuario"><i class="material-icons teal-text right">help_outline</i>Login</a></li>
                <li><a href="${path}/registro">Crear cuenta</a></li>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <li><a class="dropdown-account" href="#!" data-activates="dropdown-acc"><i class="material-icons account-icon">account_circle</i><i class="material-icons right arrow-down">arrow_drop_down</i></a></li>
            </sec:authorize>
        </ul>
        <!-- Form búsqueda -->
        <form class="right hide-on-med-and-down" method="get" action="${path}/catalogo">
            <div class="input-field">
                <input id="search" type="search" required name="buscar" placeholder="buscar...">
                <label class="label-icon" for="search"><i class="material-icons">search</i></label>
                <i class="material-icons">close</i>
            </div>
        </form><!-- /Form búsqueda -->
        <!-- Panel de menú para movil -->
        <ul class="side-nav grey darken-3" id="mobile-nav">
            <li><a href="${path}/catalogo">Catálogo</a></li>
            <li class="no-padding">
                <ul class="collapsible collapsible-accordion">
                    <li>
                        <a class="collapsible-header">Por Géneros<i class="material-icons right white-text">arrow_drop_down</i></a>
                        <div class="collapsible-body grey darken-2">
                            <ul>
                                <c:forEach items="${genresList}" var="genre">
                                    <li><a href="${path}/buscar?ref=genero&buscar=${genre.name}">${genre.name}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </li>
                </ul>
            </li>
            <li><a href="${path}/informe">Informe PED</a></li>
            <li class="divider"></li>
            <sec:authorize access="isAnonymous()">
                <li><a href="${path}/login" class="tooltipped" data-position="bottom" data-delay="50" data-tooltip="Administrador: admin/admin - Registrado: usuario/usuario"><i class="material-icons teal-text right">help_outline</i>Login</a></li>
                <li><a href="${path}/registro">Crear cuenta</a></li>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <li><a href="${path}/micuenta">Mi cuenta</a></li>
                <li><a href="${path}/micuenta/milista">Mi Lista de reproducción</a></li>
                <sec:authorize access="hasRole('ADMIN')">
                    <li><a href="${path}/admin">Panel de administración</a></li>
                </sec:authorize>
                <li>
                    <a href="#" onclick="document.getElementById('logout-form').submit()"">Cerrar sesión</a>
                    <form action="${path}/logout" method="POST" id="logout-form">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </li>
            </sec:authorize>
            <li>
                <form method="get" action="${path}/catalogo">
                    <div class="input-field">
                        <input id="search" type="search" required name="buscar" placeholder="buscar...">
                        <label class="label-icon" for="search"><i class="material-icons">search</i></label>
                        <i class="material-icons">close</i>
                    </div>
                </form>
            </li>
        </ul><!-- /Panel de menú para movil -->
        <!-- Dropdown Géneros -->
        <ul id="dropdown-gen" class="dropdown-content">
            <c:forEach items="${genresList}" var="genre" varStatus="loopStatus">
                <li><a href="${path}/buscar?ref=genero&buscar=${genre.name}">${genre.name}</a></li>
                ${!loopStatus.last ? '<li class="divider"></li>' : ''}
            </c:forEach>
        </ul><!-- /Dropdown Géneros -->
        <!-- Dropdown Géneros Movil-->
        <ul id="dropdown-gen-mobile" class="dropdown-content">
            <c:forEach items="${genresList}" var="genre" varStatus="loopStatus">
                <li><a href="${path}/buscar?ref=genero&buscar=${genre.name}">${genre.name}</a></li>
                ${!loopStatus.last ? '<li class="divider"></li>' : ''}
            </c:forEach>
        </ul><!-- /Dropdown Géneros Movil -->
        <!-- Dropdown Cuenta -->
        <sec:authorize access="isAuthenticated()">
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
    </div>
</nav>
</header>
<main>
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