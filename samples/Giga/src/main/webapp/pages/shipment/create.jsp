<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="chico" uri="/META-INF/tags/chico.tld" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

<style>
    #step-by-step{margin:10px auto 30px;width:370px;}
    .step{border-radius:20px;padding:14px; font-size: 23px; font-weight: 900;background-color: #e4e8e9}
    .active{background:#FF817A; color:#fff;}
    .step-increment{margin:21px 10px;height:5px; width:100px; background-color: #efefef}
</style>

<div style="text-align: center">
    <div id="step-by-step">
        <a href="${pageContext.request.contextPath}/${business.uri}/cart" class="step left-float">1</a>
        <div class="step-increment previous left-float"></div>
        <a href="${pageContext.request.contextPath}/${business.uri}/shipment/create" class="step active left-float">2</a>
        <div class="step-increment left-float"></div>
        <div class="step left-float">3</div>
        <br class="clear"/>
        <br class="clear"/>
    </div>
</div>

<h1 class="align-left">Shipping!</h1>

<p>Please enter your mailing address below in order to continue to process your order!</p>

<form action="${pageContext.request.contextPath}/${business.uri}/shipment/save" method="post">

    <div id="shipping-details-wrapper">

        <input type="hidden" name="businessId" value="${business.id}"/>
        <input type="hidden" name="shipCountry" value="USA">

        <label>Name</label>
        <input type="text" name="name" value="${cart.get(cart.shipName)}" id="ship-name" style="width:90%;"/>

        <label>Phone</label>
        <input type="text" name="phone" value="${cart.get(cart.shipPhone)}" id="ship-phone" style="width:40%;"/>

        <label>Email</label>
        <input type="text" name="username" value="${cart.get(cart.shipEmail)}" id="ship-email" style="width:40%;"/>

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
            <input type="submit" value="Continue Step 3: Checkout!" class="button green" id="create" onclick="this.disabled=true;this.value='Validating address, hold on...';this.form.submit();"/>
        </div>

    </div>
</form>

${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}
