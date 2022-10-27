



<h1 class="left-float">New Item Group</h1>

<a href="/${business.id}/groups/options/create/" class="button orange right-float" style="margin-top:20px;">New Item Option</a>
<br class="clear"/>

<form action="/${business.id}/groups/save" method="post">

    <div class="left-float" style="width:75%;">

        <input type="hidden" name="businessId" value="${business.id}"/>

        <label>Name</label>
        <input type="text" name="name" value="${itemGroup.name}"/>

        <div class="left-float" style="width:140px;">
            <label>Categories</label>
            <span class="tiny">Active: Select 1 or many!<br/></span>
            <select name="categories" multiple>
                <ocean:each items="${categories}" var="category">
                    <ocean:if spec="${activeCategories.contains(category)}">
                        <ocean:set var="selected" value="selected"/>
                    </ocean:if>
                    <ocean:if spec="${!activeCategories.contains(category)}">
                        <ocean:set var="selected" value=""/>
                    </ocean:if>
                    <option value="${category.id}" ${selected}>${category.name}</option>
                </ocean:each>
            </select>
        </div>

        <div class="left-float" style="width:30%;">
            <label>Design<span class="information">&nbsp;&nbsp;</span></label><br/>
            <select name="designId">
                <ocean:each items="${designs}" var="active">
                    <ocean:if spec="${active.id == item.designId}">
                        <ocean:set var="selected" value="selected"/>
                    </ocean:if>
                    <ocean:if spec="${active.id != item.designId}">
                        <ocean:set var="selected" value=""/>
                    </ocean:if>
                    <option value="${active.id}" ${selected}>${active.name}</option>
                </ocean:each>
            </select>
        </div>

        <div class="left-float" style="width:250px;">
            <label>Image</label>
            <input type="file" name="media" style="width:100%;"/>
        </div>

    </div>

    <br class="clear"/>

    <div style="margin-top:40px;text-align: right">
        <input type="submit" name="save" value="Start Item Group" class="button green" onclick="this.disabled=true;this.value='Saving Item...';this.form.submit();"/>
    </div>

</form>






