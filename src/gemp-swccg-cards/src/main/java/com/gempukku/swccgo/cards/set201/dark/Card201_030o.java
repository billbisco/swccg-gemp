package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Effect
 * Title: Imperial Enforcement
 * Text state: o (as first published: PC card image of 12 July 2020; the "E1"-marked re-render of 3 October 2022,
 * PC-E1, prints the same words; docs/legacy-versions.md "Text states")
 * <p>
 * Plays the printed words: the take-into-hand redraw has no per-battle limit (the 2020 GEMP code limited it to once per
 * battle; the card never did), and "unless you occupy fewer locations than opponent" is checked when each part is used.
 */
public class Card201_030o extends AbstractNormalEffect {
    public Card201_030o() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Enforcement, Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setLore("When Vader's forces impose the New Order upon a region, Rebel resources and lifelines are quickly eliminated.");
        setGameText("Deploy on table. Lost if your non-Imperial character (or starship) on table. Unless you occupy fewer locations than opponent: if you win a battle, retrieve an Imperial; and, during battle, may take an Imperial just drawn for destiny into hand to cancel and redraw that destiny. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
        setTextState(new TextState("201_30", "Printed",
                "PC card image, 12 July 2020 (Holotable fdfe14b); same words on the E1-marked image of 3 October 2022",
                LocalDate.of(2020, 7, 12), LocalDate.of(2022, 12, 12)));
    }

    private static boolean occupiesAtLeastAsManyLocations(SwccgGame game, String playerId) {
        String opponent = game.getOpponent(playerId);
        return Filters.countTopLocationsOnTable(game, Filters.occupies(playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE))
                >= Filters.countTopLocationsOnTable(game, Filters.occupies(opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();

        // Lost if your non-Imperial character (or starship) on table.
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.your(self),
                Filters.or(Filters.and(Filters.character, Filters.not(Filters.Imperial)), Filters.and(Filters.starship, Filters.not(Filters.Imperial_starship)))))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        // Unless you occupy fewer locations than opponent: if you win a battle, retrieve an Imperial.
        if (TriggerConditions.wonBattle(game, effectResult, playerId)
                && occupiesAtLeastAsManyLocations(game, playerId)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setText("Retrieve an Imperial");
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.Imperial));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Unless you occupy fewer locations than opponent: during battle, may take an Imperial just drawn for destiny
        // into hand to cancel and redraw that destiny.
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDuringBattle(game)
                && occupiesAtLeastAsManyLocations(game, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.Imperial)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take destiny card into hand and cause re-draw");
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
