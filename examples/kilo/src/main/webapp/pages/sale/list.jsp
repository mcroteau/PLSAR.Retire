

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1 class="left-float">Sales</h1>
<br class="clear"/>

<ocean:if spec="${sales.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Customer</th>
            <th>Details</th>
        </tr>
        <ocean:each items="${sales}" var="sale" varStatus="idx">
            <tr>
                <td>${sale.id}</td>
                <td style="width:150px;">
                    <ocean:if spec="${sale.cart.shipName != '' && sale.cart.shipName != 'null'}">
                        ${sale.cart.shipName}<br/>
                    </ocean:if>
                    <ocean:if spec="${sale.cart.shipPhone != '' && sale.cart.shipPhone != 'null'}">
                        <span class="information">Phone:<br/>
                                ${sale.cart.shipPhone}</span><br/>
                    </ocean:if>
                    <ocean:if spec="${sale.cart.shipEmail != '' && sale.cart.shipEmail != 'null'}">
                        <span class="information">Email:<br/>
                        ${sale.cart.shipEmail}</span>
                    </ocean:if>
                    <span class="tiny">Date: ${sale.prettyDate}</span>
<%--                    <a href="/sales/${business.id}/${sale.id}" class="button orange">Show Details</a>--%>

                    <br/><span class="tiny">Primary Amount : $${sale.convert(sale.primaryAmount)}</span>

                    <ocean:if spec="${sale.affiliateAmount != null}">
                        <br/><span class="tiny">Affiliate Commission : $${sale.convert(sale.affiliateAmount)}</span>
                    </ocean:if>
                </td>
                <td style="padding:0px !important">
                    <table>
                        <tr>
                            <th>Id</th>
                            <th>Item</th>
                            <th>Quantity</th>
                            <th>Price</th>
                            <th>Total</th>
                        </tr>
                        <ocean:each items="${sale.cart.cartItems}" var="cartItem">
                            <tr>
                                <td>${cartItem.item.id}</td>
                                <td>${cartItem.item.name}
                                    <ocean:each items="${cartItem.cartOptions}" var="cartOption">
                                        <br/>
                                        <span class="information">${cartOption.itemOption.name}  ${cartOption.optionValue.value} @ +$${siteService.getPriceDos(cartOption.optionValue.price)}</span>
                                    </ocean:each>
                                </td>
                                <td>${cartItem.quantity}</td>
                                <td>$${siteService.getPriceDos(cartItem.item.price)}</td>
                                <td>$${siteService.getPriceDos(cartItem.itemTotal)}</td>
                            </tr>
                        </ocean:each>
                        <tr>
                            <td colspan="4">Sub Total:</td>
                            <td>${siteService.getPriceDos(sale.cart.subtotal)}</td>
                        </tr>
                        <tr>
                            <td colspan="4">Shipping:</td>
                            <td>
                                $${siteService.getPriceDos(sale.cart.shipping)}
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">Total:</td>
                            <td>$${siteService.getPriceDos(sale.amount)}</td>
                        </tr>
                    </table>
                </td>
            </tr>
        </ocean:each>
    </table>
</ocean:if>

<ocean:if spec="${sales.size() == 0}">
    <p class="notify">No Sales yet. Have no fear clients are on their way!</p>
</ocean:if>
