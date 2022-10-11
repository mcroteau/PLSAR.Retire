
<%@ page import="giga.Giga" %>

${siteService.getPageBit(Giga.HEAD, page, business, request)}

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

${page.content}

${siteService.getPageBit(Giga.BOTTOM, page, business, request)}
