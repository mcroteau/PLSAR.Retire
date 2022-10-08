<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>New Asset</h1>
<p>Use the following to upload artifacts to support your web front end.</p>

<form action="${pageContext.request.contextPath}/assets/save" method="post" enctype="multipart/form-data">

    <input type="hidden" name="businessId" value="${business.id}"/>

    <label>Name</label>
    <p class="information">Give your web asset a good name.</p>
    <input type="text" name="name" value="Change Me."/>

    <label>Asset Type<span class="information">&nbsp;&nbsp;Is it a javascript, css or image file? We he^rt Youtube, so no video. You can embed video in pages, category headers and item pages.</span></label>
    <select name="type">
        <option value="media">Image</option>
        <option value="css">Css</option>
        <option value="javascript">Javascript</option>
    </select>

    <label>Asset File</label>
    <input type="file" name="asset"/>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Upload Asset!" class="button green" class="button retro gigantic" id="process" onclick="this.disabled=true;this.value='Uploading Asset, Please Wait...';this.form.submit();"/>
    </div>
</form>
