<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.Giga" %>

<c:if test="${not empty message}">
    <p class="notify">${message}</p>
</c:if>

<h1 class="left-float">Power Grid!
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 134 134" id="giga-bolt">
        <path d="M49 1L21 88L57 88L42 134L84 134L113 47L92 47L79 47L75 47L91 1L49 1Z" />
    </svg>
</h1>
<br class="clear"/>

<p>The grid view gives you a quick and easy way to update items.</p>

<c:if test="${items.size() > 0}">
    <table>
        <tr>
            <th></th>
            <th>Details</th>
        </tr>
        <c:forEach var="item" items="${items}" varStatus="idx">
            <tr>
                <td style="width:60px;">
                    <img src="${item.imageUri}" style="width:150px;border-radius:12px;"/>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/items/grid/update/${business.id}/${item.id}" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="id" value="${item.id}"/>
                        <input type="hidden" name="businessId" value="${item.businessId}"/>
                        <input type="hidden" name="imageUri" value="${item.imageUri}"/>
                        <input type="hidden" name="designId" value="${item.designId}"/>
                        <input type="hidden" name="active" value="${item.active}"/>


                        <c:if test="${business.affiliate}">
                            <input type="hidden" name="name" value="${item.name}"/>
                            <input type="hidden" name="quantity" value="${item.quantity}"/>
                            <input type="hidden" name="weight" value="${item.weight}"/>
                        </c:if>

                        <c:if test="${!business.affiliate}">
                            <label>Name</label>
                            <input type="text" name="name" placeholder="Name" value="${item.name}" class="grid"/>
                        </c:if>
                        <c:if test="${business.affiliate}">
                            <label>Name</label>
                            <strong>${item.name}</strong>
                        </c:if>

                        <label>Price</label>
                        <input type="text" name="price"  placeholder="Price" value="${siteService.getPriceTres(item.price)}" class="grid"/>

                        <c:if test="${!business.affiliate}">
                            <label>Quantity</label>
                            <input type="text" name="quantity"  placeholder="Quantity" value="${item.quantity}" class="grid"/>

                            <label>Weight</label>
                            <input type="text"  name="weight" placeholder="Weight" value="${item.weight}" class="grid"/>
                        </c:if>

                        <label>Categories <span class="information"></span></label>
                        <select name="categories" multiple>
                            <c:forEach items="${categories}" var="activeCategory">
                                <c:if test="${item.categories.contains(activeCategory)}">
                                    <c:set var="selected" value="selected"/>
                                </c:if>
                                <c:if test="${!item.categories.contains(activeCategory)}">
                                    <c:set var="selected" value=""/>
                                </c:if>
                                <option value="${activeCategory.id}" ${selected}>${activeCategory.name}</option>
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
</c:if>

<c:if test="${items == null || items.size() == 0}">
    <p class="notify">No items added yet! <a href="${pageContext.request.contextPath}/items/new/${business.id}" class="href-dotted">New Item</a></p>
</c:if>
