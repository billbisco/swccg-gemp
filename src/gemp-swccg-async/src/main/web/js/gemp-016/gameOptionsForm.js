/*
 * Game options form (swccg-guild/work#114/#116).
 *
 * The hall sends the option registry as <gameOption> elements (id, kind, type, label, help, scopes, <choice>s) and, on
 * each <format>, the format's availability, default and range per option (opt-<id>-available, -default, -defaultLabel,
 * -min, -max). This builds one row per option from that, so a new option on the server appears here with no client
 * change. Empty values mean "the format's default"; only values that differ are sent.
 */
var GameOptionsForm = Class.extend({
    options: null,      // [{id, kind, type, label, help, scopes:[], choices:[{value,label,shortLabel}]}]
    rows: null,         // id -> {row, input, note, option}
    container: null,
    scope: "CASUAL",
    locked: false,
    onChange: null,
    remember: null,     // id -> true for options whose last value is kept in a cookie (timer)

    init: function (container, onChange) {
        this.container = container;
        this.onChange = onChange;
        this.options = [];
        this.rows = {};
        this.remember = {timer: true};
    },

    /** Reads the <gameOption> elements of a GET /hall document and rebuilds the rows. */
    setRegistry: function (root) {
        var elems = root.getElementsByTagName("gameOption");
        var options = [];
        for (var i = 0; i < elems.length; i++) {
            var e = elems[i];
            var choices = [];
            var choiceElems = e.getElementsByTagName("choice");
            for (var j = 0; j < choiceElems.length; j++) {
                choices.push({
                    value: choiceElems[j].getAttribute("value"),
                    label: choiceElems[j].getAttribute("label"),
                    shortLabel: choiceElems[j].getAttribute("shortLabel")
                });
            }
            options.push({
                id: e.getAttribute("id"),
                kind: e.getAttribute("kind"),
                type: e.getAttribute("type"),
                label: e.getAttribute("label"),
                help: e.getAttribute("help") || "",
                scopes: (e.getAttribute("scopes") || "").split(","),
                choices: choices
            });
        }
        if (options.length === 0)
            return;
        this.options = options;
        this.build();
    },

    build: function () {
        var that = this;
        this.container.empty();
        this.rows = {};
        $.each(this.options, function (index, option) {
            var id = "game-option-" + option.id + "-" + Math.floor(Math.random() * 1e6);
            var row = $("<div class='play-form-row game-option-row'></div>").attr("data-option", option.id);
            row.append($("<label class='play-form-label'></label>").attr("for", id).text(option.label));
            var input;
            if (option.type === "CHOICE") {
                input = $("<select class='play-form-select game-option-input'></select>").attr("id", id);
                $.each(option.choices, function (i, choice) {
                    input.append($("<option></option>").attr("value", choice.value).text(choice.label));
                });
            } else if (option.type === "BOOLEAN") {
                input = $("<select class='play-form-select game-option-input'></select>").attr("id", id);
                input.append($("<option value=''></option>"));
                input.append($("<option value='true'>On</option>"));
                input.append($("<option value='false'>Off</option>"));
            } else {
                input = $("<input type='number' step='1' class='play-form-select game-option-input'>").attr("id", id);
            }
            var note = $("<span class='game-option-note'></span>");
            row.append(input).append(note);
            var timer = null;
            input.on("input change", function () {
                if (that.remember[option.id])
                    try { $.cookie("game-option-" + option.id, input.val(), {expires: 365}); } catch (e) { }
                if (timer != null)
                    clearTimeout(timer);
                timer = setTimeout(function () {
                    timer = null;
                    if (that.onChange)
                        that.onChange(option.id);
                }, option.type === "INTEGER" ? 400 : 0);
            });
            that.rows[option.id] = {row: row, input: input, note: note, option: option};
            that.container.append(row);
        });
    },

    /**
     * Resets the rows for a selected format (a jQuery <option> carrying the format's data-opt-* attributes) and a
     * scope ("CASUAL", "BOT", "TOURNAMENT", "LEAGUE"). A locked form (league tables) only shows the league's options.
     */
    refresh: function (formatOption, scope, locked, lockedText) {
        this.scope = scope || "CASUAL";
        this.locked = !!locked;
        var that = this;
        $.each(this.rows, function (id, r) {
            var attr = function (name) {
                return formatOption != null && formatOption.length ? formatOption.attr("data-opt-" + id + "-" + name) : null;
            };
            var available = attr("available") === "true";
            var inScope = $.inArray(that.scope, r.option.scopes) >= 0;
            var show = available && inScope && !that.locked;
            r.row.toggle(show);
            r.input.prop("disabled", !show);
            var def = attr("default") || "";
            var defLabel = attr("defaultLabel") || def;
            if (r.option.type === "INTEGER") {
                r.input.attr("min", attr("min")).attr("max", attr("max"));
                r.input.attr("placeholder", defLabel || "default");
                r.input.val("");
                r.note.text((r.option.help ? r.option.help + " " : "")
                    + (attr("min") != null ? attr("min") + "\u2013" + attr("max") + "." : ""));
            } else {
                var remembered = null;
                if (that.remember[id])
                    try { remembered = $.cookie("game-option-" + id); } catch (e) { }
                var value = remembered && r.input.find("option[value='" + remembered + "']").length ? remembered : def;
                r.input.val(value);
                r.note.text(r.option.help || "");
            }
        });
        if (this.lockedNote == null) {
            this.lockedNote = $("<div class='play-form-row game-option-locked'></div>");
            this.container.prepend(this.lockedNote);
        }
        this.lockedNote.toggle(this.locked).text(this.locked
            ? "Game options are set by the league" + (lockedText ? ": " + lockedText : " (format defaults).")
            : "");
        this.formatOption = formatOption;
    },

    /** Puts saved values back into the rows after a refresh ({id: value}; unknown ids are ignored). */
    setValues: function (values) {
        var that = this;
        $.each(values || {}, function (id, value) {
            var r = that.rows[id];
            if (r != null && !r.input.prop("disabled") && value != null && value !== "")
                r.input.val(String(value));
        });
    },

    /** The chosen value of one option, or "" when it is left at the default. */
    value: function (id) {
        var r = this.rows[id];
        if (r == null || r.input.prop("disabled"))
            return "";
        var value = $.trim(r.input.val() || "");
        var def = this.formatOption != null ? (this.formatOption.attr("data-opt-" + id + "-default") || "") : "";
        return value === def ? "" : value;
    },

    /** {id: value} of the options that differ from the format's defaults. */
    values: function () {
        var result = {};
        var that = this;
        $.each(this.rows, function (id) {
            var v = that.value(id);
            if (v !== "")
                result[id] = v;
        });
        return result;
    },

    /** A message for the first value out of range, or null. The server checks again. */
    validate: function () {
        var message = null;
        var that = this;
        $.each(this.rows, function (id, r) {
            if (message != null || r.input.prop("disabled") || r.option.type !== "INTEGER")
                return;
            var v = $.trim(r.input.val() || "");
            if (v === "")
                return;
            var min = Number(r.input.attr("min")), max = Number(r.input.attr("max"));
            if (!/^[0-9]{1,6}$/.test(v) || Number(v) < min || Number(v) > max)
                message = r.option.label + " must be a whole number from " + min + " to " + max + ".";
        });
        return message;
    },

    /** The key the deck lists use for a format at the chosen deck size ("open@100"), or the format code itself. */
    deckListCode: function (baseCode) {
        var size = this.value("deckSize");
        return baseCode && size !== "" ? baseCode + "@" + size : baseCode;
    }
});

/** "Open" plus its option chips as a jQuery fragment, for list cells (#116). Text only, never HTML from the server. */
function gameFormatWithChips(formatName, chipsText) {
    var cell = $("<span class='format-with-options'></span>");
    cell.append($("<span class='format-name-text'></span>").text(formatName || ""));
    if (chipsText) {
        var chips = $("<span class='game-option-chips'></span>");
        $.each(chipsText.split(" \u00b7 "), function (i, chip) {
            chips.append($("<span class='game-option-chip'></span>").text(chip));
        });
        cell.append(" ").append(chips);
    }
    return cell;
}
