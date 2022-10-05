<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

    <h1>Congratulations!</h1>
    <h2>Order Placed! $${siteService.getPriceDos(sale.amount)}</h2>

    <p>Your order has been placed and should be delivered within ${rate.deliveryDays} days after being received.</p>

    <p>The following items are in your order:</p>

    <style>
        .cart-item-wrapper{
            padding:30px;
            border-radius: 19px;
            margin:20px 0px 20px 0px;
            text-align: center;
        }
        .cart-item-wrapper img{
            width:150px;
            border-radius: 19px;
            margin:0px auto;
        }
    </style>
    <c:forEach items="${cart.cartItems}" var="cartItem">
        <div class="cart-item-wrapper">
            <img src="${cartItem.item.imageUri}" />
            <h2>${cartItem.item.name}</h2>
            <h3>$${siteService.getPrice(cartItem.item.price)}</h3>
            <p>Quantity : ${cartItem.quantity}</p>
            <c:forEach items="${cartItem.cartOptions}" var="cartOption">
                <br/>
                <span class="cart-option">${cartOption.itemOption.name}  ${cartOption.optionValue.value}</span>
            </c:forEach>
        </div>
    </c:forEach>

    <h2 class="align-center">Ship Address</h2>
    <address class="align-center">
        ${cart.get(cart.shipName)} <br/>
        ${cart.get(cart.shipStreet)}<br/>
        ${cart.get(cart.shipCity)}, ${cart.get(cart.shipState)}<br/>
        ${cart.get(cart.shipZip)}<br/>
        ${cart.get(cart.shipCountry)}<br/>
        ${cart.get(cart.shipEmail)}
        Phone: ${cart.get(cart.shipPhone)}<br/>
    </address>

    <style>
        #details input{font-size:9px !important;width:10px;}
        #details input:hover{font-size:9px !important;}
    </style>
    <div id="details" class="align-center">
        <br/>
        <input type="text" id="amount" value="${sale.amount}" disabled/>
        <input type="text" id="primary-amount" value="${sale.primaryAmount}" disabled/>
        <input type="text" id="affiliate-amount" value="${sale.affiliateAmount}" disabled/>
    </div>

${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}
