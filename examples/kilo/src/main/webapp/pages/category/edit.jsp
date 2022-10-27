

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1>Edit Category</h1>
<form action="/categories/update/${category.businessId}/${category.id}" method="post">

    <input type="hidden" name="id" value="${category.id}"/>

    <input type="hidden" name="businessId" value="${category.businessId}"/>

    <label>Name<span class="information">&nbsp;&nbsp;Give your category a name!</span></label>
    <input type="text" name="name" value="${category.name}"/>


    <label>Top Category<span class="information">&nbsp;&nbsp;</span></label>
    <select name="categoryId">
        <option>This is a top level Category</option>
        <ocean:each items="${categories}" var="active">
            <ocean:if spec="${active.id == category.categoryId}">
                <ocean:set var="selected" value="selected"/>
            </ocean:if>
            <ocean:if spec="${active.id != category.categoryId}">
                <ocean:set var="selected" value=""/>
            </ocean:if>

            <option value="${active.id}" ${selected}>${active.name}</option>
        </ocean:each>
    </select>


    <label>URI<span class="information">&nbsp;&nbsp;Give your category a uri, must not contain any special characters.</span></label>
    <input type="text" name="uri" value="${category.uri}"/>

    <label>Header</label><span class="information">&nbsp;&nbsp;The header information that will be displayed at the top of the category page.</span>
    <textarea name="header" class="code">${category.header}</textarea>

    <label>Design<span class="information">&nbsp;&nbsp;</span></label><br/>
    <select name="designId">
        <ocean:each items="${designs}" var="active">
            <ocean:if spec="${active.id == category.designId}">
                <ocean:set var="selected" value="selected"/>
            </ocean:if>
            <ocean:if spec="${active.id != category.designId}">
                <ocean:set var="selected" value=""/>
            </ocean:if>

            <option value="${active.id}" ${selected}>${active.name}</option>
        </ocean:each>
    </select>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Update Category!" class="button green"/>
    </div>
</form>
