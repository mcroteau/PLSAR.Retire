<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

${siteService.getBaseBit(Giga.HEAD, design, business, request)}

    <c:if test="${not empty message}">
        <p class="notify">${message}</p>
    </c:if>

    <h1>${business.name} Signin</h1>

    <form action="${pageContext.request.contextPath}/${business.uri}/signin" modelAttribute="user" method="post" >

        <div class="form-group">
            <label for="username">Username
            <br/><span class="tiny">Phone Number or Email Address</span>
            </label>
            <input type="text" name="username" class="form-control" id="username" placeholder=""  value=""  style="width:100%;">
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" name="password" class="form-control" id="password" style="width:100%;" value=""  placeholder="&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;">
        </div>

        <div class="button-wrapper">
            <input type="submit" class="button green" value="Signin!">
        </div>

    </form>

    <div class="button-wrapper-bottom align-center">
        <a href="${pageContext.request.contextPath}/${business.uri}/users/password/get">I need help signing in!</a>
    </div>

${siteService.getBaseBit(Giga.BOTTOM, design, business, request)}

