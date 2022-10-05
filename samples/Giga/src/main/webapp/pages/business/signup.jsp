<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

<style>
    #signup-wrapper{
        margin: -10px auto 0px auto;
    }
    p{font-size:19px; font-weight: 300}
</style>


<div id="signup-wrapper" class="section">

    <c:if test="${not empty message}">
        <p class="notify">${message}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/${business.uri}/register" method="post" id="signup-form">
        <fieldset style="text-align: left">

            <h1 style="margin-bottom:20px;">${business.name} Signup!</h1>

            <p>Welcome to ${business.name}. Create an account or continue as a Guest!</p>

            <input type="hidden" name="businessId" value="${business.id}"/>

            <label>Name</label>
            <input id="name" type="text" placeholder="Full Name" name="name">

            <label>Cell Phone</label>
            <input id="phone" type="text" placeholder="" name="phone">

            <label>Email</label>
            <input id="username" type="email" placeholder="Email Address" name="username">

            <label>Password</label>
            <input id="password" type="password" placeholder="Password &#9679;&#9679;&#9679;" name="password">

        </fieldset>

        <div class="action-wrapper">
            <input type="submit" class="button retro" id="signup-button" value="Register Now!"
                   onclick="this.disabled=true;this.value='Please wait...';this.form.submit();"/>
        </div>

    </form>

    <style>
        .action-wrapper{
            width:100%;
            margin:41px auto 170px;
            text-align:right;
            margin-bottom:30px;
        }
    </style>


    <br class="clear"/>

</div>


${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}

