<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s12">
        <h3 class="center-align">Informe PED</h3>
        <h5><a href="${path}/informe/informe.pdf">Descargar informe en PDF</a></h5>
        <h6><a href="${path}/informe/apidocs/index.html">Javadoc del proyecto</a></h6>
        <h6><a href="${path}/informe/xref/index.html">Código fuente del proyecto (navegable)</a></h6>
        <h6>Código fuente del proyecto (descarga)</h6>
    </div>
</div>

<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>