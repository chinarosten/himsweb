/**
 * @author rosten
 * @created 2013-12-01
 */
define([ "dijit/Dialog", "dojo/_base/declare","dojo/_base/kernel","dojo/dom-style"

], function(dialog, declare,kernel,domStyle) {

	return declare("rosten.widget.RostenDialog", [ dialog ], {

		/*
		 * summary: extended version of the dojo Dialog widget with the option
		 * to disable the close button and supress the escape key.
		 */

		disableCloseButton : true,

		/*
		 * ***********************************************************
		 * postCreate
		 */
		postCreate : function() {
			this.inherited(arguments);
			this._updateCloseButtonState();
		},
		/*
		 * ***************************************************************
		 * _onKey
		 */
		_onKey : function(evt) {
			if (this.disableCloseButton && evt.charOrCode == kernel.keys.ESCAPE)
				return;
			this.inherited(arguments);
		},
		/*
		 * ************************************************
		 * setCloseButtonDisabled
		 */
		setCloseButtonDisabled : function(flag) {
			this.disableCloseButton = flag;
			this._updateCloseButtonState();
		},
		/*
		 * **********************************************
		 * _updateCloseButtonState
		 */
		_updateCloseButtonState : function() {
			domStyle.set(this.closeButtonNode, "display",
					this.disableCloseButton ? "none" : "block");

		}

	});
});