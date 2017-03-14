<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="mytags" uri="/WEB-INF/mytaglibs/MyTagsLib.tld" %>

<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s12">
        <h5>Editando película</h5>
    </div>
    <c:if test="${not empty peliculaForm.poster}">
        <div class="col s12">
            <img src="${path}/img/posters/${peliculaForm.poster}" width="150px"/>
        </div>
    </c:if>
</div>
<form action="${path}/admin/${formActionUrl}" method="post" enctype="multipart/form-data" id="new-film">
    <%-- Para acceder a los errores fuera de las etiquetas del formulario --%>
    <spring:hasBindErrors name="peliculaForm">
        <c:if test="${errors.hasFieldErrors('filmDirectors')}">
            <c:set var="errorDirectors" value="1" />
        </c:if>
        <c:if test="${errors.hasFieldErrors('filmStars')}">
            <c:set var="errorStars" value="1" />
        </c:if>
        <c:if test="${errors.hasFieldErrors('filmCountries')}">
            <c:set var="errorCountries" value="1" />
        </c:if><c:if test="${errors.hasFieldErrors('poster')}">
        <c:set var="errorPoster" value="1" />
    </c:if>

    </spring:hasBindErrors>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="peliculaForm.title">
                <form:input path="peliculaForm.title" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="peliculaForm.title">Título</form:label>
                <form:errors path="peliculaForm.title" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s4 input-field">
            <spring:bind path="peliculaForm.year">
                <form:input path="peliculaForm.year" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="peliculaForm.year">Año</form:label>
                <form:errors path="peliculaForm.year" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 input-field">
            <spring:bind path="peliculaForm.duration">
                <form:input path="peliculaForm.duration" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="peliculaForm.duration">Duración (min)</form:label>
                <form:errors path="peliculaForm.duration" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 input-field">
            <spring:bind path="peliculaForm.rating">
                <form:select path="peliculaForm.rating" cssErrorClass="invalid">
                    <form:option value="TP">TP</form:option>
                    <form:option value="7+">7+</form:option>
                    <form:option value="12+">12+</form:option>
                    <form:option value="16+">16+</form:option>
                    <form:option value="18+">18+</form:option>
                    <form:option value="X">X</form:option>
                </form:select>
                <form:label path="peliculaForm.rating">Edades</form:label>
                <form:errors path="peliculaForm.rating" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="peliculaForm.description">
                <form:textarea path="peliculaForm.description" cssClass="materialize-textarea" cssErrorClass="materialize-textarea invalid"></form:textarea>
                <form:label path="peliculaForm.description">Sinopsis</form:label>
                <form:errors path="peliculaForm.description" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s6 input-field" id="genresContainer">
            <spring:bind path="peliculaForm.filmGenres">
                <form:select path="peliculaForm.filmGenres" cssErrorClass="invalid" multiple="true">
                    <form:options items="${genres}" itemValue="name" itemLabel="name"/>
                </form:select>
                <form:label path="peliculaForm.filmGenres" cssClass="active">Géneros</form:label>
                <form:errors path="peliculaForm.filmGenres" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s6">
            <p>Si no está disponible, puedes <a href="${path}/admin/catalogo/nuevoGenero">añadir una nueva categoría</a>.</p>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="peliculaForm.filmDirectors">
                <c:forEach items="${peliculaForm.filmDirectors}" var="director" varStatus="loopStatus">
                    <c:set var="directors" value="${loopStatus.first ? '' : directors}${director.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmDirectors" name="filmDirectors" value="${directors}" class="hidden"/>
                <form:label path="peliculaForm.filmDirectors">Directores</form:label>
                <div class="chips ${errorDirectors == 1 ? "invalid" : ""}" id="chips-directors"></div>
                <form:errors path="peliculaForm.filmDirectors" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="peliculaForm.filmStars">
                <c:forEach items="${peliculaForm.filmStars}" var="star" varStatus="loopStatus">
                    <c:set var="stars" value="${loopStatus.first ? '' : stars}${star.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmStars" name="filmStars" value="${stars}" class="hidden"/>
                <form:label path="peliculaForm.filmStars">Actores Principales</form:label>
                <div class="chips ${errorStars == 1 ? "invalid" : ""}" id="chips-stars"></div>
                <form:errors path="peliculaForm.filmStars" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="peliculaForm.filmSupportings">
                <c:forEach items="${peliculaForm.filmSupportings}" var="supporting" varStatus="loopStatus">
                    <c:set var="supportings" value="${loopStatus.first ? '' : supportings}${supporting.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmSupportings" name="filmSupportings" value="${supportings}" class="hidden"/>
                <form:label path="peliculaForm.filmSupportings">Actores Secundarios</form:label>
                <div class="chips" id="chips-supportings"></div>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s4 input-field">
            <spring:bind path="peliculaForm.filmCountries">
                <c:forEach items="${peliculaForm.filmCountries}" var="country" varStatus="loopStatus">
                    <c:set var="countries" value="${loopStatus.first ? '' : countries}${country.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmCountries" name="filmCountries" value="${countries}" class="hidden"/>
                <form:label path="peliculaForm.filmCountries">País/es</form:label>
                <div class="chips ${errorCountries == 1 ? "invalid" : ""}" id="chips-countries"></div>
                <form:errors path="peliculaForm.filmCountries" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 input-field">
            <spring:bind path="peliculaForm.trailer">
                <form:input path="peliculaForm.trailer" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="peliculaForm.trailer">Tráiler (ID de Youtube)</form:label>
                <form:errors path="peliculaForm.trailer" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 file-field input-field">
            <div class="btn">
                <span>Póster</span>
                <input type="file" name="posterFile" id="posterFile">
            </div>
            <div class="file-path-wrapper">
                <spring:bind path="peliculaForm.poster">
                    <input class="file-path ${errorPoster == 1 ? "invalid" : ""}" type="text" placeholder="Máximo 1 MB">
                    <form:errors path="peliculaForm.poster" cssClass="red-text form-error"></form:errors>
                </spring:bind>
            </div>
        </div>
    </div>
    <div class="section row">
        <div class="col s12">
            <button class="btn waves-effect waves-light center-align" type="submit" name="action">
                Guardar película <i class="material-icons right">send</i>
            </button>
        </div>
    </div>
    <form:hidden path="peliculaForm.score"/>
    <form:hidden path="peliculaForm.nvotes"/>
    <form:hidden path="peliculaForm.views"/>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>

