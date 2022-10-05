<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    #signup-wrapper{
        margin: -10px auto 0px auto;
    }
    p{font-size:19px; font-weight: 300}
</style>

<div class="section-wrapper">

    <div id="signup-wrapper" class="section">

        <c:if test="${not empty message}">
            <p class="notify">${message}</p>
        </c:if>

        <form action="${pageContext.request.contextPath}/business/signup" method="post" id="signup-form">
            <fieldset style="text-align: left">

                <h1 style="margin-bottom:20px;">Business<br/>Signup!</h1>

                <p>Are you ready to add your online storefront for your business?
                Let's get started!</p>

                <input type="hidden" name="role" value="BUSINESS_ROLE"/>

                <label>Business</label>
                <input type="text" placeholder="Buzz's Baked Goods" name="name" style="width:100%;">

                <label>Cell Phone</label>
                <span class="tiny">No spaces or special characters. Used a lot.</span>
                <input id="phone" type="text" placeholder="(909) 123-4567" name="phone" style="width:100%;">

                <label>Email</label>
                <input id="username" type="email" placeholder="Email Address" name="email" style="width:100%;">

                <label>Password</label>
                <input id="password" type="password" placeholder="Password &#9679;&#9679;&#9679;" name="password" style="width:100%;">

            </fieldset>

            <div class="action-wrapper">
                <input type="submit" class="button retro" id="signup-button" value="Business Signup!" onclick="this.disabled=true;this.value='Please wait...';this.form.submit();"/>
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


<%--    <script>--%>

<%--        var processing = false--%>
<%--        var form = document.getElementById("signup-form");--%>
<%--        var button = document.getElementById("signup-button");--%>

<%--        button.addEventListener("click", function(event){--%>
<%--            event.preventDefault();--%>
<%--            if(!processing){--%>
<%--                processing = true;--%>
<%--                form.submit();--%>
<%--            }--%>
<%--        })--%>

<%--    </script>--%>

</div>
