<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}


    <c:if test="${not empty message}">
        <p class="notify">${message}</p>
    </c:if>


    <div id="shipment-wrapper">

        <c:if test="${shipment == null}">
            <form action="${pageContext.request.contextPath}/${business.uri}/shipment/rates" method="post">

                <h2>Shipping Address</h2>

                <input type="hidden" name="businessId" value="${business.id}"/>
                <input type="hidden" name="shipCountry" value="USA">

                <label>Name</label>
                <input type="text" name="name" value="${cart.get(cart.shipName)}" id="ship-name" style="width:90%;"/>

                <label>Phone</label>
                <input type="text" name="phone" value="${cart.get(cart.shipPhone)}" id="ship-phone" style="width:40%;"/>

                <label>Street</label>
                <input type="text" name="shipStreet" value="${cart.get(cart.shipStreet)}" id="ship-street" style="width:70%;"/>

                <label>City</label>
                <input type="text" name="shipCity" value="${cart.get(cart.shipCity)}" id="ship-city" style="width:60%;"/>
                <br class="clear"/>

                <div class="left-float" style="width:30%">
                    <label>State</label>
                    <input type="text" name="shipState" value="${cart.get(cart.shipState)}" id="ship-state"/>
                </div>

                <div class="left-float" style="width:30%; margin-left:20px;">
                    <label>Zip</label>
                    <input type="text" name="shipZip" value="${cart.get(cart.shipZip)}" id="ship-zip" style="width:100%;"/>
                </div>

                <br class="clear"/>

                <div class="align-right" style="margin:30px 0px 170px;">
                    <input type="submit" value="Get Shipping Rates!" class="button" id="calculate" onclick="this.disabled=true;this.value='Getting shipping rates...';this.form.submit();"/>
                </div>
            </form>
        </c:if>
    </div>

${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}
