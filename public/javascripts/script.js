$(document).ready(function(){
	/* The following code is executed once the DOM is loaded */

	$('#bigbutton').click(function() {
		var flipped = 0;
		var features = [];
		$('.componentFlip').each(function() {
			var elem = $(this);
			if(elem.data('flipped')) {
				var id = elem.attr('data-modal');
				var children = $('#' + id).find('input');
				$('#' + id).find("input:checkbox:checked").each(function() {
					var feature = $(this).val();
					features.push(feature)
				});
			}
		});
		var result = JSON.stringify({ features : features });
		$.ajax
		({
			type: "POST",
			url: '/download',
			contentType: "application/json; charset=utf-8",
			data: result
		});
	});
});
