<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s6 offset-s3">
        <h5 class="center-align">¿Tienes una cuenta?</h5>
        <p class="center-align">Acceso usuarios</p>
        <form action="${path}/login" method="post" class="col s12">
            <c:if test="${param.error != null}">
                <p>Usuario y contraseña incorrecto.</p>
            </c:if>
            <div class="row">
                <div class="input-field col s12">
                    <i class="material-icons prefix">account_circle</i>
                    <input id="username" type="text" name="username">
                    <label for="username">Usuario</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s12">
                    <i class="material-icons prefix">lock_outline</i>
                    <input id="password" type="password" name="password">
                    <label for="password">Contraseña</label>
                </div>
            </div>
            <div class="row">
                <div class="col s12">
                    <button class="btn waves-effect waves-light center-align" type="submit" name="action">
                        Enviar <i class="material-icons right">send</i>
                    </button>
                </div>
            </div>
            <div class="row">
                <div class="col s12">
                    <input type="checkbox" id="rememberme" name="rememberMe" />
                    <label for="rememberme">Recuérdame</label>
                </div>
            </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>

<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>
