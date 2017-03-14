<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s6 offset-s3">
        <h5 class="center-align">Crea tu cuenta</h5>
        <p class="center-align">Formulario de registro para nuevos usuarios</p>
        <form:form method="post" modelAttribute="registroForm" cssClass="col s12">
            <div class="input-field">
                <spring:bind path="userName">
                    <form:input path="userName" type="text" cssErrorClass="invalid"></form:input>
                    <form:label path="userName">Nombre de Usuario</form:label>
                    <form:errors path="userName" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="input-field">
                <spring:bind path="email">
                    <form:input path="email" type="text" cssErrorClass="invalid"></form:input>
                    <form:label path="email">Email</form:label>
                    <form:errors path="email" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="input-field">
                <spring:bind path="password">
                    <form:input path="password" type="password" cssErrorClass="invalid"></form:input>
                    <form:label path="password">Contraseña</form:label>
                    <form:errors path="password" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="input-field">
                <spring:bind path="passwordConfirm">
                    <form:input path="passwordConfirm" type="password" cssErrorClass="invalid"></form:input>
                    <form:label path="passwordConfirm">Repite la contraseña</form:label>
                    <form:errors path="passwordConfirm" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="input-field">
                <spring:bind path="provincia">
                    <form:select path="provincia" cssErrorClass="invalid">
                        <form:option value="0" disabled="true" selected="true">Selecciona Provincia</form:option>
                        <form:options items="${provincias}"/>
                    </form:select>
                    <form:label path="provincia">Provincia</form:label>
                    <form:errors path="provincia" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="section row">
                <div class="col s12">
                    <button class="btn waves-effect waves-light center-align" type="submit" name="action">
                        Crear cuenta <i class="material-icons right">send</i>
                    </button>
                </div>
            </div>
        </form:form>
    </div>
</div>

<%@ include file="_js.jsp"%>
<script>
    $( document ).ready(function() {
        $("select").material_select();
    });
</script>
<%@ include file="_footer.jsp"%>
