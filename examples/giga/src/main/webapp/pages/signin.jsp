
<style>
    #signin-wrapper{
        width:560px;
        text-align: left;
        margin: auto;
    }
    a{font-size:21px; font-weight:300; color:#000; text-decoration: none}
</style>

<plsar:if spec="${not empty congratulations}">
    <script>
        startConfetti();
    </script>
</plsar:if>

<div class="content-wrapper">

    <div id="signin-wrapper">

        <plsar:if spec="${message != ''}">
            <p class="notify">${message}</p>
        </plsar:if>

        <h1>Business &<br/>
            Affiliate Signin!</h1>

        <p class="lightf" style="font-size:21px">All business owners, get ready, set Sell!</p>

        <form action="/authenticate" modelAttribute="user" method="post" >

            <div class="form-group">
                <label for="username">Username
                <br/><span class="tiny">Phone Number or Email Address</span>
                </label>
                <input type="text" name="username" class="form-control" id="username" placeholder=""  value=""  style="width:100%;">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" name="password" class="form-control" id="password" style="width:100%;" value=""  placeholder="&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;">
            </div>

            <div style="text-align:right; margin:30px 0px;">
                <input type="submit" class="button green" value="Signin!">
            </div>

        </form>

        <div  style="text-align: center;margin:21px auto 130px auto">
            <a href="/users/reset">Forgot Password</a>&nbsp;&nbsp;
            <a href="/signup">Business Signup!</a>&nbsp;&nbsp;
        </div>
    </div>
</div>

