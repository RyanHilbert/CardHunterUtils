var REMOTE_URL = 'https://raw.githubusercontent.com/Xayrn/CardHunterUtils/t_htmlCardView/src/templates/card.handlebars'

function InitializeAnd(callback) {
    $(document).ready(function () {
        var render = null;
        $.ajaxPrefilter("html", function (options) {
            options.crossDomain = true;
        });
        if (window.Handlebars)
            if ($("#cardTemplate").length) {
                render = Handlebars.compile($("#cardTemplate").html())
                Handlebars.registerPartial('box', $("#boxTemplate").html())
                Handlebars.registerPartial('icon', $("#iconTemplate").html())
                Handlebars.registerHelper('iconImg', iconImgHelper);
                Handlebars.registerHelper('iconAmt', iconAmtHelper);
                callback(render);
            } else
                $.get(REMOTE_URL, function (template) {
                    if (template) {
                        render = Handlebars.compile(template);
                        callback(render);
                    } else
                        throw new Error("Provided template was empty.");
                }, "html"); // The available data types are text, html, xml, json, jsonp, and script.
        else
            throw new Error("Cannot compile template - Handlebars has not been loaded.");
    });
}

function iconImgHelper(type, amount) {
    return (type === "Dice") ? "D" + Math.abs(amount) : type;
}

function iconAmtHelper(type, amount) {
    return (type === "Dice") ? "+" : (amount != 0 ? amount : "&nbsp;");
}
