<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>New Design</h1>
<form action="${pageContext.request.contextPath}/designs/save" method="post">

    <input type="hidden" name="businessId" value="${business.id}"/>

    <label>Name<span class="information">&nbsp;&nbsp;Give your design a name!</span></label>
    <input type="text" name="name" value=""/>

    <label>Html<span class="information">&nbsp;&nbsp;Html of the design, {{content}} tag required.
        Can include Javascript & Css. Actually we expect it to.</span></label>
    <textarea name="design" class="code"></textarea>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Save Design!" class="button green" onclick="this.disabled=true;this.value='Saving Design...';this.form.submit();"/>
    </div>

</form>