<%@ include file="../_js.jsp"%>

<script>
    $( document ).ready(function() {
        $("select").material_select();

        /*
            INICIALIZACIÓN DE LOS CHIPS
        */
        $('.chips').material_chip();

        var directors = '${mytags:escapeJS(directors)}';
        var directorsInit = textToInitChip(directors);
        var stars = '${mytags:escapeJS(stars)}';
        var starsInit = textToInitChip(stars);
        var supportings = '${mytags:escapeJS(supportings)}';
        var supportingsInit = textToInitChip(supportings);
        var countries = '${mytags:escapeJS(countries)}';
        var countriesInit = textToInitChip(countries);
        /* Se recupera mediante JSON una lista con todos los directores que hay en la BBDD para
           el autocompletado. Se añade esta lista al objeto que inicializa los chips */
        $.getJSON('${path}/rest/directoresJSON', function(data){
            directorsInit.autocompleteData = data.autocompleteData;
            $('#chips-directors').material_chip(directorsInit);
        });

        /* Mismo proceder para los chips de los actores y de los paises. */
        $.getJSON('${path}/rest/actoresJSON', function(data){
            starsInit.autocompleteData = data.autocompleteData;
            $('#chips-stars').material_chip(starsInit);
            supportingsInit.autocompleteData = data.autocompleteData;
            $('#chips-supportings').material_chip(supportingsInit);
        });
        $.getJSON('${path}/rest/paisesJSON', function(data){
            countriesInit.autocompleteData = data.autocompleteData;
            $('#chips-countries').material_chip(countriesInit);
        });

        /* Cada vez que se añade o se borra un chip hay que actualizar su
            input asociado */
        $('#chips-directors').on('chip.add', function (e, chip) {
            var mischips = $('#chips-directors').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmDirectors").val(input);
        });
        $('#chips-directors').on('chip.delete', function (e, chip) {
            var mischips = $('#chips-directors').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmDirectors").val(input);
        });
        $('#chips-stars').on('chip.add', function (e, chip) {
            var mischips = $('#chips-stars').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmStars").val(input);
        });
        $('#chips-stars').on('chip.delete', function (e, chip) {
            var mischips = $('#chips-stars').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmStars").val(input);
        });
        $('#chips-supportings').on('chip.add', function (e, chip) {
            var mischips = $('#chips-supportings').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmSupportings").val(input);
        });
        $('#chips-supportings').on('chip.delete', function (e, chip) {
            var mischips = $('#chips-supportings').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmSupportings").val(input);
        });
        $('#chips-countries').on('chip.add', function (e, chip) {
            var mischips = $('#chips-countries').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmCountries").val(input);
        });
        $('#chips-countries').on('chip.delete', function (e, chip) {
            var mischips = $('#chips-countries').material_chip('data');
            var input = chipsToString(mischips);
            $("#filmCountries").val(input);
        });
    });

    /*
        Funciones de soporte para manejar converiones chips<->formulario
    */

    /* Convertir el array de chips en una cadena de chips separados por comas */
    function chipsToString(chips) {
        var text = "";
        $.each(chips, function (key, chip) {
            text += toUCwords(chip.tag.trim());
            if (key < chips.length - 1)
                text += ", ";
        });
        return text;
    }
    /* Todas las palabras deben comenzar por mayúscula (son nombres propios) */
    function toUCwords(text) {
        var words = text.split(/(\s|-)+/);
        var output = [];
        for (var i = 0, len = words.length; i < len; i += 1) {
            output.push(words[i][0].toUpperCase() +
                words[i].toLowerCase().substr(1));
        }
        return output.join('');
    }
    /* text viene del POST previo desde el formulario.
     Convertimos la cadena de texto (entidades separadas por comas) en el objeto necesario
     para inicializar los chips */
    function textToInitChip(text) {
        var initObj = {};
        if (text.length > 0) {
            var textArr = text.split(",");
            var obj, objs = [];
            for (var i = 0; i < textArr.length; i++) {
                obj = {};
                obj.tag = textArr[i];
                objs.push(obj);
            }
            initObj.data = objs;
        }
        return initObj;
    }
</script>

<%@ include file="../_footer.jsp"%>