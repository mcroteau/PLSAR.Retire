
<script src="/benefit/media/confetti.js"></script>
<script>
    startConfetti();
</script>

<div class="section-wrapper" style="z-index: 0">
    <div class="section" style="left:calc(50% - 345px); position:absolute; top:200px;">

        <plsar:if spec="${message != ''}">
            <p class="notify">${message}</p>
        </plsar:if>

        <h1>Congratulations Affiliate!</h1>
        <p>Congratulations on starting a new business! We are here to help! Update your
        business uri and password in order to login.</p>

        <form action="/affiliates/onboarding/finalize/${business.id}" method="post">

            <input type="hidden" name="id" value="${business.id}">
            <input type="hidden" name="name" value="${business.name}">
            <input type="hidden" name="phone" value="${business.phone}">
            <input type="hidden" name="owner" value="${business.owner}">
            <input type="hidden" name="uri" value="${business.uri}">
            <input type="hidden" name="primaryId" value="${business.primaryId}">

            <label>Business Name: ${business.name}</label>

            <label>Shop Uri : /${business.uri}</label>

            <label>Username/Email</label>
            <input type="text" name="email" value="${business.email}"/>

            <label>Password</label>
            <span class="tiny">Your shiny new password for your account</span>
            <input type="text" name="password" value="${business.password}"/>

            <div class="align-right" style="margin:30px 0px 170px 0px;">
                <input type="submit" value="Complete Business!" class="button retro" onclick="this.disabled=true;this.value='Please wait...';this.form.submit();"/>
            </div>
        </form>
    </div>
</div>