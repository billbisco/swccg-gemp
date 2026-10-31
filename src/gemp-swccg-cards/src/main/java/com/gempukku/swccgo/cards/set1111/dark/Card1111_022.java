package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Defensive Shield
 * Title: Frozen Dinner
 */
public class Card1111_022 extends AbstractDefensiveShield {
    public Card1111_022() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Frozen Dinner", ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Freeze dried and ready to serve, a tasty, wholesome Rebel makes a nutritious meal for the whole wampa clan. They're Gr-r-reat!");
        setGameText("Plays on table. Colo Claw Fish is canceled. Tawss Khaa affects Force drains only at her site. Once per game, may ▲ Pinned Down. While Ellorrs Madak (V) on table: limit one copy on table, each \"2\" in its game text is treated as \"1\", and opponent may retrieve Force only once per turn.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.not(Filters.sameSiteAs(self, Filters.title(Title.Tawss_Khaa))),
                new OnTableCondition(self, Filters.title(Title.Tawss_Khaa)), opponent));

        Condition ellorrsOnTable = new OnTableCondition(self, Filters.and(Filters.title(Title.Ellorrs_Madak), Filters.Effect));
        Condition fiveSpeciesOnOpponent = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return GameConditions.countSpeciesOnTable(game, opponent) >= 5;
            }
        };
        Condition ellorrsFive = new AndCondition(ellorrsOnTable, fiveSpeciesOnOpponent);
        modifiers.add(new ForceDrainModifier(self, Filters.any, ellorrsFive, -1, opponent));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, ellorrsFive, -1, opponent, true));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, ellorrsFive, 1, playerId, true));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__FROZEN_DINNER__TAKE_PINNED_DOWN;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Pinned Down into hand from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Pinned_Down), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Colo_Claw_Fish)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Colo_Claw_Fish)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Colo_Claw_Fish, Title.Colo_Claw_Fish);
            actions.add(action);
        }
        return actions;
    }
}
