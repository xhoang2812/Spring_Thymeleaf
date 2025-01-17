(function ($) {
  'use strict';

  // aos
  AOS.init({
    duration: 1500,
  });

  // drawer menu
  $('.open-submenu').on('click', function () {
    $(this).parent().siblings('.submenu-transform').addClass('active');
  });

  $('.btn-menu-back').on('click', function () {
    $(this).closest('.submenu-transform').removeClass('active');
  });

  // header search
  $('.icon-search, .search-close').on('click', function () {
    $('.search-wrapper').toggleClass('search-appear');
  });

  // quickview slider initiation
  $('#quickview-modal').on('shown.bs.modal', function (e) {
    $('.qv-large-slider').slick('setPosition');
    $('.qv-thumb-slider').slick('setPosition');
  });

  // common slider
  $('.common-slider').each(function () {
    var $this = $(this);
    var verticalSlide = $(this).attr('data-vertical-slider');

    if ($this.children().length > 1) {
      var selectorAppendDots = $this.parent().find('.activate-dots');
      var selectorAppendArrows = $this.parent().find('.activate-arrows');
      var selectorPrevArrow = `<span class="arrow-slider arrow-prev"><svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 24 24" fill="none" stroke="#FEFEFE" stroke-width="0.5" stroke-linecap="round" stroke-linejoin="round" class="icon-arrow-left"><polyline points="15 18 9 12 15 6"></polyline></svg></span>`;
      var selectorNextArrow = `<span class="arrow-slider arrow-next"><svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 24 24" fill="none" stroke="#FEFEFE" stroke-width="0.5" stroke-linecap="round" stroke-linejoin="round" class="icon-arrow-right"><polyline points="9 18 15 12 9 6"></polyline></svg></span>`;

      $($this).slick({
        infinite: false,
        speed: 500,
        cssEase: 'ease',
        swipeToSlide: true,        
        vertical: verticalSlide ? true : false,
        verticalSwiping: verticalSlide ? true : false,
        appendDots: selectorAppendDots,
        appendArrows: selectorAppendArrows,
        prevArrow: selectorPrevArrow,
        nextArrow: selectorNextArrow,
        responsive: [
            {
                breakpoint: 768,
                settings: {
                    vertical: false,
                    verticalSwiping: false,
                }
            }
        ]
      });
    }
  });

  // slideshow
  $('.activate-slider').each(function () {
    var $this = $(this);
    if ($this.children().length > 1) {
      var selectorAppendDots = $this.parent().find('.activate-dots');
      var selectorAppendArrows = $this.parent().find('.activate-arrows');
      var selectorPrevArrow = `<span class="arrow-slider arrow-prev"><svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 24 24" fill="none" stroke="#000" stroke-width="0.5" stroke-linecap="round" stroke-linejoin="round" class="icon-arrow-left"><polyline points="15 18 9 12 15 6"></polyline></svg></span>`;
      var selectorNextArrow = `<span class="arrow-slider arrow-next"><svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 24 24" fill="none" stroke="#000" stroke-width="0.5" stroke-linecap="round" stroke-linejoin="round" class="icon-arrow-right"><polyline points="9 18 15 12 9 6"></polyline></svg></span>`;

      function doAnimation() {
        $this.find('.slick-slide').each(function () {
          $(this).find('[data-animation]').each(function () {
            var dataAnimation = $(this).attr('data-animation');
            $(this).removeClass(dataAnimation);

            if ($(this).closest('.slick-slide').is('.slick-current.slick-active')) {
              $(this).addClass(dataAnimation);
            }
          })
        })

        return false;
      }

      $(this)
        .on('init', function (event, slick) {
          doAnimation();
        })
        .on('afterChange', function (event, slick, direction) {
          doAnimation();
        })
        .slick({
          appendDots: selectorAppendDots,
          appendArrows: selectorAppendArrows,
          prevArrow: selectorPrevArrow,
          nextArrow: selectorNextArrow
        });
    }
  })

  // increament-decreament button


  // footer copyright current date
  $('.current-year').text(new Date().getFullYear());

  // toggle footer menu
  $('.footer-heading').on('click', function () {
    $(this).siblings().slideToggle();
  });

  // toggle & accordion 
  $('.accordion-btn').on('click', function () {
    $(this).siblings('.accordion-child').slideToggle();
    $(this).toggleClass('active');
  });
  $('.write-btn').on('click', function () {
    $(this).closest('.accordion-parent').find('.accordion-child').slideToggle();
  });

  // filter drawer open
  $('.filter-drawer-trigger').on('click', function () {
    $('.filter-drawer').toggleClass('active');
  });

  // product img popup
  $('[data-fancybox="gallery"]').fancybox({
    buttons: [
      "slideShow",
      "thumbs",
      "zoom",
      "fullScreen",
      "share",
      "close"
    ],
    loop: false,
    protect: true
  });


  /* --------------------------------------------------------
    scrollUp active
  -------------------------------------------------------- */
  var scrollUpBtn = $('#scrollup');

  $(window).scroll(function() {
    if ($(window).scrollTop() > 700) {
      scrollUpBtn.addClass('show');
    } else {
      scrollUpBtn.removeClass('show');
    }
  });

  scrollUpBtn.on('click', function(e) {
    e.preventDefault();
    $('html, body').animate({scrollTop:0}, 300);
  });

  /* --------------------------------------------------------
    newsletter popup
  -------------------------------------------------------- */
  // $ (window).ready (function () {
  //   setTimeout (function () {
  //     $ ('#modal-subscribe').modal ("show")
  //   }, 3000);
  // })

})(jQuery);