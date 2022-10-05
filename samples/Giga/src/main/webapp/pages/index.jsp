<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>

    #features-wrapper{
        padding:0px 30px 10px 30px;
        background-color: #3878E3;
    }
    #features-wrapper h1{
        color:#fff;
    }

    #splash-wrapper{
        margin:auto;
    }

    #splash{margin-bottom:-30px;}
    #splash img{
        z-index: 1;
        box-shadow: 0px 0px 101px rgba(0,0,0,0.29);
    }

    #features-wrapper{
        z-index: 21;
    }

    #features{
        padding:20px 0px;
        text-align: left;
        margin:0px auto;
        width:760px;
    }

    #features p{color:#fff}
    #features p,
    #friends p,
    #costs p{font-size:21px;font-weight: 300}

    #friends-wrapper{text-align: center}

    #splash{
        text-align: center;
        margin:0px auto;
        width:760px;
    }
    #friends{
        padding:20px 0px;
        width:760px;
        text-align: left;
        margin:0px auto;
        width:760px;
    }
    #friends p{width:53%}

</style>


<div id="splash-wrapper">
    <div id="splash" class="section">
        <h1 style="margin-bottom:10px;">Simple. Easy. Online Commerce.</h1>
        <img src="${pageContext.request.contextPath}/benefit/media/giga.splash.png" style="margin:auto;width:650px"/>
    </div>
</div>

<div id="features-wrapper">
    <div id="features">

        <h1>Features</h1>

        <p>Customize this as your Homepage, *affiliate registration requires at
            least one entity to be registered with this application. What you get with Giga:</p>

        <p><strong>Multi-Business</strong> One or many shops, you decide. Running multiple shops at once on Giga is a breeze.</p>

        <p><strong>Resellers Logic</strong> That's right, with Giga, you can allow your companies to operate as affiliates.</p>

        <p><strong>Multi-Design</strong> Completely customizable designs, and to boot Giga boasts a design per page, item and category.</p>

        <p><strong>Multi-Option</strong> Items can have multiple options or dimensions and variants with additional pricing. These can
            include things Size, Weight & Color.</p>

        <p><strong>Fully Functional Content Management System (CMS)</strong> Giga touts as a fully functional CMS system as well.
        If you want to run Giga as a plain site, it will do the job. </p>

        <p><strong>Simple Item Import</strong> Importing items should be easy, in previous attempts
            we fell short and relied on spreadsheets. With Giga, all you need is a little bit of
            up front work and items are easily imported on the fly, and we
            didn't rely on China to create it for us. Go USA!</p>

        <p><strong>Power Grid</strong> We took item maintenance and thoroughly thought it through,
            in turn we made a daunting task easy.</p>

        <p><strong>Ease of Maintenance</strong> We made sure to make shop maintenance a breeze. Give it a try!</p>

        <p><strong>Designed for Web Developers</strong> Giga aint no fancy drag and drop development system, it expects you to know how to design and write
        beautiful Html + Css + Javascript user experiences and allows you to do such.</p>

        <div class="align-center" style="margin:30px 0px 60px">
            <a href="${pageContext.request.contextPath}/signup" class="button yellow gigantic">Get Started as a Business!</a>
        </div>

    </div>
</div>

<div id="friends-wrapper">
    <div id="friends" class="section">
        <h1>Business<br/>Partner Program</h1>
        <p>This could be your blurb...
            <strong>Say what? Business Partners?</strong>
            Better yet, friends program,
            if you like them so much why don't
            you marry em'.
            On Giga, you can pick a business
            or a product you are excited about and create an online
            store their behalf. You get a copy of all of their items &
            categories and set pricing on demand.</p>

        <p>*Yes we are giving this ability away for free.</p>

        <p>As a business owner get excited about the idea of having
            people work on your behalf, we tried to make it as easy as possible
            giving you full control over the approval process.</p>

        <p style="width:39%; position: absolute;top:382px; right:0px;">
            There is no one best way to do something and we believe this
            to be true for the sales process as well. Which is
            why we are introducing the Affiliate/Partner program capability!</p>

        <p>Business owners can set a base commission rate on a case by case
            basis for your business partner.</p>

        <p>As a Business Partner, you get all of the above
            features, nothing hidden, just an agreement between
            you and the business you love.</p>

        <style>
            #friends{position: relative;}
            #friends a{position:absolute; right:50px; top:254px;}
        </style>

        <a href="${pageContext.request.contextPath}/affiliates/onboarding" class="button green gigantic">Get Started Now!</a>
    </div>
</div>

