<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>New Item</h1>
<form action="${pageContext.request.contextPath}/items/save/${business.id}" method="post" enctype="multipart/form-data">

    <input type="hidden" name="businessId" value="${business.id}"/>

    <label>Name<span class="information">&nbsp;&nbsp;Give your item a name!</span></label>
    <input type="text" name="name" value=""/>

    <label>Price<span class="information">&nbsp;&nbsp;</span></label>
    <input type="text" name="price" value="" style="width:30%;"/>

    <label>Image</label>
    <input type="file" name="media"/>

    <label>Quantity<span class="information">&nbsp;&nbsp;</span></label>
    <input type="text" name="quantity" value="" style="width:30%;"/>

    <label>Categories</label>
    <select name="categories" multiple>
        <c:forEach items="${categories}" var="category">
            <option value="${category.id}">${category.name}</option>
        </c:forEach>
    </select>

    <label>Description<span class="information">&nbsp;&nbsp;Description can contain html.</span></label>
    <textarea name="description" class="code"></textarea>

    <label>Design<span class="information">&nbsp;&nbsp;</span></label><br/>
    <select name="designId">
        <c:forEach items="${designs}" var="active">
            <option value="${active.id}" ${selected}>${active.name}</option>
        </c:forEach>
    </select>

    <label>Weight<span class="information">&nbsp;Weight in ounces. Required when leveraging shipping calculations.</span></label>
    <input type="text" name="weight" value="" style="width:30%;"/>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Save Item!" class="button green" onclick="this.disabled=true;this.value='Saving Item...';this.form.submit();"/>
    </div>
</form>
