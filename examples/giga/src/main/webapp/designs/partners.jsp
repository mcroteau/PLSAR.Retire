<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="chico" uri="/META-INF/tags/chico.tld" %>
<html>
<head>
    <title>Giga. ${title}</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/benefit/media/icon.gif?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/benefit/css/default.css?v=<%=System.currentTimeMillis()%>">
    <script src="${pageContext.request.contextPath}/benefit/media/confetti.js"></script>

</head>
<body>
<style>
    body{
        background-color: #fff;
    }
    #header-wrapper{
        text-align: center;
    }
    #top-wrapper{
        width:760px;
        margin:30px auto;
        text-align: center;
    }
    #identity{
        font-size: 49px;
        margin-left:-50px;
    }
    #home-href{
        height:100px;
        width:300px;
        margin:40px auto;
    }
    .square,.circle,.triangle{display:inline-block;position: absolute; top:-30px;}
    .square{
        left:135px;
        height:13px;
        width:13px;
        background:#3878E3;
        animation: slide-square;
        animation-duration: 1s;
        animation-iteration-count: 1;
    }

    .circle {
        left:153px;
        height:13px;
        width:13px;
        border-radius: 0px;
        background-color: #7DD9FD;
        border-radius: 50%;
        animation: slide-circle, bounce;
        animation-duration: 0.4s;
        animation-iteration-count: 1;
    }

    .triangle {
        left:167px;
        width: 0;
        height: 0;
        border-left: 7px solid transparent;
        border-right: 7px solid transparent;
        border-bottom: 13px solid #FF817A;
        animation: slide-triangle;
        animation-duration: 1s;
        animation-iteration-count: 1;
    }

    @keyframes slide-square {
        0% {left:100px}
        100% { left: 135px; }
    }
    @keyframes slide-circle {
        0% {left:100px}
        100% { left: 154px; }
    }
    @keyframes slide-triangle {
        0% {left:100px}
        100% { left: 169px; }
    }

    #guest-menu a{
        color:#000;
        font-size: 23px;
        font-weight: 300;
        text-decoration: none;
        display:inline-block;
        margin:0px 4px;
    }
    #guest-menu a.button{font-size:14px}
    #header-wrapper{position: relative}
    #signin-button{
        text-decoration: none;
        display:inline-block;
        position: absolute;right:100px;top:40px;
        margin:0px 4px;
    }

    .section-wrapper{text-align: center}
    .section{width:560px;margin:auto;text-align: left;}
</style>
    <div id="top-wrapper" class="section">

        <div id="header-wrapper">

            <chico:isAnonymous>
                <a href="${pageContext.request.contextPath}/signin" id="signin-button" class="button remove">Signin!</a>
            </chico:isAnonymous>
            <chico:isAuthenticated>
                <a href="${pageContext.request.contextPath}/signout" id="signin-button" class="button remove">Signout!</a>
            </chico:isAuthenticated>


            <a href="${pageContext.request.contextPath}/" id="home-href">
                <div id="identity-wrapper">
                    <span class="square">&nbsp;</span>
                    <span class="circle">&nbsp;</span>
                    <span class="triangle">&nbsp;</span>
                </div>
                <span id="identity">Giga</span>
                <span class="information" style="margin-top:71px;display: inline-block;">A Marketplace for<br/> Marketplaces</span>
            </a>
        </div>

    </div>

    <jsp:include page="${page}"/>

</body>
</html>