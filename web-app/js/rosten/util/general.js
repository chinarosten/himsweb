/**
 * @author rosten
 * @created 2013-12-01
 */
define(["dojo/_base/declare"], function(declare) {
    return declare("rosten.util.general", null, {

        /*
         * select option 增删改，解决dojo不完善功能
         */
        selectObject : declare("rosten.util.select", null, {
            
            //get all value
            getAllValue:function(objSelect){
                var data =[];
                for (var i = 0; i < objSelect.options.length; i++) {
                    var map ={
                        name:objSelect.options[i].text,
                        value:objSelect.options[i].value,
                        departId:objSelect.options[i].departId
                    };
                    data.push(map);
                }
                return data;
            },
            
            // get the select text
            getSelectText : function(objSelect) {
                var index = objSelect.selectedIndex;
                var value = objSelect.options[index].text;
                return value;
            },
            //get the select value
            getSelectValue : function(objSelect) {
                var index = objSelect.selectedIndex;
                var value = objSelect.options[index].value;
                return value;
            },
            //set the first select is true
            selectItemByDefault : function(objSelect) {
                objSelect.options[0].selected = true;

            },
            //set the value select is true
            selectItemByValue : function(objSelect, objItemText) {
                for (var i = 0; i < objSelect.options.length; i++) {
                    if (objSelect.options[i].value == objItemText) {
                        objSelect.options[i].selected = true;
                        break;
                    }
                }
            },
            //set the value select is true
            selectItemByText : function(objSelect, objItemText) {
                for (var i = 0; i < objSelect.options.length; i++) {
                    if (objSelect.options[i].text == objItemText) {
                        objSelect.options[i].selected = true;
                        break;
                    }
                }
            },
            //delete option from select
            removeItemFromSelect : function(objSelect, objItemValue) {
                if (this.selectIsExitItem(objSelect, objItemValue)) {
                    for (var i = 0; i < objSelect.options.length; i++) {
                        if (objSelect.options[i].value == objItemValue) {
                            objSelect.options.remove(i);
                            break;
                        }
                    }
                }
            },
            //add option
            AddItemToSelect : function(objSelect, objItemText, objItemValue) {
                //check the option is exist
                if (this.selectIsExitItem(objSelect, objItemValue)) {
                } else {
                    var varItem = new Option(objItemText, objItemValue);
                    objSelect.options.add(varItem);
                }
            },
            //check the value is exist
            selectIsExitItem : function(objSelect, objItemValue) {
                var isExit = false;
                for (var i = 0; i < objSelect.options.length; i++) {
                    if (objSelect.options[i].value == objItemValue) {
                        isExit = true;
                        break;
                    }
                }
                return isExit;
            },
            //add option
            addSelectOption : function(oListbox, sName, sValue) {
                var oOption = document.createElement("option");
                oOption.appendChild(document.createTextNode(sName));
                if (arguments.length == 3) {
                    oOption.setAttribute("value", sValue);
                }
                oListbox.appendChild(oOption);
            }
        }),

        setSpace : function(num) {
            var oString = "";
            for (var i = 0; i < num; i++) {
                oString += "&nbsp;";
            };
            return oString;
        },
        //get href 's args
        getUrlArgs : function() {
            var args = new Object();
            var query = location.search.substring(1);
            // Get query string
            var pairs = query.split("&");
            // Break at ampersand
            for (var i = 0; i < pairs.length; i++) {
                var pos = pairs[i].indexOf('=');
                // Look for "name=value"
                if (pos == -1)
                    continue;
                // If not found, skip
                var argname = pairs[i].substring(0, pos);
                // Extract the name
                var value = pairs[i].substring(pos + 1);
                // Extract the value
                value = decodeURIComponent(value);
                // Decode it, if needed
                args[argname] = value;
                // Store as a property
            };
            return args;
            // Return the object
        },
        implodeArray : function(/*Array*/arr, /*String*/parm) {
            if ((arr && arr instanceof Array || typeof arr == "array")) {
                var sString = "";
                for (var i = 0; i < arr.length; i++) {
                    if (i == 0) {
                        sString += arr[i];
                    } else {
                        sString += parm + arr[i];
                    }
                }
                return sString;
            } else {
                return arr;
            }
        },
        // trim a array
        arrayTrim : function(/*Array*/arr) {
            if (arr && arr instanceof Array || typeof arr == "array") {
                var o = [];
                for (var i = 0; i < arr.length; i++) {
                    if (this.isInArray(o, arr[i]) == false && arr[i] != "" && arr[i] != " ")
                        o.push(arr[i]);
                }
                return o;
            } else
                return arr;
        },
        //find the position
        findInArray : function(/*String or Array*/arr, /*String*/parm) {
            if (arr && arr instanceof Array || typeof arr == "array") {
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i] == parm)
                        return i;
                }
                return -1;
            } else if (arr && typeof arr == "string" || arr instanceof String) {
                return arr.indexOf(parm);
            }
        },
        // check a string is in array or string
        isInArray : function(/*String or Array*/arr, /*String*/parm) {
            if (this.findInArray(arr, parm) == -1)
                return false;
            else
                return true;
        },
        // return the right string from the right
        stringRightBack : function(/*String*/str, /*String*/parm) {
            var index = str.lastIndexOf(parm);
            if (index == -1)
                return str;
            return str.substr(index + 1);
        },
        // return the left string from the right
        stringLeftBack : function(/*String*/str, /*String*/parm) {
            var index = str.lastIndexOf(parm);
            if (index == -1)
                return str;
            return str.substring(0, index);
        },
        // return the right string
        stringRight : function(/*String*/str, /*String*/parm) {
            var index = str.indexOf(parm);
            if (index == -1)
                return str;
            return str.substr(index + 1);
        },
        // return the left string
        stringLeft : function(/*String*/str, /*String*/parm) {
            var index = str.indexOf(parm);
            if (index == -1)
                return str;
            return str.substring(0, index);
        },
        _stringTrim : function(/*String*/str, /*integer*/parm) {
            if (!str.replace) {
                return str;
            } else {
                if (!str.length) {
                    return str;
                } else {
                    //  str = str.replace( /^\s+/g, "" );// strip leading
                    //  return str.replace( /\s+$/g, "" );// strip trailing
                    var re = (parm > 0) ? (/^\s+/) : (parm < 0) ? (/\s+$/) : (/^\s+|\s+$/g);
                    return str.replace(re, "");
                    //  string
                }
            }
        },
        stringTrim : function(/*String*/str, /*String*/parm) {
            if (!parm) {
                var oStr = this.splitString(str, " ");
            } else {
                var oStr = this.splitString(str, parm);
            }
            return oStr.join("");
        },
        splitString : function(/*String*/str, /*String*/parm) {
            var o = [];
            if (str.indexOf(parm) == -1) {
                o.push(str);
                return o;
            } else {
                return o = str.split(parm);
            }
        },
        getStrLength : function(str) {
            var len = 0;
            for (var i = 0; i < str.length; i++) {
                var _char = str.charCodeAt(i);
                if (!(_char < 27 || _char > 126)) {
                    len = len + 1;
                } else {
                    len = len + 2;
                }
            }
            return len;
        },
        checkStrLength : function(str, checkLen) {
            var _length = this.getStrLength(str);

            if (_length > checkLen) {
                return false;
            } else {
                return true;
            }
        },
        chgNumToRMB : function(Dight, How) {
            var Dight = Math.round(Dight * Math.pow(10, How)) / Math.pow(10, How);
            return Dight;
        },
        obj2str : function(obj) {
            var THIS = this;
            switch (typeof(obj)) {
                case 'string':
                    return '"' + obj.replace(/(["\\])/g, '\\$1') + '"';
                case 'array':
                    return '[' + obj.map(THIS.obj2str).join(',') + ']';
                case 'object':
                    if ( obj instanceof Array) {
                        var strArr = [];
                        var len = obj.length;
                        for (var i = 0; i < len; i++) {
                            strArr.push(THIS.obj2str(obj[i]));
                        }
                        return '[' + strArr.join(',') + ']';
                    } else if (obj == null) {
                        return 'null';

                    } else {
                        var string = [];
                        for (var property in obj)
                        string.push(THIS.obj2str(property) + ':' + THIS.obj2str(obj[property]));
                        return '{' + string.join(',') + '}';
                    }
                case 'number':
                    return obj;
                case false:
                    return obj;
            }
        }
    });

});
