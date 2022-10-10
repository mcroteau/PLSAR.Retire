
<%@ page import="giga.Giga" %>

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

<h1 class="left-float">Categories</h1>
<a href="/categories/new/${business.id}" class="button retro right-float">New Category</a>
<br class="clear"/>

<plsar:if spec="${categories.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Uri</th>
            <th></th>
        </tr>
        <c:forEach var="category" items="${categories}" varStatus="idx">
            <tr>
                <td>${category.id}</td>
                <td>${category.businessId}</td>
                <td><a href="/categories/edit/${business.id}/${category.id}" class="href-dotted">${category.name}</a></td>
                <td>${category.uri}</td>
                <td>
                    <form action="/categories/delete/${business.id}/${category.id}" method="post" m onsubmit="return confirm('Are you sure you want to delete this category? All Items will be deleted for this category as well.');">
                        <a href="/categories/edit/${business.id}/${category.id}" class="button orange">Edit</a>
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</plsar:if>


<plsar:if spec="${categories.size() == 0}">
    <p class="notify">No category added yet! <a href="/categories/new/${business.id}" class="href-dotted">New Cateogry</a></p>
</plsar:if>
