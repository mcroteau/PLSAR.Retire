
<%@ page import="kilo.Giga" %>

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

<h1 class="left-float">Web Assets</h1>
<a href="/assets/new/${business.id}" class="button retro right-float" style="margin-top:20px;">New Asset</a>
<br class="clear"/>

<plsar:if spec="${assets.size() > 0}">
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
                    <plsar:if spec="${asset.type == 'media'}">
                        <img src="/media/${asset.meta}" style="width:150px;border-radius:12px;"/>
                        <a href="/media/${asset.meta}" target="_blank">/media/${asset.meta}</a>
                    </plsar:if>
                    <plsar:if spec="${asset.type != 'media'}">
                        <a href="/go/${asset.meta}" target="_blank">/go/${asset.meta}</a>
                    </plsar:if>
                </td>
                <td>${asset.type}</td>
                <td>
                    <form action="/assets/delete/${business.id}/${asset.id}" method="post" m onsubmit="return confirm('Are you sure you want to delete this asset?');">
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</plsar:if>

<plsar:if spec="${assets == null || assets.size() == 0}">
    <p class="notify">No assets added yet! <a href="/assets/new/${business.id}" class="href-dotted">New Asset</a></p>
</plsar:if>
