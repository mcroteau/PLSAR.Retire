<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<h1 class="left-float">Site Pages</h1>
<a href="${pageContext.request.contextPath}/pages/new/${business.id}" class="button retro right-float" style="margin-top:20px;">New Page</a>
<br class="clear"/>

<c:if test="${pages.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Url</th>
            <th></th>
        </tr>
        <c:forEach var="page" items="${pages}" varStatus="idx">
            <tr>
                <td>${page.id}</td>
                <td><a href="${pageContext.request.contextPath}/${business.uri}/${page.uri}" class="href-dotted-black" target="_blank">${page.name}</a></td>
                <td>${page.uri}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/pages/delete/${business.id}/${page.id}" method="post" m onsubmit="return confirm('Are you sure you want to delete this page?');">
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>


<c:if test="${pages.size() == 0}">
    <p class="notify">No pages added yet! <a href="${pageContext.request.contextPath}/pages/new/${business.id}" class="href-dotted">New Page</a></p>
</c:if>
