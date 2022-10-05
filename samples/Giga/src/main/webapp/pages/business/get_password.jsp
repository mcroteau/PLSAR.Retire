<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

<div class="section-wrapper">
    <div class="section">
        <c:if test="${not empty message}">
            <div class="notify">${message}</div>
        </c:if>

        <h1>Reset Password</h1>

        <p>Enter cell phone that is registered with your account.</p>

        <form action="${pageContext.request.contextPath}/${business.uri}/users/password/send" method="post">
            <fieldset>

                <label for="phone">Cell Phone</label>
                <span class="tiny">The phone associated with your account!</span>
                <input id="phone" type="text" placeholder="" name="phone" style="width:60%;">

                <div class="align-right" style="margin:20px 0px 170px; text-align:right;">
                    <input type="submit" class="button green" id="reset-password" value="Get Password"
                           onclick="this.disabled=true;this.value='Sending password to your phone...';this.form.submit();"/>
                </div>
            </fieldset>
        </form>
    </div>
</div>

${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}


