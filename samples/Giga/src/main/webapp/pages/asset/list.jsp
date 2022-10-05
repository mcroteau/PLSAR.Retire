<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<h1 class="left-float">Web Assets</h1>
<a href="${pageContext.request.contextPath}/assets/new/${business.id}" class="button retro right-float" style="margin-top:20px;">New Asset</a>
<br class="clear"/>

<c:if test="${assets.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Url</th>
            <th>Type</th>
            <th></th>
        </tr>
        <c:forEach var="asset" items="${assets}" varStatus="idx">
            <tr>
                <td>${asset.id}
                    <span class="tiny">${asset.uri}</span></td>
                <td>${asset.name}<br/>${asset.meta}</td>
                <td style="width:60px;">
                    <c:if test="${asset.type == 'media'}">
                        <img src="${pageContext.request.contextPath}/media/${asset.meta}" style="width:150px;border-radius:12px;"/>
                        <a href="${pageContext.request.contextPath}/media/${asset.meta}" target="_blank">${pageContext.request.contextPath}/media/${asset.meta}</a>
                    </c:if>
                    <c:if test="${asset.type != 'media'}">
                        <a href="${pageContext.request.contextPath}/go/${asset.meta}" target="_blank">${pageContext.request.contextPath}/go/${asset.meta}</a>
                    </c:if>
                </td>
                <td>${asset.type}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/assets/delete/${business.id}/${asset.id}" method="post" m onsubmit="return confirm('Are you sure you want to delete this asset?');">
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<c:if test="${assets == null || assets.size() == 0}">
    <p class="notify">No assets added yet! <a href="${pageContext.request.contextPath}/assets/new/${business.id}" class="href-dotted">New Asset</a></p>
</c:if>
