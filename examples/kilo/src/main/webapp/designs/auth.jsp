<html>
<head>
    <title>Kilo: A Marketplace for Marketplaces</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/assets/media/icon.gif" type="image/gif">
    <link rel="stylesheet" href="/assets/css/default.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"
            integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4="
            crossorigin="anonymous"></script>

    <style>
        #giga-bolt{
            height:24px;
            width: 24px;
            fill: #94e6ff;
            position: absolute;
            left: 730px;
            top:20px;
        }

        #business-select{width:171px;}

        .payments{
            color:#fff;
            background: #FF817A;
            border: solid 1px #FF817A;
            animation-name: payments;
            animation-iteration-count: infinite;
            animation-duration: 4s;
        }

        /*@keyframes payments {*/
        /*    0%{background: #FEF57A; border: solid 1px #FEF57A; color:#000;}*/
        /*    30%{background: #F0F4F5; color:#000; border:solid 1px #C9DCDC}*/
        /*    60%{background: #F0F4F5; color:#000; border:solid 1px #C9DCDC}*/
        /*    0%{background: #FEF57A; border: solid 1px #FEF57A; color:#000;}*/
        /*}*/
        /*thank you!!!!! excuse me*/
        @keyframes payments {
            0%{background: #FF817A; border: solid 1px #FF817A; color:#fff;}
            30%{background: #F0F4F5; color:#000; border:solid 1px #C9DCDC}
            60%{background: #F0F4F5; color:#000; border:solid 1px #C9DCDC}
            0%{background: #FF817A; border: solid 1px #FF817A; color:#fff;}
        }FF817A
    </style>
</head>
<body>

<div id="header-identity"></div>

<div id="outer-wrapper">

    <a href="/" id="home-href">
        <div id="identity-wrapper">
            <span class="square">&nbsp;</span>
            <span class="circle">&nbsp;</span>
            <span class="triangle">&nbsp;</span>
        </div>
        <span id="identity">Kilo</span>
        <span class="information" style="margin-top:48px;display: inline-block;">A Marketplace for Marketplaces</span>
    </a>

    <div id="header-wrapper">
        <span id="hello">Hi
            <ocean:if spec="${authUser.name != '' && authUser.name != 'null'}">
                <a href="/users/edit/${business.id}/${authUser.id}" style="color:#000;font-size:17px;" class="top">${authUser.name}</a>,
            </ocean:if>
            <ocean:if spec="${authUser.name == '' || authUser.name == 'null'}">
                <a href="/users/edit/${business.id}/${authUser.id}" style="color:#000;font-size:17px;" class="top">Mr./Mrs.</a>,
            </ocean:if>
            Welcome Back! <a href="/signout" style="color:#000;font-size:17px;" class="top">Signout!</a></span><br/><br/>
            <ocean:if spec="${businessOptions.size() > 0}">
                Business :
                <select name="business" id="business-select">
                    <ocean:each items="${businessOptions}" var="current">
                        <ocean:set var="selected" val=""/>
                        <ocean:if spec="${current.id == business.id}">
                            <ocean:set var="selected" val="selected"/>
                        </ocean:if>
                        <option value="${current.id}" ${selected}>${current.name}</option>
                    </ocean:each>
                </select>
                <a href="/businesses/new/${business.id}" class="button modern">New Business</a>
                <ocean:if spec="${!business.activationComplete}">
                    <a href="/snapshot/${business.id}" class="button payments">Setup Payments!</a>
                </ocean:if>
            </ocean:if>
            <ocean:if spec="${businessOptions.size() == 0}">
                <a href="/businesses/new/${business.id}" class="button modern">New Business</a>
            </ocean:if>
        </span>
    </div>

    <div id="menu-wrapper">

        <span id="business-identity">${business.name}.
            <ocean:if spec="${!business.activationComplete}">
                <br/>
                <a href="/businesses/signup/complete/${business.id}" class="tiny">Congratulations!</a>
            </ocean:if>
        </span>
        <ul id="sales-menu" class="menu">
            <li><a href="/snapshot/${business.id}"><span>Snapshot</span></a></li>
            <li><a href="/sales/${business.id}"><span>Sales</span></a></li>
            <li><a href="/clients/${business.id}"><span>Clients</span></a></li>
            <ocean:if spec="${!business.affiliate}">
                <li><a href="/affiliates/${business.id}"><span>Affiliates</span></a></li>
                <li><a href="/affiliates/requests/${business.id}"><span>Requests</span></a></li>
            </ocean:if>
        </ul>
        <ul id="settings-menu" class="menu">
            <li><a href="/items/${business.id}"><span>Items</span></a></li>
            <li><a href="/imports/item_groups/${business.id}"><span>Item Groups</span></a></li>
            <li><a href="/categories/${business.id}"><span>Categories</span></a></li>
            <li><a href="/designs/${business.id}"><span>Designs</span></a></li>
            <li><a href="/pages/${business.id}"><span>Pages</span></a></li>
        </ul>
        <ul id="business-menu" class="menu">
            <li><a href="/${business.uri}/asset/home" target="_blank"><span>Homepage</span></a></li>
            <li><a href="/imports/media/${business.id}"><span>Data Imports</span></a></li>
            <li><a href="/businesses/settings/${business.id}"><span>Settings</span></a></li>
            <li><a href="/assets/${business.id}"><span>Assets</span></a></li>
        </ul>

    </div>

    <div id="content-wrapper">
        <ocean:content/>
    </div>

    <ocean:if spec="${assets.size() > 0}">
        <div id="assets-wrapper">
            <h2 style="text-align: center">Web Assets</h2>
            <table>
                <tr>
                    <th>Name</th>
                    <th>Url</th>
                </tr>
                <ocean:each items="${assets}" var="asset">
                    <tr>
                        <ocean:if spec="${asset.type == 'media'}">
                            <td>
                                <span class="information">${asset.name}</span>
                            </td>
                            <td>
                                <img src="/media/${asset.meta}" />
                                <a href="/media/${asset.meta}" class="information" target="_blank">/media/${asset.meta}</a>
                            </td>
                        </ocean:if>
                        <ocean:if spec="${asset.type != 'media'}">
                            <td>
                                <span class="information">${asset.name}</span>
                            </td>
                            <td>
                                <a href="/go/${asset.meta}" class="information" target="_blank">/go/${asset.meta}</a>
                            </td>
                        </ocean:if>
                    </tr>
                </ocean:each>
            </table>
        </div>
    </ocean:if>

    <br class="clear"/>

</div>




<div id="footer-wrapper"></div>


<script>
    $(document).ready(function(){
        let $businessSelect = $('#business-select');
        $businessSelect.change(function(){
            const id = $businessSelect.val();
            console.info('id', id);
            window.location = "/snapshot/" + id;
        })


        $('.code').keydown(function (evt) {
            var keyCode = evt.keyCode || evt.which;

            if (keyCode === 9) {
                evt.preventDefault();
                const TAB_SIZE = 4;
                document.execCommand('insertText', false, ' '.repeat(TAB_SIZE));
            }
        });
    })
</script>

</body>
</html>
