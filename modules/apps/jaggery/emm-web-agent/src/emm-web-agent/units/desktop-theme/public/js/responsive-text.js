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

var resTextRatio = 0.2;

(function($){

    /* ========================================================================
     * responsive text function
     * ======================================================================== */
    $.fn.res_text = function(compress, options){

        // Setup options
        var compressor = compress || 1,
            settings = $.extend({
                'minFontSize' : Number.NEGATIVE_INFINITY,
                'maxFontSize' : Number.POSITIVE_INFINITY
            }, options);

        return this.each(function(){

            /**
             * store the object
             */
            var $this = $(this);

            /**
             * resizer() resizes items based on the object width divided by the compressor * 10
             */
            var resizer = function() {
                $this.css('font-size', Math.max(Math.min($this.width() / (compressor*10), parseFloat(settings.maxFontSize)), parseFloat(settings.minFontSize)));
            };

            /**
             *  call once to set.
             */
            resizer();

            /**
             *  call on resize. Opera debounces their resize by default.
             */
            $(window).on('resize.fittext orientationchange.fittext', resizer);

        });

    };

}(jQuery));

$(document).ready(function(){
    $(".icon .text").res_text(resTextRatio);
});

$(window).scroll(function(){
    $(".icon .text").res_text(resTextRatio);
});

$(document).bind('click', function() {
    $(".icon .text").res_text(resTextRatio);
});