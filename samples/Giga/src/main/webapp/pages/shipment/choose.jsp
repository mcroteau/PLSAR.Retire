<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

    <h1>Select Shipping</h1>
    <p>Shipment weight : ${weight}oz</p>

    <c:forEach items="${rates}" var="rate" varStatus="idx">

        <div class="rate-wrapper">
            <form action="${pageContext.request.contextPath}/${business.uri}/shipment/add" method="post">
                <input type="hidden" name="cartId" value="${cart.id}"/>
                <input type="hidden" name="rate" value="${rate.rate}"/>
                <input type="hidden" name="carrier" value="${rate.carrier}"/>
                <input type="hidden" name="deliveryDays" value="${rate.deliveryDays}"/>
                    $${siteService.getPriceTres(rate.rate)}<br/>
                    ${rate.carrier}<br/>
                    ${rate.deliveryDays} Day Delivery
                <input type="submit" value="Select Shipment!" class="button green" id="rate-${idx.index}"/>
            </form>
        </div>
    </c:forEach>


    <a href="${pageContext.request.contextPath}/${business.uri}/shipment" class="button yellow">Re-calculate</a>

    <h2>Ship Address</h2>
    <address>
        ${cart.shipName}, ${cart.shipPhone}<br/>
        ${cart.shipStreet}<br/>
        ${cart.shipStreetDos}<br/>
        ${cart.shipCity}, ${cart.shipState}<br/>
        ${cart.shipZip}<br/>
        ${cart.shipCountry}<br/>
        ${cart.shipEmail}
    </address>


${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}
