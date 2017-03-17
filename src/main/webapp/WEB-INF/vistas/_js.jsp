<%@ page contentType="text/html;charset=UTF-8" language="java" %>
</div> <!-- /container -->
<script type="text/javascript" src="${path}/js/jquery-2.2.4.min.js"></script>
<script type="text/javascript" src="${path}/js/materialize.min.js"></script>
<script>
    $( document ).ready(function() {
        $(".dropdown-generos").dropdown({
            belowOrigin: true,
            constrainWidth: false
        });
        $(".dropdown-account").dropdown({
            belowOrigin: true,
            constrainWidth: false
        });
        $('.tooltipped').tooltip({
            delay: 20
        });
    });
</script>
