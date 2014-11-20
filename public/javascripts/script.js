$(document).ready(function(){
	/* The following code is executed once the DOM is loaded */

	$('.featuresSelected').hide();

	$('.componentFlip').bind("click",function(){

		// $(this) point to the clicked .sponsorFlip element (caching it in elem for speed):

		var elem = $(this);

		// data('flipped') is a flag we set when we flip the element:

		if(elem.data('flipped'))
		{
			// If the element has already been flipped, use the revertFlip method
			// defined by the plug-in to revert to the default state automatically:

			elem.revertFlip();
			elem.parent().parent().children('.featuresSelected').hide();
			// Unsetting the flag:
			elem.data('flipped',false)
		}
		else
		{
			// Using the flip method defined by the plugin:

			elem.flip({
				direction:'lr',
				speed: 350,
				onBefore: function(){
					// Insert the contents of the .sponsorData div (hidden from view with display:none)
					// into the clicked .sponsorFlip div before the flipping animation starts:
					elem.parent().parent().children('.featuresSelected').show();
					elem.html(elem.siblings('.componentSelected').html());
				}
			});

			// Setting the flag:
			elem.data('flipped',true);
		}
	});

	$('#bigbutton').click(function() {
		var flipped = 0;
		var features = [];
		$('.componentFlip').each(function() {
			var elem = $(this);
			if(elem.data('flipped')) {
				elem.parent().parent().children('.featuresSelected').children("input:checkbox:checked").each(function() {
					var feature = $(this).val();
					features.push(feature)
				});
			}
		});
		var result = JSON.stringify({ featuresSelected : features });
		$.post("/download", result);

	});

});