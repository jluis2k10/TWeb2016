<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="../_header.jsp"%>

<div class="section"></div>
<div class="section row">
    <div class="col s6 offset-s3">
        <h5>Editar información de mi cuenta</h5>
        <div class="section"></div>
        <form action="${path}/micuenta" method="post" class="col s12" id="edit-account">
            <div class="input-field">
                <spring:bind path="editarCuentaForm.userName">
                    <i class="material-icons prefix">account_circle</i>
                    <form:input path="editarCuentaForm.userName" type="text" cssErrorClass="invalid"></form:input>
                    <form:label path="editarCuentaForm.userName">Nombre de Usuario</form:label>
                    <form:errors path="editarCuentaForm.userName" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="input-field">
                <spring:bind path="editarCuentaForm.email">
                    <i class="material-icons prefix">mail</i>
                    <form:input path="editarCuentaForm.email" type="text" cssErrorClass="invalid"></form:input>
                    <form:label path="editarCuentaForm.email">Dirección Email</form:label>
                    <form:errors path="editarCuentaForm.email" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="input-field">
                <spring:bind path="editarCuentaForm.provincia">
                    <i class="material-icons prefix">place</i>
                    <form:select path="editarCuentaForm.provincia" cssErrorClass="invalid">
                        <form:option value="0" disabled="true" selected="true">Selecciona Provincia</form:option>
                        <form:options items="${provincias}"/>
                    </form:select>
                    <form:label path="editarCuentaForm.provincia">Provincia</form:label>
                    <form:errors path="editarCuentaForm.provincia" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
            <div class="section">
                <h6>Cambiar contraseña</h6>
            </div>

                <div class="input-field">
                    <spring:bind path="changePasswordForm.newPassword">
                        <i class="material-icons prefix">lock_outline</i>
                        <form:input path="changePasswordForm.newPassword" type="password" cssErrorClass="invalid"></form:input>
                        <form:label path="changePasswordForm.newPassword">Nueva Contraseña</form:label>
                        <form:errors path="changePasswordForm.newPassword" cssClass="red-text form-error"></form:errors>
                    </spring:bind>
                </div>
                <div class="input-field">
                    <spring:bind path="changePasswordForm.oldPassword">
                        <i class="material-icons prefix">lock_open</i>
                        <form:input path="changePasswordForm.oldPassword" type="password" cssErrorClass="invalid"></form:input>
                        <form:label path="changePasswordForm.oldPassword">Contraseña Actual</form:label>
                        <form:errors path="changePasswordForm.oldPassword" cssClass="red-text form-error"></form:errors>
                    </spring:bind>
                </div>
                <div class="section"></div>
                <div class="section row">
                    <div class="col s12">
                        <button class="btn waves-effect waves-light center-align" type="submit" name="action">
                            Confirmar cambios <i class="material-icons right">send</i>
                        </button>
                    </div>
                </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <form:hidden path="editarCuentaForm.id"/>
        </form>
    </div>
</div>
<%@ include file="../_js.jsp"%>
<script>
    $( document ).ready(function() {
        $("select").material_select();
    });
</script>
<%@ include file="../_footer.jsp"%>
