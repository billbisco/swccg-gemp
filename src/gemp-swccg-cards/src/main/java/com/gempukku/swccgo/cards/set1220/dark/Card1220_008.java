package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostCardFromTableResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Derdram
 */
public class Card1220_008 extends AbstractImperial {
    public Card1220_008() {
        super(Side.DARK, 2, 2, 3, 1, 4, "Corporal Derdram", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Member of Imperial Intelligence, Internal Security division. Assigned by IntSec to Executor. Responsible for physical safety of Imperial personnel against Rebel saboteurs.");
        setGameText("Adds 2 to power of anything he pilots. While at an Executor site, may use 1 Force to place a just lost non-unique smuggler (and all copies of that card in opponent's Lost Pile) out of play. At same non-docking bay site, opponent may not deploy Jar Jar or spies.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.Jar_Jar, Filters.spy)),
                Filters.and(Filters.sameSite(self), Filters.not(Filters.docking_bay))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DERDRAM_V__PLACE_SMUGGLER_OUT_OF_PLAY;
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.non_unique, Filters.smuggler))
                && GameConditions.isAtLocation(game, self, Filters.Executor_site)
                && GameConditions.canUseForce(game, playerId, 1)) {

            PhysicalCard lostCard = ((LostCardFromTableResult) effectResult).getCard();
            Set<PhysicalCard> toPlaceOutOfPlay = new HashSet<PhysicalCard>();
            toPlaceOutOfPlay.add(lostCard);
            toPlaceOutOfPlay.addAll(Filters.filter(game.getGameState().getLostPile(opponent), game, Filters.sameTitle(lostCard)));

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(lostCard) + " out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(lostCard) + " and copies in opponent's Lost Pile out of play");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new PlaceCardsOutOfPlayFromOffTableEffect(action, toPlaceOutOfPlay));
            return Collections.singletonList(action);
        }
        return null;
    }
}
