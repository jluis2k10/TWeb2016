<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <link type="text/css" rel="stylesheet" href="${path}/css/custom.css"/>
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
            <li><a href="${path}/admin"><i class="material-icons left">lock</i>Panel</a></li>
            <li><a href="${path}/admin/catalogo"><i class="material-icons left">theaters</i>Administrar Películas</a></li>
            <li><a href="${path}/admin/usuarios"><i class="material-icons left">supervisor_account</i>Administrar Usuarios</a></li>
        </ul>
        <ul class="right hide-on-med-and-down">
            <li><a class="dropdown-account" href="#!" data-activates="dropdown-acc"><i class="material-icons account-icon">account_circle</i><i class="material-icons right arrow-down">arrow_drop_down</i></a></li>
        </ul>
        <!-- Panel de menú para movil -->
        <ul class="side-nav grey darken-3" id="mobile-nav">
            <li><a href="${path}/admin"><i class="material-icons left white-text">lock</i>Panel</a></li>
            <li><a href="${path}/admin/catalogo"><i class="material-icons left white-text">theaters</i>Administrar Películas</a></li>
            <li><a href="${path}/admin/usuarios"><i class="material-icons left white-text">supervisor_account</i>Administrar Usuarios</a></li>
            <li class="divider"></li>
            <li><a href="${path}/micuenta">Mi cuenta</a></li>
            <li><a href="${path}/micuenta/milista">Mi Lista de reproducción</a></li>
            <li><a href="${path}/admin">Panel de administración</a></li>
            <li>
                <a href="#" onclick="document.getElementById('logout-form').submit()"">Cerrar sesión</a>
                <form action="${path}/logout" method="POST" id="logout-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </form>
            </li>
        </ul><!-- /Panel de menú para movil -->
        <!-- Dropdown Cuenta -->
        <ul id="dropdown-acc" class="dropdown-content">
            <li><a href="${path}/micuenta">Mi cuenta</a></li>
            <li class="divider"></li>
            <li><a href="${path}/micuenta/milista">Mi Lista de reproducción</a></li>
            <li class="divider"></li>
            <li><a href="${path}/admin">Panel de administración</a></li>
            <li class="divider"></li>
            <li>
                <a href="#" onclick="document.getElementById('logout-form').submit()"">Cerrar sesión</a>
                <form action="${path}/logout" method="POST" id="logout-form">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </form>
            </li>
        </ul><!-- Dropdown Cuenta -->
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