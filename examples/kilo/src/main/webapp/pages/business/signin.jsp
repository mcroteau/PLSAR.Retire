
<%@ page import="giga.Giga" %>

${siteService.getBaseBit(Kilo.HEAD, design, business, request)}

    <ocean:if spec="${message != ''}">
        <p class="notify">${message}</p>
    </ocean:if>

    <h1>${business.name} Signin</h1>

    <form action="/${business.uri}/signin" modelAttribute="user" method="post" >

        <div class="form-itemGroup">
            <label for="username">Username
            <br/><span class="tiny">Phone Number or Email Address</span>
            </label>
            <input type="text" name="username" class="form-control" id="username" placeholder=""  value=""  style="width:100%;">
        </div>

        <div class="form-itemGroup">
            <label for="password">Password</label>
            <input type="password" name="password" class="form-control" id="password" style="width:100%;" value=""  placeholder="&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;">
        </div>

        <div class="button-wrapper">
            <input type="submit" class="button green" value="Signin!">
        </div>

    </form>

    <div class="button-wrapper-bottom align-center">
        <a href="/${business.uri}/users/password/get">I need help signing in!</a>
    </div>

${siteService.getBaseBit(Kilo.BOTTOM, design, business, request)}

