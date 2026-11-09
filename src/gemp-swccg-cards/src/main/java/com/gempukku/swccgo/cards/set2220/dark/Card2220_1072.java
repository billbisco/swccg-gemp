package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_072;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.modifiers.ChangeCardSubtypeModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.InterruptPlacedOutOfPlayWhenCompletedModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Objective
 * Title: Imperial Occupation / Imperial Control (front)
 *
 * PC-E1 version of 1220_72 (front): the Competitive Balance Team errata posted on the Players Committee forum
 * ("Current Rulings/Errata", 2012-09-21) and printed on the E-marked PC card image. Like the current card it deploys
 * [Virtual Block 8] Main Power Generators, cancels non-[Virtual Block 8] You May Start Your Landing and takes a card into
 * hand once during your turn, but Walker Garrison is still an Out Of Play Interrupt (not "once per game"). Replaced on
 * 2012-11-19 ("obsoletes all previous errata to this card"), when the Out Of Play Interrupt subtype was removed.
 * The back side is 2220_1072_BACK. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1072 extends Card1220_072 {
    private static final Filter NON_BLOCK_8_LANDING = Filters.and(Filters.title(Title.You_May_Start_Your_Landing),
            Filters.not(Icon.LEGACY_BLOCK_8));

    public Card2220_1072() {
        super();
        setGameText("Deploy Hoth system, [Virtual Block 6] Ice Plains, and [Virtual Block 8] Main Power Generators. May deploy Imperial Decree. For remainder of game, Walker Garrison is an Out Of Play Interrupt and [Immune to Sense]. AT-ATs are deploy -1 to marker sites. Non-[Virtual Block 8] You May Start Your Landing is canceled. While this side up, Revolution is canceled. Once during your turn, may take one AT-AT Cannon, Hoth Blockade, Electro-Rangefinder, or a [Hoth] Epic Event into hand from Reserve Deck; reshuffle. Flip this card if Main Power Generators 'blown away' and Hoth Blockade on Hoth.");
        setLegacyVersion(new LegacyVersion("1220_72", "PC-E1",
                "PC forum \"Current Rulings/Errata\" (2012-09-21, Competitive Balance Team errata); E-marked PC card image",
                LocalDate.of(2012, 9, 21), LocalDate.of(2012, 11, 19)));
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId,
            SwccgGame game, PhysicalCard self, int sourceId) {
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new DeployCostToLocationModifier(self, Filters.AT_AT, -1, Filters.marker_site), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, Filters.Walker_Garrison, Title.Sense), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ChangeCardSubtypeModifier(self, Filters.Walker_Garrison, CardSubtype.OUT_OF_PLAY), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new InterruptPlacedOutOfPlayWhenCompletedModifier(self, Filters.Walker_Garrison, CardSubtype.LOST), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new InterruptPlacedOutOfPlayWhenCompletedModifier(self, Filters.Walker_Garrison, CardSubtype.USED), null));
        int source = self.getPermanentCardId();
        action.appendEffect(new AddUntilEndOfGameActionProxyEffect(action, new AbstractActionProxy() {
            @Override
            public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame currentGame, Effect effect) {
                return new ArrayList<>(Card2220_072.cancelAsPlayed(currentGame, effect,
                        currentGame.findCardByPermanentId(source), sourceId, NON_BLOCK_8_LANDING));
            }

            @Override
            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame currentGame, EffectResult result) {
                return new ArrayList<>(Card2220_072.cancelOnTable(currentGame, result,
                        currentGame.findCardByPermanentId(source), sourceId, NON_BLOCK_8_LANDING,
                        Title.You_May_Start_Your_Landing));
            }
        }));
        return action;
    }
}
