<script src="${pageContext.request.contextPath}/benefit/media/confetti.js"></script>

<div class="align-center">
    <h1>Congratulations!</h1>
    <h3>Congratulations on starting your new business!</h3>
    <p>Holy Moly! Yes, we said holy moly.. we are just so excited
    to have you using Giga!</p>

    <h2>What's next? </h2>
    <p>Here is a checklist:</p>

    <style>
        ul.checklist{margin-left:50px; text-align: left}
        ul.checklist li{
            margin:13px 0px;
            list-style: decimal;
        }
    </style>

    <ul class="checklist">
        <li>Celebrate.. right on, <span class="tiny">whoa, who brought the guy from the movie Big, he's not welcome.</span></li>
        <li><a href="${pageContext.request.contextPath}/snapshot/${business.id}">Activate Payment</a>
            <span class="tiny">A breeze, you just need to enter some business information on Stripe.com</span></li>
        <li><a href="${pageContext.request.contextPath}/businesses/settings/${business.id}">Update Business Address</a>
            <span class="tiny">Used for realtime shipping calculations if enabled.</span></li>
    </ul>
</div>
