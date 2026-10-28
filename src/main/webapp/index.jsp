<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${not empty sessionScope.usuarioLogueado}">
        <c:redirect url="/calculadora"/>
    </c:when>
    <c:otherwise>
        <c:redirect url="/auth?action=login"/>
    </c:otherwise>
</c:choose>
