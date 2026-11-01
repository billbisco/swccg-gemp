package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Objective
 * Title: Imperial Entanglements / No One To Stop Us This Time
 */
public class Card1115_050_BACK extends AbstractObjective {
    public Card1115_050_BACK() {
        super(Side.DARK, 7, "No One To Stop Us This Time", ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, wherever you have a trooper riding a dewback, add one battle destiny. [Virtual Set 2] Local Trouble may be played at exterior Tatooine sites. Once during your control phase, may peek at the top X cards of your Reserve Deck, where X = number of Tatooine locations you occupy; take one into hand and reshuffle. Opponent's characters require +1 Force to move from Tatooine sites using their landspeed. Flip this card if opponent controls more Tatooine sites than you.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        Filter riding = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.location.accepts(gameState, modifiersQuerying, physicalCard)
                        && Filters.canSpot(gameState.getGame(), self, Filters.and(Filters.your(playerId), Filters.trooper,
                        Filters.aboard(Filters.title("Dewback")), Filters.at(physicalCard)));
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, riding, 1, playerId));
        modifiers.add(new MoveCostFromLocationUsingLandspeedModifier(self,
                Filters.and(Filters.opponents(self), Filters.character), 1, Filters.Tatooine_site));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isTableChanged(game, effectResult) && GameConditions.canBeFlipped(game, self)) {
            int yours = 0;
            int theirs = 0;
            for (PhysicalCard site : Filters.filterTopLocationsOnTable(game, Filters.Tatooine_site)) {
                if (GameConditions.controls(game, playerId, site)) {
                    yours++;
                }
                if (GameConditions.controls(game, opponent, site)) {
                    theirs++;
                }
            }
            if (theirs > yours) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.appendEffect(new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
