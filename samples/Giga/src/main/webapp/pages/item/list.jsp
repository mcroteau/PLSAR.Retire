<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<h1 class="left-float">${title}</h1>

<a href="${pageContext.request.contextPath}/items/grid/${business.id}" class="button orange right-float" style="margin-top:20px">Power Grid</a>
<a href="${pageContext.request.contextPath}/items/new/${business.id}" class="button retro right-float" style="margin-top:20px">New Item</a>
<br class="clear"/>

<c:if test="${items.size() > 0}">
    <table>
        <tr>
            <th>Name</th>
            <th>Price</th>
            <th>Quantity</th>
            <th></th>
        </tr>
        <c:forEach var="item" items="${items}" varStatus="idx">
            <tr>
                <td style="width:150px;">${item.name}</td>
                <td>$${siteService.getPriceTres(item.price)}</td>
                <td>${item.quantity}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/items/delete/${item.businessId}/${item.id}" method="post">
                        <a href="${pageContext.request.contextPath}/items/options/${business.id}/${item.id}" class="button orange">Options</a>
                        <a href="${pageContext.request.contextPath}/items/edit/${business.id}/${item.id}" class="button retro">Edit</a>
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>

    <a href="${pageContext.request.contextPath}/items/inactive/${business.id}" class="button modern right-float" style="margin-top:20px">Inactive Items</a>
    <br class="clear"/>

</c:if>
<c:if test="${items.size() == 0}">
    <p class="notify">No ${title} added yet! <a href="${pageContext.request.contextPath}/items/new/${business.id}" class="href-dotted">New Item</a></p>
</c:if>
