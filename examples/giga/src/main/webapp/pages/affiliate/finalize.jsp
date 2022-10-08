<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
    startConfetti();
</script>
<style>
    .section-wrapper{z-index: 1}
</style>

<div class="section-wrapper">
    <div class="section">

        <c:if test="${not empty message}">
            <p class="notify">${message}</p>
        </c:if>

        <h1>Congratulations Affiliate!</h1>
        <p>Congratulations on starting a new business! We are here to help! Update your
        business uri and password in order to login.</p>

        <form action="${pageContext.request.contextPath}/affiliates/onboarding/finalize/${business.id}" method="post">

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
                <input type="submit" value="Complete Business Setup!" class="button retro" onclick="this.disabled=true;this.value='Setting up your shop! Please wait...';this.form.submit();"/>
            </div>
        </form>
    </div>
</div>