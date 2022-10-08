<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>


<h1 class="left-float">Designs</h1>

<a href="${pageContext.request.contextPath}/designs/new/${business.id}" class="button retro right-float">New Design</a>
<br class="clear"/>

<c:if test="${designs.size() > 0}">
    <table>
        <tr>
            <th>Name</th>
            <th>Default</th>
            <th></th>
        </tr>
        <c:forEach var="design" items="${designs}" varStatus="idx">
            <tr>
                <td>${design.name}</td>
                <td>
                    <c:if test="${design.baseDesign}">
                        &check;
                    </c:if>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/designs/delete/${design.id}" method="post">
                        <a href="${pageContext.request.contextPath}/designs/edit/${design.id}" class="button orange">Edit</a>
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>
