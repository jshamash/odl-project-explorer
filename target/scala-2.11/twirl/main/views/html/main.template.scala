
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._

import play.api.templates.PlayMagic._
import models._
import controllers._
import play.api.i18n._
import play.api.mvc._
import play.api.data._
import views.html._

/**/
object main extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template2[String,Html,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(title: String)(content: Html):play.twirl.api.HtmlFormat.Appendable = {
      _display_ {

Seq[Any](format.raw/*1.32*/("""
"""),format.raw/*2.1*/("""<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <meta charset="utf-8">
        <title>"""),_display_(/*7.17*/title),format.raw/*7.22*/("""</title>
        <meta name="generator" content="Bootply" />
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <link href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" media="screen" href=""""),_display_(/*11.54*/routes/*11.60*/.Assets.at("stylesheets/main.css")),format.raw/*11.94*/("""">
        <link rel="stylesheet" media="screen" href=""""),_display_(/*12.54*/routes/*12.60*/.Assets.at("stylesheets/theme.css")),format.raw/*12.95*/("""">
        <link rel="shortcut icon" type="image/png" href=""""),_display_(/*13.59*/routes/*13.65*/.Assets.at("images/favicon.png")),format.raw/*13.97*/("""">
        <script src=""""),_display_(/*14.23*/routes/*14.29*/.Assets.at("javascripts/hello.js")),format.raw/*14.63*/("""" type="text/javascript"></script>

        <!--[if lt IE 9]>
          <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

		<link href="theme.css" rel="stylesheet">
    </head>

    <body >
        """),_display_(/*24.10*/navigation()),format.raw/*24.22*/("""

        """),_display_(/*26.10*/content),format.raw/*26.17*/("""

        """),format.raw/*28.9*/("""<script type='text/javascript' src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        <script type='text/javascript' src="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js"></script>

        <!-- JavaScript jQuery code from Bootply.com editor -->

        <script type='text/javascript'>

        $(document).ready(function() """),format.raw/*35.38*/("""{"""),format.raw/*35.39*/("""

            """),format.raw/*37.13*/("""/* toggle layout */
			$('#btnToggle').click(function()"""),format.raw/*38.36*/("""{"""),format.raw/*38.37*/("""
				"""),format.raw/*39.5*/("""if ($(this).hasClass('on')) """),format.raw/*39.33*/("""{"""),format.raw/*39.34*/("""
					"""),format.raw/*40.6*/("""$('#main .col-md-6').addClass('col-md-4').removeClass('col-md-6');
					$(this).removeClass('on');
				"""),format.raw/*42.5*/("""}"""),format.raw/*42.6*/("""
				"""),format.raw/*43.5*/("""else """),format.raw/*43.10*/("""{"""),format.raw/*43.11*/("""
					"""),format.raw/*44.6*/("""$('#main .col-md-4').addClass('col-md-6').removeClass('col-md-4');
					$(this).addClass('on');
				"""),format.raw/*46.5*/("""}"""),format.raw/*46.6*/("""
			"""),format.raw/*47.4*/("""}"""),format.raw/*47.5*/(""");

        """),format.raw/*49.9*/("""}"""),format.raw/*49.10*/(""");

        </script>

    </body>
</html>"""))}
  }

  def render(title:String,content:Html): play.twirl.api.HtmlFormat.Appendable = apply(title)(content)

  def f:((String) => (Html) => play.twirl.api.HtmlFormat.Appendable) = (title) => (content) => apply(title)(content)

  def ref: this.type = this

}
              /*
                  -- GENERATED --
                  DATE: Tue Aug 05 20:35:55 EDT 2014
                  SOURCE: /home/inocult/Projects/public/opendaylight/project-explorer/app/views/main.scala.html
                  HASH: 41bb22039073e6c738a90fe3fc7410878d08f3ab
                  MATRIX: 509->1|627->31|654->32|847->199|872->204|1215->520|1230->526|1285->560|1368->616|1383->622|1439->657|1527->718|1542->724|1595->756|1647->781|1662->787|1717->821|1985->1062|2018->1074|2056->1085|2084->1092|2121->1102|2521->1474|2550->1475|2592->1489|2675->1544|2704->1545|2736->1550|2792->1578|2821->1579|2854->1585|2984->1688|3012->1689|3044->1694|3077->1699|3106->1700|3139->1706|3266->1806|3294->1807|3325->1811|3353->1812|3392->1824|3421->1825
                  LINES: 19->1|22->1|23->2|28->7|28->7|32->11|32->11|32->11|33->12|33->12|33->12|34->13|34->13|34->13|35->14|35->14|35->14|45->24|45->24|47->26|47->26|49->28|56->35|56->35|58->37|59->38|59->38|60->39|60->39|60->39|61->40|63->42|63->42|64->43|64->43|64->43|65->44|67->46|67->46|68->47|68->47|70->49|70->49
                  -- GENERATED --
              */
          