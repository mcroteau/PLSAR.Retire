<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>New Item Image Import</h1>
<p>Okay, this is the deal, name your images:</p>

<p class="yellow">Name-With-Casing.Price.Quantity.Weight.extension (jpg/png)</p>

<p>If you do the following, you can easily convert images into items! This makes life easier!
We hope you enjoy.</p>

<form action="${pageContext.request.contextPath}/import/media/${business.id}" method="post" enctype="multipart/form-data">

    <label>Item Image Files</label>
    <input type="file" name="media" multiple/>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Import Item Images!" class="button green" onclick="this.disabled=true;this.value='Processing Import, please wait...';this.form.submit();"/>
    </div>
</form>
