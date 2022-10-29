<div id="sheets-wrapper">

    <div id="query-lover-wrapper">
        <input type="text" name="q" placeholder="users: activity:" id="query-lover"/>
    </div>

    <p id="main-message" class="message" style="display: none"></p>

${papers.size()}
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

                    <svg class="heart-icon-svg activity-action activity" on-click="['activate', paper]">
                        <path id="heart" class="s0" d="m13.1 23.7q-0.3 0.3-0.6 0.3-0.3 0-0.6-0.3l-8-9q-0.1-0.1-0.3-0.4-0.3-0.2-0.7-1-0.5-0.7-0.9-1.4-0.4-0.8-0.7-1.8-0.3-1.1-0.3-2.1 0-3.3 1.6-5.1 1.7-1.9 4.5-1.9 0.8 0 1.7 0.3 0.8 0.3 1.5 0.9 0.7 0.5 1.2 1 0.5 0.5 1 1 0.5-0.5 1-1 0.5-0.5 1.2-1 0.7-0.6 1.5-0.9 0.9-0.3 1.7-0.3 2.8 0 4.5 1.9 1.6 1.8 1.6 5.1 0 3.3-2.9 6.7l-8 9z"/>
                    </svg>
                    <span class="activity-count">${paper.likesCount}</span>

                    <a href="#!/patron/{{paper.id}}">
                        <svg class="share-icon-svg activity-action activity">
                            <path id="share" class="s0" d="m15.6 15.8q1.3-1.2 3-1.2 1.8 0 3.1 1.3 1.3 1.2 1.3 3 0 1.9-1.3 3.1-1.3 1.3-3.1 1.3-1.8 0-3.1-1.3-1.3-1.2-1.3-3.1 0-0.1 0-0.4l-4.9-2.5q-1.2 1.2-2.9 1.2-1.8 0-3.1-1.3-1.3-1.3-1.3-3.1 0-1.8 1.3-3.1 1.3-1.2 3.1-1.2 1.7 0 2.9 1.1l4.9-2.4q0-0.3 0-0.5 0-1.8 1.3-3.1 1.3-1.2 3.1-1.2 1.8 0 3.1 1.2 1.3 1.3 1.3 3.1 0 1.8-1.3 3.1-1.3 1.3-3.1 1.3-1.7 0-3-1.2l-4.9 2.5q0 0.3 0 0.4 0 0.2 0 0.5l4.9 2.5z"/>
                        </svg>
                    </a>
                    <span class="activity-count">${paper.sharesCount}</span>
                    <br class="clear"/>
                </div>
            </div>
        </plsar:foreach>

    </plsar:if>

</div>


