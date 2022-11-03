

<ocean:if spec="${message != ''}">
    <p class="notify">${message}</p>
</ocean:if>

<h1 class="left-float">Business<br/>Partner Requests</h1>
<br class="clear"/>

<ocean:if spec="${businessRequests.size() > 0}">
    <table>
        <tr>
            <th></th>
            <th>Request</th>
        </tr>
        <ocean:each var="businessRequest" items="${businessRequests}" varStatus="idx">
            <tr>
                <td style="width:160px;">
                    <a href="/affiliates/onboarding/status/${businessRequest.guid}" class="button orange">Status Page</a>
                    <p class="tiny">View the request status page that the person who requested it sees.</p>
                </td>
                <td>
                    ${businessRequest.name}<br/>
                    ${businessRequest.businessName}<br/>
                        <span class="tiny"><strong>Phone:</strong>${businessRequest.phone}</span>
                    <strong>Notes:</strong><br/>
                    <p>${businessRequest.notes}</p>
                </td>
                <ocean:if spec="${businessRequest.pending}">
                    <td>
                        <form action="/affiliates/onboarding/approve/${businessRequest.id}" method="post"">
                            <input type="submit" value="Approve!" class="button green"/>
                        </form>
                        <form action="/affiliates/onboarding/pass/${businessRequest.id}" method="post" onsubmit="return confirm('Are you sure you want to pass on this offering?');">
                            <input type="submit" value="Pass" class="button orange"/>
                        </form>
                    </td>
                </ocean:if>
            </tr>
        </ocean:each>
    </table>
</ocean:if>


<ocean:if spec="${businessRequests.size() == 0}">
    <p class="notify">No business partner requests yet.</p>
    <h3>What are Business Partner requests?</h3>

    <p>Business Partner requests are requests made by individuals who are interested in
    running an online store as an affiliate or business partner. They gain reference to your
    products and can set prices and add designs to your item page. They cannot change
    quantity, descriptions or item options.</p>
</ocean:if>
