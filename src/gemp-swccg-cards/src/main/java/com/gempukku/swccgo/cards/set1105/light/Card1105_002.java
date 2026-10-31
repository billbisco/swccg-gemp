package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Away Put Your Weapon
 */
public class Card1105_002 extends AbstractUsedOrLostInterrupt {
    public Card1105_002() {
        super(Side.LIGHT, 5, Title.Away_Put_Your_Weapon, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I mean you no harm.'");
        setGameText("USED: An opponent's just drawn lightsaber weapon destiny is -3. (Immune to Sense.) LOST: Once per game, place all of your weapon cards from Lost Pile and/or on table in Used Pile (limit 5).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, opponent, Filters.lightsaber)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Subtract 3 from weapon destiny");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Make opponent's lightsaber weapon destiny -3",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -3));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId placeId = GameTextActionId.OVS__AWAY_PUT_YOUR_WEAPON__PLACE_WEAPONS;
        Collection<PhysicalCard> lostWeapons = Filters.filter(game.getGameState().getLostPile(playerId), game, Filters.weapon);
        Collection<PhysicalCard> tableWeapons = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.weapon));
        if (GameConditions.isOncePerGame(game, self, placeId)
                && (!lostWeapons.isEmpty() || !tableWeapons.isEmpty())) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, placeId, CardSubtype.LOST);
            action.setText("Place weapons in Used Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Place your weapons from Lost Pile and/or table in Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            List<PhysicalCard> toPlace = new ArrayList<PhysicalCard>();
                            toPlace.addAll(Filters.filter(game.getGameState().getLostPile(playerId), game, Filters.weapon));
                            toPlace.addAll(Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.weapon)));
                            int limit = Math.min(5, toPlace.size());
                            for (int i = 0; i < limit; i++) {
                                PhysicalCard weapon = toPlace.get(i);
                                if (weapon.getZone() == Zone.LOST_PILE || weapon.getZone() == Zone.TOP_OF_LOST_PILE) {
                                    action.appendEffect(
                                            new PutCardFromLostPileInUsedPileEffect(action, playerId, weapon, false));
                                } else {
                                    action.appendEffect(
                                            new PlaceCardInUsedPileFromTableEffect(action, weapon));
                                }
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
