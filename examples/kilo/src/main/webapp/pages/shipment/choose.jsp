
<%@ page import="giga.Giga" %>

${siteService.getBaseBit(Kilo.HEAD, design, business, request)}

    <h1>Select Shipping</h1>
    <p>Shipment weight : ${weight}oz</p>

    <ocean:each items="${rates}" var="rate" varStatus="idx">

        <div class="rate-wrapper">
            <form action="/${business.uri}/shipment/add" method="post">
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
    </ocean:each>


    <a href="/${business.uri}/shipment" class="button yellow">Re-calculate</a>

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


${siteService.getBaseBit(Kilo.BOTTOM, design, business, request)}
