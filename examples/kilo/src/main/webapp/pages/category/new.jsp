

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1>New Category</h1>
<form action="/categories/save" method="post">

    <input type="hidden" name="businessId" value="${business.id}"/>

    <label>Name<span class="information">&nbsp;&nbsp;Give your category a name!</span></label>
    <input type="text" name="name" value=""/>

    <label>Parent Category<span class="information">&nbsp;&nbsp;</span></label>
    <select name="categoryId">
        <option value="">This is a top level Category</option>
        <ocean:each items="${categories}" var="active">
            <option value="${active.id}">${active.name}</option>
        </ocean:each>
    </select>

    <label>URI<span class="information">&nbsp;&nbsp;Give your category a uri, must not contain any special characters.</span></label>
    <input type="text" name="uri" value=""/>

    <label>Header</label><span class="information">&nbsp;&nbsp;The header information that will be displayed at the top of the category page.</span>
    <textarea name="header" class="code"></textarea>

    <label>Design<span class="information">&nbsp;&nbsp;</span></label><br/>
    <select name="designId">
        <ocean:each items="${designs}" var="active">
            <option value="${active.id}">${active.name}</option>
        </ocean:each>
    </select>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Save Category!" class="button green" onclick="this.disabled=true;this.value='Saving...';this.form.submit();"/>
    </div>
</form>
