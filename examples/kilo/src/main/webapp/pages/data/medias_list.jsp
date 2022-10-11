
<%@ page import="kilo.Giga" %>

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

<h1 class="left-float">Item Image Imports</h1>
<a href="/import/media/${business.id}" class="button retro right-float" style="margin-top:20px;">New Item Image Import</a>
<br class="clear"/>

<p>What is an item image import? An item image import is an image import that can be easily converted
into items.</p>

<plsar:if spec="${imports.size() > 0}">
    <table>
        <tr>
<%--            <th>Id</th>--%>
<%--            <th>Name</th>--%>
<%--            <th>Url</th>--%>
<%--            <th>Type</th>--%>
            <th>Date</th>
            <th></th>
        </tr>
        <c:forEach var="import" items="${imports}" varStatus="idx">
            <tr>
                <td>
                    ${import.dateImport}
                </td>
<%--                <td>${asset.id}</td>--%>
<%--                <td>${asset.name}<br/>${asset.meta}</td>--%>
<%--                <td style="width:60px;">--%>
<%--                    <plsar:if spec="${asset.type == 'media'}">--%>
<%--                        <img src="/media/${asset.meta}" style="width:150px;border-radius:12px;"/>--%>
<%--                        <a href="/media/${asset.meta}" target="_blank">/media/${asset.meta}</a>--%>
<%--                    </plsar:if>--%>
<%--                    <plsar:if spec="${asset.type != 'media'}">--%>
<%--                        <a href="/go/${asset.meta}" target="_blank">/go/${asset.meta}</a>--%>
<%--                    </plsar:if>--%>
<%--                </td>--%>
<%--                <td>--%>
<%--                    <label>Name</label>--%>
<%--                    <input type="text" placeholder="Name"/>--%>
<%--                    <label>Price</label>--%>
<%--                    <input type="text" placeholder="Price"/>--%>
<%--                    <label>Quantity</label>--%>
<%--                    <input type="text" placeholder="Quantity"/>--%>
<%--                    <label>Weight</label>--%>
<%--                    <input type="text" placeholder="Weight" value="16"/>--%>
<%--                </td>--%>
<%--                <td>${asset.type}</td>--%>
<%--                <td>--%>
<%--                    <form action="/assets/delete/${business.id}/${asset.id}" method="post" m onsubmit="return confirm('Are you sure you want to delete this asset?');">--%>
<%--                        <input type="submit" value="Delete" class="button remove"/>--%>
<%--                    </form>--%>
<%--                </td>--%>
            </tr>
        </c:forEach>
    </table>
</plsar:if>

<plsar:if spec="${imports == null || imports.size() == 0}">
    <p class="notify">No item image imports added yet! <a href="/import/media/${business.id}" class="href-dotted">New Import</a></p>
</plsar:if>
