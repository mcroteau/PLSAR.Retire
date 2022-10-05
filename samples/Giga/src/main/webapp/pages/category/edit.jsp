<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<h1>Edit Category</h1>
<form action="${pageContext.request.contextPath}/categories/update/${category.businessId}/${category.id}" method="post">

    <input type="hidden" name="id" value="${category.id}"/>

    <input type="hidden" name="businessId" value="${category.businessId}"/>

    <label>Name<span class="information">&nbsp;&nbsp;Give your category a name!</span></label>
    <input type="text" name="name" value="${category.name}"/>


    <label>Top Category<span class="information">&nbsp;&nbsp;</span></label>
    <select name="categoryId">
        <option>This is a top level Category</option>
        <c:forEach items="${categories}" var="active">
            <c:if test="${active.id == category.categoryId}">
                <c:set var="selected" value="selected"/>
            </c:if>
            <c:if test="${active.id != category.categoryId}">
                <c:set var="selected" value=""/>
            </c:if>

            <option value="${active.id}" ${selected}>${active.name}</option>
        </c:forEach>
    </select>


    <label>URI<span class="information">&nbsp;&nbsp;Give your category a uri, must not contain any special characters.</span></label>
    <input type="text" name="uri" value="${category.uri}"/>

    <label>Header</label><span class="information">&nbsp;&nbsp;The header information that will be displayed at the top of the category page.</span>
    <textarea name="header" class="code">${category.header}</textarea>

    <label>Design<span class="information">&nbsp;&nbsp;</span></label><br/>
    <select name="designId">
        <c:forEach items="${designs}" var="active">
            <c:if test="${active.id == category.designId}">
                <c:set var="selected" value="selected"/>
            </c:if>
            <c:if test="${active.id != category.designId}">
                <c:set var="selected" value=""/>
            </c:if>

            <option value="${active.id}" ${selected}>${active.name}</option>
        </c:forEach>
    </select>

    <div style="text-align: right;margin-top: 20px;">
        <input type="submit" value="Update Category!" class="button green"/>
    </div>
</form>
