
<%@ page import="giga.Giga" %>

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

<h1 class="left-float">Power Grid!
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 134 134" id="giga-bolt">
        <path d="M49 1L21 88L57 88L42 134L84 134L113 47L92 47L79 47L75 47L91 1L49 1Z" />
    </svg>
</h1>
<br class="clear"/>

<p>The grid view gives you a quick and easy way to update items.</p>

<plsar:if spec="${items.size() > 0}">
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
                    <form action="/items/grid/update/${business.id}/${item.id}" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="id" value="${item.id}"/>
                        <input type="hidden" name="businessId" value="${item.businessId}"/>
                        <input type="hidden" name="imageUri" value="${item.imageUri}"/>
                        <input type="hidden" name="designId" value="${item.designId}"/>
                        <input type="hidden" name="active" value="${item.active}"/>


                        <plsar:if spec="${business.affiliate}">
                            <input type="hidden" name="name" value="${item.name}"/>
                            <input type="hidden" name="quantity" value="${item.quantity}"/>
                            <input type="hidden" name="weight" value="${item.weight}"/>
                        </plsar:if>

                        <plsar:if spec="${!business.affiliate}">
                            <label>Name</label>
                            <input type="text" name="name" placeholder="Name" value="${item.name}" class="grid"/>
                        </plsar:if>
                        <plsar:if spec="${business.affiliate}">
                            <label>Name</label>
                            <strong>${item.name}</strong>
                        </plsar:if>

                        <label>Price</label>
                        <input type="text" name="price"  placeholder="Price" value="${siteService.getPriceTres(item.price)}" class="grid"/>

                        <plsar:if spec="${!business.affiliate}">
                            <label>Quantity</label>
                            <input type="text" name="quantity"  placeholder="Quantity" value="${item.quantity}" class="grid"/>

                            <label>Weight</label>
                            <input type="text"  name="weight" placeholder="Weight" value="${item.weight}" class="grid"/>
                        </plsar:if>

                        <label>Categories <span class="information"></span></label>
                        <select name="categories" multiple>
                            <c:forEach items="${categories}" var="activeCategory">
                                <plsar:if spec="${item.categories.contains(activeCategory)}">
                                    <c:set var="selected" value="selected"/>
                                </plsar:if>
                                <plsar:if spec="${!item.categories.contains(activeCategory)}">
                                    <c:set var="selected" value=""/>
                                </plsar:if>
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
</plsar:if>

<plsar:if spec="${items == null || items.size() == 0}">
    <p class="notify">No items added yet! <a href="/items/new/${business.id}" class="href-dotted">New Item</a></p>
</plsar:if>
