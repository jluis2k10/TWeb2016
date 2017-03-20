<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="../_header.jsp"%>

<div class="section"></div>
<div class="section row">
    <div class="col s6 offset-s3">
        <h5>¿Quieres eliminar tu cuenta?</h5>
        <div class="section"></div>
        <form action="${path}/micuenta/borrar" method="post" class="col s12" id="edit-account">
            <div class="input-field">
                <input type="checkbox" id="confirm" name="confirm" />
                <label for="confirm">Sí, borrar mi cuenta.</label>
            </div>
            <div class="section"></div>
            <div class="section row">
                <div class="col s12">
                    <button class="red btn waves-effect waves-light center-align" type="submit" name="action">
                        Eliminar cuenta<i class="material-icons right">send</i>
                    </button>
                </div>
            </div>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>
<%@ include file="../_js.jsp"%>
<script>
    $(document).ready(function () {
        $('button[type="submit"]').prop('disabled', true);
        $('#confirm').on('click', function () {
            if($(this).is(":checked")) {
                $('button[type="submit"]').prop('disabled', false);
            } else {
                $('button[type="submit"]').prop('disabled', true);
            }
        });
    });
</script>
<%@ include file="../_footer.jsp"%>
