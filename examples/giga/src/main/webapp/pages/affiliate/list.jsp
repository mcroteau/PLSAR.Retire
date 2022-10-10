

<plsar:if spec="${message != ''}">
    <p class="notify">${message}</p>
</plsar:if>

<h1 class="left-float">Your Affiliates</h1>
<br class="clear"/>

<plsar:if spec="${affiliates.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Details</th>
            <th></th>
        </tr>
        <c:forEach var="affiliate" items="${affiliates}" varStatus="idx">
            <tr>
                <td>${affiliate.id}</td>
                <td>
                    ${affiliate.name}<br/>
                        Total Sales : <strong>$${siteService.getPrice(affiliate.salesTotal)}</strong>
                    <div class="tiny">
                        ${affiliate.phone}<br/>
                        ${affiliate.email}
                    </div>
                </td>
                <td>
                    <a href="/${affiliate.uri}/" target="_blank" class="button orange">Visit Live Site</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</plsar:if>


<plsar:if spec="${affiliates.size() == 0}">
    <p class="notify">No affiliates yet, keep checking "Requests" for new Affiliate/Business partner requests.</p>

    <p></p>
</plsar:if>
