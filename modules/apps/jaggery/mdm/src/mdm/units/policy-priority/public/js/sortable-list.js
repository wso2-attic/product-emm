/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var sortableListFunction = (function(){
    var returnObj = {};
    returnObj.init = function(){

        var sortableElem = '.wr-sortable',
            sortUpdateBtn = '#sortUpdateBtn',
            applyChangesBtn = '#applyChangesBtn',
            sortableElemList = [],
            sortedIDs,
            currentElemId,
            sortableElemLength = $(sortableElem + ' .list-group-item').not('.ui-sortable-placeholder').length;

        /**
         * create list of available index numbers for autocomplete listing
         */
        for(var i = 1; i <= sortableElemLength; i++){
            sortableElemList.push(i.toString());
        }

        /**
         * sort index number reset function
         */
        function addSortableIndexNumbers(){
            $(sortableElem + ' .list-group-item').not('.ui-sortable-placeholder').each(function(i){
                $('.wr-sort-index input.index', this).val(i+1);
                $(this).attr('data-sort-index', i+1);
            });
        }

        /**
         * on input text field focus autocomplete bind function
         */
        $(sortableElem).on('focus', '.wr-sort-index input.index', function(){
            currentElemId = $(this).val();
            $(this).autocomplete({
                source: sortableElemList,
                minLength: 0,
                close: function(event, ui){
                    if($.inArray($(this).val(), sortableElemList) !== -1){
                        $(this).removeClass('has-error');
                    }
                }
            });
        });

        /**
         * on index edit icon click focusing input text field function
         */
        $(sortableElem).on('click', '.wr-sort-index .icon', function(){
            $(sortableElem + ' .wr-sort-index input.index').focusout();
            $(this).siblings('input.index').focus();
        });

        /**
         * on input key press validation function
         */
        $(sortableElem).on('keyup', '.wr-sort-index input.index', function(e){
            if (e.which == 13) {
                $(this).focusout();
            }
            else if ($.inArray($(this).val(), sortableElemList) == -1){
                $(this).addClass('has-error');
            }
            else {
                $(this).removeClass('has-error');
            }
        });

        /**
         * on input text value enter re-sorting function with validation
         */
        $(sortableElem).on('blur', '.wr-sort-index input.index', function(){
            if(($(this).val() > 0) && ($(this).val() < sortableElemLength+1)){

                $(this).closest('.list-group-item').attr('data-sort-index', $(this).val());

                $(sortableElem + ' .list-group-item').not('.ui-sortable-placeholder').sort(function(a, b) {
                    return parseInt($(a).data('sort-index')) < parseInt($(b).data('sort-index'));
                }).each(function(){
                    var elem = $(this);
                    elem.remove();
                    $(elem).prependTo(sortableElem);
                });

                $(sortUpdateBtn).prop('disabled', false);
                //$(applyChangesBtn).prop('disabled', false);
                addSortableIndexNumbers();
            }
            else{
                $(this).val(currentElemId);
            }
            $(this).removeClass('has-error');
        });

        /**
         * on page loaded functions
         *          add sortable list index numbers
         *          enable drag & drop sortable function
         */
        $(function() {
            addSortableIndexNumbers();

            $(sortableElem).sortable({
                beforeStop: function(event, ui){
                    sortedIDs = $(this).sortable('toArray');
                    addSortableIndexNumbers();
                    $(sortUpdateBtn).prop('disabled', false);
                    //$(applyChangesBtn).prop('disabled', false);
                }
            });
            $(sortableElem).disableSelection();
        });

        returnObj.getSortedItems = function (){
            return sortedIDs;
        }
    };
    return returnObj;
})();

$(function(){
    sortableListFunction.init();
});