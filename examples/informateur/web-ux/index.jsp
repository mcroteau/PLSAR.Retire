<html>
<head>
    <title>social elite*</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/ractive/1.4.0/ractive.min.js" integrity="sha512-PZFuxjUwWqMaq5255WJAk2qsa+4uEGdwWgn9qa10j+RBsRF0XqDRMs7nTB6HH9GPTeWXi+YV+z/a2jtnnXpZjA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://code.jquery.com/jquery-3.6.1.min.js" integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <script src="/resources/action/signals.js"></script>
    <script src="/resources/action/crossroads.js"></script>
    <script src="/resources/action/hasher.js"></script>
    <link href="/resources/app.css" rel="stylesheet"/>
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
        <div id="template-render"></div>
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

<div id="meta">
    <span id="meta-minutes"></span>:<span id="meta-seconds"></span>
    <span id="meta-artist"></span>
    <span id="meta-title"></span>
</div>


<svg id="play-action" class="activity">
    <path id="play" class="play-nice navigation-action" d="m7 15.8c0-0.9 0-8.4 0-9.7 0-1.2 1-1.4 1.8-0.8 0.8 0.5 7.3 4.4 7.9 4.8 0.6 0.5 0.3 1.3-0.4 1.8-0.8 0.4-6.7 3.8-7.5 4.4-0.9 0.5-1.8 0.5-1.8-0.5z"/>
</svg>

<svg id="pause-action"  class="invincible activity">
    <path class="s0" d="m3 0h7v25h-7z"/>
    <path class="s0" d="m15 0h7v25h-7z"/>
</svg>

<svg id="previous" class="activity">
    <path class="s0" d="m12.1 13q-0.2-0.1-0.2-0.3v10q0 0.4-0.2 0.5-0.2 0-0.5-0.2l-9.9-10q-0.3-0.3-0.3-0.6 0-0.4 0.3-0.6l9.9-10q0.3-0.3 0.5-0.2 0.2 0.1 0.2 0.5v9.9q0-0.1 0.2-0.2l9.9-10q0.3-0.3 0.5-0.2 0.2 0.1 0.2 0.5v20.6q0 0.4-0.2 0.5-0.2 0-0.5-0.2z"/>
</svg>

<svg id="next" class="activity">
    <path class="s0" d="m4 23.7q-0.3 0.3-0.5 0.2-0.2-0.1-0.2-0.4v-20.7q0-0.3 0.2-0.4 0.2-0.1 0.5 0.2l9.9 9.9q0.2 0.1 0.2 0.3v-10q0-0.3 0.2-0.4 0.2-0.1 0.5 0.2l9.9 9.9q0.3 0.3 0.3 0.6 0 0.4-0.3 0.7l-9.9 9.9q-0.3 0.3-0.5 0.2-0.2-0.1-0.2-0.4v-10q0 0.2-0.2 0.3z"/>
</svg>

<div id="track-duration-seeker-wrapper">
    <span id="track-duration-seeker-percentage"></span>
</div>

