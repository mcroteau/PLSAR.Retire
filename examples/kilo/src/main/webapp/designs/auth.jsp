<html>
<head>
    <title>Giga: A Marketplace for Marketplaces</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/benefit/media/icon.gif" type="image/gif">
    <link rel="stylesheet" href="/benefit/css/default.css?v=<%=System.currentTimeMillis()%>">
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

<div id="header-identity">
<%--    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 134 134" id="kilo-bolt">--%>
<%--        <path d="M49 1L21 88L57 88L42 134L84 134L113 47L92 47L79 47L75 47L91 1L49 1Z" />--%>
<%--    </svg>--%>
</div>

<div id="outer-wrapper">

    <a href="/" id="home-href">
        <div id="identity-wrapper">
            <span class="square">&nbsp;</span>
            <span class="circle">&nbsp;</span>
            <span class="triangle">&nbsp;</span>
        </div>
        <span id="identity">Giga</span>
        <span class="information" style="margin-top:48px;display: inline-block;">A Marketplace for Marketplaces</span>
    </a>

    <div id="header-wrapper">

        <span id="hello">Hi
            <plsar:if spec="${authUser.name != ''}">
                <a href="/users/edit/${business.id}/${authUser.id}" style="color:#000;font-size:17px;" class="top">${authUser.name}</a>,
            </plsar:if>
            <plsar:if spec="${authUser.name == ''}">
                <a href="/users/edit/${business.id}/${authUser.id}" style="color:#000;font-size:17px;" class="top">Mr./Mrs.</a>,
            </plsar:if>
            Welcome Back! <a href="/signout" style="color:#000;font-size:17px;" class="top">Signout!</a></span><br/><br/>
        <plsar:if spec="${businessOptions.size() > 0}">
            Business :
            <select name="business" id="business-select">
                <c:forEach var="current" items="${businessOptions}">
                    <plsar:if spec="${current.id == business.id}">
                        <plsar:set var="selected" value="selected"/>
                    </plsar:if>
                    <plsar:if spec="${current.id != business.id}">
                        <plsar:set var="selected" value=""/>
                    </plsar:if>
                    <option value="${current.id}" ${selected}>${current.name}</option>
                </c:forEach>
            </select>
            <a href="/businesses/new/${business.id}" class="button modern">New Business</a>
            <plsar:if spec="${!business.activationComplete}">
                <a href="/snapshot/${business.id}" class="button payments">Setup Payments!</a>
            </plsar:if>
        </plsar:if>
        <plsar:if spec="${businessOptions.size() == 0}">
            <a href="/businesses/new/${business.id}" class="button modern">New Business</a>
        </plsar:if>
    </div>

    <div id="menu-wrapper">

        <span id="business-identity">${business.name}.
            <plsar:if spec="${!business.activationComplete}">
                <br/>
                <a href="/businesses/signup/complete/${business.id}" class="tiny">Congratulations!</a>
            </plsar:if>
        </span>
        <ul id="sales-menu" class="menu">
            <li><a href="/snapshot/${business.id}"><span>Snapshot</span></a></li>
            <li><a href="/sales/${business.id}"><span>Sales</span></a></li>
            <li><a href="/clients/${business.id}"><span>Clients</span></a></li>
            <plsar:if spec="${!business.affiliate}">
                <li><a href="/affiliates/${business.id}"><span>Affiliates</span></a></li>
                <li><a href="/affiliates/requests/${business.id}"><span>Requests</span></a></li>
            </plsar:if>
        </ul>
        <ul id="settings-menu" class="menu">
            <li><a href="/items/${business.id}"><span>Items</span></a></li>
            <li><a href="/categories/${business.id}"><span>Categories</span></a></li>
            <li><a href="/designs/${business.id}"><span>Designs</span></a></li>
            <li><a href="/pages/${business.id}"><span>Pages</span></a></li>
        </ul>
        <ul id="business-menu" class="menu">
            <li><a href="/${business.uri}/asset/home" target="_blank"><span>Homepage</span></a></li>
            <li><a href="/businesses/settings/${business.id}"><span>Settings</span></a></li>
            <li><a href="/imports/media/${business.id}"><span>Data Imports</span></a></li>
            <li><a href="/assets/${business.id}"><span>Assets</span></a></li>
        </ul>

    </div>

    <div id="content-wrapper">
        <plsar:content/>
    </div>

    <plsar:if spec="${assets.size() > 0}">
        <div id="assets-wrapper">
            <h2 style="text-align: center">Web Assets</h2>
            <table>
                <tr>
                    <th>Name</th>
                    <th>Url</th>
                </tr>
                <plsar:iterate items="${assets}" var="asset">
                    <tr>
                        <plsar:if spec="${asset.type == 'media'}">
                            <td>
                                <span class="information">${asset.name}</span>
                            </td>
                            <td>
                                <img src="/media/${asset.meta}" />
                                <a href="/media/${asset.meta}" class="information" target="_blank">/media/${asset.meta}</a>
                            </td>
                        </plsar:if>
                        <plsar:if spec="${asset.type != 'media'}">
                            <td>
                                <span class="information">${asset.name}</span>
                            </td>
                            <td>
                                <a href="/go/${asset.meta}" class="information" target="_blank">/go/${asset.meta}</a>
                            </td>
                        </plsar:if>
                    </tr>
                </plsar:iterate>
            </table>
        </div>
    </plsar:if>

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
