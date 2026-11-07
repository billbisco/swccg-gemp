package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Final Legacy Block 2 errata (72E), front face. */
public class Card1220_072 extends AbstractObjective {
    static final Filter BLOCKADE_ON_HOTH = Filters.and(Filters.title("Hoth Blockade"),
            Filters.attachedTo(Filters.Hoth_system));
    private static final Filter GENERATORS = Filters.and(Icon.LEGACY_BLOCK_8, Filters.Main_Power_Generators);
    private static final Filter NON_BLOCK_8_LANDING = Filters.and(Filters.title(Title.You_May_Start_Your_Landing),
            Filters.not(Icon.LEGACY_BLOCK_8));
    private static final Filter UPLOAD = Filters.or(Filters.AT_AT_Cannon, Filters.title("Hoth Blockade"),
            Filters.title("Electro-Rangefinder"), Filters.and(Icon.HOTH, Filters.Epic_Event));

    public Card1220_072() {
        super(Side.DARK, 0, Title.Imperial_Occupation, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Hoth system, [Virtual Block 6] Ice Plains, and [Virtual Block 8] Main Power Generators. May deploy Imperial Decree. For remainder of game, AT-ATs deploy -1 to marker sites. Walker Garrison may only be played once per game and is [Immune to Sense]. Non-[Virtual Block 8] You May Start Your Landing is canceled. While this side up, Revolution is canceled. Once during your turn, may take one AT-AT Cannon, Hoth Blockade, Electro-Rangefinder, or a [Hoth] Epic Event into hand from Reserve Deck; reshuffle. Flip this card if Main Power Generators 'blown away' and Hoth Blockade on Hoth.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(startingDownload(action, "Hoth system", Filters.Hoth_system));
        action.appendRequiredEffect(startingDownload(action, "Block 6 Ice Plains", Filters.and(Icon.LEGACY_BLOCK_6, Filters.Ice_Plains)));
        action.appendRequiredEffect(startingDownload(action, "Block 8 Main Power Generators", GENERATORS));
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
                new MayNotPlayModifier(self, Filters.Walker_Garrison, (state, query) ->
                        !Filters.filter(query.getCardsPlayedThisGame(playerId), state.getGame(), Filters.Walker_Garrison).isEmpty(), playerId),
                null));
        int source = self.getPermanentCardId();
        action.appendEffect(new AddUntilEndOfGameActionProxyEffect(action, new AbstractActionProxy() {
            @Override
            public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame currentGame, Effect effect) {
                return new ArrayList<>(cancelAsPlayed(currentGame, effect, currentGame.findCardByPermanentId(source), sourceId, NON_BLOCK_8_LANDING));
            }

            @Override
            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame currentGame, EffectResult result) {
                return new ArrayList<>(cancelOnTable(currentGame, result, currentGame.findCardByPermanentId(source), sourceId,
                        NON_BLOCK_8_LANDING, Title.You_May_Start_Your_Landing));
            }
        }));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new CancelEffectsOfRevolutionModifier(self, null));
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
        if (!GameConditions.isOnceDuringYourTurn(game, self, playerId, sourceId, id)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Take a Hoth card into hand");
        action.appendUsage(new OncePerTurnEffect(action));
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
