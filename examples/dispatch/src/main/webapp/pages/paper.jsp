

<div id="paper-expressions" ng-controller="PaperController" class="active-page">

    <a href="#!/sheets" class="flux-capacitor">&larr;&nbsp;Back</a>

    <p class="message" ng-if="render_message()">{{message}}</p>

    <div ng-if="render_paper()">
        <div class="identity-wrapper">
            <a href="#!profile:{{paper.id}}" class="profile-action">
                <img src="{{paper.photo}}" class="paper-profile-img profile-img-background"/>
                <span class="paper-user">{{paper.name}}</span>
            </a>
            <br class="clear"/>
        </div>
        <div id="patrons-photos" ng-if="paper.photos.length > 0">
            <img src="{{photo}}" ng-repeat="photo in paper.photos" class="patron-photo"/>
        </div>

        <p>{{paper.content}}</p>

        <span class="pretty-time">{{paper.timeAgo}}</span>

        <style><!-- todo: -->
            #delete-button:active{margin-top:1px;}
        </style>
        <button href class="button delete right-guide" ng-click="delete(this)" id="delete-button">Delete</button>
        <a href>&#9873;&#10097;</a>
        <br class="clear"/>
    </div>
</div>