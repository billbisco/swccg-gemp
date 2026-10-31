package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttackModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Malakili
 */
public class Card1108_041 extends AbstractAlien {
    public Card1108_041() {
        super(Side.DARK, 3, 6, 2, 1, 3, Title.Malakili, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corellian. Worked for Circus Horrificus. Disapproves of Jabba's treatment of the rancor. Plotting with Lady Valarian to steal the immense beast away from Jabba's palace.");
        setGameText("Deploys -4 on Tatooine. Rancors may deploy here (even if Malakili is not the only character, habitat permitting), are defense value +4 here, and may not attack Malakili. During your deploy phase, may deploy one Rancor here from Reserve Deck; reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -4, Filters.Deploys_on_Tatooine));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter rancorsHere = Filters.and(Filters.Rancor, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToLocationModifier(self, Filters.Rancor, Filters.here(self)));
        modifiers.add(new DefenseValueModifier(self, rancorsHere, 4));
        modifiers.add(new MayNotAttackModifier(self, rancorsHere));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MALAKILI_V__DEPLOY_RANCOR;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Rancor from Reserve Deck");
            action.setActionMsg("Deploy a Rancor here from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Rancor, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
