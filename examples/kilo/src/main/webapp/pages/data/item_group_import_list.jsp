
<%@ page import="giga.Giga" %>

<style>
    #menu-wrapper{display:none;}
    #content-wrapper{width:100%;}
</style>

<a href="/snapshot/${business.id}" class="href-dotted">&larr;&nbsp;Back</a>
<br class="clear"/>

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h3 class="left-float">Item Groups</h3>
<a href="/imports/item_groups/new/${business.id}" class="button orange right-float" style="margin-top:20px;">New Item Group Import</a>
<br class="clear"/>

<ocean:if spec="${ingests.size() > 0}">
    <ocean:each items="${ingests}" var="ingest" varStatus="idx">
        <ocean:each items="${ingest.itemGroups}" var="itemGroup" varStatus="idxn">
            <h2>${itemGroup.name}</h2>
            <table>
                <tr>
                    <th>Model Number</th>
                    <th>Quantity</th>
                    <th>Weight</th>
                    <ocean:each var="groupOption" items="${itemGroup.groupOptions}" varStatus="idxi">
                        <th>${groupOption.title}</th>
                    </ocean:each>
                    <ocean:each var="pricingOption" items="${itemGroup.pricingOptions}" varStatus="idxd">
                        <th>${pricingOption.description}</th>
                    </ocean:each>
                </tr>
                <ocean:each var="groupModel" items="${itemGroup.groupModels}" varStatus="idc">
                    <tr>
                        <td>${groupModel.modelNumber}</td>
                        <td>${groupModel.quantity}</td>
                        <td>${groupModel.weight}</td>
                        <ocean:each var="optionValue" items="${groupModel.groupValues}" varStatus="idxb">
                            <td>${optionValue.value}</td>
                        </ocean:each>
                        <ocean:each var="pricingValue" items="${groupModel.pricingValues}" varStatus="idxa">
                            <td>$${pricingValue.price}</td>
                        </ocean:each>
                    </tr>
                </ocean:each>
            </table>
            <div class="button-wrapper">
                <form action="/imports/item_groups/group/delete/${business.id}/${itemGroup.id}" method="post">
                    <input type="submit" value="Delete Above Item Group" class="button remove"/>
                </form>
            </div>
        </ocean:each>
    </ocean:each>
</ocean:if>

<ocean:if spec="${ingests == null || ingests.size() == 0}">
    <p class="notify">No item group imports yet! <a href="/imports/item_groups/new/${business.id}" class="href-dotted">New Item Group Import</a></p>
</ocean:if>

