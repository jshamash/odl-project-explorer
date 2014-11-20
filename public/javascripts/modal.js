$(document).ready(function(){
/**
	 * modalEffects.js v1.0.0
	 * http://www.codrops.com
	 *
	 * Licensed under the MIT license.
	 * http://www.opensource.org/licenses/mit-license.php
	 * 
	 * Copyright 2013, Codrops
	 * http://www.codrops.com
*/
	var ModalEffects = (function() {

		function init() {

			

			[].slice.call( document.querySelectorAll( '.md-trigger' ) ).forEach( function( el, i ) {
				
				var numero = el.getAttribute( 'data-modal' );
				var overlay = document.querySelector( '#overlay-' + numero.split("-")[1]);
				var modal = document.querySelector( '#' + numero ),
					close = modal.querySelector( '.md-close' );

				function removeModal( hasPerspective ) {
					overlay.removeEventListener( 'click', removeModalHandler );
					var elem = $(el);
					var flipped = elem.data('flipped');
					if(flipped)
					{
						// If the element has already been flipped, use the revertFlip method
						// defined by the plug-in to revert to the default state automatically:

						elem.revertFlip();

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

								elem.html(elem.siblings('.componentSelected').html());
							}
						});

						// Setting the flag:
						elem.data('flipped',true);
					}
					classie.remove( modal, 'md-show' );

					if( hasPerspective ) {
						classie.remove( document.documentElement, 'md-perspective' );
					}
				}

				function removeModalHandler() {
					removeModal( classie.has( el, 'md-setperspective' ) ); 
				}

				el.addEventListener( 'click', function( ev ) {
					var elem = $(el);
					var flipped = elem.data('flipped');
					if(!flipped) {
						classie.add( modal, 'md-show' );

						overlay.addEventListener( 'click', removeModalHandler );

						if( classie.has( el, 'md-setperspective' ) ) {
							setTimeout( function() {
								classie.add( document.documentElement, 'md-perspective' );
							}, 25 );
						}
					}
					else {
						elem.revertFlip();
						overlay.removeEventListener( 'click', removeModalHandler );
						// Unsetting the flag:
						elem.data('flipped',false);
					}
					
				});

				close.addEventListener( 'click', function( ev ) {
					overlay.removeEventListener( 'click', removeModalHandler );
					ev.stopPropagation();
					removeModalHandler();
				});

			});

		}

		init();

	})();

});