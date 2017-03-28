<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s12">
        <h3 class="center-align">Informe PED</h3>
        <h5><a href="${path}/informe/informe.pdf">Descargar informe en PDF</a></h5>
        <h6><a href="${path}/informe/apidocs/index.html">Javadoc del proyecto</a></h6>
        <h6><a href="${path}/informe/xref/index.html">Código fuente del proyecto</a> (navegable, sólo las clases Java)</h6>
        <p><a href="">Descarga</a> del código fuente completo del proyecto, que incluye:</p>
            <ul class="browser-default">
                <li>Archivo de configuración maven</li>
                <li>Código de las vistas JSP</li>
                <li>Todos los recursos utilizados (css, scripts SQL, Javascript, etc.)</li>
            </ul>
    </div>
</div>

<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>