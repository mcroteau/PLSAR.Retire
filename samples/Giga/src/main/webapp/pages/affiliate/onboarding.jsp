<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="section-wrapper" style="margin-bottom:200px">
    <div class="section">

        <h1>Affiliate Business<br/> Partner Registration Process!</h1>
        <p>Interested in becoming a business partner, and sell here. Look no further, it all starts right now!</p>

        <form action="${pageContext.request.contextPath}/affiliates/onboarding/begin" method="post">
            <label>Select your business!</label>
            <select name="businessId">
                <c:forEach items="${businesses}" var="business">
                    <option value="${business.id}">${business.name} @ ${business.baseCommission}% Commission</option>
                </c:forEach>
            </select>

            <label>What's your name?</label>
            <input type="text" name="name" value="" placeholder="Jack!"/>

            <label>Give your Partner Business a Name</label>
            <input type="text" name="businessName" value="" placeholder="Jack's Silly Suds"/>

            <label>What is your Cell Phone Number?</label>
            <span class="tiny">No spaces or special characters please, makes our lives easier
            when notifying you.</span>
            <input type="text" name="phone" value="" placeholder="9079878652"/>

            <label>Internet Mailbox?</label>
            <span class="tiny"></span>
            <input type="text" name="email" value="" placeholder=""/>

            <label>Tell us a little bit about yourself and your plans with the Business.</label>
            <span class="tiny">Ask em, whats the worst the can say, No?</span>
            <textarea name="notes" placeholder="">Thank you for your time. I really love your store and would like to start my own business using your store items. Would this be possible?</textarea>
            <div class="align-right" style="margin-top:30px;">
                <input type="submit" value="Submit for Approval" class="button green" onclick="this.disabled=true;this.value='Please wait...';this.form.submit();"/>
            </div>

        </form>
    </div>
</div>


