<style>
    .paper-photo{width:100%;}
    .paper-photos{margin-top:20px;}
    .paper-photo{margin-bottom:10px;border-radius:35px;}
</style>

<div id="sheets-wrapper" ng-controller="SheetsController" class="active-page">

<!--    <div id="search-box-wrapper">-->
<!--        <input type="text" name="q" placeholder="users: artists: albums: genres: activity:" id="query-lover"/>-->
<!--    </div>-->

    <div class="paper-wrapper" ng-repeat="paper in sheets" id="sheet-{{paper.id}}">
        <div class="identity-wrapper">
            <a href="#!/user/{{paper.id}}" class="profile-action">
                <img src="{{paper.photo}}" class="paper-profile-img profile-img-background"/>
                <span class="paper-user">{{paper.name}}</span>
            </a>
            <br class="clear"/>
        </div>

        <a href="#!/sheet/{{paper.id}}">
            <div ng-if="paper.photos.length > 0" class="paper-photos">
                <img src="{{photo}}" ng-repeat="photo in paper.photos" class="paper-photo"/>
            </div>
        </a>

        <a href="#!/sheet/{{paper.id}}">
            <p>{{paper.content}}</p>
        </a>

        <div class="paper-action-wrapper">
            <span class="pretty-time">{{paper.timeAgo}}</span>

            <svg class="heart-icon-svg activity-action activity" ng-click="heart(paper)">
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
        </a>
    </div>

    <div class="center-align">
        <a href ng-click="retrieve()" ng-class="{inactive:get_inactive_fetch()}" class="button" id="fetch">Fetch More.</a>
    </div>

    <div id="send-material-wrapper">
        <form action="/paper/action" method="post" enctype="multipart/form-data" id="material-form">
            <textarea ng-change="validate_sheet(this)" ng-model="paper" type="text" name="material" id="material" placeholder="What's on your mind?"></textarea>
            <div id="send-material-divider"></div>

            <div id="send-material-fotos-wrapper">
                <input type="file" name="material-fotos" id="send-material-fotos-input" multiple/>
                <svg id="send-material-fotos-svg">
                    <path fill-rule="evenodd" style="cursor:hand" class="s0" d="m7.6 6.3q0.7 0.7 0.7 1.8 0 1.1-0.7 1.9-0.7 0.8-1.8 0.7-1 0.1-1.7-0.7-0.8-0.8-0.8-1.9 0-1.1 0.8-1.8 0.7-0.8 1.7-0.8 1.1 0 1.8 0.8zm8.6 1.4l-6.6 7-2.1-2.2-4.2 4.4v2.6h18.4v-6.1zm6.9-3.8q0.1 0.1 0.2 0.3v16.6q-0.1 0.2-0.2 0.3-0.1 0.1-0.3 0.1h-20.8q-0.2 0-0.3-0.1-0.1-0.1-0.1-0.3v-16.6q0-0.2 0.1-0.3 0.1-0.2 0.3-0.2h20.8q0.2 0 0.3 0.2zm1.2-1.3q-0.6-0.6-1.5-0.6h-20.8q-0.9 0-1.5 0.6-0.6 0.7-0.6 1.6v16.6q0 0.9 0.6 1.5 0.6 0.7 1.5 0.7h20.8q0.9 0 1.5-0.7 0.6-0.6 0.6-1.5v-16.6q0-0.9-0.6-1.6z"/>
                </svg>
            </div>
        </form>
        <a href class="button" id="material-button" ng-click="send_thoughts()" ng-class="{active:is_valid()}">Share Thoughts</a>
        <br class="clear"/>
    </div>

</div>

