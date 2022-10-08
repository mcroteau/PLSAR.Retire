<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form action="${pageContext.request.contextPath}/items/options/save/${business.id}/${item.id}" method="post">

    <h1>New Option</h1>
    <h4>${item.name} Item Options</h4>

    <input type="hidden" name="itemId" value="${item.id}"/>

    <label>Option</label>
    <p class="information">Options include things like, size, weight, color, etc.</p>
    <input type="text" name="name"/>
    <br/>
    <br/>
    <input type="submit" value="Save Option!" class="button green" onclick="this.disabled=true;this.value='Saving Option...';this.form.submit();"/>
</form>

<c:if test="${itemOptions.size() > 0}">

    <p>Below is a list of item options with values. The form may be daunting at first, but for
    each option there is a list of values and a form for new Item Option values.</p>


    <c:forEach var="itemOption" items="${itemOptions}" varStatus="idx">

        <div id="item-option-wrapper">
            <h2 class="left-float">${itemOption.name}</h2>
            <div class="right-float">
                <form action="${pageContext.request.contextPath}/items/options/delete/${item.businessId}/${itemOption.id}/${item.id}" method="post">
                    <input type="submit" value="Delete Option!" class="button remove"/>
                </form>
            </div>
            <br class="clear"/>


            <style>
                #item-option-wrapper{
                    padding:30px 0px 40px 0px;
                    border-bottom:solid 1px #ccc;
                }
                #option-value-form{
                    width:40%;
                    float:right;
                }
                #option-value-form label{
                    margin:2px 0px 2px 0px;
                }
                #option-values{
                    width:30%;
                    float:left;
                }
            </style>

            <div id="option-value-form">
                <form action="${pageContext.request.contextPath}/items/options/values/save/${business.id}/${item.id}" method="post">

                    <h3>New ${itemOption.name}</h3>

                    <label>Value</label>
                    <input type="text" name="value"/>

                    <label>Price</label>
                    <input type="text" name="price"/>

<%--                    <label>Quantity</label>--%>
<%--                    <input type="text" name="quantity"/>--%>

                    <input type="hidden" name="itemOptionId" value="${itemOption.id}">

                    <br/>
                    <br/>

                    <input type="submit" value="Add Value!" class="button retro" onclick="this.disabled=true;this.value='Saving Value...';this.form.submit();"/>
                </form>
            </div>


            <c:if test="${itemOption.optionValues.size() == 0}">
                <p class="notify" id="option-values">No option values added yet for ${itemOption.name}!</p>
            </c:if>

            <c:if test="${itemOption.optionValues.size() > 0}">
                <table id="option-values">
                    <tr>
                        <th>Value Label</th>
                        <th>Price</th>
<%--                        <th>Quantity</th>--%>
                    </tr>
                    <c:forEach items="${itemOption.optionValues}" var="optionValue">
                        <tr>
                            <td>${optionValue.value}</td>
                            <td>${siteService.getPrice(optionValue.price)}</td>
<%--                            <td>${optionValue.quantity}</td>--%>
                            <td>
                                <form action="${pageContext.request.contextPath}/items/options/values/delete/${item.businessId}/${optionValue.id}/${item.id}" method="post">
                                    <input type="submit" value="Delete" class="button remove"/>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>

            <br class="clear"/>

        </div>
    </c:forEach>
</c:if>