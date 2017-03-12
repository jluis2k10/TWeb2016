<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>

<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="_header.jsp"%>

<div class="section row">
    <div class="col s12">
        <h5>Nueva película</h5>
    </div>
</div>
<form action="${path}/admin/catalogo/nueva" method="post" enctype="multipart/form-data" id="new-film">
    <%-- Para acceder a los errores fuera de las etiquetas del formulario --%>
    <spring:hasBindErrors name="nuevaPeliculaForm">
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
            <spring:bind path="nuevaPeliculaForm.title">
                <form:input path="nuevaPeliculaForm.title" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="nuevaPeliculaForm.title">Título</form:label>
                <form:errors path="nuevaPeliculaForm.title" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s4 input-field">
            <spring:bind path="nuevaPeliculaForm.year">
                <form:input path="nuevaPeliculaForm.year" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="nuevaPeliculaForm.year">Año</form:label>
                <form:errors path="nuevaPeliculaForm.year" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 input-field">
            <spring:bind path="nuevaPeliculaForm.duration">
                <form:input path="nuevaPeliculaForm.duration" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="nuevaPeliculaForm.duration">Duración (min)</form:label>
                <form:errors path="nuevaPeliculaForm.duration" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 input-field">
            <spring:bind path="nuevaPeliculaForm.rating">
                <form:select path="nuevaPeliculaForm.rating" cssErrorClass="invalid">
                    <form:option value="TP">TP</form:option>
                    <form:option value="7+">7+</form:option>
                    <form:option value="12+">12+</form:option>
                    <form:option value="16+">16+</form:option>
                    <form:option value="18+">18+</form:option>
                    <form:option value="X">X</form:option>
                </form:select>
                <form:label path="nuevaPeliculaForm.rating">Edades</form:label>
                <form:errors path="nuevaPeliculaForm.rating" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="nuevaPeliculaForm.description">
                <form:textarea path="nuevaPeliculaForm.description" cssClass="materialize-textarea" cssErrorClass="materialize-textarea invalid"></form:textarea>
                <form:label path="nuevaPeliculaForm.description">Sinopsis</form:label>
                <form:errors path="nuevaPeliculaForm.description" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s6 input-field" id="genresContainer">
            <spring:bind path="nuevaPeliculaForm.filmGenres">
                <form:select path="nuevaPeliculaForm.filmGenres" cssErrorClass="invalid" multiple="true">
                    <form:options items="${genres}" itemValue="name" itemLabel="name"/>
                </form:select>
                <form:label path="nuevaPeliculaForm.filmGenres" cssClass="active">Géneros</form:label>
                <form:errors path="nuevaPeliculaForm.filmGenres" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s6">
            <p>Si no está disponible, puedes <a href="${path}/admin/catalogo/nuevoGenero">añadir una nueva categoría</a>.</p>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="nuevaPeliculaForm.filmDirectors">
                <c:forEach items="${nuevaPeliculaForm.filmDirectors}" var="director" varStatus="loopStatus">
                    <c:set var="directors" value="${loopStatus.first ? '' : directors}${director.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmDirectors" name="filmDirectors" value="${directors}" class="hidden"/>
                <form:label path="nuevaPeliculaForm.filmDirectors">Directores</form:label>
                <div class="chips ${errorDirectors == 1 ? "invalid" : ""}" id="chips-directors"></div>
                <form:errors path="nuevaPeliculaForm.filmDirectors" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="nuevaPeliculaForm.filmStars">
                <c:forEach items="${nuevaPeliculaForm.filmStars}" var="star" varStatus="loopStatus">
                    <c:set var="stars" value="${loopStatus.first ? '' : stars}${star.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmStars" name="filmStars" value="${stars}" class="hidden"/>
                <form:label path="nuevaPeliculaForm.filmStars">Actores Principales</form:label>
                <div class="chips ${errorStars == 1 ? "invalid" : ""}" id="chips-stars"></div>
                <form:errors path="nuevaPeliculaForm.filmStars" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s12 input-field">
            <spring:bind path="nuevaPeliculaForm.filmSupportings">
                <c:forEach items="${nuevaPeliculaForm.filmSupportings}" var="supporting" varStatus="loopStatus">
                    <c:set var="supportings" value="${loopStatus.first ? '' : supportings}${supporting.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmSupportings" name="filmSupportings" value="${supportings}" class="hidden"/>
                <form:label path="nuevaPeliculaForm.filmSupportings">Actores Secundarios</form:label>
                <div class="chips" id="chips-supportings"></div>
            </spring:bind>
        </div>
    </div>
    <div class="row">
        <div class="col s4 input-field">
            <spring:bind path="nuevaPeliculaForm.filmCountries">
                <c:forEach items="${nuevaPeliculaForm.filmCountries}" var="country" varStatus="loopStatus">
                    <c:set var="countries" value="${loopStatus.first ? '' : countries}${country.name}${loopStatus.last ? '' : ', '}"></c:set>
                </c:forEach>
                <input type="text" id="filmCountries" name="filmCountries" value="${countries}" class="hidden"/>
                <form:label path="nuevaPeliculaForm.filmCountries">País/es</form:label>
                <div class="chips ${errorCountries == 1 ? "invalid" : ""}" id="chips-countries"></div>
                <form:errors path="nuevaPeliculaForm.filmCountries" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 input-field">
            <spring:bind path="nuevaPeliculaForm.trailer">
                <form:input path="nuevaPeliculaForm.trailer" type="text" cssErrorClass="invalid"></form:input>
                <form:label path="nuevaPeliculaForm.trailer">Tráiler (ID de Youtube)</form:label>
                <form:errors path="nuevaPeliculaForm.trailer" cssClass="red-text form-error"></form:errors>
            </spring:bind>
        </div>
        <div class="col s4 file-field input-field">
            <div class="btn">
                <span>Póster</span>
                <input type="file" name="posterFile" id="posterFile">
            </div>
            <div class="file-path-wrapper">
                <spring:bind path="nuevaPeliculaForm.poster">
                    <input class="file-path ${errorPoster == 1 ? "invalid" : ""}" type="text" placeholder="Máximo 1 MB">
                    <form:errors path="nuevaPeliculaForm.poster" cssClass="red-text form-error"></form:errors>
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
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>

<%@ include file="../_js.jsp"%>

<script>
    $( document ).ready(function() {
        /*
            INICIALIZACIÓN DE LOS CHIPS
        */
        $('.chips').material_chip();

        var directors = '${directors}';
        var directorsInit = textToInitChip(directors);
        var stars = '${stars}';
        var starsInit = textToInitChip(stars);
        var supportings = '${supportings}';
        var supportingsInit = textToInitChip(supportings);
        var countries = '${countries}';
        var countriesInit = textToInitChip(countries);
        /* Se recupera mediante JSON una lista con todos los directores que hay en la BBDD para
           el autocompletado. Se añade esta lista al objeto que inicializa los chips */
        $.getJSON('${path}/admin/directoresJSON', function(data){
            directorsInit.autocompleteData = data.autocompleteData;
            $('#chips-directors').material_chip(directorsInit);
        });

        /* Mismo proceder para los chips de los actores y de los paises. */
        $.getJSON('${path}/admin/actoresJSON', function(data){
            starsInit.autocompleteData = data.autocompleteData;
            $('#chips-stars').material_chip(starsInit);
            supportingsInit.autocompleteData = data.autocompleteData;
            $('#chips-supportings').material_chip(supportingsInit);
        });
        $.getJSON('${path}/admin/paisesJSON', function(data){
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