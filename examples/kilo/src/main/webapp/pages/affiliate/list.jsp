

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1 class="left-float">Your Affiliates</h1>
<br class="clear"/>

<ocean:if spec="${affiliates.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Details</th>
            <th></th>
        </tr>
        <ocean:each var="affiliate" items="${affiliates}" varStatus="idx">
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
        </ocean:each>
    </table>
</ocean:if>


<ocean:if spec="${affiliates.size() == 0}">
    <p class="notify">No affiliates yet, keep checking "Requests" for new Affiliate/Business partner requests.</p>

    <p></p>
</ocean:if>
