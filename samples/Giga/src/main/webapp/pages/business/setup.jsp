<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Giga. A Marketplace for Marketplaces</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/media/icon.png?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=<%=System.currentTimeMillis()%>">

</head>
<body>

<div id="header-identity"></div>

<div id="outer-wrapper">

    <a href="${pageContext.request.contextPath}/" id="home-href">
        <div id="identity-wrapper">
            <span class="square">&nbsp;</span>
            <span class="circle">&nbsp;</span>
            <span class="triangle">&nbsp;</span>
        </div>
        <span id="identity">Giga</span>
    </a>

    <div id="header-wrapper">
        <span id="hello">Hi ${authUser.username}, Welcome Back!</span><br/><br/>
    </div>

    <div id="content-wrapper">
        <h1>Business Setup</h1>
        <h3>Congratulations</h3>
        <p>On starting a new business! Enter your business details below to get started!</p>

        <form action="${pageContext.request.contextPath}/businesses/save" method="post">

            <input type="hidden" name="userId" value="${authUser.id}"/>

            <label>Name<span class="information">&nbsp;&nbsp;Give your business a name!</span></label>
            <input type="text" name="name" value=""/>

            <label>URI<span class="information">&nbsp;&nbsp;Give your business a uri, must not contain any special characters.</span></label>
            <input type="text" name="uri" value=""/>

            <div style="text-align: right;margin-top: 20px;">
                <input type="submit" value="Start Business!" class="button retro"/>
            </div>
        </form>

    </div>

    <br class="clear"/>

</div>

<div id="footer-wrapper"></div>


</body>
</html>
