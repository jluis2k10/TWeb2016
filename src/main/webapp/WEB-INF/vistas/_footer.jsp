<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="year" value="${now}" pattern="yyyy"/>

<footer class="page-footer">
    <div class="footer-copyright">
        <div class="container">
            © ${year} Copyleft uned.es
        </div>
    </div>
</footer>
</body>
</html>