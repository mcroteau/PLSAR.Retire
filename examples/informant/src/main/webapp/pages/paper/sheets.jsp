<style>
    body{background:#fff;}
    #left,#middle,#right,#end{float:left;height:100%;}
    #left{
        text-align:center;
        width: calc(40%);
        overflow:auto;
    }
    #middle{
        text-align: left;
        padding:0px 40px;
        width: calc(20% - 40px);
        text-align: center;
        border-left:dashed 1px #d9dde3;
    }
    #right{
        width: calc(15%);
        border-right:solid 1px #68718d;
    }
    #end {
        width: calc(0%);
        border-right: solid 1px #68718d;
        clear: right;
    }

</style>

<div id="left">
    <form action="/sheets/save" enctype="multipart/form-data" method="post">
        <textarea name="paper-editor"></textarea>
    </form>
</div>

<style>

</style>

<div id="middle">
    <plsar:if spec="${papers.size() > 0}">

        <plsar:foreach items="${papers}" var="paper">
            <div class="paper-wrapper" id="sheet-${paper.id}">
                <div class="identity-wrapper">
                    <a href="/users/${paper.userId}" class="profile-action">
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
                    <a href="/patron/${paper.id}" class="future button">Share : ${paper.sharesCount}</a>
                    <br class="clear"/>
                </div>
            </div>
        </plsar:foreach>

    </plsar:if>

</div>

<div id="right">

</div>
<div id="end"></div>

</div>


