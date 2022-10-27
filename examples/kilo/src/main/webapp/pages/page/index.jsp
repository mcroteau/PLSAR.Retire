
<%@ page import="giga.Giga" %>

${siteService.getPageBit(Kilo.HEAD, page, business, request)}

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

${page.content}

${siteService.getPageBit(Kilo.BOTTOM, page, business, request)}
