package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
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
 * Original version of 1220_72 (Virtual Block 2 release sheet, Dec 2009, card #72E): deploys any Main Power Generators
 * (no [Virtual Block 8] icon), makes Walker Garrison an Out Of Play Interrupt, limits opponent's Force loss from your [Special Edition]
 * Effects to 1 and takes a card into hand during your control phase; no You May Start Your Landing clause. The back
 * side (2220_72_BACK) adds "At marker sites, AT-ATs move for free." Both were changed by the CBT errata of 2012-09-21 (PC forum,
 * PC-E1 = 2220_1072) and again on 2012-11-19 (the current card). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_072 extends AbstractObjective {
    static final Filter BLOCKADE_ON_HOTH = Filters.and(Filters.title("Hoth Blockade"),
            Filters.attachedTo(Filters.Hoth_system));
    // "Main Power Generators (Light Side)": a deck holds only its own side's cards, so, as the Virtual Set 4 card with this
    // text (1104_40) does, any Main Power Generators the Dark Side player has may be deployed.
    private static final Filter GENERATORS = Filters.Main_Power_Generators;
    private static final Filter UPLOAD = Filters.or(Filters.AT_AT_Cannon, Filters.title("Hoth Blockade"),
            Filters.title("Electro-Rangefinder"), Filters.and(Icon.HOTH, Filters.Epic_Event));

    public Card2220_072() {
        super(Side.DARK, 0, Title.Imperial_Occupation, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Hoth system, [Virtual Block 6] Ice Plains, and Main Power Generators (Light Side). May deploy Imperial Decree. For remainder of game, Walker Garrison is an Out Of Play Interrupt and [Immune to Sense]. AT-ATs are deploy -1 to marker sites. While this side up, Revolution is canceled. Opponent loses no more than 1 Force to your [Special Edition] Effects. During your control phase, may take one AT-AT Cannon, Hoth Blockade, Electro-Rangefinder, or a [Hoth] Epic Event into hand from Reserve Deck; reshuffle. Flip this card if Main Power Generators 'blown away' and Hoth Blockade on Hoth.");
        setLegacyVersion(new LegacyVersion("1220_72", "",
                "Virtual Block 2 release sheet (2009-12-07), card #72", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 9, 21)));
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(startingDownload(action, "Hoth system", Filters.Hoth_system));
        action.appendRequiredEffect(startingDownload(action, "Block 6 Ice Plains", Filters.and(Icon.LEGACY_BLOCK_6, Filters.Ice_Plains)));
        action.appendRequiredEffect(startingDownload(action, "Main Power Generators", GENERATORS));
        action.appendOptionalEffect(startingDownload(action, "Imperial Decree", Filters.title(Title.Imperial_Decree)));
        return action;
    }

    private DeployCardFromReserveDeckEffect startingDownload(ObjectiveDeployedTriggerAction action,
                                                             String title, Filter candidates) {
        return new DeployCardFromReserveDeckEffect(action, candidates, true, false) {
            @Override
            public String getChoiceText() {
                return "Choose " + title + " to deploy";
            }
        };
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
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new CancelEffectsOfRevolutionModifier(self, null),
                new LimitForceLossFromCardModifier(self, Filters.and(Filters.your(self), CardType.EFFECT, Icon.SPECIAL_EDITION),
                        1, game.getOpponent(self.getOwner())));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int sourceId) {
        return cancelAsPlayed(game, effect, self, sourceId, Filters.Revolution);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var actions = new ArrayList<>(cancelOnTable(game, result, self, sourceId, Filters.Revolution, Title.Revolution));
        if (TriggerConditions.isTableChanged(game, result) && GameConditions.canBeFlipped(game, self)
                && game.getModifiersQuerying().isBlownAway(game.getGameState(), Filters.title(Title.Main_Power_Generators, true))
                && GameConditions.canSpot(game, self, BLOCKADE_ON_HOTH)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.appendEffect(new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__IMPERIAL_OCCUPATION_V__UPLOAD;
        if (!GameConditions.isOnceDuringYourPhase(game, self, playerId, sourceId, id, Phase.CONTROL)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Take a Hoth card into hand");
        action.appendUsage(new OncePerPhaseEffect(action));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, UPLOAD, true));
        return List.of(action);
    }

    static List<RequiredGameTextTriggerAction> cancelAsPlayed(SwccgGame game, Effect effect,
            PhysicalCard self, int sourceId, Filter target) {
        if (self == null || !TriggerConditions.isPlayingCard(game, effect, target)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect))
            return List.of();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    static List<RequiredGameTextTriggerAction> cancelOnTable(SwccgGame game, EffectResult result,
            PhysicalCard self, int sourceId, Filter target, String title) {
        if (self == null || !TriggerConditions.isTableChanged(game, result)
                || !GameConditions.canTargetToCancel(game, self, target))
            return List.of();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardAction(action, target, title);
        return List.of(action);
    }

}
