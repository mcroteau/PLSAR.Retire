
<%@ page import="giga.Giga" %>

<ocean:if spec="${category != null}">
    ${siteService.getItemBit(Kilo.HEAD, item, category, business, request)}
</ocean:if>

<ocean:if spec="${category == null}">
    ${siteService.getItemBit(Kilo.HEAD, item, null, business, request)}
</ocean:if>
    <div id="item-wrapper">

        <style>
            #item-price{font-size: 102px;font-weight: 900}
        </style>

        <div class="align-center">
            <img src="${item.imageUri}" width="250" style="border-radius: 16px;margin: auto;"/>
        </div>

        <div id="barter-item-details">
            <h1>${item.name}</h1>
            <span id="item-price">$${siteService.getPriceDos(item.price)}</span>

            <form action="/${business.uri}/cart/add/${item.id}" method="post">

                <label>Quantity</label>
                <input type="text" name="quantity" value="0" id="quantity" style="width:100px;"/>

                <ocean:each items="${itemOptions}" var="option">
                    <label>${option.name}</label>
                    <select name="optionId">
                        <ocean:each items="${option.optionValues}" var="value">
                            <option value="${value.id}">${value.value}
                                <ocean:if spec="${value.price > 0}">
                                    @
                                    $${siteService.getPrice(value.price)}
                                </ocean:if>
                            </option>
                        </ocean:each>
                    </select>
                </ocean:each>


                <div style="margin-top:30px;">
                    <input type="submit" value="Add Cart" id="add" class="button blue gigantic" onclick="this.disabled=true;this.value='Adding to Kart...';this.form.submit();"/>
                </div>
            </form>

            <h3>Description</h3>
            <p>${item.description}</p>
        </div>

    </div>

<ocean:if spec="${category != null}">
    ${siteService.getItemBit(Kilo.BOTTOM, item, category, business, request)}
</ocean:if>

<ocean:if spec="${category == null}">
    ${siteService.getItemBit(Kilo.BOTTOM, item, null, business, request)}
</ocean:if>