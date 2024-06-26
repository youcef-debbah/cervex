/**
 * PrimeFaces OrderList Widget
 */
PrimeFaces.widget.OrderList = PrimeFaces.widget.BaseWidget
		.extend({
			init : function(a) {
				this._super(a);
				this.list = this.jq.find(".ui-orderlist-list"),
						this.items = this.list.children(".ui-orderlist-item");
				this.input = $(this.jqId + "_values");
				this.cfg.effect = this.cfg.effect || "fade";
				this.cfg.disabled = this.jq.hasClass("ui-state-disabled");
				var b = this;
				if (!this.cfg.disabled) {
					this.generateItems();
					this.setupButtons();
					this.bindEvents();
					this.list.sortable({
						revert : 1,
						start : function(c, d) {
							//PrimeFaces.clearSelection()
						},
						update : function(c, d) {
							b.onDragDrop(c, d)
						}
					})
				}
			},
			generateItems : function() {
				var a = this;
				this.list
						.children(".ui-orderlist-item")
						.each(
								function() {
									var c = $(this), d = c.data("item-value"), b = $('<option selected="selected"></option>');
									b.prop("value", d).text(d);
									a.input.append(b)
								})
			},
			bindEvents : function() {
				var a = this;
				this.items.on("mouseover.orderList", function(c) {
					var b = $(this);
					if (!b.hasClass("ui-state-highlight")) {
						$(this).addClass("ui-state-hover")
					}
				}).on("mouseout.orderList", function(c) {
					var b = $(this);
					if (!b.hasClass("ui-state-highlight")) {
						$(this).removeClass("ui-state-hover")
					}
				}).on(
						"mousedown.orderList",
						function(c) {
							var b = $(this), d = (c.metaKey || c.ctrlKey);
							if (!d) {
								b.removeClass("ui-state-hover").addClass(
										"ui-state-highlight").siblings(
										".ui-state-highlight").removeClass(
										"ui-state-highlight");
								a.fireItemSelectEvent(b, c)
							} else {
								if (b.hasClass("ui-state-highlight")) {
									b.removeClass("ui-state-highlight");
									a.fireItemUnselectEvent(b)
								} else {
									b.removeClass("ui-state-hover").addClass(
											"ui-state-highlight");
									a.fireItemSelectEvent(b, c)
								}
							}
						})
			},
			setupButtons : function() {
				var a = this;
				PrimeFaces.skinButton(this.jq.find(".ui-button"));
				this.jq.find(
						" .ui-orderlist-controls .ui-orderlist-button-move-up")
						.click(function() {
							a.moveUp(a.sourceList)
						});
				this.jq
						.find(
								" .ui-orderlist-controls .ui-orderlist-button-move-top")
						.click(function() {
							a.moveTop(a.sourceList)
						});
				this.jq
						.find(
								" .ui-orderlist-controls .ui-orderlist-button-move-down")
						.click(function() {
							a.moveDown(a.sourceList)
						});
				this.jq
						.find(
								" .ui-orderlist-controls .ui-orderlist-button-move-bottom")
						.click(function() {
							a.moveBottom(a.sourceList)
						})
			},
			onDragDrop : function(a, b) {
				//b.item.removeClass("ui-state-highlight");
				this.saveState();
				this.fireReorderEvent()
			},
			saveState : function() {
				this.input.children().remove();
				this.generateItems()
			},
			moveUp : function() {
				var c = this, e = c.list
						.children(".ui-orderlist-item.ui-state-highlight"), d = e.length, b = 0, a = e
						.is(":first-child");
				if (a) {
					return
				}
				e.each(function() {
					var f = $(this);
					if (!f.is(":first-child")) {
						f.hide(c.cfg.effect, {}, "fast", function() {
							f.insertBefore(f.prev()).show(c.cfg.effect, {},
									"fast", function() {
										b++;
										if (d === b) {
											c.saveState();
											c.fireReorderEvent()
										}
									})
						})
					} else {
						d--
					}
				})
			},
			moveTop : function() {
				var d = this, f = d.list
						.children(".ui-orderlist-item.ui-state-highlight"), e = f.length, b = 0, a = f
						.is(":first-child"), c = f.eq(0).index();
				if (a) {
					return
				}
				f
						.each(function(h) {
							var i = $(this), g = (h === 0) ? 0
									: (i.index() - c);
							if (!i.is(":first-child")) {
								i
										.hide(
												d.cfg.effect,
												{},
												"fast",
												function() {
													i
															.insertBefore(
																	d.list
																			.children(
																					".ui-orderlist-item")
																			.eq(
																					g))
															.show(
																	d.cfg.effect,
																	{},
																	"fast",
																	function() {
																		b++;
																		if (e === b) {
																			d
																					.saveState();
																			d
																					.fireReorderEvent()
																		}
																	})
												})
							} else {
								e--
							}
						})
			},
			moveDown : function() {
				var c = this, e = $(c.list.children(
						".ui-orderlist-item.ui-state-highlight").get()
						.reverse()), d = e.length, b = 0, a = e
						.is(":last-child");
				if (a) {
					return
				}
				e.each(function() {
					var f = $(this);
					if (!f.is(":last-child")) {
						f.hide(c.cfg.effect, {}, "fast", function() {
							f.insertAfter(f.next()).show(c.cfg.effect, {},
									"fast", function() {
										b++;
										if (d === b) {
											c.saveState();
											c.fireReorderEvent()
										}
									})
						})
					} else {
						d--
					}
				})
			},
			moveBottom : function() {
				var d = this, g = $(d.list.children(
						".ui-orderlist-item.ui-state-highlight").get()
						.reverse()), f = g.length, c = 0, a = g
						.is(":last-child"), e = g.eq(0).index(), b = this.items.length;
				if (a) {
					return
				}
				g
						.each(function(i) {
							var j = $(this), h = (i === 0) ? b - 1
									: (j.index() - e) - 1;
							if (!j.is(":last-child")) {
								j
										.hide(
												d.cfg.effect,
												{},
												"fast",
												function() {
													j
															.insertAfter(
																	d.list
																			.children(
																					".ui-orderlist-item")
																			.eq(
																					h))
															.show(
																	d.cfg.effect,
																	{},
																	"fast",
																	function() {
																		c++;
																		if (f === c) {
																			d
																					.saveState();
																			d
																					.fireReorderEvent()
																		}
																	})
												})
							} else {
								f--
							}
						})
			},
			hasBehavior : function(a) {
				if (this.cfg.behaviors) {
					return this.cfg.behaviors[a] != undefined
				}
				return false
			},
			fireItemSelectEvent : function(b, d) {
				if (this.hasBehavior("select")) {
					var c = this.cfg.behaviors.select, a = {
						params : [ {
							name : this.id + "_itemIndex",
							value : b.index()
						}, {
							name : this.id + "_metaKey",
							value : d.metaKey
						}, {
							name : this.id + "_ctrlKey",
							value : d.ctrlKey
						} ]
					};
					c.call(this, a)
				}
			},
			fireItemUnselectEvent : function(c) {
				if (this.hasBehavior("unselect")) {
					var a = this.cfg.behaviors.unselect, b = {
						params : [ {
							name : this.id + "_itemIndex",
							value : c.index()
						} ]
					};
					a.call(this, b)
				}
			},
			fireReorderEvent : function() {
				if (this.hasBehavior("reorder")) {
					this.cfg.behaviors.reorder.call(this)
				}
			}
		});