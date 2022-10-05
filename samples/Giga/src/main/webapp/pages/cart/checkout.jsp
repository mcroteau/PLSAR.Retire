<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<style>
</style>

<div id="cart-wrapper">
    <c:if test="${items.size() > 0}">
        <div style="text-align: center">
            <div id="step-by-step">
                <a href="${pageContext.request.contextPath}/${business.uri}/cart" class="step left-float">1</a>
                <div class="step-increment previous left-float"></div>
                <c:if test="${business.flatShipping}">
                    <a href="${pageContext.request.contextPath}/${business.uri}/shipment/create" class="step left-float">2</a>
                </c:if>
                <c:if test="${!business.flatShipping}">
                    <a href="${pageContext.request.contextPath}/${business.uri}/shipment" class="step left-float">2</a>
                </c:if>
                <div class="step-increment left-float"></div>
                <a href="${pageContext.request.contextPath}/${business.uri}/checkout" class="step active left-float">3</a>
                <br class="clear"/>
            </div>
        </div>
    </c:if>

    <h1 class="align-left">Checkout</h1>

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

        <div class="left-float" style="width:30%;">
            <strong style="margin:20px 0px 10px;display:block;font-size: 19px;">Shipping</strong>
            <address style="line-height: 1.3em;font-size:19px;">
                    ${cart.get(cart.shipName)} <br/>
                    ${cart.get(cart.shipStreet)}<br/>
                    ${cart.get(cart.shipCity)}, ${cart.get(cart.shipState)}<br/>
                    ${cart.get(cart.shipZip)}<br/>
                    ${cart.get(cart.shipCountry)}<br/>
                    ${cart.get(cart.shipEmail)}
                    Phone: ${cart.get(cart.shipPhone)}<br/>

                    <a href="${pageContext.request.contextPath}/${business.uri}/shipment/create">Edit Address</a>

            </address>
        </div>

        <div class="right-float" style="width:50%;">
            <form action="${pageContext.request.contextPath}/${business.uri}/sale/${cart.id}" method="post">

                <input type="hidden" name="chargeAmount" value="${cart.total}"/>

                <div id="credit-card-wrapper">
                    <label>
                        <strong>Payment Information</strong>
                        <br/>
                        Credit Card
                    </label>
                    <input type="text" name="card" value="" id="card">

                    <div class="left-float" style="width:40%;">
                        <label>Exp Month</label>
                        <input type="text" name="expMonth" value="" id="exp-month">
                    </div>

                    <div class="left-float" style="width:40%;margin-left:12px">
                        <label>Exp Year</label>
                        <input type="text" name="expYear" value="" id="exp-year">
                    </div>
                    <br class="clear"/>

                    <label>CVC</label>
                    <input type="text" name="cvc" value="" id="cvc" style="width:40%;">
                </div>

                <style>
                    #credit-card-wrapper{
                        width:100%;
                        float:right;
                        padding:10px;
                    }
                    #credit-card-wrapper label{margin:4px 0px 0px;}
                </style>

                <br class="clear"/>

                <div class="align-right" style="margin:30px 0px 170px;">
                    <input type="submit"
                           value="Place Order $${siteService.getPriceTres(cart.total)}" class="button green gigantic" id="process"
                           onclick="this.disabled=true;this.value='Processing Order...';this.form.submit();"/>
                </div>
            </form>
        </div>

    </c:if>
    <c:if test="${items.size() == 0}">
        <p>No items added to your cart yet!</p>
    </c:if>

</div>

${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}

