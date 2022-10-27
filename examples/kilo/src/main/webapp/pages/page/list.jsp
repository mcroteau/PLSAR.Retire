
<%@ page import="giga.Giga" %>

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1 class="left-float">Site Pages</h1>
<a href="/pages/new/${business.id}" class="button retro right-float" style="margin-top:20px;">New Page</a>
<br class="clear"/>

<ocean:if spec="${pages.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Url</th>
            <th></th>
        </tr>
        <ocean:each var="page" items="${pages}" varStatus="idx">
            <tr>
                <td>${page.id}</td>
                <td><a href="/${business.uri}/${page.uri}" class="href-dotted-black" target="_blank">${page.name}</a></td>
                <td>${page.uri}</td>
                <td>
                    <form action="/pages/delete/${business.id}/${page.id}" method="post" onsubmit="return confirm('Are you sure you want to delete this page?');">
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </ocean:each>
    </table>
</ocean:if>


<ocean:if spec="${pages.size() == 0}">
    <p class="notify">No pages added yet! <a href="/pages/new/${business.id}" class="href-dotted">New Page</a></p>
</ocean:if>
