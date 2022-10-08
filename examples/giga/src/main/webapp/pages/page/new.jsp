<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>New Page</h1>
<p>Create a new page to support your business front end.</p>

<form action="${pageContext.request.contextPath}/pages/save" method="post" enctype="multipart/form-data">

    <input type="hidden" name="businessId" value="${business.id}"/>

    <label>Name</label>
    <p class="information"></p>
    <input type="text" name="name" value=""/>

    <label>Uri</label>
    <p class="information">All lowercase no special characters</p>
    <input type="text" name="uri" value=""/>

    <label>Page Content</label>
    <p class="information">Accepts Html</p>
    <textarea name="content" class="code"></textarea>

    <label>Design<span class="information">&nbsp;&nbsp;</span></label><br/>
    <select name="designId">
        <c:forEach items="${designs}" var="active">
            <option value="${active.id}">${active.name}</option>
        </c:forEach>
    </select>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Save Page!" class="button green" onclick="this.disabled=true;this.value='Saving Page...';this.form.submit();"/>
    </div>
</form>
