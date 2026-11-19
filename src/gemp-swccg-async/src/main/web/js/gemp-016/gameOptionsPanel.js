/* The Play form's optional rules share its dialog, without moving its main column (#778). */
var GameOptionsPanel = Class.extend({
    init: function (form, mainPanel, overlay) {
        this.form = form;
        this.overlay = overlay;
        this.workspace = $("<div class='play-options-workspace' role='dialog' aria-modal='true' aria-label='Create Table'></div>");
        mainPanel.removeAttr("role aria-modal aria-label").wrap(this.workspace);
        this.workspace = mainPanel.parent();
        this.trigger = $("<button type='button' class='play-options-trigger' aria-controls='play-game-options' aria-expanded='false'><span>Game options <small>(optional)</small><span class='play-options-status'></span></span><span aria-hidden='true'>▸</span></button>");
        this.summary = $("<div class='play-options-summary' aria-live='polite'></div>");
        this.panel = $("<section id='play-game-options' class='play-flow-panel play-options-panel' aria-labelledby='play-options-title' hidden><div class='play-options-header'><h2 id='play-options-title'>Game options</h2><button type='button' class='play-close-button' aria-label='Close game options'>×</button></div><div class='play-options-body'><p>Closing this panel keeps your choices.</p><div class='play-options-error' role='alert' hidden></div></div></section>");
        this.panel.find(".play-options-body").append(form.container);
        this.workspace.append(this.panel);
        var that = this;
        this.trigger.on("click", function () { that.show(!that.isOpen()); });
        this.panel.find(".play-options-header button").on("click", function () { that.show(false, true); });
        this.workspace.on("keydown", function (event) {
            if (event.key === "Escape" && that.isOpen()) {
                event.stopPropagation();
                event.preventDefault();
                that.show(false, true);
            }
        });
        form.container.on("gameoptionschange", function () { that.render(); });
        this.render();
    },

    activate: function (active) {
        this.workspace.toggleClass("play-options-active", active);
        this.overlay.toggleClass("play-flow-options", active);
        this.show(false);
    },

    isOpen: function () { return !this.panel.prop("hidden"); },

    show: function (open, returnFocus) {
        this.panel.prop("hidden", !open);
        this.trigger.attr("aria-expanded", String(open));
        if (returnFocus)
            this.trigger.trigger("focus");
        else if (open && window.matchMedia("(max-width:1080px)").matches)
            this.panel[0].scrollIntoView({block: "start"});
    },

    render: function () {
        var values = this.form.values(), keys = Object.keys(values), that = this;
        this.trigger.find(".play-options-status").text(keys.length
            ? keys.length + " custom " + (keys.length === 1 ? "option" : "options") + " active"
            : "Using format defaults");
        this.summary.empty();
        $.each(keys, function (_, id) {
            var r = that.form.rows[id], value = values[id];
            if (value === "true" || value === "false")
                value = value === "true" ? "On" : "Off";
            else if (r.option.type === "CHOICE")
                $.each(r.option.choices, function (_, c) { if (c.value === value) value = c.label; });
            that.summary.append($("<span class='game-option-chip'></span>").text(r.option.label + ": " + value));
        });
        if (this.form.validate() == null)
            this.panel.find(".play-options-error").prop("hidden", true);
    },

    showValidation: function (message) {
        this.show(true);
        this.panel.find(".play-options-error").text(message).prop("hidden", false);
        if (this.form.invalidInput)
            this.form.invalidInput.attr("aria-invalid", "true").trigger("focus");
    }
});
