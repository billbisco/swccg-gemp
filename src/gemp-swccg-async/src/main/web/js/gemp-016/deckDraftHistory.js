/*
 * Undo / redo history for an unsaved deck draft (work#221, the join screen's deck editor).
 * A state is {deck: [card], outside: [card]} where card is {blueprintId, testingText, backSideTestingText, horizontal}.
 * Each recorded state is one edit step, so a grouped edit (e.g. adding all shields) is undone in one step and a move
 * between the deck and outside-of-deck zones is one step. No jQuery or DOM use, so tests run it under node.
 */
var DeckDraftHistory = function (initialState) {
    this.reset(initialState);
};

/** The deck contents string the server takes ("ids in deck|ids outside"), used to compare states. */
DeckDraftHistory.contentsOf = function (state) {
    var ids = function (cards) {
        var result = [];
        for (var i = 0; i < (cards || []).length; i++)
            result.push(cards[i].blueprintId);
        return result.join(",");
    };
    if (state == null)
        return "|";
    return ids(state.deck) + "|" + ids(state.outside);
};

DeckDraftHistory.prototype = {
    /** Starts over from a freshly loaded or saved deck. */
    reset: function (state) {
        this.states = [state];
        this.index = 0;
        this.savedContents = DeckDraftHistory.contentsOf(state);
    },

    current: function () {
        return this.states[this.index];
    },

    /** Adds an edit step; drops any redo steps. Returns false when the contents did not change. */
    record: function (state) {
        if (DeckDraftHistory.contentsOf(state) === DeckDraftHistory.contentsOf(this.current()))
            return false;
        this.states = this.states.slice(0, this.index + 1);
        this.states.push(state);
        this.index = this.states.length - 1;
        return true;
    },

    canUndo: function () {
        return this.index > 0;
    },

    canRedo: function () {
        return this.index < this.states.length - 1;
    },

    /** The state to show after undoing, or null when there is nothing to undo. */
    undo: function () {
        if (!this.canUndo())
            return null;
        this.index--;
        return this.current();
    },

    /** The state to show after redoing, or null when there is nothing to redo. */
    redo: function () {
        if (!this.canRedo())
            return null;
        this.index++;
        return this.current();
    },

    /** True when the draft differs from the last loaded or saved deck. */
    isDirty: function () {
        return DeckDraftHistory.contentsOf(this.current()) !== this.savedContents;
    },

    /** The current draft was saved: it becomes the clean point (history is kept). */
    markSaved: function () {
        this.savedContents = DeckDraftHistory.contentsOf(this.current());
    }
};

/** True when a join failure message is about the chosen deck (so editing the deck can fix it). */
DeckDraftHistory.isDeckJoinFailure = function (message) {
    return message != null && /deck is not valid|not valid for this format/i.test(message);
};

/** Deck names follow the Deckbuilder's rule: 3 to 40 characters. Returns an error text or null. */
DeckDraftHistory.deckNameProblem = function (name, currentName) {
    if (name == null || name.length < 3 || name.length > 40)
        return "Deck name has to have at least 3 characters and at most 40 characters.";
    if (currentName != null && name === currentName)
        return "Choose a name different from the original deck to save a copy.";
    return null;
};

if (typeof module !== "undefined")
    module.exports = DeckDraftHistory;
