package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceJustLostCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Obi-Wan's Apparition
 */
public class Card1220_131 extends AbstractNormalEffect {
    public Card1220_131() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.ObiWans_Apparition, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Luminous beings are we, not this crude matter.' The inner consciousness of a Jedi can transcend even death.");
        setGameText("Deploy on table. May place your just lost Jedi out of play. May use X Force to add X to a just drawn weapon or battle destiny, where X = number of your Jedi out of play.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.Jedi))) {
            PhysicalCard lostJedi = ((LostFromTableResult) effectResult).getCard();
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(lostJedi) + " out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(lostJedi) + " out of play");
            action.appendEffect(
                    new PlaceJustLostCardOutOfPlayFromLostPileEffect(action, playerId, playerId, lostJedi, false));
            actions.add(action);
        }

        int x = Filters.filterCount(game.getGameState().getOutOfPlayPile(playerId), game, Integer.MAX_VALUE, Filters.Jedi).size();
        GameTextActionId addId = GameTextActionId.LEGACY__OBI_WANS_APPARITION_V__ADD_DESTINY;
        if (x > 0
                && (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult) || TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult))
                && GameConditions.canUseForce(game, playerId, x)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, addId);
            action.setText("Add " + x + " to destiny");
            action.appendCost(
                    new UseForceEffect(action, playerId, x));
            action.appendEffect(
                    new ModifyDestinyEffect(action, x));
            actions.add(action);
        }
        return actions;
    }
}
