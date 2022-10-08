<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<h1>Settings</h1>

<p>Please enter valid phone, email and address information to complete business setup.
    The information below is required to calculate shipping, send email notifications and sms alerts.</p>

<form action="${pageContext.request.contextPath}/businesses/settings/save/${business.id}" method="post">

    <input type="hidden" name="id" value="${business.id}"/>
    <input type="hidden" name="live" value="${business.live}"/>
    <input type="hidden" name="userId" value="${authUser.id}"/>
    <input type="hidden" name="primaryId" value="${business.primaryId}"/>
    <input type="hidden" name="stripeId" value="${business.stripeId}"/>
    <input type="hidden" name="initial" value="${business.initial}"/>
    <input type="hidden" name="affiliate" value="${business.affiliate}"/>
    <input type="hidden" name="activationComplete" value="${business.activationComplete}"/>

    <label>Name</label>
    <input type="text" name="name" value="${business.get(business.name)}"/>

    <div style="margin:20px 0px;">
        <label class="left-float" style="margin-right:20px;">Shop Uri /</label>
        <input type="text" name="uri" value="${business.get(business.uri)}" class="left-float" style="width:130px;"/>
        <br class="clear"/>
    </div>

    <c:if test="${!business.affiliate}">
        <label>Commission Rate</label>
        <span class="tiny">Enter full percent. For instance for 13%, enter 13.0.</span>
        <input type="text" name="baseCommission" value="${business.get(business.baseCommission)}" style="width:40%"/>
    </c:if>



    <input type="hidden" name="allowAffiliates" value="true"/>
<%--    <div id="allow-affiliate-select-wrapper" style="float:left;width:346px;">--%>
<%--        <label>Allow Affiliates</label>--%>
<%--        <span class="tiny">Would you like to give others the ability to sell on your platform using your products. Affiliates cannot--%>
<%--        add or remove items from your store, only theirs and the cannot change an item detail--%>
<%--        for a business partner's item, they are only allowed to list an item at an equal or higher price.</span>--%>
<%--        <select name="allowAffiliates" id="allow-affiliate" style="width:340px;">--%>

<%--            <c:if test="${business.allowAffiliates}">--%>
<%--                <c:set var="allow" value="selected"/>--%>
<%--                <c:set var="disallow" value=""/>--%>
<%--            </c:if>--%>

<%--            <c:if test="${!business.allowAffiliates}">--%>
<%--                <c:set var="allow" value=""/>--%>
<%--                <c:set var="disallow" value="selected"/>--%>
<%--            </c:if>--%>

<%--            <option value="true" ${allow}>Allow Affiliates</option>--%>
<%--            <option value="false" ${disallow}>Don't Alllow Affiliates</option>--%>
<%--        </select>--%>
<%--    </div>--%>
<%--    <br class="clear"/>--%>

    <label>Cell Phone<span class="information">&nbsp;&nbsp;</span></label>
    <span class="information">Used to send SMS notifications and alerts</span>
    <input type="text" name="phone" value="${business.get(business.phone)}"/>


    <div id="flat-rate-select-wrapper" style="float:left;width:346px;">
        <label>Flat Rate Shipping</label>
        <span class="tiny">Would you like to use a flat rate or real time shipping calculations on the fly?</span>
        <select name="flatShipping" id="flat-shipping" style="width:340px;">

            <c:if test="${business.flatShipping}">
                <c:set var="flat" value="selected"/>
                <c:set var="realtime" value=""/>
            </c:if>

            <c:if test="${!business.flatShipping}">
                <c:set var="flat" value=""/>
                <c:set var="realtime" value="selected"/>
            </c:if>

            <option value="true" ${flat}>Use Flat Rate</option>
            <option value="false" ${realtime}>Use Real-time Shipping Calculations</option>
        </select>
    </div>

    <div id="flat-shipping-wrapper" style="float:left;margin-left:20px">
        <label>Flat Shipping Rate<span class="tiny"></span></label>
        <input type="text" name="shipping" value="${business.get(business.shipping)}" style="width:150px;"/>
    </div>
    <br class="clear"/>

    <label>Street<span class="information">&nbsp;&nbsp;</span></label>
    <span class="information">Business address is used to calculate shipping</span>
    <input type="text" name="street" value="${business.get(business.street)}"/>

    <label>Street 2<span class="information">&nbsp;&nbsp;</span></label>
    <input type="text" name="streetDos" value="${business.get(business.streetDos)}"/>

    <label>City<span class="information">&nbsp;&nbsp;</span></label>
    <input type="text" name="city" value="${business.get(business.city)}"/>

    <label>State<span class="information">&nbsp;&nbsp;</span></label>
    <input type="text" name="state" value="${business.get(business.state)}"/>

    <label>Zip<span class="information">&nbsp;&nbsp;</span></label>
    <input type="text" name="zip" value="${business.get(business.zip)}"/>

    <label>Country<span class="information">&nbsp;&nbsp;Current : <strong>${business.get(business.country)}</strong></span></label><br/>
    <select name="country">
        <option value="US" ${selected}>USA</option>
        <option value="UK"}>United Kingdom</option>
        <option value="JP"}>Japan</option>
    </select>


    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Save & Validate!" class="button green"/>
    </div>
</form>
