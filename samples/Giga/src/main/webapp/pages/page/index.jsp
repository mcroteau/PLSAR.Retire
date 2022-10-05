<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getPageBit(Giga.HEAD, page, business, request)}

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

${page.content}

${siteService.getPageBit(Giga.BOTTOM, page, business, request)}
