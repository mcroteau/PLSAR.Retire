<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>New Business</h1>
<form action="${pageContext.request.contextPath}/businesses/save" method="post">

    <input type="hidden" name="userId" value="${authUser.id}"/>

    <label>Name<span class="information">&nbsp;&nbsp;Give your business a name!</span></label>
    <input type="text" name="name" value=""/>

    <label>URI<br/><span class="information">Give your business a uri, must not contain special characters except for underscores _.</span>
    <br/><span class="assert information" style="color:#ff0000">Cannot be changed once saved.</span>
    </label>
    <input type="text" name="uri" value=""/>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Save Business!" class="button green" onclick="this.disabled=true;this.value='Saving...';this.form.submit();"/>
    </div>
</form>
