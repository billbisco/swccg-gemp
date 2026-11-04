package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.LoseDestinyCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Salacious Crumb (V)
 */
public class Card1282_005 extends AbstractAlien {
    public Card1282_005() {
        super(Side.DARK, 3, 0, 0, 1, 3, Title.Salacious_Crumb, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male Kowakian. Prankster. Humiliates others for Jabba's amusement. His life depends on making Jabba laugh at least once per day.");
        setGameText("Unless R2-D2 present, while with Jabba at Audience Chamber and opponent just drew weapon or battle destiny, may say \"AH-hahahahaha!\" and place this character in Used Pile to cancel and redraw that destiny (canceled destiny card is lost).");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.KOWAKIAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if ((TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, opponent)
                || TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent))
                && GameConditions.isAtLocation(game, self, Filters.Audience_Chamber)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Jabba, Filters.with(self)))
                && !GameConditions.isPresentWith(game, self, Filters.R2D2)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel and redraw destiny");
            action.setActionMsg("Say \"AH-hahahahaha!\" and place " + GameUtils.getCardLink(self) + " in Used Pile to cancel and redraw opponent's destiny");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new LoseDestinyCardEffect(action));
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
