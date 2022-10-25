<html>
<head>
    <title>social elite*</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/ractive/1.4.0/ractive.min.js" integrity="sha512-PZFuxjUwWqMaq5255WJAk2qsa+4uEGdwWgn9qa10j+RBsRF0XqDRMs7nTB6HH9GPTeWXi+YV+z/a2jtnnXpZjA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://code.jquery.com/jquery-3.6.1.min.js" integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <script src="/assets/action/signals.js"></script>
    <script src="/assets/action/crossroads.js"></script>
    <script src="/assets/action/hasher.js"></script>
    <link href="/assets/app.css" rel="stylesheet"/>
</head>
<body>

<a href="#users/${userId}">
    <img src="${photo}" id="profile-img" class="profile-img-background"/>
</a>

<style>
    #meta{
        right: 0px;
        bottom: 47%;
        transform: rotate(90deg);
    }
</style>

<div class="row">

    <a href="#lorem/ipsum" id="amadeus-action" class="crossroads-href" style="background:#0b76ef;background:transparent;padding:45px 30px 20px 30px;text-align:center;vertical-align:middle;height:67px;width:67px;position:fixed;top:0px;left:0px;">
        <span class="jkl crossroads-href" style="color:#fff;font-size:49px;">
<%--            &#10010;--%>
            &gimel;&deg;
<%--            &Delta;--%>
<%--            &Sigma;--%>
        </span>
    </a>

    <div class="thankyoujava2scom">
        <div id="template-render">
            <blueocean:content/>
        </div>
    </div>
    <div class="thankyoujava2scom-right">

        <div id="material-status">
            <p id="material-message"></p>
            <div id="video-progress">
                <div id="progress-wrapper">
                    <span id="progress-percentage"></span>
                </div>
            </div>
        </div>

        <style>
            #progress-wrapper{
                height:25px;
                text-align:left;
                background:#fff;
                border-radius: 12px;
            }
            #progress-percentage{
                width:30%;
                height:25px;
                display:inline-block;
                background:#0b76ef;
                border-radius: 12px;
            }
            #material-status{
                bottom:170px;
                margin:auto;
                z-index: 4;
                width:calc(100% - 20px);
                position:absolute;
            }
        </style>

        <div id="material-edit-wrapper">

            <form enctype="multipart/form-data" method="post" id="material-form">
                <div id="send-material-wrapper">
                    <h1 class="material-header" style="margin-bottom:0px;">What's going on?</h1>
                    <p style="margin: 0px 0px 3px 0px;font-size:24px;">Once you replace negative thoughts with positive ones, you'll start having positive results.</p>

                    <br class="clear"/>
                </div>

                <input type="file" name="material-photos" id="material-photos" multiple/>
                <input type="file" name="material-video" id="material-video"/>
                <textarea name="material" id="material-text" style="z-index:-1;height:0px;width:0px;position:absolute;top:0px;left:0px;opacity: 1"></textarea>

                <input type="text" name="upload-guid" id="upload-guid"/>

                <div id="media">
                    <svg id="material-photos-svg">
                        <path fill-rule="evenodd" style="cursor:hand" class="s0" d="m7.6 6.3q0.7 0.7 0.7 1.8 0 1.1-0.7 1.9-0.7 0.8-1.8 0.7-1 0.1-1.7-0.7-0.8-0.8-0.8-1.9 0-1.1 0.8-1.8 0.7-0.8 1.7-0.8 1.1 0 1.8 0.8zm8.6 1.4l-6.6 7-2.1-2.2-4.2 4.4v2.6h18.4v-6.1zm6.9-3.8q0.1 0.1 0.2 0.3v16.6q-0.1 0.2-0.2 0.3-0.1 0.1-0.3 0.1h-20.8q-0.2 0-0.3-0.1-0.1-0.1-0.1-0.3v-16.6q0-0.2 0.1-0.3 0.1-0.2 0.3-0.2h20.8q0.2 0 0.3 0.2zm1.2-1.3q-0.6-0.6-1.5-0.6h-20.8q-0.9 0-1.5 0.6-0.6 0.7-0.6 1.6v16.6q0 0.9 0.6 1.5 0.6 0.7 1.5 0.7h20.8q0.9 0 1.5-0.7 0.6-0.6 0.6-1.5v-16.6q0-0.9-0.6-1.6z"/>
                    </svg>
                    <svg id="material-videos-svg">
                        <path id="Shape 28" class="s0" d="m22.5 5.5q0.5 0.2 0.5 0.7v13.5q0 0.5-0.5 0.7-0.1 0.1-0.3 0.1-0.3 0-0.5-0.2l-4.7-5v2q0 1.5-1 2.6-1 1-2.4 1h-8.2q-1.4 0-2.4-1-1-1.1-1-2.6v-8.7q0-1.5 1-2.5 1-1.1 2.4-1.1h8.2q1.4 0 2.4 1.1 1 1 1 2.5v2l4.7-5q0.2-0.2 0.5-0.2 0.2 0 0.3 0.1z"/>
                    </svg>
                </div>
                <div contenteditable="true" id="material"></div>
                <div id="count">0</div>
                <a href="javascript:" class="button" id="material-button">Share&nbsp;&nbsp;&nbsp;&nbsp;&#8690;</a>
            </form>
        </div>
    </div>

    <div class="falco"></div>
</div>


<style>
    video{
        height:300px;
        width:100%;
    }
    #material-edit-wrapper{
        text-align: left;
    }
    #material-status{
        margin:auto;
        display:none;
        width:calc(100% - 20px)
    }
    #material-video{
        z-index: 4;
        bottom:30px;
        left:90px;
        height:45px;
        width:45px;
        opacity:0;
        cursor:hand;
        position:absolute;
    }
    #material-photos{
        height: 45px;
        width:45px;
        left:0px;
        bottom:30px;
    }
    #material{
        height:202px;
        left:10px;
        right:65px;
        bottom:59px;
        overflow: scroll;
        background: #fff;
        padding:14px 20px;
        position:absolute;
        border-top-left-radius: 26px;
        border-bottom-left-radius: 26px;
    }
    #material-button{
        color:#000;
        font-size: 21px;
        margin: 0px;
        padding: 26px 0px;
        width: 230px;
        font-weight: 900;
        text-align: center;
        border-radius: 0px;
        transform: rotate(90deg);
        vertical-align: middle;
        position: absolute;
        right: -69px;
        bottom: 148px;
        border-top-right-radius: 26px;
        border-top-left-radius: 26px;
        background: #eedb2d;
        border: solid 1px #eedb2d;
        border-bottom: solid 1px #c0af12;
        border-right:solid 1px #c0af12;
    }
    #material-button:active{
        border-bottom: solid 1px #eedb2d;
        border-right:solid 1px #eedb2d;
    }
    #send-material-wrapper {
        left:10px;
        right:10px;
        width:auto;
        bottom:292px;
        position:absolute;
    }
    #send-material-wrapper h1,
    #send-material-wrapper p{margin:0px;}
    #material.active{
        background:#fff;
    }
    .paper-user{
        font-size: 34px;
        font-weight: 600;
        width: calc(100% - 81px);
        margin-top: 0px;
        margin-left: 15px;
        float: left;
        display: inline-block;
    }
    #count{
        padding:10px 20px;
        color:#fff;
        background:#000;
        bottom:25px;
        right:83px;
        position:absolute;
        border-radius:12px;
    }
    #material-photos-svg{
        bottom:30px;
    }
    #material-videos-svg{
        height: 25px;
        width:25px;
        left:90px;
        bottom:30px;
        position:absolute;
        transform: scale(1.3);
    }
</style>

</body>
</html>