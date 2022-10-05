<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}


    <c:if test="${not empty message}">
        <p class="notify">${message}</p>
    </c:if>

    <h1>My Orders</h1>

    <p>A recap for all the orders placed for ${business.name}</p>

    <c:if test="${sales.size() > 0}">
        <table>
            <tr>
                <th>Id</th>
                <th>Date</th>
                <th>Details</th>
            </tr>
            <c:forEach var="sale" items="${sales}" varStatus="idx">
                <tr>
                    <td>${sale.id}</td>
                    <td style="width:150px;">
                        <strong>${sale.prettyDate}</strong>
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
                            <c:forEach items="${sale.cart.cartItems}" var="cartItem">
                                <tr>
                                    <td>${cartItem.item.id}</td>
                                    <td>${cartItem.item.name}
                                        <c:forEach items="${cartItem.cartOptions}" var="cartOption">
                                            <br/>
                                            <span class="information">${cartOption.itemOption.name}  ${cartOption.optionValue.value} @ +$${siteService.getPrice(cartOption.optionValue.price)}</span>
                                        </c:forEach>
                                    </td>
                                    <td>${cartItem.quantity}</td>
                                    <td>$${siteService.getPrice(cartItem.item.price)}</td>
                                    <td>$${siteService.getPrice(cartItem.itemTotal)}</td>
                                </tr>
                            </c:forEach>
                            <tr>
                                <td colspan="4">Sub Total:</td>
                                <td>$${siteService.getPrice(sale.cart.subtotal)}</td>
                            </tr>
                            <tr>
                                <td colspan="4">Shipping:</td>
                                <td>
                                    $${siteService.getPrice(sale.cart.shipping)}
                                </td>
                            </tr>
                            <tr>
                                <td colspan="4">Total:</td>
                                <td>${siteService.getPrice(sale.cart.total)}</td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <c:if test="${sales.size() == 0}">
        <p class="notify">Nothing to see yet! No orders placed.</p>
    </c:if>



${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}