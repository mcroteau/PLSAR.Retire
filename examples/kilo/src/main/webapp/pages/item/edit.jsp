

<style>
    #href{
        top:230px;
        right:322px;
        position:absolute;
    }
</style>

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<a href="/${business.uri}/items/${categoryId}/${item.id}" class="button orange" id="href" target="_blank">Live Item Page</a>

<img src="${item.imageUri}" width="150" style="border-radius: 16px"/>

<h1>Edit Item</h1>

<form action="/items/update/${business.id}/${item.id}" method="post" enctype="multipart/form-data">

    <input type="hidden" name="id" value="${item.id}"/>
    <input type="hidden" name="businessId" value="${business.id}"/>
    <input type="hidden" name="imageUri" value="${item.imageUri}"/>


    <ocean:if spec="${business.affiliate}">
        <input type="hidden" name="name" value="${item.name}"/>
        <input type="hidden" name="quantity" value="${item.quantity}"/>
        <input type="hidden" name="weight" value="${item.weight}"/>
        <input type="hidden" name="price" value="${item.price}"/>
        <input type="hidden" name="description" value="${item.description}"/>
    </ocean:if>

    <ocean:if spec="${!business.affiliate}">
        <label>Name<span class="information">&nbsp;&nbsp;Give your item a name!</span></label>
        <input type="text" name="name" value="${item.name}"/>

        <label>Price $<span class="information">&nbsp;&nbsp;</span></label>
        <span class="tiny">Enter price without the $ dollar sign.</span>
        <input type="text" name="price" value="${item.price}" style="width:30%;"/>

        <input type="hidden" name="affiliatePrice" value="${item.affiliatePrice}"/>
    </ocean:if>


    <ocean:if spec="${business.affiliate}">
        <label>Your Price $ <br/>Business Price : <strong>$${siteService.getPriceTres(item.price)}</strong></label>
        <input type="text" name="affiliatePrice" value="${item.affiliatePrice}" style="width:30%;float:left;"/>
        <span class="tiny left-float" style="display: inline-block;margin-left:20px;">Enter price without the $ dollar sign. <br/>Cannot set lower than the business list price.</span><br/>
        <br class="clear"/>
    </ocean:if>

    <ocean:if spec="${!business.affiliate}">
        <label>Image</label>
        <input type="file" name="media"/>
        <p class="information">${item.imageUri}</p>
    </ocean:if>

    <ocean:if spec="${!business.affiliate}">
        <label>Quantity<span class="information">&nbsp;&nbsp;</span></label>
        <input type="text" name="quantity" value="${item.quantity}" style="width:30%;"/>
    </ocean:if>

    <label>Categories</label>
    <span class="tiny">Active: Select 1 or many<br/></span>
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

    <label>Description<span class="information">&nbsp;&nbsp;Description can contain html.</span></label>
    <textarea name="description" class="code">${item.description}</textarea>

    <ocean:if spec="${!business.affiliate}">
        <label>Weight<span class="information">&nbsp;Weight in ounces. Required when leveraging shipping calculations.</span></label>
        <input type="text" name="weight" value="${item.weight}" style="width:30%;"/>
    </ocean:if>
    <br class="clear"/>

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

    <div class="left-float" style="margin-left:10px;width:40%;">
        <label>Status<span class="information">&nbsp;&nbsp;</span></label><br/>
        <select name="active">
            <ocean:if spec="${item.active}">
                <ocean:set var="trueSelected" value="selected"/>
                <ocean:set var="falseSelected" value=""/>
            </ocean:if>
            <ocean:if spec="${!item.active}">
                <ocean:set var="trueSelected" value=""/>
                <ocean:set var="falseSelected" value="selected"/>
            </ocean:if>
            <option value="true" ${trueSelected}>Active</option>
            <option value="false" ${falseSelected}>Inactive</option>
        </select>
    </div>
    <br class="clear"/>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Update Item!" class="button green"/>
    </div>
</form>
