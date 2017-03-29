<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="../_header.jsp"%>

<div class="section"></div>
<div class="section row">
    <div class="col s8 offset-s2">
        <div class="row">
            <div class="col s12">
                <h5>Refinar búsqueda</h5>
            </div>
            <form method="get" action="${path}/buscar">
                <div class="input-field col s4 right">
                    <select name="ref" id="campo">
                        <option value="" disabled selected>Selecciona campo</option>
                        <option value="titulo">Título</option>
                        <option value="actor">Actor</option>
                        <option value="director">Director</option>
                        <option value="genero">Género</option>
                        <option value="pais">País</option>
                        <option value="fecha">Año</option>
                        <option value="puntuacion">Puntuación</option>
                    </select>
                    <label>Buscar por</label>
                </div>
                <div class="input-field col s8 termino">
                    <i class="material-icons prefix">search</i>
                    <input id="termino" name="buscar" type="text">
                    <label for="termino">Término de búsqueda</label>
                </div>
                <p class="range-field col s8 rango" style="display: none;">
                    <label for="rango">Puntuación (estrellas)</label>
                    <input type="range" id="rango" name="buscar" min="0" max="5" disabled/>
                </p>
                <div class="col s12">
                    <button class="btn waves-effect waves-light center-align" type="submit" disabled>
                        Buscar <i class="material-icons right">send</i>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="../_js.jsp"%>

<script>
    $(document).ready(function() {
        $('select').material_select();

        /*
            Activar botón Buscar al elegir una opción de búsqueda y mostrar/ocultar,
            activar/desactivar el slider/input de texto en función de si selecciona
            buscar por puntuación u otro campo cualquiera.
         */
        $('select#campo').on('change', function () {
            $('button[type="submit"]').prop('disabled', false);
            if (this.value == "puntuacion") {
                $('p.rango').show();
                $('div.termino').hide();
                $('input#rango').prop('disabled', false);
                $('input#termino').prop('disabled', true);
            } else {
                $('p.rango').hide();
                $('div.termino').show();
                $('input#rango').prop('disabled', true);
                $('input#termino').prop('disabled', false);
            }
        });
    });
</script>

<%@ include file="../_footer.jsp"%>