$(function () {
		/////////////////////// select box /////////////////////////
		// Common
		var select_root = $('div.select');
		var select_value = $('.my_value');
		var select_a = $('div.select>.a_list>ul>li>a');
		var select_input = $('div.select>.a_list>ul>li>input[type=radio]');
		var select_ctrl = $('span.ctrl');

		// Radio Default Value
		$('div.my_value').each(function () {
			var default_value = $(this).next('.i_list').find('input[checked]').next('label').text();
			$(this).append(default_value);
		});

		// Line
		select_value.bind('focusin', function () {
			$(this).addClass('outLine')
		});
		select_value.bind('focusout', function () {
			$(this).removeClass('outLine')
		});
		select_input.bind('focusin', function () {
			$(this).parents('div.select').children('div.my_value').addClass('outLine')
		});
		select_input.bind('focusout', function () {
			$(this).parents('div.select').children('div.my_value').removeClass('outLine')
		});

		// Show
		function show_option() {
			if (!$(this).closest('.select').hasClass('current')) {
				$('.select').removeClass('current', 'open');
				$('.select').removeClass('open');
				$(this).closest('.select').addClass('current', 'open');
				$(this).closest('.select').addClass('open');
			} else {
				$(this).closest('.select').toggleClass('open');
			}
		}

		// Hover
		function i_hover() {
			$(this).parents('.a_list:first').find('li').removeClass('hover');
			$(this).parents('li:first').toggleClass('hover');
		}

		// Hide
		function hide_option() {
			var t = $(this);
			setTimeout(function () {
				t.parents('div.select:first').removeClass('open');
			}, 1);
		}

		// Set Input
		function set_label() {
			var v = $(this).next('label').text();
			$(this).parents('.a_list:first').prev('.my_value').text('').append(v);
			$(this).parents('.a_list:first').prev('.my_value').addClass('selected');
		}

		// Set Anchor
		function set_anchor() {
			var v = $(this).text();
			$(this).parents('.a_list:first').prev('.my_value').text('').append(v);
			$(this).parents('.a_list:first').prev('.my_value').addClass('selected');
		}

		select_ctrl.click(show_option);
		select_value.click(show_option);
		select_root.removeClass('open');
		select_a.click(set_anchor).click(hide_option).focus(i_hover).hover(i_hover);
		select_input.change(set_label).focus(set_label);

	}
);
