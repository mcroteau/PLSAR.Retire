

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1 class="left-float">${title}</h1>

<a href="/items/grid/${business.id}" class="button orange right-float" style="margin-top:20px">Power Grid</a>
<a href="/items/new/${business.id}" class="button retro right-float" style="margin-top:20px">New Item</a>
<br class="clear"/>

<ocean:if spec="${items.size() > 0}">
    <table>
        <tr>
            <th>Name</th>
            <th>Price</th>
            <th>Quantity</th>
            <th></th>
        </tr>
        <ocean:each var="item" items="${items}" varStatus="idx">
            <tr>
                <td style="width:150px;">${item.name}</td>
                <td>$${siteService.getPriceTres(item.price)}</td>
                <td>${item.quantity}</td>
                <td>
                    <form action="/items/delete/${item.businessId}/${item.id}" method="post">
                        <a href="/items/options/${business.id}/${item.id}" class="button orange">Options</a>
                        <a href="/items/edit/${business.id}/${item.id}" class="button retro">Edit</a>
                        <input type="submit" value="Delete" class="button remove"/>
                    </form>
                </td>
            </tr>
        </ocean:each>
    </table>

    <a href="/items/inactive/${business.id}" class="button modern right-float" style="margin-top:20px">Inactive Items</a>
    <br class="clear"/>

</ocean:if>
<ocean:if spec="${items.size() == 0}">
    <p class="notify">No ${title} added yet! <a href="/items/new/${business.id}" class="href-dotted">New Item</a></p>
</ocean:if>
