<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="section-wrapper">
    <div class="section">
        <c:if test="${not empty message}">
            <div class="notify">${message}</div>
        </c:if>

        <h1>Reset Password</h1>

        <p>Enter cell phone that is registered with your account.</p>

        <form action="${pageContext.request.contextPath}/users/send" method="post">
            <fieldset>

                <label for="cellphone">Cell Phone</label>
                <input id="cellphone" type="text" placeholder="cell phone" name="phone" style="width:60%;">

                <div class="align-right" style="margin:20px 0px 170px; text-align:right;">
                    <input type="submit" class="button retro" id="reset-password" value="Click to Begin Reset Process"/>
                </div>
            </fieldset>
        </form>
    </div>
</div>


