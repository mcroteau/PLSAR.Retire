<style>
    body{background:#fff;padding:0px 0px 200px 0px;}
    #row{display:table-row}
    #left,#middle,#right,#end{display:table-cell;vertical-align: top;}
    #left{
        padding:30px 40px 0px 100px;
        text-align:center;
        width: 43%;
        overflow:auto;
        background: #f5f7f8;
        background: #fff;
    }
    #middle{
        text-align: left;
        padding:30px 40px 200px 40px;
        width: 30%;
        border-left:dashed 1px #d9dde3;
    }
    #right{
        width: calc(15%);
        border-left:dashed 1px #d9dde3;
    }
    #final {
        width: calc(0%);
        clear: right;
    }
    .paper-wrapper{
        text-align:left;
        margin-bottom:40px;
    }
    .paper-action-wrapper{
        margin-top:0px;
    }
    .pretty-time{
        display:block;
    }
    #query-lover{color:#000;background:#fff;border:solid 1px #d1d7df;}
    #query-lover::placeholder{color:#000;}
    #dispatch{float:right;margin:30px 0px}
    #occurrence{float:left;margin-left:10px;margin-top:13px;}
    #paper-profile-img{height:39px; width:39px;margin-bottom:10px;}
    #paper-editor{height:450px;background: #fff;border-bottom:dashed 1px #000;}
    #paper-editor:focus{background: #fff;}
    #paper-editor::placeholder{color:#000;}
    .profile-img-background{background:#FEF57A}
    .profile-img{float:left;height:70px;width:70px;border-radius:170px;}
    .left{float:left;}
    .block{display:block}
    .user-action{width:190px;}
</style>


<div id="row">
    <div id="left">

        <img src="${user.photo}" class="profile-img-background profile-img"/>
        <br class="clear"/>

        <h1 class="left">${user.name}</h1>
        <br class="clear"/>

        <plsar:if spec="${userId != user.id}">
            <div class="left user-action">
                <plsar:if spec="${!following}">
                    <a href="/users/follow/${user.id}" class="future button">Follow</a>
                </plsar:if>
                <plsar:if spec="${following}">
                    <a href="/users/unfollow/${user.id}" class="future button">Unfollow</a>
                </plsar:if>
                <p>Get activity in your dispatch feed by this user.</p>
            </div>

            <div class="left user-action">
                <plsar:if spec="${!requested}">
                    <a href="/users/request/${user.id}" class="button yellow">Request Access</a>
                </plsar:if>
                <plsar:if spec="${requested}">
                    <a href="/users/request/cancel/${user.id}" class="button remove">Cancel Access</a>
                </plsar:if>
                <p>Get reference to what users have @ to this user.</p>
            </div>
            <br class="clear"/>
        </plsar:if>

    </div>


    <div id="middle">

        <plsar:if spec="${papers.size() > 0}">

            <div id="query-lover-wrapper">
                <input type="text" name="q" placeholder="users: activity:" id="query-lover"/>
            </div>

            <plsar:foreach items="${papers}" var="paper">
                <div class="paper-wrapper" id="sheet-${paper.id}">
                    <div class="identity-wrapper">
                        <a href="/users/identity/${paper.userId}" class="profile-action">
                            <img src="${paper.photo}" class="paper-profile-img profile-img-background"/>
                            <span class="paper-user">${paper.name}</span>
                        </a>
                        <br class="clear"/>
                    </div>
                    <a href="/paper/${paper.id}">
                        <div class="paper-photos">
                            <plsar:foreach items="${paper.photos}" var="paperphoto">
                                <img src="${paperphoto}" class="paper-photo"/>
                            </plsar:foreach>
                        </div>
                    </a>

                    <a href="/paper/${paper.id}">
                        <p>${paper.content}</p>
                    </a>

                    <div class="paper-action-wrapper">
                        <span class="pretty-time">${paper.timeAgo}</span>
                        <a href="/patron/${paper.id}" class="future button action action-share">Share : ${paper.sharesCount}</a>

                        <style>
                            .action{display:inline-block;}
                            .action-celebrity{float:right}
                            .action-share{float:left;}
                            .visual{background: transparent; border: transparent; padding:10px 23px;}
                            .celebrity{height:25px; width:25px;}
                        </style>
                        <a href="/sheets/heart/${paper.id}" class="button visual action action-celebrity">
                            <svg class="celebrity" viewBox="0 0 102 102" width="102" height="102">
                                <path id="Shape 1" class="s0" d="m77.8 25.8q1.3 0 2.3 0.9 1.1 1.3 0.4 2.7l-35.3 70.7q-0.9 1.5-2.8 1.5-0.2 0-0.9-0.1-1.1-0.3-1.6-1.2-0.6-0.8-0.3-1.8l12.8-49.3-26.5 6.1q-0.3 0.1-0.8 0.1-1.2 0-2-0.7-1.2-0.9-0.9-2.4l13.2-50.3q0.2-0.9 1-1.5 0.8-0.5 1.8-0.5h21.5q1.3 0 2.1 0.8 0.9 0.7 0.9 1.8 0 0.5-0.4 1.1l-11.2 28.2 26-5.9q0.5-0.2 0.7-0.2z"/>
                            </svg>
                            <span class="activity-count">${paper.likesCount}</span>
                        </a>

                        <br class="clear"/>
                    </div>
                </div>
            </plsar:foreach>

        </plsar:if>

    </div>

    <div id="right"></div>
    <div id="final"></div>

</div>


