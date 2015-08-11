<%@ page import="ru.yandex.qatools.allure.AllureConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="allureAdminVersions" type="ru.yandex.qatools.allure.Info" scope="request"/>

<div>YOYOYO</div>
<div>
    <c:forEach var="yo" items="${allureAdminVersions.items}">
        <div>${yo}</div>
    </c:forEach>
</div>