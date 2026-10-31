package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Vizam
 */
public class Card1108_049 extends AbstractAlien {
    public Card1108_049() {
        super(Side.DARK, 3, 1, 2, 1, 3, Title.Vizam, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Kajain'sa'Nikto. Keeps Jabba's vehicles and starships well armed. Has hidden several weapon caches on board Jabba's sail barge.");
        setGameText("Whenever opponent deploys a starship, vehicle, or weapon (including a [Permanent Weapon] weapon) to same or related location, you may activate 2 Force.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.justDeployedTo(game, effectResult, opponent,
                Filters.or(Filters.starship, Filters.vehicle, Filters.weapon, Filters.and(Filters.character, Icon.PERMANENT_WEAPON)),
                Filters.sameOrRelatedLocation(self))
                && GameConditions.canActivateForce(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Activate 2 Force");
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
