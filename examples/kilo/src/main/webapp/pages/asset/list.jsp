
<%@ page import="giga.Giga" %>

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1 class="left-float">Web Assets</h1>
<a href="/assets/new/${business.id}" class="button retro right-float" style="margin-top:20px;">New Asset</a>
<br class="clear"/>

<ocean:if spec="${assets.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Url</th>
            <th>Type</th>
            <th></th>
        </tr>
        <ocean:each var="asset" items="${assets}" varStatus="idx">
            <tr>
                <td>${asset.id}
                    <span class="tiny">${asset.uri}</span></td>
                <td>${asset.name}<br/>${asset.meta}</td>
                <td style="width:60px;">
                    <ocean:if spec="${asset.type == 'media'}">
                        <img src="/media/${asset.meta}" style="width:150px;border-radius:12px;"/>
                        <a href="/media/${asset.meta}" target="_blank">/media/${asset.meta}</a>
                    </ocean:if>
                    <ocean:if spec="${asset.type != 'media'}">
                        <a href="/go/${asset.meta}" target="_blank">/go/${asset.meta}</a>
                    </ocean:if>
                </td>
                <td>${asset.type}</td>
                <td>
                    <form action="/assets/delete/${business.id}/${asset.id}" method="post" m onsubmit="return confirm('Are you sure you want to delete this asset?');">
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </ocean:each>
    </table>
</ocean:if>

<ocean:if spec="${assets == null || assets.size() == 0}">
    <p class="notify">No assets added yet! <a href="/assets/new/${business.id}" class="href-dotted">New Asset</a></p>
</ocean:if>
