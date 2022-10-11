
<%@ page import="kilo.Giga" %>

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

<h1 class="left-float">Site Pages</h1>
<a href="/pages/new/${business.id}" class="button retro right-float" style="margin-top:20px;">New Page</a>
<br class="clear"/>

<plsar:if spec="${pages.size() > 0}">
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
                <td><a href="/${business.uri}/${page.uri}" class="href-dotted-black" target="_blank">${page.name}</a></td>
                <td>${page.uri}</td>
                <td>
                    <form action="/pages/delete/${business.id}/${page.id}" method="post" m onsubmit="return confirm('Are you sure you want to delete this page?');">
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</plsar:if>


<plsar:if spec="${pages.size() == 0}">
    <p class="notify">No pages added yet! <a href="/pages/new/${business.id}" class="href-dotted">New Page</a></p>
</plsar:if>
