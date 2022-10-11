

<div class="section-wrapper">
    <div class="section">
        <plsar:if spec="${message != ''}">
            <div class="notify">${message}</div>
        </plsar:if>

        <h1>Reset Password</h1>

        <p>Enter cell phone that is registered with your account.</p>

        <form action="/users/send" method="post">
            <fieldset>

                <label for="cellphone">Cell Phone</label>
                <input id="cellphone" type="text" placeholder="cell phone" name="phone" style="width:60%;">

                <div class="align-right" style="margin:20px 0px 170px; text-align:right;">
                    <input type="submit" class="button retro" id="reset-password" value="Click to Begin Reset Process"/>
                </div>
            </fieldset>
        </form>
    </div>
</div>


