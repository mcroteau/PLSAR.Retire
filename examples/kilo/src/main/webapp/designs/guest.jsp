<html>
<head>
    <title>Kilo. ${title}</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/assets/media/icon.gif">
    <link rel="stylesheet" href="/assets/css/default.css">

    <script src="/assets/media/confetti.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/mustache@4.2.0/mustache.js"></script>

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
        margin-left:0px;
    }
    #home-href{
        height:100px;
        width:300px;
        margin:40px auto;
    }
    .square,.circle,.triangle{display:inline-block;position: absolute; top:-30px;}
    .square{
        left:155px;
        height:12px;
        width:20px;
        background:#3878E3;
        animation: slide-square;
        animation-duration: 1s;
        animation-iteration-count: 1;
    }

    .circle {
        left:162px;
        height:20px;
        width:20px;
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
        border-left: 10px solid transparent;
        border-right: 10px solid transparent;
        border-bottom: 20px solid #FF817A;
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

    @keyframes bounce {
        0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
        40% {transform: translateY(-15px);}
        60% {transform: translateY(-7px);}
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
            <ocean:isAnonymous>
                <a href="/signin" id="signin-button" class="button green">Signin!</a>
            </ocean:isAnonymous>
            <ocean:isAuthenticated>
                <a href="/signout" id="signin-button" class="button green">Signout!</a>
            </ocean:isAuthenticated>

            <a href="/" id="home-href">
                <div id="identity-wrapper">
                    <span class="square">&nbsp;</span>
                    <span class="circle">&nbsp;</span>
                    <span class="triangle">&nbsp;</span>
                </div>
                <span id="identity">Kilo</span>
                <span class="information" style="margin-top:71px;display: inline-block;">A Wholesales<br/> Marketplaces</span>
            </a>

            <div id="guest-menu">
                <ocean:isAuthenticated>
                    <a href="/">Admin</a>&nbsp;
                </ocean:isAuthenticated>
                <a href="/home#features">Features</a>&nbsp;
                <a href="/home#friends">Resellers*</a>&nbsp;
                <a href="/signup" class="button orange">Start Business!</a>
            </div>
        </div>

    </div>

    <ocean:content/>

</body>
</html>