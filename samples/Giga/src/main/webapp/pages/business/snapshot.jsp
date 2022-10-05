<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.lang.String" %>

<style>
    .value{font-size:72px;display: block;}
    .value.large{font-size:102px;}
    .lightf{font-weight: 300}
    .activate{margin-top:30px;}
</style>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<c:if test="${!business.activationComplete}">
    <div class="align-center">

        <h1 class="large activate">Activate Payments</h1>
        <h2>Time to Complete : 4 minutes</h2>

        <h3 style="margin:20px 0px 0px;">What is Stripe?</h3>
        <p style="width:76%;margin:10px auto 30px" class="align-center" >Stripe is great! Better yet fantastic! They make payments processing very secure. Click
            on setup payments to setup a Stripe account in order to continue with your business.
            You'll be redirected to Stripe.com to complete your on boarding process!</p>

        <a href="${pageContext.request.contextPath}/stripe/onboarding/setup/${business.id}" class="button gigantic green">Setup Payments to Complete</a>

        <p style="margin-top:30px;">Each business requires a separate Stripe account.</p>

    </div>
</c:if>

<c:if test="${business.activationComplete}">

    <h2 class="center-align" style="margin:20px 0px 0px;">Snapshot</h2>

    <c:if test="${business.affiliate}">
        <h3 class="center-align">
            <span class="value large">$${siteService.getPriceTres(commissionTotal)}</span>
            Total Commissions
        </h3>
    </c:if>

    <c:if test="${!business.affiliate}">
        <h3 class="center-align">
            <span class="value large">$${siteService.getPriceTres(salesTotal)}</span>
            Total Sales
        </h3>
    </c:if>


    <c:if test="${conversionRate != null}">
        <h3 style="width:40%;" class="left-float">
            <span class="value">${conversionRate}%</span>
            ${salesCarts}/${totalCarts}  <br/><span class="lightf">sales/total carts</span><br/>
            Conversion Rate
        </h3>
    </c:if>

    <p class="right-float" style="font-size:18px;width:40%;line-height:1.50em;" >
        Giga fights for the right to Data! Who's better at Data than Google?
        Their search engine is the best, so isn't Google Analytics!
        Simple and easy to setup and the most thorough solution on the market hands down! Best of
        all, it is free. No we are not Google, we just want them to buy us out!

        <br/>
        <br/>
        <a href="http://analytics.google.com" class="button green" target="_blank">Go Google Analytics!</a>
    </p>

    <c:if test="${conversionRate == null}">
        <p>No carts yet!</p>
    </c:if>

</c:if>