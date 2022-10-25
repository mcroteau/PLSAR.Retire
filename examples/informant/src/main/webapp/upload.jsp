<kakai:if spec="${message != ''}">
    ${message}
</kakai:if>

<h1>Upload</h1>
<form action="/upload" method="post" enctype="multipart/form-data">
    <input type="file" name="media"/>
    <input type="submit" value="Upload"/>
</form>