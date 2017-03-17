<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<%@ include file="../_header.jsp"%>

<div class="section row">
    <h4 class="center-align">Reproduciendo <em>${film.title}</em></h4>
</div>
<div class="row player-container">
    <div class="player-background" style="background-image: url('${path}/img/posters/${film.poster}')">
        <div class="ply-bkg-cover"></div>
    </div>
    <div class="player-spin center-align">
        <div class="preloader-wrapper big active">
            <div class="spinner-layer spinner-blue">
                <div class="circle-clipper left">
                    <div class="circle"></div>
                </div><div class="gap-patch">
                <div class="circle"></div>
            </div><div class="circle-clipper right">
                <div class="circle"></div>
            </div>
            </div>

            <div class="spinner-layer spinner-red">
                <div class="circle-clipper left">
                    <div class="circle"></div>
                </div><div class="gap-patch">
                <div class="circle"></div>
            </div><div class="circle-clipper right">
                <div class="circle"></div>
            </div>
            </div>

            <div class="spinner-layer spinner-yellow">
                <div class="circle-clipper left">
                    <div class="circle"></div>
                </div><div class="gap-patch">
                <div class="circle"></div>
            </div><div class="circle-clipper right">
                <div class="circle"></div>
            </div>
            </div>

            <div class="spinner-layer spinner-green">
                <div class="circle-clipper left">
                    <div class="circle"></div>
                </div><div class="gap-patch">
                <div class="circle"></div>
            </div><div class="circle-clipper right">
                <div class="circle"></div>
            </div>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="progress grey ">
        <div class="determinate teal darken-2"></div>
    </div>
</div>
<%@ include file="../_js.jsp"%>
<script>
    var interval;
    $(document).ready(function () {
        var tic = ${film.duration} * 60;
        interval = setInterval(increaseBar, tic);
    });

    var percent = 0.0;
    function increaseBar() {
        percent = percent + 0.1;
        $('.determinate').width(percent + '%');
        if (percent >= 100.0)
            clearInterval(interval);
    };
</script>
<%@ include file="../_footer.jsp"%>
