<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s6 offset-s3">
        <h5>Nuevo Género</h5>
    </div>
</div>
<form:form method="post" modelAttribute="genreForm">
    <div class="row">
        <div class="col s6 offset-s3">
            <div class="input-field">
                <spring:bind path="name">
                    <form:input path="name" type="text" cssErrorClass="invalid"></form:input>
                    <form:label path="name">Género</form:label>
                    <form:errors path="name" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
        </div>
    </div>
    <div class="section row">
        <div class="col s6 offset-s3">
            <button class="btn waves-effect waves-light center-align" type="submit" name="action">
                Guardar <i class="material-icons right">send</i>
            </button>
        </div>
    </div>
</form:form>

<%@ include file="../_js.jsp"%>
<%@ include file="../_footer.jsp"%>