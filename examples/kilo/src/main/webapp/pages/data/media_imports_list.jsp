
<%@ page import="kilo.Giga" %>

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

<h1 class="left-float">Item Image Imports</h1>
<div class="right-float" style="text-align: right;margin-top: 20px;">
    <div class="right-float">
        <form action="/import/media/convert/${business.id}/${dataImport.id}" method="post">
            <input type="submit" class="button remove" value="Convert to Items!">
        </form>
    </div>
</div>
<br class="clear"/>

<p>What is an item image import? An item image import is an image import that can be easily converted
into items.</p>

<plsar:if spec="${mediaImports.size() > 0}">
    <table>
        <tr>
            <th></th>
            <th>Details</th>
        </tr>
        <c:forEach var="mediaImport" items="${mediaImports}" varStatus="idx">
            <tr>
                <td style="width:60px;">
                    <img src="${mediaImport.uri}" style="width:150px;border-radius:12px;"/>
                </td>
                <td>
                    <form action="/import/media/update/${business.id}/${mediaImport.importId}" method="post">
                        <input type="hidden" name="id" value="${mediaImport.id}"/>
                        <label>Name</label>
                        <input type="text" name="name" placeholder="Name" value="${mediaImport.name}" class="grid"/>

                        <label>Price</label>
                        <input type="text" name="price"  placeholder="Price" value="${siteService.getPrice(mediaImport.price)}" class="grid"/>

                        <label>Quantity</label>
                        <input type="text" name="quantity"  placeholder="Quantity" value="${mediaImport.quantity}" class="grid"/>

                        <label>Weight</label>
                        <input type="text"  name="weight" placeholder="Weight" value="${mediaImport.weight}" class="grid"/>

                        <label>Category</label>
                        <select name="categoryId">
                            <c:forEach items="${categories}" var="category">
                                <plsar:if spec="${category.id == mediaImport.categoryId}">
                                    <c:set var="selected" value="selected"/>
                                </plsar:if>
                                <plsar:if spec="${category.id != mediaImport.categoryId}">
                                    <c:set var="selected" value=""/>
                                </plsar:if>
                                <option value="${category.id}" ${selected}>${category.name}</option>
                            </c:forEach>
                        </select>

                        <div style="text-align: right;margin:20px">
                            <input type="submit" value="Update Item!" class="button green"/>
                        </div>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</plsar:if>

<plsar:if spec="${mediaImports == null || mediaImports.size() == 0}">
    <p class="notify">No item image imports added yet! <a href="/import/media/${business.id}" class="href-dotted">New Import</a></p>
</plsar:if>
