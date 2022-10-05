<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="chico" uri="/META-INF/tags/chico.tld" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<style>
    #step-by-step{margin:10px auto 30px;width:370px;}
    .step{border-radius:20px;padding:14px; font-size: 23px; font-weight: 900;display:inline-block;background-color: #e4e8e9}
    .active{background:#FF817A; color:#fff;}
    .step-increment{margin:21px 10px;height:5px; width:100px; background-color: #efefef}
</style>

<div id="cart-wrapper">

    <c:if test="${items.size() > 0}">
        <div style="text-align: center">
            <div id="step-by-step">
                <div class="step active left-float">1</div>
                <div class="step-increment previous left-float"></div>
                <div class="step left-float">2</div>
                <div class="step-increment left-float"></div>
                <div class="step left-float">3</div>
                <br class="clear"/>
            </div>
        </div>
    </c:if>

    <h1 class="align-left">Shopping Cart</h1>

    <c:if test="${items.size() > 0}">
        <table>
            <tr>
                <th>Id</th>
                <th>Item</th>
                <th>Quantity</th>
                <th>Price</th>
                <th>Total</th>
            </tr>
            <c:forEach items="${items}" var="cartItem">
                <tr>
                    <td>${cartItem.itemId}</td>
                    <td>${cartItem.item.name}
                        <c:forEach items="${cartItem.cartOptions}" var="cartOption">
                            <br/>
                            <span class="cart-option">${cartOption.itemOption.name}  ${cartOption.optionValue.value} @ +$${siteService.getPriceDos(cartOption.optionValue.price)}</span>
                        </c:forEach>
                        <span class="information">${cartItem.item.weight}oz</span>
                    </td>
                    <td>${cartItem.quantity}</td>
                    <td>$${siteService.getPriceDos(cartItem.price)}</td>
                    <td>$${siteService.getPriceDos(cartItem.itemTotal)}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/${business.uri}/cart/minus/${cartItem.id}" method="post">
                            <input type="submit" value="x" class="button light"/>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="4">Sub Total:</td>
                <td>$${siteService.getPriceDos(cart.subtotal)}</td>
            </tr>
            <tr>
                <td colspan="4">Shipping:</td>
                <td>
                    <c:if test="${!business.flatShipping}">
                        <c:if test="${cart.validAddress}">
                            $${siteService.getPriceDos(rate.rate)}
                            ${rate.carrier}, est days ${rate.deliveryDays}
                        </c:if>
                        <c:if test="${!cart.validAddress}">
                            <a href="${pageContext.request.contextPath}/${business.uri}/shipment" id="choose-shipping">Choose Shipping!</a>
                        </c:if>
                    </c:if>
                    <c:if test="${business.flatShipping}">
                        $${siteService.getPriceDos(business.shipping)}
                    </c:if>
                </td>
            </tr>
            <tr>
                <td colspan="4">Total:</td>
                <td>$${siteService.getPriceDos(cart.total)}</td>
            </tr>
        </table>

        <c:if test="${!business.flatShipping && !cart.validAddress}">
            <div class="align-right" style="margin-top:20px">
                <a href="${pageContext.request.contextPath}/${business.uri}/shipment" id="choose-shipping-dos" class="button orange">Choose Shipping!</a>
            </div>
        </c:if>

        <c:if test="${business.flatShipping}">
            <br/>
            <div class="align-right">
                <a href="${pageContext.request.contextPath}/${business.uri}/shipment/create" class="button orange">Continue Step 2 : Shipping</a>
            </div>
        </c:if>

        <c:if test="${cart.validAddress}">
            <div class="align-right" style="margin:20px 90px;">or</div>
            <div class="align-right">
                <a href="${pageContext.request.contextPath}/${business.uri}/checkout" class="button green">Continue Step 3 : Checkout</a>
            </div>

            <h3 class="align-left">Ship Address</h3>
            <address style="line-height:1.2em;">
                ${cart.get(cart.shipName)} <br/>
                ${cart.get(cart.shipStreet)}<br/>
                ${cart.get(cart.shipCity)}, ${cart.get(cart.shipState)}<br/>
                ${cart.get(cart.shipZip)}<br/>
                ${cart.get(cart.shipCountry)}<br/>
                ${cart.get(cart.shipEmail)}
                Phone: ${cart.get(cart.shipPhone)}<br/>
                <a href="${pageContext.request.contextPath}/${business.uri}/shipment/create">Edit Address</a>
            </address>


        </c:if>


    </c:if>
    <c:if test="${items.size() == 0}">
        <p>No items added to your cart yet!</p>
    </c:if>

</div>

${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}

