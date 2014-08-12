
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
object navigation extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template0[play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply():play.twirl.api.HtmlFormat.Appendable = {
      _display_ {

Seq[Any](format.raw/*2.4*/("""<div class="navbar navbar-fixed-top header">
		<div class="col-md-12">
			<div class="navbar-header">
			  <a href="#" class="navbar-brand"><!--<img src=""""),_display_(/*5.54*/routes/*5.60*/.Assets.at("images/odl_logo.jpg")),format.raw/*5.93*/(""""/>-->Project Explorer</a>
			  <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse1">
			  <i class="glyphicon glyphicon-search"></i>
			  </button>

			</div>
			<div class="collapse navbar-collapse" id="navbar-collapse1">
			<!--  <form class="navbar-form pull-left">
				  <div class="input-group" style="max-width:470px;">
					<input type="text" class="form-control" placeholder="Search" name="srch-term" id="srch-term">
					<div class="input-group-btn">
					  <button class="btn btn-default btn-primary" type="submit"><i class="glyphicon glyphicon-search"></i></button>
					</div>
				  </div>
			  </form> -->
			  <ul class="nav navbar-nav navbar-right">
				 <li>
					<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="glyphicon glyphicon-flag"></i></a>
					<ul class="dropdown-menu">
					  <li><a href="#"><span class="badge alert-danger pull-right">40</span>Errors</a></li>
					  <li><a href="#"><span class="badge alert-warning pull-right">2</span>Warnings</a></li>
					</ul>
				 </li>
				 </ul>
			</div>
		 </div>
		</div>

		<div class="navbar navbar-default" id="subnav">
			<div class="col-md-12">
				<div class="navbar-header">

				  <a href="#" style="margin-left:15px;" class="navbar-btn btn btn-default btn-plus dropdown-toggle" data-toggle="dropdown"><i class="glyphicon glyphicon-home" style="color:#dd1111;"></i> Home <small><i class="glyphicon glyphicon-chevron-down"></i></small></a>
				  <ul class="nav dropdown-menu">
					  <li><a href="#"><i class="glyphicon glyphicon-user" style="color:#1111dd;"></i> Profile</a></li>
					  <li><a href="#"><i class="glyphicon glyphicon-dashboard" style="color:#0000aa;"></i> Dashboard</a></li>
					  <li><a href="#"><i class="glyphicon glyphicon-inbox" style="color:#11dd11;"></i> Pages</a></li>
					  <li class="nav-divider"></li>
					  <li><a href="#"><i class="glyphicon glyphicon-cog" style="color:#dd1111;"></i> Settings</a></li>
					  <li><a href="#"><i class="glyphicon glyphicon-plus"></i> More..</a></li>
				  </ul>


				  <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse2">
				  <span class="sr-only">Toggle navigation</span>
				  <span class="icon-bar"></span>
				  <span class="icon-bar"></span>
				  <span class="icon-bar"></span>
				  </button>

				</div>
				<div class="collapse navbar-collapse" id="navbar-collapse2">
				  <ul class="nav navbar-nav navbar-right">
				     <li><a href="#loginModal" role="button" data-toggle="modal">Projects</a></li>
					 <li><a href="#aboutModal" role="button" data-toggle="modal">Components</a></li>
				   </ul>
				</div>
			 </div>
		</div>
"""))}
  }

  def render(): play.twirl.api.HtmlFormat.Appendable = apply()

  def f:(() => play.twirl.api.HtmlFormat.Appendable) = () => apply()

  def ref: this.type = this

}
              /*
                  -- GENERATED --
                  DATE: Mon Aug 11 19:17:12 EDT 2014
                  SOURCE: /home/inocult/Projects/public/opendaylight/project-explorer/app/views/navigation.scala.html
                  HASH: 51e50117441f078b864969f06875413152094c7e
                  MATRIX: 585->4|766->159|780->165|833->198
                  LINES: 22->2|25->5|25->5|25->5
                  -- GENERATED --
              */
          