<html>
<head>
    <title>Giga. A Marketplace for Marketplaces</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/benefit/media/icon.png?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/benefit/css/app.css?v=<%=System.currentTimeMillis()%>">

</head>
<body>

    <style>
        body{
            text-align: center;
        }
        #home-href{
            display:block;
            height:13px;
            width:140px;
            text-decoration: none;
            margin-top:30px;
        }
        #identity{
            font-weight:900;
            color:#000;
            font-size:31px;
        }
        #identity-wrapper{position: relative;}
        .square,.circle,.triangle{display:inline-block;position: absolute; top:-10px;}
        .square{
            left:50px;
            left:93px;
            height:10px;
            width:10px;
            background:#3979e4;
            animation: slide-square;
            animation-duration: 1s;
            animation-iteration-count: 1;
        }

        .circle {
            left:63px;
            left:74px;
            left:80px;
            height:10px;
            width:10px;
            border-radius: 0px;
            background-color: #d9392b;
            border-radius: 50%;
            animation: slide-circle, bounce;
            animation-duration: 0.4s;
            animation-iteration-count: 1;
        }

        .triangle {
            left:74px;
            left:63px;
            left:104px;
            width: 0;
            height: 0;
            border-left: 5px solid transparent;
            border-right: 5px solid transparent;
            border-bottom: 10px solid #ecaf01;
            animation: slide-triangle;
            animation-duration: 1s;
            animation-iteration-count: 1;
        }

        @keyframes slide-square {
            0% {left:-0px}
            100% { left: 50px; }
        }
        @keyframes slide-circle {
            0% {left:-0px}
            100% { left: 74px; }
        }
        @keyframes slide-triangle {
            0% {left:-0px}
            100% { left: 63px; }
        }
    </style>

    <a href="${pageContext.request.contextPath}/" id="home-href">
        <div id="identity-wrapper">
            <span class="square">&nbsp;</span>
            <span class="circle">&nbsp;</span>
            <span class="triangle">&nbsp;</span>
        </div>
        <span id="identity">Barter</span>
    </a>

    <br class="clear"/>

    <jsp:include page="${page}"/>

</body>
</html>
