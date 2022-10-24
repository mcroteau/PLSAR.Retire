<html>
<head>
    <title>social elite*</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.8.2/angular.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-route/1.8.2/angular-route.min.js"></script>
<%--    <script src="sha512-PZFuxjUwWqMaq5255WJAk2qsa+4uEGdwWgn9qa10j+RBsRF0XqDRMs7nTB6HH9GPTeWXi+YV+z/a2jtnnXpZjA=="></script>--%>
    <script src="https://code.jquery.com/jquery-3.6.1.min.js" integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@100;300;400;500;700;900&display=swap');
        body{
            background:#91a8bd;
            background:#0b76ef;
            padding-bottom:300px;
            background: rgb(159,185,191);
            background: #0b76ef;
            background: #16242d;
            background:#fff;
        }
        body,html,div,p,li,span,a,textarea,input{color:#000;font-family: Roboto;}
        .s0 {
            fill: #000;
            /*fill: #7fbafc;*/
        }
        .s6 { fill: #fe9643; }
        .s7 { fill: #8104c9; fill:#7fbafc}
        .pisa{ fill:#8104c9}
        .play-nice{fill:#000;}

        #play-action,#media-action,#amadeus-action,#pause-action{display:block;width:70px;position:fixed;}
        #amadeus-action{color:#ee2d62;color:#000;left:32px;top:45px;}
        #amadeus-action:focus{top:46px;}
        #media-action{left:60px;top:160px;}

        #amadeus-svg{
            width:70px;
            transform:scale(1.10);
        }
        #play-svg{width:29px;height:35px;transform: scale(2.1);}
        #pause-svg{width:22px;height:25px;transform: scale(1.0);}

        img{box-shadow: 0px 13px 47px 0px rgba(0,0,0,0.0);}
        /*textarea{box-shadow: 0px 13px 47px 0px rgba(0,0,0,0.20);}*/

        h1{font-size:43px;font-weight: 900;}
        p{font-size:23px;font-weight: 300;}
        a{text-decoration: none;color:#000;}

        #profile-img{position:absolute;right:25px;top:36px;width:62px;border-radius: 62px;}
        .profile-img-background{
            padding:2px;
            background: #F5305B;
            background: linear-gradient(35deg, rgb(253, 171, 106) 20%, rgb(245, 48, 91) 36%, rgb(245, 48, 91) 52%, rgb(129, 4, 201) 71%, rgba(0,137,255,1) 100%);
        }
        .activity{cursor:hand}
        #center-stage-wrapper{
            margin-top:37px;
            margin-left:145px;
            width:450px;
        }
        #query-lover{
            color:#fff;
            height:30px;
            padding:22px;
            font-size:23px;
            font-weight: 200;
            width:100%;
            border: solid 0px #fff;
            border-radius: 45px;
            background: #000;
        }
        #query-lover:focus{
            outline: none;
            -webkit-appearance: none;
            -moz-appearance:none;
        }
        #query-lover::placeholder{
            color:#fff;
        }
        .button{
            cursor:pointer;
            color:#fff;
            font-size:16px;
            padding:15px 23px;
            line-height: 1.0em;
            display:inline-block;
            background:#0b76ef;
            border:solid 1px #0b76ef;
            text-decoration:none;
            border-radius: 30px;
            font-family:Roboto !important;
            outline:none !important;
            -webkit-appearance: none;
            -moz-appearance:none;
        }
        .rain{background: #830DCA;border:solid 1px #830DCA;background:#fe9643;border:solid 1px #fe9643}

        #music-selection li{list-style:none;font-size:19px;font-weight: 300;margin:7px 0px;cursor:hand;}
        #next, #previous{width:25px;height:25px;margin:11px;cursor:pointer;cursor:hand;position:absolute;bottom:1px;}
        #next{right:0px;}
        #previous{right:39px;}
        #next:active, #previous:active{bottom:0px;}
        #track-duration-seeker-wrapper{
            height:23px;
            cursor:hand;
            position:fixed;
            bottom:0px;
            left:0px;
            right:0px;
            background: #d8fafa;
            background: #000;
        }
        #track-duration-seeker-percentage{
            width:30px;
            height:100%;
            background: #FE9643;
            display:inline-block;
            transition: left .5s ease-in, top .5s ease-in;
        }

        .invincible{display:none !important;}

        /**************************/
        /*     sheets.htm css     */
        /**************************/

        #sheets-wrapper{
            margin-top:32px;
        }

        .active-page{
            animation: fadeInAnimation ease 0.4s;
            animation-iteration-count: 1;
            animation-fill-mode: forwards;
        }

        @keyframes fadeInAnimation {
            0% {
                opacity: 0;
            }
            100% {
                opacity: 1;
            }
        }
        .paper-wrapper{margin-bottom:110px;}

        .paper-profile-img{
            width:62px;
            border-radius: 52px;
            float: left;
        }
        .paper-user{font-size: 23px; font-weight: 600; width:calc(100% - 81px); margin-top:10px; margin-left:15px; float:left; display: inline-block}

        .paper-action-wrapper{
            margin-top:39px;
            margin-right:59px;
            vertical-align: bottom;
        }
        .activity-action{float:right;margin-right:23px;}
        .share-icon-svg, .heart-icon-svg{
            width:25px;height:25px;
            margin-top:3px;
            margin-left:10px;
            display:inline-block;
        }
        .share-icon-svg:active,
        .heart-icon-svg:active{
            margin-top:4px;
        }
        .activity-count{
            font-size:23px;
            font-weight: 300;
            display:inline-block;
            line-height: 1.3em;
            float:right;
        }
        .pretty-time{font-size:13px;font-weight: 300;display:inline-block;margin-top:8px;}

        .clear{clear:both}

        #sheets-wrapper{
            transition-duration: 1s;
            transition-property: opacity;
        }

        textarea{color:#000;font-weight:300;height:130px;font-size:20px;width:100%;padding:10px;border:none;border-radius:0px;resize:none;background: rgba(145, 166, 181, 0.2);}
        textarea:focus{outline: none;-webkit-appearance: none;-moz-appearance:none;}
        textarea::placeholder{color: #000000;font-weight: 200}


        .jkl{font-weight: 900;}

        #paper-expressions{}
        #self-express-button{margin-top:17px;display:inline-block;}
        .patron-photo{width:130px;border-radius: 23px;margin-top:20px;}
        .right-guide{float:right;}
        .pretty-time{margin-bottom:10px;}
        .rain{background: #7fbafc;border:solid 1px #7fbafc;}
        .rain{background: #830DCA;border:solid 1px #830DCA;}
        .delete{background: #f22f5e; border:solid 1px #f22f5e;}


        .message{padding:10px;border-radius: 30px;border:solid 1px #000; }
        .flux-capacitor{display:block;margin:40px 0px 30px;}

        #amadeus-action{font-size:53px;}
    </style>
</head>
<body ng-app="amadeus" ng-controller="AppController" id="wrapper">


<a href="#!profile/:${userId}">
    <img src="${photo}" id="profile-img" class="profile-img-background"/>
</a>

<div class="actions-wrapper">

    <a href="#!activity" id="amadeus-action" class="jkl">&gimel;&deg;</a>

    <a href="#!sheets/{{query | escape}}" id="query-friends">
        {{query}}
    </a>

    <a href="javascript:" id="play-action">
        <svg id="play-svg" class="activity">
            <path id="play" class="play-nice navigation-action" d="m7 15.8c0-0.9 0-8.4 0-9.7 0-1.2 1-1.4 1.8-0.8 0.8 0.5 7.3 4.4 7.9 4.8 0.6 0.5 0.3 1.3-0.4 1.8-0.8 0.4-6.7 3.8-7.5 4.4-0.9 0.5-1.8 0.5-1.8-0.5z"/>
        </svg>
    </a>

</div>

<div id="center-stage-wrapper">
    <div id="search-box-wrapper">
        <input type="text" name="q" placeholder="users: artists: albums: genres: activity:" id="query-lover"/>
    </div>

    <div ng-view></div>
</div>


<style>
    #play-action{right: 26px;bottom: 51px;}
    #play-action:focus{bottom:50px;}
    #pause-action{right:31px;bottom:70px;}
    #pause-action:focus{bottom:69px;}
    #previous,#next{position:fixed;bottom:59px;}
    #previous:active,#next:active{bottom:57px;}
    #previous{right:113px;}
    #next{right:21px;}

    #meta-wrapper{width:300px;}
    #meta{position:fixed; right:29px;bottom:120px; }
    #meta-artist,
    #meta-title,
    #meta-minutes,
    #meta-seconds{display:inline-block;font-weight:200;color: #258afe;color: #1281fc;color: #1a2a34;color: #000;}
    #meta-artist{display:block;}

    /*#meta { animation: tickerh linear 15s infinite; }*/
    /*#meta:hover { animation-play-state: paused; }*/
    /*@keyframes tickerh {*/
    /*    0% { transform: translate3d(100%, 0, 0); }*/
    /*    100% { transform: translate3d(-400%, 0, 0); }*/
    /*}*/
</style>

<div id="meta-wrapper">
    <div id="meta">
        <span id="meta-artist"></span>
        <span id="meta-title">Amadeus. Copyright 2022 Mike Croteau</span>
        <span id="meta-minutes"></span>:<span id="meta-seconds"></span>
    </div>
</div>

<a href id="pause-action" class="invincible">
    <svg id="pause-svg" class="activity">
        <path class="s0" d="m3 0h7v25h-7z"/>
        <path class="s0" d="m15 0h7v25h-7z"/>
    </svg>
</a>

<svg id="previous" class="activity">
    <path class="s0" d="m12.1 13q-0.2-0.1-0.2-0.3v10q0 0.4-0.2 0.5-0.2 0-0.5-0.2l-9.9-10q-0.3-0.3-0.3-0.6 0-0.4 0.3-0.6l9.9-10q0.3-0.3 0.5-0.2 0.2 0.1 0.2 0.5v9.9q0-0.1 0.2-0.2l9.9-10q0.3-0.3 0.5-0.2 0.2 0.1 0.2 0.5v20.6q0 0.4-0.2 0.5-0.2 0-0.5-0.2z"/>
</svg>

<svg id="next" class="activity">
    <path class="s0" d="m4 23.7q-0.3 0.3-0.5 0.2-0.2-0.1-0.2-0.4v-20.7q0-0.3 0.2-0.4 0.2-0.1 0.5 0.2l9.9 9.9q0.2 0.1 0.2 0.3v-10q0-0.3 0.2-0.4 0.2-0.1 0.5 0.2l9.9 9.9q0.3 0.3 0.3 0.6 0 0.4-0.3 0.7l-9.9 9.9q-0.3 0.3-0.5 0.2-0.2-0.1-0.2-0.4v-10q0 0.2-0.2 0.3z"/>
</svg>

<style>
    .lower{bottom:0px;}
</style>



<div id="track-duration-seeker-wrapper">
    <span id="track-duration-seeker-percentage"></span>
</div>

<script>

    let app = angular.module('amadeus', ['ngRoute']);

    app.filter('escape', function() {
        return window.encodeURIComponent;
    });

    app.controller('AppController', function($scope, $rootScope, $location){
        $scope.query = '';

        $scope.$on('$routeChangeSuccess', function handleRouteChangeEvent(event) {
            let query_input = document.querySelector('#query-lover');
            let query_friends = document.querySelector('#query-friends')

            query_input.addEventListener("keyup", function (event) {
                if(query_input.value.length < 3){
                    event.preventDefault();
                    return;
                }
                if(query_input.value.length > 3){
                    console.log(query_input.value)
                    $scope.query = query_input.value;
                    query_friends.click()
                }
            });
        });
    });

    app.controller('PaperController', function($scope, $rootScope, $http, $routeParams){
        $scope.message = '';
        $scope.paper = {};
        $scope.show = true;
        $scope.papersCount = 0;
        $scope.show_message = false;

        $scope.renderPaper = function(){
            return $scope.render;
        }

        $scope.delete = function(scope){
            $http.post('/sheet/delete/' + scope.paper.id).then(function(response){
                $scope.show = false;
                $scope.show_message = true;
                $scope.message = 'Successfully deleted.';
            });
        }

        $scope.render_paper = function(){
            return $scope.show;
        }
        $scope.render_message = function(){
            return $scope.show_message;
        }

        $scope.$on('$routeChangeSuccess', function handleRouteChangeEvent(event) {
            $http.get('/sheet/' + $routeParams.id).then( function(response) {
                if(response.data.status !== 'horizons'){
                    $scope.show = true;
                    $scope.show_message = false;
                    $scope.paper = response.data;
                }
                if(!response.data.status == 'horizons'){
                    $scope.show = false;
                    $scope.show_message = true;
                    $scope.message = 'The post no longer exists.';
                }
            });
        });
    });

    //rc2C2IGaaMkBH7uc93k1eTC
    app.controller('SheetsController', function ($scope, $rootScope, $http, $timeout, $location, $routeParams) {
        $scope.offset = 0;
        $scope.sheets = [];
        $scope.valid = false;
        $scope.fetching = false;
        $scope.papersCount = 0;
        $scope.is_inactive_fetch = false;

        $scope.material_button = document.querySelector('#material-button');

        $scope.efx = new Audio();
        $scope.efx.src = $scope.snd;

        $scope.heart = function(paper){
            $http.post('/heart/' + paper.id).then(function(response){
                $scope.sheets.splice(0, 1, response.data);
            });
        }

        $scope.is_valid = function(){
            return $scope.valid;
        }

        $scope.validate_sheet = function(element){
            let material = document.getElementById('material');
            if(material.value.length > 21){
                $scope.valid = true;
            }
            if(material.value.length < 21){
                $scope.valid = false;
            }
        }

        $scope.get_inactive_fetch = function(){
            return $scope.is_inactive_fetch;
        }

        $scope.retrieve = function(){
            console.log($scope.papersCount, $scope.sheets.length, $scope.papersCount <= $scope.sheets.length)
            if(!$scope.fetching && $scope.sheets.length < $scope.papersCount){
                $scope.fetching = true;
                $http.get('/sheets/' + $scope.offset).then(function(response) {
                    $timeout(function() {
                        console.log('offset', $scope.offset, response.data.papers)
                        for(let sex = 0; sex < response.data.papers.length; sex++) {
                            $scope.sheets.push(response.data.papers[sex]);
                        }
                        $scope.fetching = false;
                        $scope.papersCount = $scope.sheets.length;
                        $scope.offset = $scope.offset + 3;

                        if($scope.papersCount >= $scope.sheets.length){
                            document.querySelector('#fetch').style.display = 'none';
                        }

                    }, 300);
                })
            }

        }

        $scope.send_thoughts = function() {
            if ($scope.valid) {
                let material_form = document.getElementById("material-form");
                let material_data = new FormData(material_form);
                $scope.post('/sheet/save', material_data).then(function (response) {
                    document.querySelector('#amadeus-action').click();
                })
            }
        }

        $scope.post = function(uri, form_data){
            let request = new XMLHttpRequest();
            return new Promise(function(resolve, reject){
                request.onreadystatechange = function (event) {
                    if (request.readyState !== 4) {
                        return;
                    }
                    if(request.status >= 200 && request.status < 300) {
                        resolve(JSON.parse(request.response));
                    }else {
                        console.log('error',event,request);
                        reject({
                            status: request.status,
                            statusText: request.statusText
                        });
                    }
                };
                request.open('post', uri, true);
                request.send(form_data);
            });
        }

        $scope.$on('$routeChangeSuccess', function handleRouteChangeEvent(event) {
            const has_params = JSON.stringify($routeParams) === '';
            console.log($routeParams, has_params)

            if(has_params){
                $http.get('/sheets/' + $scope.offset + '?q=' + $routeParams.query).then(function(response) {
                    $scope.offset = 3;
                    $scope.papersCount = response.data.papersCount;
                    $scope.sheets = response.data.papers;
                    console.log($scope.sheets.length >= $scope.papersCount)
                    if($scope.sheets.length >= $scope.papersCount){
                        document.querySelector('#fetch').style.display = 'none';
                    }
                })
            }
            if(!has_params){
                $http.get('/sheets/' + $scope.offset).then(function(response) {
                    $scope.offset = 3;
                    $scope.papersCount = response.data.papersCount;
                    $scope.sheets = response.data.papers;
                    console.log($scope.sheets.length >= $scope.papersCount)
                    if($scope.sheets.length >= $scope.papersCount){
                        document.querySelector('#fetch').style.display = 'none';
                    }
                })
            }

        });


    });



    app.controller('PersonController', function ($scope) {
        $scope.tab = 1;

        $scope.isSet = function (tabId) {
            console.log('is set', this.tab === tabId)
            return this.tab === tabId;
        };
    });

    app.config(function($routeProvider) {
        $routeProvider
            .when("/", {
                templateUrl : "/resources/pages/sheets.htm",
                controller: 'SheetsController'
            })
            .when("/sheets", {
                templateUrl : "/resources/pages/sheets.htm",
                controller: 'SheetsController'
            })
            .when("/sheets/:query", {
                templateUrl : "/resources/pages/sheets.htm",
                controller: 'SheetsController'
            })
            .when("/patron/:id", {
                templateUrl : "/resources/pages/patron.htm",
                controller: 'PaperController'
            })
            .when("/profile", {
                templateUrl : "/resources/pages/profile.htm",
                controller: 'PersonController'
            })
            .when("/media", {
                templateUrl : "/resources/pages/media.htm",
                controller: 'PersonController'
            })
            .when("/sheet/:id", {
                templateUrl : "/resources/pages/paper.htm",
                controller: 'PaperController'
            })
            .otherwise({
                redirectTo: '/',
                controller: 'SheetsController'
            });
    });



    const music_playlist = [
        {
            title: 'Bayi Wa Bayk',
            artist: 'Fares Karem',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/THoSekACTf6vilEuEcvQBba.mp3'
        },
        {
            title: 'Unknown',
            artist: 'Unknown',
            duration : 286,
            src : 'https://ewr1.vultrobjects.com/zeus/fTG2hiccntzFqetP5GT5gWO.mp3'
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

</body>
</html>