<script>

    var WebForm = function(){

        var publish = function(uri, form){

            var request = new XMLHttpRequest();
            var formData = new FormData(form);
            return new Promise(function(resolve, reject){
                request.onreadystatechange = function () {
                    if (request.readyState !== 4) return;

                    if(request.status >= 200 && request.status < 300) {
                        resolve(request);
                    }
                    else {
                        reject({
                            status: request.status,
                            statusText: request.statusText
                        });
                    }
                };
                request.open('post', uri, true);
                request.send(formData);
            });
        }
        return {
            publish: publish,
        };
    }


    const webForm = new WebForm();



    $(document).ready(function(){

        let offset = 0;
        let $template_render = $("#template-render")


        let timeout;
        let videoTotalBytes;

        const MAX_CHARACTERS = 890;
        const DELETE = 8;
        const PHOTO_TYPES = ["image/gif", "image/png", "image/jpeg"]

        const editor = document.getElementById("material");
        let editor_text = document.getElementById("material-text");
        let materialButton = document.getElementById("material-button");


        const navigateHref = function(a){
            const href = $(a).attr('href')
            hasher.setHash('noop')
            hasher.setHash(href.split("#").pop());
        }

        const setMaterialActiveRange = function(){
            editor.focus();
            let hashtagRange = document.createRange();
            let windowSelection = window.getSelection();
            windowSelection.removeAllRanges();
            let theNodes = editor.childNodes;
            let firstNode = theNodes[0];
            let lastNode = theNodes[theNodes.length - 1];
            let start = theNodes[0];
            let end = theNodes[theNodes.length - 1];
            hashtagRange.setStartBefore(firstNode);
            hashtagRange.setEndAfter(lastNode);
            hashtagRange.collapse(false);
            windowSelection.addRange(hashtagRange);
            windowSelection.collapseToEnd();
        }

        function displayMaterialStatus(message){
            document.querySelector("#material-message").innerHTML = message;
            document.querySelector("#material-status").style.display = "block";
            document.querySelector("#material-edit-wrapper").style.display = "none";
        }

        let videoUploadInterval = null;
        materialButton.addEventListener("click", function() {
            if (editor.innerText.trim() !== "") {
                const guid = document.querySelector("#upload-guid").value;
                if(guid !== ""){
                    displayMaterialStatus("upload in progress...");
                    document.querySelector("#video-progress").style.display = "block";
                    videoUploadInterval = setInterval(function(){
                        $.getJSON("/video/" + guid).then(function(resp){
                            const totalBytes = Math.round(videoTotalBytes/1000);
                            const progressBytes = Math.round(resp.progressBytes/1000);
                            const percent = Math.round((resp.progressBytes / videoTotalBytes) * 100)
                            document.querySelector("#material-message").innerHTML =  "Uploading... " + percent + "%";
                            document.querySelector("#progress-percentage").style.width = percent + "%";

                            if(progressBytes >= totalBytes){
                                clearInterval(videoUploadInterval);
                                document.querySelector("#material-message").innerHTML = "Success!";
                            }
                        })
                    }, 1000);
                }
                const materialForm = document.getElementById("material-form")
                webForm.publish("/sheets/save", materialForm).catch(function (error) {
                    console.log("error", error)
                })
            }
        })

        editor.addEventListener('paste', function(event) {
            event.preventDefault();

            const text = event.clipboardData ? (event.originalEvent || event).clipboardData.getData('text/plain') :
                window.clipboardData ? window.clipboardData.getData('Text') : '';

            if (document.queryCommandSupported('insertText')) {
                document.execCommand('insertText', false, text);
                editor_text.value = editor.innerText;
            } else {
                const range = document.getSelection().getRangeAt(0);
                range.deleteContents();

                const textNode = document.createTextNode(text);
                range.insertNode(textNode);
                range.selectNodeContents(textNode);
                range.collapse(false);

                const selection = window.getSelection();
                selection.removeAllRanges();
                selection.addRange(range);
            }
        });

        document.querySelector("#count").innerHTML = MAX_CHARACTERS - document.querySelector("#material").innerText.length

        document.querySelector("#material").addEventListener("input", validateTimeoutSetHrefs("input"));
        document.querySelector("#material").addEventListener("keydown", validateTimeoutSetHrefs("keydown"));
        document.querySelector("#material").addEventListener("keyup", validateTimeoutSetHrefs("keyup"));

        document.querySelector("#material-photos").addEventListener("change", function(event){
            const files = event.target.files;
            const arr = Array.from(files);
            arr.forEach(file => {
                if(!PHOTO_TYPES.includes(file.type)){
                    alert("photo type accepted include: " + PHOTO_TYPES.join(","))
                    event.target.value = "";
                }
            });
        })

        document.querySelector("#material-video").addEventListener("change", function(event){
            const file = event.target.files[0];
            if("video/mp4" !== file.type){
                alert("video format mp4 required");
                document.querySelector("#upload-guid").value = "";
                event.target.value = "";
                return 0;
            }
            videoTotalBytes = file.size;
            document.querySelector("#upload-guid").value = createGuid();
        });

        function createGuid() {
            return 'xxxxxxxx-xxxx-xxxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        }

        function validateTimeoutSetHrefs(eventType){
            return function(event) {

                validateTimeout();

                if(((eventType === "keydown" || eventType === "keypress") &&
                    (document.querySelector("#material").innerText.length > MAX_CHARACTERS)) &&
                        event.keyCode != DELETE){
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }

                if(document.querySelector("#material").innerText.length > MAX_CHARACTERS &&
                        event.keyCode != DELETE){
                    event.preventDefault();
                    event.stopPropagation();
                    return 0;
                }

                document.querySelector("#count").innerHTML = MAX_CHARACTERS - document.querySelector("#material").innerText.length

                const regex = /((<strong>)+(@[a-zA-Z]+)(<\/strong>)+)|((<span>)+(@[a-zA-Z]+)(<\/span>)+)|(\<span\>@[a-zA-Z]+\<\/span\>)|(\<strong\>@[a-zA-Z]+\<\/strong\>)|(\<strong\>@[a-zA-Z]+(&nbsp;)+\<\/strong\>)/g;
                const collection = editor.innerHTML.match(regex);
                if (collection !== null) collection.map((match) => cleanMaterialEditor)
                if (timeout === null) timeout = setTimeout(setUserCodeHref, 1000)

                if(eventType === "input"){
                    setMaterialActiveRange();
                }

                editor_text.value = editor.innerText;
            }
        }

        function validateTimeout(){
            if(timeout !== null){
                clearTimeout(timeout)
                timeout = null;
            }
        }

        function cleanMaterialEditor(match){
            console.log("cleanMaterialEditor", match)
            const clean = match.replaceAll("<a>", "")
                .replaceAll("</a>", "")
                .replaceAll("<span>", "")
                .replaceAll("</span>", "")
                .replaceAll("&nbsp;", "");
            editor.innerHTML = editor.innerHTML.replaceAll(match, clean);
        }

        function setUserCodeHref(){
            console.log("setUserCodeHref")
            const regex = /(@[a-zA-Z]+)/g;
            const collection = editor.innerHTML.match(regex);
            if(collection !== null)collection.map((match) => getCleanCodeUserHref(match))
        }

        function getCleanCodeUserHref(match){
            console.log("getCleanCodeUserHref", match)
            const code = match.replaceAll("@", "");
            $.getJSON("/code/" + code).then(getUserCodeHre(match)).catch(errorGettingCode)
        }

        function getUserCodeHre(match){
            return function(data) {
                console.log("getUserCodeHre", data)
                if (data.status === "ok") {
                    const stuff = "<a href=\"#users/" + data.user.id + "\" style=\"text-decoration:none;cursor:hand\" onclick=\"navigateHref(this)\">" + match + "</a>"
                    editor.innerHTML = editor.innerHTML.replaceAll(match, stuff);
                }
                if (data.status === "terrible") {
                    editor.innerHTML = editor.innerHTML.replaceAll(match, "<span>" + match + "</span>");
                }
                setMaterialActiveRange();
            }
        }

        function errorGettingCode(event){
            console.log("error", event)
        }


        crossroads.addRoute('paper/{id}', function(id){
            console.log("a", id);
            $template_render.fadeOut(30, function() {
                $.getJSON("/sheets/feature/" + id).then(function (data) {
                    var ractive = new Ractive({
                        target: "template-render",
                        template: $("#paper-template").html(),
                        data: data
                    });
                    ractive.set("editable", true);
                    ractive.update();

                    ractive.on({
                        delete: function(element, id){
                            $.post("/sheets/delete/" + id, function(resp){
                                console.log(resp)
                                ractive.set("message", "Successfully deleted.")
                                ractive.set("editable", false);
                                ractive.update();
                            })
                        }
                    });
                    $template_render.fadeIn(30)
                })
            });
        });

        crossroads.addRoute('lorem/ipsum', function(message){
            $("#main-message").html(message).fadeIn();
            $template_render.fadeOut(30, function(){
                $.getJSON("/sheets/" + offset).then(function(data){
                    var ractive = new Ractive({
                        target: 'template-render',
                        template: $('#sheets-template').html(),
                        data: data
                    });
                    ractive.on({
                        activate: function(element, paper) {
                            console.log("Activating!", paper );
                        }
                    });
                    let query_lover = document.querySelector('#query-lover');
                    query_lover.addEventListener('keypress', function(event){
                        if(event.keyCode === 13){
                            if(query_lover.value.length >= 3){
                                $template_render.fadeOut(130, function(){
                                    let query = query_lover.value;
                                    if(query.includes("users: "))query = query.replace("users: ", "users:")
                                    $.getJSON("/sheets/" + offset + '?q=' + query).then(function(query_data){
                                        console.log(query_data)
                                        ractive.set('papers', query_data.papers);
                                        ractive.update('papers');
                                        $template_render.fadeIn(130)
                                    })
                                })
                            }
                        }
                    })

                    $('#send-material-wrapper').fadeIn(30)
                    $template_render.fadeIn(30)
                })
            })
        });


        crossroads.addRoute('users/{id}', function(id) {
            $template_render.fadeOut(30, function () {
                $.getJSON("/users/" + id).then(function (data) {
                    var ractive = new Ractive({
                        target: 'template-render',
                        template: $('#users-template').html(),
                        data: data
                    });
                    ractive.on({
                        activate: function (element, paper) {
                        },
                    });
                    $template_render.fadeIn(30)
                })
            })
        })

        crossroads.addRoute('noop', function() { } );
        crossroads.routed.add(console.log, console); //log all routes

        //setup hasher
        function parseHash(newHash, oldHash){
            crossroads.parse(newHash);
        }
        hasher.initialized.add(parseHash); //parse initial hash
        hasher.changed.add(parseHash); //parse hash changes
        hasher.init(); //start listening for history change

        //update URL fragment generating new history record
        hasher.setHash('lorem/ipsum');


        let crossroads_href = document.querySelector('.crossroads-href');
        crossroads_href.addEventListener('click', function(event){
            event.preventDefault();
            hasher.setHash('noop')
            hasher.setHash(this.href.split("#").pop());
        });


    });











    const music_playlist = [
        {
            title : "Control",
            artist : "Rokysopp",
            duration: 301,
            src : "https://ewr1.vultrobjects.com/zeus/iHYe7izrj5qGlPWA7W3B84Z.mp3"
        },
        {
            title: 'Sorry',
            artist: 'Royksopp',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/uEtBVQb1OA6tLJGpJJQnidv.mp3'
        },
        {
            title: 'Unity',
            artist: 'Royksopp',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/rpkY5v9n5dS82AnwCKMCtX2.mp3'
        },
        {
            title: 'Rock Me Amadeus',
            artist: 'Falco',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/mIXVHToeTCT1j271y61IVR95.mp3'
        },
        {
            title: 'Rock Steady',
            artist: 'Bad Company',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/jtCc9OaIlgM3LV9YymiJ2gq.mp3'
        },
        {
            title: 'Break For Lovers',
            artist: 'Men I Trust',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/fTG2hiccntzFqetP5GT5gWO.mp3'
        },
        {
            title: 'Bayi Wa Bayk',
            artist: 'Fares Karem',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/THoSekACTf6vilEuEcvQBba.mp3'
        }
    ];

    /* ///-> MUSIC COUNTDOWN ->/// */
    let duration_remaining = 0;
    let duration_complete = 0;
    let audio_playing = false;
    let music_interval = 0;

    let meta = document.getElementById('meta');
    let play = document.getElementById('play-action');
    let pause = document.getElementById('pause-action');
    let meta_artist = document.getElementById('meta-artist');
    let meta_title = document.getElementById('meta-title');
    let meta_minutes = document.getElementById('meta-minutes');
    let meta_seconds = document.getElementById('meta-seconds');
    const previous = document.getElementById('previous');
    const next = document.getElementById('next');
    let seeker_duration_wrapper = document.getElementById('track-duration-seeker-wrapper')
    let seeker_duration_percentage = document.getElementById('track-duration-seeker-percentage')

    let audio = new Audio();
    let active_song_idx = 0;
    let active_song = music_playlist[active_song_idx];
    for(let tao = 0; tao < music_playlist.length; tao++){
        audio.preload = music_playlist[tao].src;
    }

    let init_next_song = function(){
        active_song_idx++;
        if (active_song_idx == music_playlist.length) active_song_idx = 0;
        audio.src = music_playlist[active_song_idx].src
        active_song = music_playlist[active_song_idx];
        reset_media_player();
    }

    let init_previous_song = function(){
        active_song_idx--;
        if (active_song_idx < 0) active_song_idx = music_playlist.length - 1;
        active_song = music_playlist[active_song_idx];
        reset_media_player();
    }

    let reset_media_player = function(){
        audio_playing = false;
        clearInterval(music_interval);
        duration_complete = 0;
        duration_remaining = active_song.duration;
        meta_artist.innerHTML = active_song.artist;
        meta_title.innerHTML = active_song.title;
        music_interval = setInterval(initiate_countdown, 1000);
        audio.src = active_song.src
        audio.play();audio_playing = true;
        audio.addEventListener('ended', init_next_song);
        play.classList.add('invincible')
        pause.classList.remove('invincible')
    }

    let init_media_player = function(){
        if (!audio_playing) {
            if(duration_remaining <= 0) {
                duration_complete = 0;
                duration_remaining = active_song.duration;
            }
            meta_artist.innerHTML = active_song.artist;
            meta_title.innerHTML = active_song.title;
            music_interval = setInterval(initiate_countdown, 1000);
            if(!audio.src)audio.src = active_song.src
            audio.play();audio_playing = true;
            audio.addEventListener('ended', init_next_song);
            play.classList.add('invincible')
            pause.classList.remove('invincible')
        } else if(audio_playing){
            audio.pause();
            audio_playing = false;
            clearInterval(music_interval);
            meta_artist.innerHTML = '';
            meta_title.innerHTML = 'Amadeus.';
            meta_minutes.innerHTML = 0;
            meta_seconds.innerHTML = 0;
            play.classList.remove('invincible')
            pause.classList.add('invincible')
        }
    }

    function initiate_countdown() {
        duration_remaining--;
        duration_complete++;
        const music_date = new Date();
        const difference = music_date.getSeconds() + duration_remaining;
        music_date.setSeconds(difference);
        const current_date = new Date();
        const total_seconds = Math.floor((music_date - current_date) / 1000);
        const minutes = Math.floor(total_seconds / 60) % 60;
        let seconds = (Math.floor(total_seconds) % 60) + '';

        meta_minutes.innerHTML = minutes;
        meta_seconds.innerHTML = seconds.length == 1 ? '0' + seconds : seconds;

        let percent = (duration_complete / active_song.duration) * 100;
        seeker_duration_percentage.style.width = percent + '%';
    }

    const manueverSeekDuration = function(event){
        const x = event.clientX;
        const width = event.target.getBoundingClientRect().width;
        const percent_seeked = (x / width);
        const percent_seeked_css = (percent_seeked * 100) + "%"
        const seeked_duration = active_song.duration * percent_seeked;
        duration_complete = seeked_duration;
        duration_remaining = active_song.duration - seeked_duration;
        audio.currentTime = seeked_duration;
        seeker_duration_percentage.style.width = percent_seeked_css + '%';
    }

    play.addEventListener('click', init_media_player);
    pause.addEventListener('click', init_media_player);
    next.addEventListener('click', init_next_song);
    previous.addEventListener('click', init_previous_song);
    seeker_duration_wrapper.addEventListener('click', manueverSeekDuration);
</script>


<kakai:content/>

<script type="text/template" id="sheets-template">

    <div id="sheets-wrapper">
        <div id="query-lover-wrapper">
            <input type="text" name="q" placeholder="users: artists: albums: genres: activity:" id="query-lover"/>
        </div>
        <p id="main-message" class="message" style="display: none"></p>
        {{#each papers as paper}}
        <div class="paper-wrapper" id="sheet-{{paper.id}}">
            <div class="identity-wrapper">
                <a href="#users/{{paper.userId}}" class="profile-action">
                    <img src="{{paper.photo}}" class="paper-profile-img profile-img-background"/>
                    <span class="paper-user">{{paper.name}}</span>
                </a>
                <br class="clear"/>
            </div>
            {{paper.video}}
<%--            {{#if hasVideo}}--%>
<%--                <video src="/media/{{paper.video}}" controls/>--%>
<%--            {{/if}}--%>

            <a href="#paper/{{paper.id}}">
                <div class="paper-photos">
                    {{#each paper.photos as photo}}
                    <img src="{{photo}}" class="paper-photo"/>
                    {{/each}}
                </div>
            </a>

            <a href="#paper/{{paper.id}}">
                <p>{{paper.content}}</p>
            </a>

            <div class="paper-action-wrapper">
                <span class="pretty-time">{{paper.timeAgo}}</span>

                <svg class="heart-icon-svg activity-action activity" on-click="['activate', paper]">
                    <path id="heart" class="s0" d="m13.1 23.7q-0.3 0.3-0.6 0.3-0.3 0-0.6-0.3l-8-9q-0.1-0.1-0.3-0.4-0.3-0.2-0.7-1-0.5-0.7-0.9-1.4-0.4-0.8-0.7-1.8-0.3-1.1-0.3-2.1 0-3.3 1.6-5.1 1.7-1.9 4.5-1.9 0.8 0 1.7 0.3 0.8 0.3 1.5 0.9 0.7 0.5 1.2 1 0.5 0.5 1 1 0.5-0.5 1-1 0.5-0.5 1.2-1 0.7-0.6 1.5-0.9 0.9-0.3 1.7-0.3 2.8 0 4.5 1.9 1.6 1.8 1.6 5.1 0 3.3-2.9 6.7l-8 9z"/>
                </svg>
                <span class="activity-count">{{paper.likesCount}}</span>

                <a href="#!/patron/{{paper.id}}">
                    <svg class="share-icon-svg activity-action activity">
                        <path id="share" class="s0" d="m15.6 15.8q1.3-1.2 3-1.2 1.8 0 3.1 1.3 1.3 1.2 1.3 3 0 1.9-1.3 3.1-1.3 1.3-3.1 1.3-1.8 0-3.1-1.3-1.3-1.2-1.3-3.1 0-0.1 0-0.4l-4.9-2.5q-1.2 1.2-2.9 1.2-1.8 0-3.1-1.3-1.3-1.3-1.3-3.1 0-1.8 1.3-3.1 1.3-1.2 3.1-1.2 1.7 0 2.9 1.1l4.9-2.4q0-0.3 0-0.5 0-1.8 1.3-3.1 1.3-1.2 3.1-1.2 1.8 0 3.1 1.2 1.3 1.3 1.3 3.1 0 1.8-1.3 3.1-1.3 1.3-3.1 1.3-1.7 0-3-1.2l-4.9 2.5q0 0.3 0 0.4 0 0.2 0 0.5l4.9 2.5z"/>
                    </svg>
                </a>
                <span class="activity-count">{{paper.sharesCount}}</span>
                <br class="clear"/>
            </div>
        </div>
        {{else}}
            There's nobody here
        {{/each}}
    </div>
</script>

<script type="text/template" id="paper-template">
    <div id="sheet-expressions" class="active-page">

        <a href="#lorem/ipsum" class="flux-capacitor">&larr;&nbsp;Back</a>

        {{#if editable}}
            <div id="paper-wrapper">
                <div class="identity-wrapper">
                    <a href="#profile:{{id}}" class="profile-action">
                        <img src="{{photo}}" class="paper-profile-img profile-img-background"/>
                        <span class="paper-user">{{name}}</span>
                    </a>
                    <br class="clear"/>
                </div>


                <div id="patrons-photos">
                    {{#each photos as foto}}
                        <img src="{{foto}}" class="patron-photo"/>
                    {{/each}}
                </div>

                <p>{{content}}</p>

                <div class="paper-action-wrapper">
                    <span class="pretty-time">{{timeAgo}}</span>

                    <svg class="heart-icon-svg activity-action activity" ng-click="heart(paper)">
                        <path id="heart" class="s0" d="m13.1 23.7q-0.3 0.3-0.6 0.3-0.3 0-0.6-0.3l-8-9q-0.1-0.1-0.3-0.4-0.3-0.2-0.7-1-0.5-0.7-0.9-1.4-0.4-0.8-0.7-1.8-0.3-1.1-0.3-2.1 0-3.3 1.6-5.1 1.7-1.9 4.5-1.9 0.8 0 1.7 0.3 0.8 0.3 1.5 0.9 0.7 0.5 1.2 1 0.5 0.5 1 1 0.5-0.5 1-1 0.5-0.5 1.2-1 0.7-0.6 1.5-0.9 0.9-0.3 1.7-0.3 2.8 0 4.5 1.9 1.6 1.8 1.6 5.1 0 3.3-2.9 6.7l-8 9z"/>
                    </svg>
                    <span class="activity-count">{{likesCount}}</span>

                    <a href="#!/patron/{{paper.id}}">
                        <svg class="share-icon-svg activity-action activity">
                            <path id="share" class="s0" d="m15.6 15.8q1.3-1.2 3-1.2 1.8 0 3.1 1.3 1.3 1.2 1.3 3 0 1.9-1.3 3.1-1.3 1.3-3.1 1.3-1.8 0-3.1-1.3-1.3-1.2-1.3-3.1 0-0.1 0-0.4l-4.9-2.5q-1.2 1.2-2.9 1.2-1.8 0-3.1-1.3-1.3-1.3-1.3-3.1 0-1.8 1.3-3.1 1.3-1.2 3.1-1.2 1.7 0 2.9 1.1l4.9-2.4q0-0.3 0-0.5 0-1.8 1.3-3.1 1.3-1.2 3.1-1.2 1.8 0 3.1 1.2 1.3 1.3 1.3 3.1 0 1.8-1.3 3.1-1.3 1.3-3.1 1.3-1.7 0-3-1.2l-4.9 2.5q0 0.3 0 0.4 0 0.2 0 0.5l4.9 2.5z"/>
                        </svg>
                    </a>
                    <span class="activity-count">{{sharesCount}}</span>
                    <br class="clear"/>
                </div>
                <button class="button delete right-guide" id="delete-button" on-click="['delete', id]">Delete</button>
                <a href>&#10097;</a>
                <br class="clear"/>
        </div>
        {{else}}
            <p class="message">{{message}}</p>
        {{/if}}
    </div>
</script>

<script type="text/template" id="users-template">
    <div id="person-content-wrapper">
        <div id="person-header-wrapper">
            <img src="{{user.photo}}" class="person-profile-img profile-img-background">
            <div id="person-activities">
                <svg id="radio" class="activity">
                    <path fill-rule="evenodd" class="s0" d="m13 14q-0.2 0-0.9-0.6-0.6-0.7-0.6-0.9 0-0.1 0.6-0.8 0.7-0.6 0.9-0.6 0.2 0 0.4 0.5 0.2 0.6 0.2 0.9 0 0.4-0.2 1-0.2 0.5-0.4 0.5zm1.6 1.8q0.6 0.6 0.8 0.6 0.1 0 0.2-0.1 0.7-0.7 1.1-1.7 0.4-1 0.4-2.1 0-1-0.4-2-0.4-1.1-1.1-1.8-0.1 0-0.2 0-0.2 0-0.8 0.6-0.7 0.7-0.7 0.8 0 0.1 0.2 0.4 0.2 0.3 0.5 0.9 0.2 0.6 0.2 1.1 0 0.6-0.2 1.2-0.3 0.5-0.5 0.9-0.2 0.3-0.2 0.3 0 0.2 0.7 0.9zm2.4 2.4q0.7 0.6 0.8 0.6 0.2 0 0.2-0.1 1.2-1.1 1.9-2.8 0.6-1.7 0.6-3.4 0-1.7-0.6-3.3-0.7-1.7-1.9-2.9 0 0-0.2 0-0.1 0-0.8 0.6-0.7 0.7-0.7 0.8 0 0.1 0.2 0.3 0.2 0.3 0.5 0.7 0.3 0.4 0.6 1 0.2 0.6 0.4 1.3 0.2 0.8 0.2 1.5 0 1.4-0.4 2.4-0.4 1-1.4 2.2-0.1 0.1-0.1 0.2 0 0.2 0.7 0.9zm2.4 2.4q0.7 0.6 0.9 0.6 0.1 0 0.2-0.1 1.7-1.6 2.6-3.9 0.9-2.3 0.9-4.7 0-2.3-0.9-4.6-0.9-2.3-2.6-4-0.1 0-0.2 0-0.2 0-0.9 0.6-0.6 0.7-0.6 0.8 0 0.1 0.1 0.2 1.4 1.6 2.1 3.3 0.7 1.7 0.7 3.7 0 2.1-0.7 3.8-0.7 1.7-2.1 3.2-0.1 0.1-0.1 0.2 0 0.2 0.6 0.9z"/>
                    <path fill-rule="evenodd" class="s0" d="m12 14q-0.2 0-0.4-0.5-0.2-0.6-0.2-1 0-0.3 0.2-0.9 0.2-0.5 0.4-0.5 0.2 0 0.9 0.6 0.6 0.7 0.6 0.8 0 0.2-0.6 0.9-0.7 0.6-0.9 0.6zm-1.6 1.8q-0.6 0.6-0.8 0.6-0.1 0-0.2-0.1-0.7-0.7-1.1-1.7-0.4-1-0.4-2.1 0-1 0.4-2 0.4-1.1 1.1-1.8 0.1 0 0.2 0 0.2 0 0.8 0.6 0.7 0.7 0.7 0.8 0 0.1-0.2 0.4-0.2 0.3-0.5 0.9-0.2 0.6-0.2 1.1 0 0.6 0.2 1.2 0.3 0.5 0.5 0.9 0.2 0.3 0.2 0.3 0 0.2-0.7 0.9zm-2.4 2.4q-0.7 0.6-0.8 0.6-0.2 0-0.2-0.1-1.2-1.1-1.9-2.8-0.6-1.7-0.6-3.4 0-1.7 0.6-3.3 0.7-1.7 1.9-2.9 0 0 0.2 0 0.1 0 0.8 0.6 0.7 0.7 0.7 0.8 0 0.1-0.2 0.3-0.2 0.3-0.5 0.7-0.3 0.4-0.6 1-0.2 0.6-0.4 1.3-0.2 0.8-0.2 1.5 0 1.4 0.4 2.4 0.4 1 1.4 2.2 0.1 0.1 0.1 0.2 0 0.2-0.7 0.9zm-2.4 2.4q-0.7 0.6-0.9 0.6-0.1 0-0.2-0.1-1.7-1.6-2.6-3.9-0.9-2.3-0.9-4.7 0-2.3 0.9-4.6 0.9-2.3 2.6-4 0.1 0 0.2 0 0.2 0 0.9 0.6 0.6 0.7 0.6 0.8 0 0.1-0.1 0.2-1.4 1.6-2.1 3.3-0.7 1.7-0.7 3.7 0 2.1 0.7 3.8 0.7 1.7 2.1 3.2 0.1 0.1 0.1 0.2 0 0.2-0.6 0.9z"/>
                </svg>
                <br class="clear"/>
                <a href="javascript:" class="button follow">Follow</a>
            </div>
            <br class="clear"/>
        </div>

        <h1>{{user.name}}</h1>
    </div>
</script>



</body>
</html>