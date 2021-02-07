/**
 * General tools for cervex project
 */

function $try(func) {
	try {
		return func()
	} catch (e) {
		return null
	}
}

var jsoftware95 = jsoftware95 || {};

jsoftware95.isAnimationEnabled = true;

jsoftware95.hidePage = function() {
	var element = document.getElementById("pageWrapper");
	if (element != null) {
		element.style.opacity = 0;
	}
}

jsoftware95.showPage = function() {
	setTimeout(function() {
		var element = document.getElementById("pageWrapper");
		if (element != null) {
			element.style.opacity = 1;
		}
	}, 1000);
}

jsoftware95.preAction = function() {
	jsoftware95.isAnimationEnabled = false;
}

jsoftware95.darkExtender = function() {
	myobj = this.cfg;
	this.cfg.grid = {
		background : '#ddddff'
	};

	if (this.cfg.legend != null)
		this.cfg.legend.background = '#666';
}

jsoftware95.updatePageWraper = function() {
	var element = document.getElementById('pageWrapper');
	var position = element.style.backgroundPositionY;

	if (position) {
		position = '';
	} else {
		position = '0px';
	}

	element.style.backgroundPositionY = position;
}

jsoftware95.rowExpansion = function(dataTable) {
	if (dataTable != null) {
		var $this = dataTable;

		$this.tbody.on('click.datatable-expansion',
				'> tr > td > div.ui-row-toggler', null, function(e) {
					try {
						var row = e.toElement.parentElement.parentElement;
						var id = row.getElementsByClassName('hasID')[0].title;

						onRowToggle([ {
							name : 'id',
							value : id
						} ]);

						var newLabels = row.getElementsByClassName('newLabel');
						for (var i = 0; i < newLabels.length; i++) {
							newLabels[i].style.display = 'none';
						}
					} catch (e) {
						console.log('could not toggle: ' + title);
					}
				});
	}
}

jsoftware95.savePeriodChangeListenerStat = function() {
	var orderList = PF('orderList');
	if (orderList) {
		var items = orderList.items;
		if (items) {
			var orderListStat = {};
			for (var i = 0; i < items.length; i++) {
				var key = jsoftware95.getKey(items[i], 'data-item-value');
				if (key) {
					orderListStat[key] = items[i].classList;
				}
			}

			if (!jsoftware95.periodChangeListenerStat)
				jsoftware95.periodChangeListenerStat = {};

			jsoftware95.periodChangeListenerStat.orderListStat = orderListStat;
		}
	}
	jsoftware95.saveOrderlistControlsStat();
}

jsoftware95.saveOrderlistControlsStat = function() {
	var controlsList = document.getElementsByClassName("ui-orderlist-controls");
	if (controlsList) {
		var controlsStat = {};
		for (var i = 0; i < controlsList.length; i++) {
			var controls = controlsList[i].childNodes;
			for (var j = 0; j < controls.length; j++) {
				var key = controls[j].getAttribute("title");
				if (key) {
					controlsStat[key] = controls[j].classList;
				}
			}
		}

		if (!jsoftware95.periodChangeListenerStat)
			jsoftware95.periodChangeListenerStat = {};

		jsoftware95.periodChangeListenerStat.controlsStat = controlsStat;
	}
}

jsoftware95.loadPeriodChangeListenerStat = function() {
	if (jsoftware95.periodChangeListenerStat) {
		var orderListStat = jsoftware95.periodChangeListenerStat.orderListStat;
		var orderList = PF('orderList');
		if (orderListStat && orderList) {
			var items = orderList.items;
			if (items) {
				for (var i = 0; i < items.length; i++) {
					var key = jsoftware95.getKey(items[i], 'data-item-value');
					if (key in orderListStat)
						jsoftware95.restoreStat(items[i], orderListStat[key]);
				}

				delete jsoftware95.periodChangeListenerStat.orderListStat;
			}
		}
		jsoftware95.loadOrderlistControlsStat();
	}
}

jsoftware95.loadOrderlistControlsStat = function() {
	var controlsStat = jsoftware95.periodChangeListenerStat.controlsStat;
	var controls = document.getElementsByClassName("ui-orderlist-controls");
	if (controlsStat && controls)
		for (var i = 0; i < controls.length; i++) {
			var childs = controls[i].childNodes;
			for (var j = 0; j < childs.length; j++) {
				var key = childs[j].getAttribute("title");
				if (key in controlsStat)
					jsoftware95.restoreStat(childs[j], controlsStat[key]);
			}
		}
}

jsoftware95.getKey = function(element, attribute) {
	if (element && attribute) {
		var tokens = element.getAttribute(attribute).split('_');
		if (tokens.length > 0) {
			var key = tokens[tokens.length - 1];
			if (key.length > 0)
				return key;
		}
	}

	return null;
}

jsoftware95.restoreStat = function(element, classes) {
	if (element && classes) {
		var hasFocus = classes.contains("ui-state-focus");
		classes.remove("ui-state-focus");
		classes.remove("ui-state-hover");

		element.setAttribute("class", classes);
		if (hasFocus)
			element.focus();
	}
}
